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

package org.sirix.service.xml.xpath.expr;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sirix.Holder;
import org.sirix.TestHelper;
import org.sirix.axis.AbsAxis;
import org.sirix.exception.SirixException;
import org.sirix.service.xml.xpath.XPathAxis;

/**
 * JUnit-test class to test the functionality of the UnionAxis.
 * 
 * @author Tina Scherer
 */
public class EveryExprTest {

  private Holder holder;

  @Before
  public void setUp() throws SirixException {
    TestHelper.deleteEverything();
    TestHelper.createTestDocument();
    holder = Holder.generateRtx();
  }

  @After
  public void tearDown() throws SirixException {
    holder.close();
    TestHelper.closeEverything();
  }

  @Test
  public void testEveryExpr() throws SirixException {

    final AbsAxis axis1 =
      new XPathAxis(holder.getRtx(), "every $child in child::node()" + "satisfies $child/@i");
    assertEquals(true, axis1.hasNext());
    assertEquals(true, Boolean.parseBoolean(holder.getRtx().getValueOfCurrentNode()));
    assertEquals(false, axis1.hasNext());

    final AbsAxis axis2 =
      new XPathAxis(holder.getRtx(), "every $child in child::node()" + "satisfies $child/@abc");
    assertEquals(true, axis2.hasNext());
    assertEquals(false, Boolean.parseBoolean(holder.getRtx().getValueOfCurrentNode()));
    assertEquals(false, axis2.hasNext());

    holder.getRtx().moveTo(1L);
    final AbsAxis axis3 =
      new XPathAxis(holder.getRtx(), "every $child in child::element()"
        + " satisfies $child/attribute::attribute()");
    assertEquals(true, axis3.hasNext());
    assertEquals(false, Boolean.parseBoolean(holder.getRtx().getValueOfCurrentNode()));
    assertEquals(false, axis3.hasNext());

    holder.getRtx().moveTo(1L);
    final AbsAxis axis4 =
      new XPathAxis(holder.getRtx(), "every $child in child::element() satisfies $child/child::c");
    assertEquals(true, axis4.hasNext());
    assertEquals(true, Boolean.parseBoolean(holder.getRtx().getValueOfCurrentNode()));
    assertEquals(false, axis4.hasNext());
  }

}
