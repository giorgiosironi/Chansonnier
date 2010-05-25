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
import org.eclipse.smila.search.lucene.index.IndexConnection;
import org.eclipse.smila.search.lucene.messages.advsearch.DQueryExpression;
import org.eclipse.smila.search.utils.searchresult.LuceneSearchResult;

/**
 * The Class DoQueryOperation. (I was too lazy to split correctly all functionality to operations and sometimes it call
 * callback to original code)
 */
public class DoQueryOperation implements ISynchronizedOperation<IndexSearcher, LuceneSearchResult> {

  /**
   * The _connection.
   */
  private final IndexConnection _connection;

  /**
   * The _query expression.
   */
  private final DQueryExpression _queryExpression;

  /**
   * The _start pos.
   */
  private final int _startPos;

  /**
   * Instantiates a new do query operation.
   * 
   * @param connection
   *          the connection
   * @param queryExpression
   *          the query expression
   * @param startPos
   *          the start pos
   */
  public DoQueryOperation(final IndexConnection connection, final DQueryExpression queryExpression,
    final int startPos) {
    _connection = connection;
    _queryExpression = queryExpression;
    _startPos = startPos;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation#process(java.lang.Object)
   */
  public LuceneSearchResult process(final IndexSearcher object) throws IndexException {
    try {
      return _connection.doQueryCallback(object, _queryExpression, _startPos);
    } catch (final IndexException e) {
      throw e;
    } catch (final Throwable e) {
      throw new IndexException("Unable to execute query", e);
    }
  }
}
