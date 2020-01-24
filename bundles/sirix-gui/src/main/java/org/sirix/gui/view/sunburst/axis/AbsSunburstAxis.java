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

package org.sirix.gui.view.sunburst.axis;

import org.sirix.api.INodeReadTrx;
import org.sirix.axis.EIncludeSelf;
import org.sirix.gui.view.AbsDiffAxis;

/**
 * <h1>AbsSunburstAxis</h1>
 * 
 * <p>
 * Provide standard Java iterator capability compatible with the new enhanced for loop available since Java 5.
 * </p>
 * 
 * <p>
 * All users must make sure to call next() after hasNext() evaluated to true.
 * </p>
 */
public abstract class AbsSunburstAxis extends AbsDiffAxis {

  /**
   * Bind axis step to transaction.
   * 
   * @param pRtx
   *          transaction to operate with
   */
  public AbsSunburstAxis(final INodeReadTrx pRtx) {
    super(pRtx);
  }

  /**
   * Bind axis step to transaction.
   * 
   * @param pRtx
   *          transaction to operate with
   * @param pIncludeSelf
   *          determines if self is included
   */
  public AbsSunburstAxis(final INodeReadTrx pRtx, final EIncludeSelf pIncludeSelf) {
    super(pRtx, pIncludeSelf);
  }

  /**
   * Get {@code descendant-or-self} count for current node.
   * 
   * @return {@code descendant-or-self} count
   */
  public abstract int getDescendantCount();

  /**
   * Get {@code modification + descendant-or-self} count for current node.
   * 
   * @return {@code modification + descendant-or-self} count
   */
  public abstract int getModificationCount();

  /**
   * Get {@code pruned-node} count for current node.
   * 
   * @return {@code pruned-node} count
   */
  public abstract int getPrunedNodes();

  /** Decrement index in the diff-datastructure by one. */
  public abstract void decrementIndex();
}
