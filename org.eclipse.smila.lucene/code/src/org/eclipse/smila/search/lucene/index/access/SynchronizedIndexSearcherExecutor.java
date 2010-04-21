/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.apache.lucene.search.IndexSearcher;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class SynchronizedIndexReaderExecutor.
 * 
 * @param <ReturnedType>
 *          class of returned type.
 */
public class SynchronizedIndexSearcherExecutor<ReturnedType> extends
  SynchronizedAbstractExecutor<IndexSearcher, ReturnedType> {

  /**
   * The _storage.
   */
  private final String _storage;

  /**
   * The _condition.
   */
  private final IndexSearcherCondition _condition;

  /**
   * Instantiates a new synchronized index reader executor.
   * 
   * @param monitored
   *          the monitored
   * @param storage
   *          the storage
   */
  public SynchronizedIndexSearcherExecutor(final Object monitored, final String storage) {
    super(monitored);
    _storage = storage;
    _condition = new IndexSearcherCondition();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.SynchronizedAbstractExecutor#close(java.lang.Object)
   */
  @Override
  protected void close(final IndexSearcher object) throws IndexException {
    try {
      object.close();
    } catch (final Throwable e) {
      throw new IndexException("Unable to close IndexSearcher", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.SynchronizedAbstractExecutor
   *      #execute(org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation)
   */
  @Override
  public ReturnedType execute(final ISynchronizedOperation<IndexSearcher, ReturnedType> operation)
    throws IndexException {
    return this.execute(_condition, operation);
  }

  /**
   * The Class IndexSearcherCondition.
   */
  private class IndexSearcherCondition implements ISynchronizedCondition<IndexSearcher> {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedCondition#initialize()
     */
    public IndexSearcher initialize() throws IndexException {
      try {
        return new IndexSearcher(_storage);
      } catch (final Exception e) {
        throw new IndexException(e);
      }
    }

  }
}
