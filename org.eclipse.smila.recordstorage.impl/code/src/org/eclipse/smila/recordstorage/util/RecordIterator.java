/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage.util;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.smila.datamodel.record.Record;

/**
 * An Iterator implementation that iterates over an internal Iterator of RecordDao objects and converts them to records
 * when accessed by the next() method.
 */
public class RecordIterator implements Iterator<Record> {

  /**
   * Internal iterator over RecordDao objects.
   */
  private Iterator<RecordDao> _daoIterator;

  /**
   * Conversion Constructor.
   * 
   * @param daoIterator
   *          a iterator over recordDao objects
   */
  public RecordIterator(Iterator<RecordDao> daoIterator) {
    if (daoIterator == null) {
      throw new IllegalArgumentException("parameter daoIterator is null");
    }
    _daoIterator = daoIterator;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    return _daoIterator.hasNext();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#next()
   */
  public Record next() {
    final RecordDao dao = _daoIterator.next();
    if (dao != null) {
      try {
        return dao.toRecord();
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    _daoIterator.remove();
  }

}
