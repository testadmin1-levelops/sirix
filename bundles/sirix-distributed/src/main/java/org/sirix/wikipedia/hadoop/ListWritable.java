/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */
package org.sirix.wikipedia.hadoop;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.events.XMLEvent;

import org.apache.hadoop.io.ArrayWritable;

/**
 * <h1>ListWritable</h1>
 * 
 * <p>
 * Wrapper to wrap a List of {@link XMLEventWritable}s in an {@link ArrayWritable}.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class ListWritable extends ArrayWritable {

  /** {@link List} of {@link XMLEventWritable}s. */
  private List<XMLEventWritable> mList;

  /**
   * Constructor.
   * 
   * @param paramList
   *          List of {@link XMLEvent}s.
   */
  public ListWritable(final List<XMLEventWritable> paramList) {
    super(XMLEventWritable.class, paramList.toArray(new XMLEventWritable[paramList.size()]));
    mList = paramList;
  }

  /**
   * Get the underlying list of {@link XMLEvent}s.
   * 
   * @return the List.
   */
  public List<XMLEventWritable> getList() {
    return mList;
  }

  /**
   * Set the underlying list of {@link XMLEvent}s.
   * 
   * @param paramList
   *          The List of {@link XMLEventWritable}s to set.
   */
  public void setmList(final List<XMLEventWritable> paramList) {
    mList = paramList;
  }

  @Override
  public void readFields(final DataInput paramIn) throws IOException {
    readFields(paramIn);
    mList = Arrays.asList((XMLEventWritable[])get());
  }

  /**
   * Read from {@link DataInput}.
   * 
   * @param paramList
   *          The underlying {@link List}.
   * @param paramIn
   *          The {@link DataInput}.
   * @return a new writable list.
   * @throws IOException
   *           In case of any I/O failure.
   */
  public static ListWritable read(final List<XMLEventWritable> paramList, final DataInput paramIn)
    throws IOException {
    final ListWritable list = new ListWritable(paramList);
    list.readFields(paramIn);
    return list;
  }
}
