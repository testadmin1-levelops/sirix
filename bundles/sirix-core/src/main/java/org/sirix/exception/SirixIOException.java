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

package org.sirix.exception;

import java.io.IOException;

/**
 * All sirix IO Exception are wrapped in this class. It inherits from
 * IOException since it is a sirix IO Exception.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class SirixIOException extends SirixException {

  /**
   * Serializable id.
   */
  private static final long serialVersionUID = 4099242625448155216L;

  /**
   * Constructor.
   * 
   * @param pMessage
   *          to be used
   */
  public SirixIOException(final String pMessage) {
    super(pMessage);
  }

  /**
   * Constructor.
   * 
   * @param pThrowable
   *          {@link Throwable} exception
   * @param pMessage
   *          for the overlaying {@link IOException}
   */
  public SirixIOException(final String pMessage, final Throwable pThrowable) {
    super(pMessage, pThrowable);
  }

  /**
   * Constructor.
   * 
   * @param pThrowable
   *          {@link Throwable} exception
   */
  public SirixIOException(final Throwable pThrowable) {
    super(pThrowable);
  }

}
