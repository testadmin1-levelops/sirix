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

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

import org.sirix.api.IItemList;

/**
 * <h1>ItemList</h1>
 * <p>
 * Data structure to store XPath items.
 * </p>
 * <p>
 * This structure is used for atomic values that are needed for the evaluation of a query. They can be results
 * of a query expression or be specified directly in the query e.g. as literals perform an arithmetic
 * operation or a comparison.
 * </p>
 * <p>
 * Since these items have to be distinguishable from nodes their key will be a negative long value (node key
 * is always a positive long value). This value is retrieved by negate their index in the internal data
 * structure.
 * </p>
 */
public final class ItemList implements IItemList<AtomicValue> {

  /**
   * Internal storage of items.
   */
  private final List<AtomicValue> mList;

  /**
   * Constructor. Initializes the list.
   */
  public ItemList() {
    mList = new ArrayList<>();
  }

  @Override
  public int addItem(final AtomicValue pItem) {
    final int key = mList.size();
    pItem.setNodeKey(key);
    // TODO: +2 is necessary, because key -1 is the NULL_NODE
    final int itemKey = (key + 2) * (-1);
    pItem.setNodeKey(itemKey);

    mList.add(pItem);
    return itemKey;
  }

  @Override
  public Optional<AtomicValue> getItem(final long mKey) {
    assert mKey <= Integer.MAX_VALUE;

    int index = (int)mKey; // cast to integer, because the list only
                           // accepts
    // int

    if (index < 0) {
      index = index * (-1);
    }

    // TODO: This is necessary, because key -1 is the NULL_NODE
    index = index - 2;

    return Optional.of(mList.get(index));
  }

  public int size() {
    return mList.size();
  }

  @Override
  public String toString() {
    return new StringBuilder("ItemList: ").append(mList.toString()).toString();
  }

}
