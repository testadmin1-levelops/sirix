package org.sirix.fs;

import com.google.common.base.Optional;

import java.nio.file.Path;
import java.util.Map;

import javax.annotation.Nonnull;

import org.sirix.api.INodeReadTrx;
import org.sirix.exception.SirixException;

/**
 * Interface for operations for usage with the command pattern.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 * @param <S>
 *          first argument for the operation, the sirix transaction which is also a generic parameter for
 *          the visitor
 */
@Nonnull
public interface IOperation<S extends INodeReadTrx> {

  /**
   * Execute an operation.
   * 
   * @param pTransaction
   *          the sirix transaction
   * @param pVisitor
   *          a visitor which can be used to plugin further metadata
   * @param pIndex
   *          simple index structure
   * @param pChild
   *          the {@link Path} instance for which a new node element should be inserted into sirix
   * @throws SirixException
   *           if any operation in sirix fails
   */
  void execute(S pTransaction, Optional<IVisitor<S>> pVisitor, Map<Path, EPath> pIndex, Path pChild)
    throws SirixException;
}
