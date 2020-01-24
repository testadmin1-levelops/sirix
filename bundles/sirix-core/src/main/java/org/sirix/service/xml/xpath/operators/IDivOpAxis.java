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

package org.sirix.service.xml.xpath.operators;

import org.sirix.api.IAxis;
import org.sirix.api.INodeReadTrx;
import org.sirix.exception.SirixXPathException;
import org.sirix.node.interfaces.INode;
import org.sirix.service.xml.xpath.AtomicValue;
import org.sirix.service.xml.xpath.XPathError;
import org.sirix.service.xml.xpath.XPathError.ErrorType;
import org.sirix.service.xml.xpath.types.Type;
import org.sirix.utils.TypedValue;

/**
 * <h1>AddOpAxis</h1>
 * <p>
 * Performs an arithmetic integer division on two input operators.
 * </p>
 */
public class IDivOpAxis extends AbsObAxis {

  /**
   * Constructor. Initializes the internal state.
   * 
   * @param rtx
   *          Exclusive (immutable) trx to iterate with.
   * @param mOp1
   *          First value of the operation
   * @param mOp2
   *          Second value of the operation
   */
  public IDivOpAxis(final INodeReadTrx rtx, final IAxis mOp1, final IAxis mOp2) {

    super(rtx, mOp1, mOp2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public INode operate(final AtomicValue mOperand1, final AtomicValue mOperand2) throws SirixXPathException {

    final Type returnType = getReturnType(mOperand1.getTypeKey(), mOperand2.getTypeKey());
    final int typeKey = getTransaction().keyForName(returnType.getStringRepr());

    final byte[] value;

    try {
      final int op1 = (int)Double.parseDouble(new String(mOperand1.getRawValue()));
      final int op2 = (int)Double.parseDouble(new String(mOperand2.getRawValue()));
      final int iValue = op1 / op2;
      value = TypedValue.getBytes(iValue);
      return new AtomicValue(value, typeKey);
    } catch (final ArithmeticException e) {
      // LOGWRAPPER.error(e);
      throw new XPathError(ErrorType.FOAR0001);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Type getReturnType(final int mOp1, final int mOp2) throws SirixXPathException {

    Type type1;
    Type type2;
    try {
      type1 = Type.getType(mOp1).getPrimitiveBaseType();
      type2 = Type.getType(mOp2).getPrimitiveBaseType();
    } catch (final IllegalStateException e) {
      throw new XPathError(ErrorType.XPTY0004);
    }

    if (type1.isNumericType() && type2.isNumericType()) {

      return Type.INTEGER;
    } else {

      throw new XPathError(ErrorType.XPTY0004);

    }
  }

}
