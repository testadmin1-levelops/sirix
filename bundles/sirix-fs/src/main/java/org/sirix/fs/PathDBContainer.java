package org.sirix.fs;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Objects;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import org.sirix.api.IDatabase;
import org.sirix.api.ISession;

/**
 * Container for {@code {@link Path}/{@link IDatabase} combinations. Note that it may be refined to {@code {
 * @link IDatabase}/{@link ISession}/{@link IResource} later on.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
@Nonnull
public final class PathDBContainer {

  /** {@link Path} to watch for modifications. */
  final Path mPath;

  /** sirix {@link IDatabase}. */
  final IDatabase mDatabase;

  /**
   * Constructor.
   * 
   * @param pPath
   *          {@link Path} reference
   * @param pDatabase
   *          sirix {@link IDatabase} reference
   */
  public PathDBContainer(final Path pPath, final IDatabase pDatabase) {
    mPath = checkNotNull(pPath);
    mDatabase = checkNotNull(pDatabase);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(mPath, mDatabase);
  }

  @Override
  public boolean equals(final Object pOther) {
    if (pOther == this) {
      return true;
    }
    if (!(pOther instanceof PathDBContainer)) {
      return false;
    }
    final PathDBContainer container = (PathDBContainer)pOther;
    return mPath.equals(container.mPath) && mDatabase.equals(container.mDatabase);
  }
}
