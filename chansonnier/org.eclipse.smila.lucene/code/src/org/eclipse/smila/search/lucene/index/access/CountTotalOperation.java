/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.apache.lucene.index.IndexReader;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class CountOperation.
 */
public class CountTotalOperation implements ISynchronizedOperation<IndexReader, Integer> {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation#process(java.lang.Object)
   */
  public Integer process(final IndexReader object) throws IndexException {
    Integer docs;
    try {
      docs = object.numDocs();
    } catch (final Exception e) {
      throw new IndexException("Can't determine number of documents in index", e);
    }
    return docs;
  }
}
