package org.sirix.fs;

import com.google.common.base.Optional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.namespace.QName;

import org.sirix.api.INodeWriteTrx;
import org.sirix.exception.SirixException;
import org.sirix.utils.LogWrapper;
import org.slf4j.LoggerFactory;

/**
 * Process file system attributes.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public class ProcessFileSystemAttributes implements IVisitor<INodeWriteTrx> {

  /** {@link LogWrapper} reference. */
  private static final LogWrapper LOGWRAPPER = new LogWrapper(LoggerFactory
    .getLogger(ProcessFileSystemAttributes.class));

  @Override
  public void processDirectory(final INodeWriteTrx pTransaction, final Path pDir,
    final Optional<BasicFileAttributes> pAttrs) {
  }

  @Override
  public void processFile(final INodeWriteTrx pTransaction, Path pFile,
    final Optional<BasicFileAttributes> pAttrs) {
    if (Files.exists(pFile)) {
      final String file = pFile.getFileName().toString();
      final int index = file.lastIndexOf('.');
      if (index > 0) {
        final String suffix = file.substring(index + 1);
        if (!suffix.isEmpty()) {
          try {
            pTransaction.insertAttribute(new QName("suffix"), file.substring(index + 1));
            pTransaction.moveToParent();
          } catch (SirixException e) {
            LOGWRAPPER.error(e.getMessage(), e);
          }
        }
      }
    }
  }
}
