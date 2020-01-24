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

package org.sirix.settings;

/**
 * Holding all byte representations for building up a XML.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public enum ECharsForSerializing {

  /** " ". */
  SPACE(new byte[] {
    32
  }),

  /** "&lt;". */
  OPEN(new byte[] {
    60
  }),

  /** "&gt;". */
  CLOSE(new byte[] {
    62
  }),

  /** "/". */
  SLASH(new byte[] {
    47
  }),

  /** "=". */
  EQUAL(new byte[] {
    61
  }),

  /** "\"". */
  QUOTE(new byte[] {
    34
  }),

  /** "=\"". */
  EQUAL_QUOTE(EQUAL.getBytes(), QUOTE.getBytes()),

  /** "&lt;/". */
  OPEN_SLASH(OPEN.getBytes(), SLASH.getBytes()),

  /** "/&gt;". */
  SLASH_CLOSE(SLASH.getBytes(), CLOSE.getBytes()),

  /** " rest:"". */
  REST_PREFIX(SPACE.getBytes(), new byte[] {
    114, 101, 115, 116, 58
  }),

  /** "ttid". */
  ID(new byte[] {
    116, 116, 105, 100
  }),

  /** " xmlns=\"". */
  XMLNS(SPACE.getBytes(), new byte[] {
    120, 109, 108, 110, 115
  }, EQUAL.getBytes(), QUOTE.getBytes()),

  /** " xmlns:". */
  XMLNS_COLON(SPACE.getBytes(), new byte[] {
    120, 109, 108, 110, 115, 58
  }),

  /** Newline. */
  NEWLINE(System.getProperty("line.separator").getBytes());

  /** Getting the bytes for the char. */
  private final byte[] mBytes;

  /**
   * Private constructor.
   * 
   * @param paramBytes
   *          the bytes for the chars
   */
  ECharsForSerializing(final byte[]... pBytes) {
    int index = 0;
    for (final byte[] runner : pBytes) {
      index = index + runner.length;
    }
    mBytes = new byte[index];
    index = 0;
    for (final byte[] runner : pBytes) {
      System.arraycopy(runner, 0, mBytes, index, runner.length);
      index = index + runner.length;
    }
  }

  /**
   * Getting the bytes.
   * 
   * @return the bytes for the char.
   */
  public byte[] getBytes() {
    return mBytes;
  }

}
