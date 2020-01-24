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

package org.sirix.service.xml.xpath;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.sirix.TestHelper;
import org.sirix.api.INodeReadTrx;
import org.sirix.axis.AbsAxis;
import org.sirix.exception.SirixException;

public class XPathStringChecker {

  @Before
  public void setUp() throws SirixException {
    TestHelper.deleteEverything();
  }

  @After
  public void tearDown() throws SirixException {
    TestHelper.closeEverything();
  }

  public static void testIAxisConventions(final AbsAxis axis, final String[] expectedValues) {

    final INodeReadTrx rtx = ((INodeReadTrx)axis.getTransaction());

    // IAxis Convention 1.
    final long startKey = rtx.getNode().getNodeKey();

    final String[] strValues = new String[expectedValues.length];
    int offset = 0;
    while (axis.hasNext()) {
      axis.next();
      // IAxis results.
      if (offset >= expectedValues.length) {
        fail("More nodes found than expected.");
      }
      if (!("".equals(rtx.getValueOfCurrentNode()))) {
        strValues[offset++] = rtx.getValueOfCurrentNode();
      } else {
        strValues[offset++] = rtx.getQNameOfCurrentNode().toString();
      }

      // // IAxis Convention 2.
      // try {
      // axis.next();
      // fail("Should only allow to call next() once.");
      // } catch (Exception e) {
      // // Must throw exception.
      // }

      // IAxis Convention 3.
      rtx.moveToDocumentRoot();

    }

    // IAxis Convention 5.
    assertEquals(startKey, rtx.getNode().getNodeKey());

    // IAxis results.
    assertArrayEquals(expectedValues, strValues);

  }
}
