package org.sirix.page;

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
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Objects;
import com.google.common.io.ByteArrayDataInput;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.sirix.page.delegates.PageDelegate;
import org.sirix.page.interfaces.IPage;

/**
 * Page to hold references to a path summary.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public class PathSummaryPage extends AbsForwardingPage {

  /** {@link PageDelegate} instance. */
  private final PageDelegate mDelegate;

  /** Offset of node page reference. */
  private static final int INDIRECT_REFERENCE_OFFSET = 0;

  /**
   * Path summary page.
   * 
   * @param pRevision
   *          revision number
   * @throws IllegalArgumentException
   *           if {@code pRevision} < 0
   */
  public PathSummaryPage(final @Nonnegative int pRevision) {
    checkArgument(pRevision >= 0, "pRevision must be >= 0!");
    mDelegate = new PageDelegate(1, pRevision);
  }

  /**
   * Get indirect page reference.
   * 
   * @return indirect page reference
   */
  public PageReference getIndirectPageReference() {
    return getReference(INDIRECT_REFERENCE_OFFSET);
  }

  /**
   * Read meta page.
   * 
   * @param pIn
   *          input bytes to read from
   */
  protected PathSummaryPage(final @Nonnull ByteArrayDataInput pIn) {
    mDelegate = new PageDelegate(1, pIn);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("mDelegate", mDelegate).toString();
  }

  @Override
  protected IPage delegate() {
    return mDelegate;
  }
  
	@Override
	public IPage setDirty(final boolean pDirty) {
		mDelegate.setDirty(pDirty);
		return this;
	}
  
}
