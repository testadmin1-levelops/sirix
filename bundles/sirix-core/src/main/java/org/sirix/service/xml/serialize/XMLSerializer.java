/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sirix.service.xml.serialize;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.sirix.service.xml.serialize.XMLSerializerProperties.S_ID;
import static org.sirix.service.xml.serialize.XMLSerializerProperties.S_INDENT;
import static org.sirix.service.xml.serialize.XMLSerializerProperties.S_INDENT_SPACES;
import static org.sirix.service.xml.serialize.XMLSerializerProperties.S_REST;
import static org.sirix.service.xml.serialize.XMLSerializerProperties.S_XMLDECL;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.sirix.access.Database;
import org.sirix.access.conf.DatabaseConfiguration;
import org.sirix.access.conf.ResourceConfiguration;
import org.sirix.access.conf.SessionConfiguration;
import org.sirix.api.IDatabase;
import org.sirix.api.INodeReadTrx;
import org.sirix.api.ISession;
import org.sirix.node.ElementNode;
import org.sirix.node.interfaces.INameNode;
import org.sirix.node.interfaces.IStructNode;
import org.sirix.settings.ECharsForSerializing;
import org.sirix.settings.IConstants;
import org.sirix.utils.Files;
import org.sirix.utils.LogWrapper;
import org.sirix.utils.XMLToken;
import org.slf4j.LoggerFactory;

/**
 * <h1>XMLSerializer</h1>
 * 
 * <p>
 * Most efficient way to serialize a subtree into an OutputStream. The encoding
 * always is UTF-8. Note that the OutputStream internally is wrapped by a
 * BufferedOutputStream. There is no need to buffer it again outside of this
 * class.
 * </p>
 */
public final class XMLSerializer extends AbsSerializer {

	/** {@link LogWrapper} reference. */
	private static final LogWrapper LOGWRAPPER = new LogWrapper(
			LoggerFactory.getLogger(XMLSerializer.class));

	/** Offset that must be added to digit to make it ASCII. */
	private static final int ASCII_OFFSET = 48;

	/** Precalculated powers of each available long digit. */
	private static final long[] LONG_POWERS = { 1L, 10L, 100L, 1000L, 10000L,
			100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L,
			100000000000L, 1000000000000L, 10000000000000L, 100000000000000L,
			1000000000000000L, 10000000000000000L, 100000000000000000L,
			1000000000000000000L };

	/** OutputStream to write to. */
	private final OutputStream mOut;

	/** Indent output. */
	private final boolean mIndent;

	/** Serialize XML declaration. */
	private final boolean mSerializeXMLDeclaration;

	/** Serialize rest header and closer and rest:id. */
	private final boolean mSerializeRest;

	/** Serialize id. */
	private final boolean mSerializeId;

	/** Number of spaces to indent. */
	private final int mIndentSpaces;

	/**
	 * Initialize XMLStreamReader implementation with transaction. The cursor
	 * points to the node the XMLStreamReader starts to read.
	 * 
	 * @param pSession
	 *          session for read XML
	 * @param pNodeKey
	 *          start node key
	 * @param pBuilder
	 *          builder of XML Serializer
	 * @param pRevision
	 *          revision to serialize
	 * @param pRevisions
	 *          further revisions to serialize
	 */
	private XMLSerializer(@Nonnull final ISession pSession,
			@Nonnegative final long pNodeKey,
			@Nonnull final XMLSerializerBuilder pBuilder,
			@Nonnegative final int pRevision, @Nonnull final int... pRevisions) {
		super(pSession, pNodeKey, pRevision, pRevisions);
		mOut = new BufferedOutputStream(pBuilder.mStream, 4096);
		mIndent = pBuilder.mIndent;
		mSerializeXMLDeclaration = pBuilder.mDeclaration;
		mSerializeRest = pBuilder.mREST;
		mSerializeId = pBuilder.mID;
		mIndentSpaces = pBuilder.mIndentSpaces;
	}

	/**
	 * Emit node (start element or characters).
	 */
	@Override
	protected void emitStartElement(final @Nonnull INodeReadTrx pRtx) {
		try {
			switch (pRtx.getNode().getKind()) {
			case DOCUMENT_ROOT:
				if (mIndent) {
					mOut.write(ECharsForSerializing.NEWLINE.getBytes());
				}
				break;
			case ELEMENT:
				// Emit start element.
				indent();
				mOut.write(ECharsForSerializing.OPEN.getBytes());
				mOut.write(pRtx.rawNameForKey(((INameNode) pRtx.getNode()).getNameKey()));
				final long key = pRtx.getNode().getNodeKey();
				// Emit namespace declarations.
				for (int index = 0, length = ((ElementNode) pRtx.getNode())
						.getNamespaceCount(); index < length; index++) {
					pRtx.moveToNamespace(index);
					if (pRtx.nameForKey(((INameNode) pRtx.getNode()).getNameKey())
							.isEmpty()) {
						mOut.write(ECharsForSerializing.XMLNS.getBytes());
						write(pRtx.nameForKey(((INameNode) pRtx.getNode()).getURIKey()));
						mOut.write(ECharsForSerializing.QUOTE.getBytes());
					} else {
						mOut.write(ECharsForSerializing.XMLNS_COLON.getBytes());
						write(pRtx.nameForKey(((INameNode) pRtx.getNode()).getNameKey()));
						mOut.write(ECharsForSerializing.EQUAL_QUOTE.getBytes());
						write(pRtx.nameForKey(((INameNode) pRtx.getNode()).getURIKey()));
						mOut.write(ECharsForSerializing.QUOTE.getBytes());
					}
					pRtx.moveTo(key);
				}
				// Emit attributes.
				// Add virtual rest:id attribute.
				if (mSerializeId) {
					if (mSerializeRest) {
						mOut.write(ECharsForSerializing.REST_PREFIX.getBytes());
					} else {
						mOut.write(ECharsForSerializing.SPACE.getBytes());
					}
					mOut.write(ECharsForSerializing.ID.getBytes());
					mOut.write(ECharsForSerializing.EQUAL_QUOTE.getBytes());
					write(pRtx.getNode().getNodeKey());
					mOut.write(ECharsForSerializing.QUOTE.getBytes());
				}

				// Iterate over all persistent attributes.
				for (int index = 0; index < ((ElementNode) pRtx.getNode())
						.getAttributeCount(); index++) {
					pRtx.moveToAttribute(index);
					mOut.write(ECharsForSerializing.SPACE.getBytes());
					mOut.write(pRtx.rawNameForKey(((INameNode) pRtx.getNode())
							.getNameKey()));
					mOut.write(ECharsForSerializing.EQUAL_QUOTE.getBytes());
					mOut.write(XMLToken.escapeAttribute(pRtx.getValueOfCurrentNode())
							.getBytes(IConstants.DEFAULT_ENCODING));// pRtx.getItem().getRawValue());
					mOut.write(ECharsForSerializing.QUOTE.getBytes());
					pRtx.moveTo(key);
				}
				if (((IStructNode) pRtx.getNode()).hasFirstChild()) {
					mOut.write(ECharsForSerializing.CLOSE.getBytes());
				} else {
					mOut.write(ECharsForSerializing.SLASH_CLOSE.getBytes());
				}
				if (mIndent) {
					mOut.write(ECharsForSerializing.NEWLINE.getBytes());
				}
				break;
			case TEXT:
				indent();
				mOut.write(XMLToken.escapeContent(pRtx.getValueOfCurrentNode())
						.getBytes(IConstants.DEFAULT_ENCODING));
				if (mIndent) {
					mOut.write(ECharsForSerializing.NEWLINE.getBytes());
				}
				break;
			}
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Emit end element.
	 * 
	 * @param pRtx
	 *          Read Transaction
	 */
	@Override
	protected void emitEndElement(final @Nonnull INodeReadTrx pRtx) {
		try {
			indent();
			mOut.write(ECharsForSerializing.OPEN_SLASH.getBytes());
			mOut.write(pRtx.rawNameForKey(((INameNode) pRtx.getNode()).getNameKey()));
			mOut.write(ECharsForSerializing.CLOSE.getBytes());
			if (mIndent) {
				mOut.write(ECharsForSerializing.NEWLINE.getBytes());
			}
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
	}

	@Override
	protected void emitStartDocument() {
		try {
			if (mSerializeXMLDeclaration) {
				write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			}
			if (mSerializeRest) {
				write("<rest:sequence xmlns:rest=\"REST\"><rest:item>");
			}
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
	}

	@Override
	protected void emitEndDocument() {
		try {
			if (mSerializeRest) {
				write("</rest:item></rest:sequence>");
			}
			mOut.flush();
		} catch (final IOException exc) {
			exc.printStackTrace();
		}

	}

	@Override
	protected void emitStartManualElement(final long pVersion) {
		try {
			write("<tt revision=\"");
			write(Long.toString(pVersion));
			write("\">");
		} catch (final IOException exc) {
			exc.printStackTrace();
		}

	}

	@Override
	protected void emitEndManualElement(final long pVersion) {
		try {
			write("</tt>");
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Indentation of output.
	 * 
	 * @throws IOException
	 *           if can't indent output
	 */
	private void indent() throws IOException {
		if (mIndent) {
			for (int i = 0; i < mStack.size() * mIndentSpaces; i++) {
				mOut.write(" ".getBytes());
			}
		}
	}

	/**
	 * Write characters of string.
	 * 
	 * @param pString
	 *          String to write
	 * @throws IOException
	 *           if can't write to string
	 * @throws UnsupportedEncodingException
	 *           if unsupport encoding
	 */
	protected void write(@Nonnull final String pString)
			throws UnsupportedEncodingException, IOException {
		mOut.write(pString.getBytes(IConstants.DEFAULT_ENCODING));
	}

	/**
	 * Write non-negative non-zero long as UTF-8 bytes.
	 * 
	 * @param pValue
	 *          value to write
	 * @throws IOException
	 *           if can't write to string
	 */
	private void write(final long pValue) throws IOException {
		final int length = (int) Math.log10(pValue);
		int digit = 0;
		long remainder = pValue;
		for (int i = length; i >= 0; i--) {
			digit = (byte) (remainder / LONG_POWERS[i]);
			mOut.write((byte) (digit + ASCII_OFFSET));
			remainder -= digit * LONG_POWERS[i];
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *          args[0] specifies the input-TT file/folder; args[1] specifies the
	 *          output XML file.
	 * @throws Exception
	 *           any exception
	 */
	public static void main(final String... args) throws Exception {
		if (args.length < 2 || args.length > 3) {
			throw new IllegalArgumentException(
					"Usage: XMLSerializer input-TT output.xml");
		}

		LOGWRAPPER.info("Serializing '" + args[0] + "' to '" + args[1] + "' ... ");
		final long time = System.nanoTime();
		final File target = new File(args[1]);
		Files.recursiveRemove(target.toPath());
		target.getParentFile().mkdirs();
		target.createNewFile();
		try (final FileOutputStream outputStream = new FileOutputStream(target)) {
			final DatabaseConfiguration config = new DatabaseConfiguration(new File(
					args[0]));
			Database.createDatabase(config);
			try (final IDatabase db = Database.openDatabase(new File(args[0]))) {
				db.createResource(new ResourceConfiguration.Builder("shredded", config)
						.build());
				final ISession session = db
						.getSession(new SessionConfiguration.Builder("shredded").build());

				final XMLSerializer serializer = new XMLSerializerBuilder(session,
						outputStream).build();
				serializer.call();
			}
		}

		LOGWRAPPER
				.info(" done [" + (System.nanoTime() - time) / 1_000_000 + "ms].");
	}

	/**
	 * XMLSerializerBuilder to setup the XMLSerializer.
	 */
	public static final class XMLSerializerBuilder {
		/**
		 * Intermediate boolean for indendation, not necessary.
		 */
		private boolean mIndent;

		/**
		 * Intermediate boolean for rest serialization, not necessary.
		 */
		private boolean mREST;

		/**
		 * Intermediate boolean for XML-Decl serialization, not necessary.
		 */
		private boolean mDeclaration = true;

		/**
		 * Intermediate boolean for ids, not necessary.
		 */
		private boolean mID;

		/**
		 * Intermediate number of spaces to indent, not necessary.
		 */
		private int mIndentSpaces = 2;

		/** Stream to pipe to. */
		private final OutputStream mStream;

		/** Session to use. */
		private final ISession mSession;

		/** Further revisions to serialize. */
		private int[] mRevisions;

		/** Revision to serialize. */
		private int mRevision;

		/** Node key of subtree to shredder. */
		private final long mNodeKey;

		/**
		 * Constructor, setting the necessary stuff.
		 * 
		 * @param pSession
		 *          Sirix {@link ISession}
		 * @param pStream
		 *          {@link OutputStream} to write to
		 * @param pRevisions
		 *          revisions to serialize
		 */
		public XMLSerializerBuilder(@Nonnull final ISession pSession,
				@Nonnull final OutputStream pStream, final int... pRevisions) {
			mNodeKey = 0;
			mSession = checkNotNull(pSession);
			mStream = checkNotNull(pStream);
			if (pRevisions == null || pRevisions.length == 0) {
				mRevision = mSession.getLastRevisionNumber();
			} else {
				mRevision = pRevisions[0];
				mRevisions = new int[pRevisions.length - 1];
				for (int i = 0; i < pRevisions.length - 1; i++) {
					mRevisions[i] = pRevisions[i + 1];
				}
			}
		}

		/**
		 * Constructor.
		 * 
		 * @param pSession
		 *          Sirix {@link ISession}
		 * @param pNodeKey
		 *          root node key of subtree to shredder
		 * @param pStream
		 *          {@link OutputStream} to write to
		 * @param pProperties
		 *          {@link XMLSerializerProperties} to use
		 * @param paramVersions
		 *          version(s) to serialize
		 */
		public XMLSerializerBuilder(@Nonnull final ISession pSession,
				@Nonnegative final long pNodeKey, @Nonnull final OutputStream pStream,
				@Nonnull final XMLSerializerProperties pProperties,
				final int... pRevisions) {
			checkArgument(pNodeKey >= 0, "pNodeKey must be >= 0!");
			mSession = checkNotNull(pSession);
			mNodeKey = pNodeKey;
			mStream = checkNotNull(pStream);
			if (pRevisions == null || pRevisions.length == 0) {
				mRevision = mSession.getLastRevisionNumber();
			} else {
				mRevision = pRevisions[0];
				mRevisions = new int[pRevisions.length - 1];
				for (int i = 0; i < pRevisions.length - 1; i++) {
					mRevisions[i] = pRevisions[i + 1];
				}
			}
			final ConcurrentMap<?, ?> map = checkNotNull(pProperties.getProps());
			mIndent = checkNotNull((Boolean) map.get(S_INDENT[0]));
			mREST = checkNotNull((Boolean) map.get(S_REST[0]));
			mID = checkNotNull((Boolean) map.get(S_ID[0]));
			mIndentSpaces = checkNotNull((Integer) map.get(S_INDENT_SPACES[0]));
			mDeclaration = checkNotNull((Boolean) map.get(S_XMLDECL[0]));
		}

		/**
		 * Setting the indendation.
		 * 
		 * @param pIndent
		 *          determines if it should be indented
		 * @return XMLSerializerBuilder reference
		 */
		public XMLSerializerBuilder setIndend(final boolean pIndent) {
			mIndent = pIndent;
			return this;
		}

		/**
		 * Setting the RESTful output.
		 * 
		 * @param pREST
		 *          set RESTful
		 * @return XMLSerializerBuilder reference
		 */
		public XMLSerializerBuilder setREST(final boolean pREST) {
			mREST = pREST;
			return this;
		}

		/**
		 * Setting the declaration.
		 * 
		 * @param pDeclaration
		 *          determines if the XML declaration should be emitted
		 * @return {@link XMLSerializerBuilder} reference
		 */
		public XMLSerializerBuilder setDeclaration(final boolean pDeclaration) {
			mDeclaration = pDeclaration;
			return this;
		}

		/**
		 * Setting the IDs on nodes.
		 * 
		 * @param pID
		 *          determines if IDs should be set for each node
		 * @return XMLSerializerBuilder reference
		 */
		public XMLSerializerBuilder setID(final boolean pID) {
			mID = pID;
			return this;
		}

		/**
		 * Setting the ids on nodes.
		 * 
		 * @param pRevisions
		 *          revisions to serialize
		 * @return XMLSerializerBuilder reference
		 */
		public XMLSerializerBuilder setVersions(final int[] pRevisions) {
			mRevisions = checkNotNull(pRevisions);
			return this;
		}

		/**
		 * Building new {@link Serializer} instance.
		 * 
		 * @return a new {@link Serializer} instance
		 */
		public XMLSerializer build() {
			return new XMLSerializer(mSession, mNodeKey, this, mRevision, mRevisions);
		}
	}

}
