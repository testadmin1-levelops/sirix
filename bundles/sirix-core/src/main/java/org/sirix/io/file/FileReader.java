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

package org.sirix.io.file;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.Nonnull;

import org.sirix.exception.SirixIOException;
import org.sirix.io.IReader;
import org.sirix.io.bytepipe.ByteHandlePipeline;
import org.sirix.io.bytepipe.Encryptor;
import org.sirix.io.bytepipe.IByteHandler;
import org.sirix.io.bytepipe.SnappyCompressor;
import org.sirix.page.PagePersistenter;
import org.sirix.page.PageReference;
import org.sirix.page.UberPage;
import org.sirix.page.interfaces.IPage;

/**
 * File Reader. Used for ReadTransaction to provide read only access on a
 * RandomAccessFile.
 * 
 * @author Marc Kramis, Seabix
 * @author Sebastian Graf, University of Konstanz.
 * 
 */
public final class FileReader implements IReader {

  /** Beacon of first references. */
  final static int FIRST_BEACON = 12;

  /** Beacon of the other references. */
  final static int OTHER_BEACON = 4;

  /** Random access mFile to work on. */
  private final RandomAccessFile mFile;

  /** Inflater to decompress. */
  final IByteHandler mByteHandler;

  /**
   * Constructor.
   * 
   * @param pConcreteStorage
   *          storage file
   * @throws SirixIOException
   *           if something bad happens
   */
  public FileReader(final @Nonnull File pConcreteStorage, final @Nonnull IByteHandler pHandler) throws SirixIOException {
    try {
      if (!pConcreteStorage.exists()) {
        pConcreteStorage.getParentFile().mkdirs();
        pConcreteStorage.createNewFile();
      }

      mFile = new RandomAccessFile(pConcreteStorage, "r");
      mByteHandler = checkNotNull(pHandler);
    } catch (final IOException e) {
      throw new SirixIOException(e);
    }
  }

  /**
   * Read page from storage.
   * 
   * @param pKey
   *          key of page reference to read
   * @return byte array reader to read bytes from
   * @throws SirixIOException
   *           if there was an error during reading.
   */
  @Override
  public IPage read(final long pKey) throws SirixIOException {
    try {
      // Read page from file.
      mFile.seek(pKey);
      final int dataLength = mFile.readInt();
      final byte[] page = new byte[dataLength];
      mFile.read(page);

      // Perform byte operations.
      final ByteArrayDataInput input =
        ByteStreams.newDataInput(mByteHandler.deserialize(page));
      
      // Return reader required to instantiate and deserialize page.
      return PagePersistenter.deserializePage(input);
    } catch (final IOException e) {
      throw new SirixIOException(e);
    }
  }

  @Override
  public PageReference readFirstReference() throws SirixIOException {
    final PageReference uberPageReference = new PageReference();
    try {
      // Read primary beacon.
      mFile.seek(0);
      uberPageReference.setKey(mFile.readLong());
      final UberPage page = (UberPage)read(uberPageReference.getKey());
      uberPageReference.setPage(page);
      return uberPageReference;
    } catch (final IOException exc) {
      throw new SirixIOException(exc);
    }
  }

  @Override
  public void close() throws SirixIOException {
    try {
      mFile.close();
    } catch (final IOException exc) {
      throw new SirixIOException(exc);
    }
  }
}
