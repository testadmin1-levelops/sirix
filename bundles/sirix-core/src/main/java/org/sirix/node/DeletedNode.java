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

package org.sirix.node;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sirix.api.visitor.EVisitResult;
import org.sirix.api.visitor.IVisitor;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.interfaces.INode;

/**
 * If a node is deleted, it will be encapsulated over this class.
 * 
 * @author Sebastian Graf
 * 
 */
public final class DeletedNode extends AbsForwardingNode {

  /**
   * Delegate for common data.
   */
  private final NodeDelegate mDel;

  /**
   * Constructor.
   * 
   * @param paramNode
   *          nodekey to be replaced with a deletednode
   * @param paramParent
   *          parent of this key.
   */
  public DeletedNode(@Nonnull final NodeDelegate pDel) {
    mDel = checkNotNull(pDel);
  }
  
  @Override
  public EKind getKind() {
    return EKind.DELETE;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(mDel);
  }

  @Override
  public boolean equals(final Object pObj) {
    boolean retVal = false;
    if (pObj instanceof DeletedNode) {
      final DeletedNode other = (DeletedNode)pObj;
      retVal = Objects.equal(mDel, other.mDel);
    }
    return retVal;
  }

  @Override
  public String toString() {
    return mDel.toString();
  }
  
  @Override
  public boolean isSameItem(@Nullable final INode pOther) {
    return mDel.isSameItem(pOther);
  }

  @Override
  protected NodeDelegate delegate() {
    return mDel;
  }

  @Override
  public EVisitResult acceptVisitor(@Nonnull IVisitor pVisitor) {
    throw new UnsupportedOperationException();
  }

}
