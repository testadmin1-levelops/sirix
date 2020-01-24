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
package org.sirix.diff.algorithm.fmse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nonnull;

import org.sirix.access.AbsVisitorSupport;
import org.sirix.api.INodeReadTrx;
import org.sirix.api.ISession;
import org.sirix.api.visitor.EVisitResult;
import org.sirix.exception.SirixException;
import org.sirix.node.EKind;
import org.sirix.node.ElementNode;
import org.sirix.node.TextNode;
import org.sirix.node.interfaces.INode;

/**
 * Initialize data structures.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class FMSEVisitor extends AbsVisitorSupport {

  /** {@link INodeReadTrx} reference. */
  private final INodeReadTrx mRtx;

  /** Determines if nodes are in order. */
  private final Map<Long, Boolean> mInOrder;

  /** Descendant count per node. */
  private final Map<Long, Long> mDescendants;

  /**
   * Constructor.
   * 
   * @param pSession
   *          {@link ISession} implementation
   * @param pInOrder
   *          {@link Map} reference to track ordered nodes
   * @param pDescendants
   *          {@link Map} reference to track descendants per node
   * @throws SirixException
   *           if setting up sirix fails
   * @throws NullPointerException
   *           if one of the arguments is {@code null}
   */
  public FMSEVisitor(@Nonnull final INodeReadTrx pReadTransaction,
    @Nonnull final Map<Long, Boolean> pInOrder,
    @Nonnull final Map<Long, Long> pDescendants) throws SirixException {
    mRtx = checkNotNull(pReadTransaction);
    mInOrder = checkNotNull(pInOrder);
    mDescendants = checkNotNull(pDescendants);
  }

  @Override
  public EVisitResult visit(@Nonnull final ElementNode pNode) {
    final long nodeKey = pNode.getNodeKey();
    mRtx.moveTo(nodeKey);
    for (int i = 0; i < pNode.getAttributeCount(); i++) {
      mRtx.moveToAttribute(i);
      fillStructuralDataStructures();
      mRtx.moveTo(nodeKey);
    }
    for (int i = 0; i < pNode.getNamespaceCount(); i++) {
      mRtx.moveToNamespace(i);
      fillStructuralDataStructures();
      mRtx.moveTo(nodeKey);
    }
    countDescendants();
    return EVisitResult.CONTINUE;
  }

  /**
   * Fill data structures.
   */
  private void fillStructuralDataStructures() {
    final INode node = mRtx.getNode();
    mInOrder.put(node.getNodeKey(), true);
    mDescendants.put(node.getNodeKey(), 1L);
  }

  /**
   * Count descendants of node (including self).
   */
  private void countDescendants() {
    long descendants = 0;
    final long nodeKey = mRtx.getNode().getNodeKey();
    ElementNode element = (ElementNode)mRtx.getNode();
    descendants += element.getNamespaceCount();
    descendants += element.getAttributeCount();
    if (mRtx.getStructuralNode().hasFirstChild()) {
      mRtx.moveToFirstChild();
      do {
        descendants += mDescendants.get(mRtx.getNode().getNodeKey());
        if (mRtx.getNode().getKind() == EKind.ELEMENT) {
          element = (ElementNode)mRtx.getNode();
          descendants += 1;
        }
      } while (mRtx.getStructuralNode().hasRightSibling()
        && mRtx.moveToRightSibling());
    }
    mRtx.moveTo(nodeKey);
    mInOrder.put(mRtx.getNode().getNodeKey(), false);
    mDescendants.put(mRtx.getNode().getNodeKey(), descendants);
  }

  @Override
  public EVisitResult visit(@Nonnull final TextNode pNode) {
    final long nodeKey = pNode.getNodeKey();
    mRtx.moveTo(nodeKey);
    mInOrder.put(mRtx.getNode().getNodeKey(), false);
    mDescendants.put(mRtx.getNode().getNodeKey(), 1L);
    return EVisitResult.CONTINUE;
  }
}
