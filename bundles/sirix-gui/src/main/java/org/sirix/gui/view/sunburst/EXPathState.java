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

package org.sirix.gui.view.sunburst;

import org.sirix.gui.view.EHover;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * XPath enum to determine if current item is found by an XPath expression or not.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public enum EXPathState {

  /** Item is found. */
  ISFOUND {
    /** {@inheritDoc} */
    @Override
    public void setStroke(final PGraphics paramGraphic, final PGraphics paramRecorder, final int paramColor,
      final EHover paramHover) {
      if (paramRecorder != null) {
        strokeColor(paramRecorder);
      }
      strokeColor(paramGraphic);
    }

    /** Define a strokeColor. */
    private void strokeColor(final PGraphics paramGraphic) {
      paramGraphic.colorMode(PConstants.RGB);
      paramGraphic.stroke(255, 240, 0);
      paramGraphic.colorMode(PConstants.HSB);
    }
  },

  /** Default: Item is not found. */
  ISNOTFOUND {
    /**
     * {@inheritDoc}
     */
    @Override
    public void setStroke(final PGraphics paramGraphic, final PGraphics paramRecorder, final int paramColor,
      final EHover paramHover) {
    }
  };

  /**
   * Set stroke.
   * 
   * @param paramGraphic
   *          {@link PGraphics} instance
   * @param paramRecorder
   *          {@link PGraphics} instance for recording PDFs
   * @param paramColor
   *          the color to use
   * @param paramHover
   *          determines if current item should be hovered or not
   */
  public abstract void setStroke(final PGraphics paramGraphic, final PGraphics paramRecorder,
    final int paramColor, final EHover paramHover);
}
