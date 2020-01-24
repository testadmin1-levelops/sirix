package org.sirix.index.path;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Objects;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sirix.api.visitor.EVisitResult;
import org.sirix.api.visitor.IVisitor;
import org.sirix.node.AbsStructForwardingNode;
import org.sirix.node.EKind;
import org.sirix.node.delegates.NameNodeDelegate;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.delegates.StructNodeDelegate;
import org.sirix.node.interfaces.INameNode;

public class PathNode extends AbsStructForwardingNode implements INameNode {

  private final NodeDelegate mNodeDel;
  private final StructNodeDelegate mStructNodeDel;
  private final NameNodeDelegate mNameNodeDel;
  private final EKind mKind;
  private int mReferences;
  private int mLevel;

  public PathNode(@Nonnull final NodeDelegate pNodeDel,
    @Nonnull final StructNodeDelegate pStructNodeDel,
    @Nonnull final NameNodeDelegate pNameNodeDel, @Nonnull final EKind pKind,
    @Nonnegative final int pReferences, @Nonnegative final int pLevel) {
    mNodeDel = checkNotNull(pNodeDel);
    mStructNodeDel = checkNotNull(pStructNodeDel);
    mNameNodeDel = checkNotNull(pNameNodeDel);
    mKind = checkNotNull(pKind);
    checkArgument(pReferences > 0, "pReferences must be > 0!");
    mReferences = pReferences;
    mLevel = pLevel;
  }

  public int getLevel() {
    return mLevel;
  }

  public int getReferences() {
    return mReferences;
  }

  public void setReferenceCount(final @Nonnegative int pReferences) {
    checkArgument(pReferences > 0, "pReferences must be > 0!");
    mReferences = pReferences;
  }

  public void incrementReferenceCount() {
    mReferences++;
  }

  public void decrementReferenceCount() {
    if (mReferences <= 1) {
      throw new IllegalStateException();
    }
    mReferences--;
  }

  /**
   * Get the kind of path (element, attribute or namespace).
   * 
   * @return path kind
   */
  public EKind getPathKind() {
    return mKind;
  }

  @Override
  public EKind getKind() {
    return EKind.PATH;
  }

  @Override
  public int getNameKey() {
    return mNameNodeDel.getNameKey();
  }

  @Override
  public int getURIKey() {
    return mNameNodeDel.getURIKey();
  }

  @Override
  public void setNameKey(final int pNameKey) {
    mNameNodeDel.setNameKey(pNameKey);
  }

  @Override
  public void setURIKey(final int pUriKey) {
    mNameNodeDel.setURIKey(pUriKey);
  }

  @Override
  public EVisitResult acceptVisitor(@Nonnull final IVisitor pVisitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected StructNodeDelegate structDelegate() {
    return mStructNodeDel;
  }

  @Override
  protected NodeDelegate delegate() {
    return mNodeDel;
  }

  /**
   * Get the name node delegate.
   * 
   * @return name node delegate.
   */
  public NameNodeDelegate getNameNodeDelegate() {
    return mNameNodeDel;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(mNodeDel, mNameNodeDel);
  }

  @Override
  public boolean equals(@Nullable Object pObj) {
    if (pObj instanceof PathNode) {
      final PathNode other = (PathNode)pObj;
      return Objects.equal(mNodeDel, other.mNodeDel)
        && Objects.equal(mNameNodeDel, other.mNameNodeDel);
    }
    return false;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("node delegate", mNodeDel).add(
      "struct delegate", mStructNodeDel).add("name delegate", mNameNodeDel)
      .add("references", mReferences).add("kind", mKind).add("level", mLevel)
      .toString();
  }

  @Override
  public void setPathNodeKey(long nodeKey) {
    // No path node key.
  }

  @Override
  public long getPathNodeKey() {
    return 0;
  }

}
