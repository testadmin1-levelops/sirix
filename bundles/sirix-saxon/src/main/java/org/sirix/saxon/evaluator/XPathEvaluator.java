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

package org.sirix.saxon.evaluator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import org.sirix.api.ISession;
import org.sirix.saxon.wrapper.DocumentWrapper;

/**
 * <h1>XPath Evaluator</h1>
 * 
 * <p>
 * The XPath evaluator takes an XPath expression and evaluates the expression against a wrapped sirix
 * document.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class XPathEvaluator implements Callable<XPathSelector> {

  /** An XPath expression. */
  private final String mExpression;

  /** Sirix {@link ISession}. */
  private final ISession mSession;

  /** The revision to open. */
  private int mRevision;

  /**
   * Builder.
   */
  public static class Builder {
    /** An XPath expression. */
    private final String mExpression;

    /** Sirix {@link ISession}. */
    private final ISession mSession;

    /** The revision to open. */
    private int mRevision;

    /**
     * Constructor.
     * 
     * @param pExpression
     *          XPath expression
     * @param pSession
     *          sirix {@link ISession} instance
     */
    public Builder(@Nonnull final String pExpression, @Nonnull final ISession pSession) {
      mExpression = checkNotNull(pExpression);
      mSession = checkNotNull(pSession);
      mRevision = pSession.getLastRevisionNumber();
    }

    /**
     * Set a revision to open.
     * 
     * @param pRevision
     *          the revision to open
     * @return this builder instance
     */
    public Builder setRevision(@Nonnegative final int pRevision) {
      checkArgument(pRevision >= 0, "pRevision must be >= 0!");
      mRevision = pRevision;
      return this;
    }

    /**
     * Build a new instance.
     * 
     * @return new {@link XPathEvaluator} instance
     * */
    public XPathEvaluator build() {
      return new XPathEvaluator(this);
    }
  }

  /**
   * Private Constructor.
   * 
   * @param pBuilder
   *          {@link Builder} instance
   * @param pSession
   *          sirix {@link ISession} instance
   * @param pRevision
   *          the revision to open
   */
  public XPathEvaluator(@Nonnull final Builder pBuilder) {
    mExpression = pBuilder.mExpression;
    mSession = pBuilder.mSession;
    mRevision = pBuilder.mRevision;
  }

  @Override
  public XPathSelector call() throws Exception {
    final Processor proc = new Processor(false);
    final Configuration config = proc.getUnderlyingConfiguration();
    final NodeInfo doc = new DocumentWrapper(mSession, mRevision, config);
    final XPathCompiler xpath = proc.newXPathCompiler();
    final DocumentBuilder builder = proc.newDocumentBuilder();
    final XdmItem item = builder.wrap(doc);
    final XPathSelector selector = xpath.compile(mExpression).load();
    selector.setContextItem(item);
    return selector;
  }
}
