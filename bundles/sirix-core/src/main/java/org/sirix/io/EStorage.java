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
package org.sirix.io;

import javax.annotation.Nonnull;

import org.sirix.access.conf.ResourceConfiguration;
import org.sirix.exception.SirixIOException;
import org.sirix.io.berkeley.BerkeleyStorage;
import org.sirix.io.bytepipe.ByteHandlePipeline;
import org.sirix.io.file.FileStorage;

/**
 * Utility methods for the storage. Those methods included common deletion
 * procedures as well as common checks. Furthermore, specific serialization are
 * summarized upon this enum.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public enum EStorage {
  File {
    @Override
    public IStorage getInstance(
      final @Nonnull ResourceConfiguration pResourceConf) throws SirixIOException {
      return new FileStorage(pResourceConf.mPath, new ByteHandlePipeline(
        pResourceConf.mByteHandler));
    }
  },

  BerkeleyDB {
    @Override
    public IStorage getInstance(
      final @Nonnull ResourceConfiguration pResourceConf) throws SirixIOException {
      return new BerkeleyStorage(pResourceConf.mPath, new ByteHandlePipeline(
        pResourceConf.mByteHandler));
    }
  };

  /**
   * Get an instance of the storage backend.
   * 
   * @param pResourceConf
   *          {@link ResourceConfiguration} reference
   * @return instance of a storage backend specified within the {@link ResourceConfiguration}
   * @throws SirixIOException
   *           if an IO-error occured
   */
  public abstract IStorage getInstance(
    final @Nonnull ResourceConfiguration pResourceConf) throws SirixIOException;

  /**
   * Factory method to retrieve suitable {@link IStorage} instances based upon
   * the suitable {@link ResourceConfiguration}.
   * 
   * @param pResourceConf
   *          determining the storage
   * @return an implementation of the {@link IStorage} interface
   * @throws SirixIOException
   *           if an IO-error occurs
   * @throws NullPointerException
   *           if {@code pResourceConf} is {@code null}
   */
  public static final IStorage getStorage(
    final @Nonnull ResourceConfiguration pResourceConf) throws SirixIOException {
    return pResourceConf.mStorage.getInstance(pResourceConf);
  }
}
