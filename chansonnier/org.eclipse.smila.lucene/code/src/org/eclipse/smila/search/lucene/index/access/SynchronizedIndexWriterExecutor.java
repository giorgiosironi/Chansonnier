/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class SynchronizedIndexWriterExecutor.
 * 
 * @param <ReturnedType>
 *          class of returned type.
 */
public class SynchronizedIndexWriterExecutor<ReturnedType> extends
  SynchronizedAbstractExecutor<IndexWriter, ReturnedType> {

  /**
   * The name of the index.
   */
  private final String _indexName;

  /**
   * The _storage.
   */
  private final String _storage;

  /**
   * The _analyzer.
   */
  private final Analyzer _analyzer;

  /**
   * The _condition.
   */
  private final IndexWriterCondition _condition;

  /**
   * Instantiates a new synchronized index writer executor.
   * 
   * @param monitored
   *          the monitored DIndex
   * @param storage
   *          the storage
   * @param analyzer
   *          the analyzer
   */
  public SynchronizedIndexWriterExecutor(final DIndex monitored, final String storage, final Analyzer analyzer) {
    super(monitored);
    _indexName = monitored.getName();
    _storage = storage;
    _analyzer = analyzer;
    _condition = new IndexWriterCondition();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.SynchronizedAbstractExecutor#close(java.lang.Object)
   */
  @Override
  protected void close(final IndexWriter object) throws IndexException {
    try {
      object.close();
    } catch (final Throwable e) {
      throw new IndexException("Unable to close IndexWriter", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.SynchronizedAbstractExecutor
   *      #execute(org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation)
   */
  @Override
  public ReturnedType execute(final ISynchronizedOperation<IndexWriter, ReturnedType> operation)
    throws IndexException {
    return this.execute(_condition, operation);
  }

  /**
   * The Class IndexWriterCondition.
   */
  private class IndexWriterCondition implements ISynchronizedCondition<IndexWriter> {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedCondition#initialize()
     */
    public IndexWriter initialize() throws IndexException {
      return IndexWriterPool.getIndexWriter(_indexName, _storage, _analyzer);
    }

  }

}
