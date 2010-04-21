/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.search.api.helper;

import org.eclipse.smila.search.api.SearchResult;

/**
 * Wrapper for a complete search result object. Basically provides method to create access helpers for the single query
 * and result records contained in the search result. Can also create a new query builder based on the effective query
 * object in the search result.
 * 
 * @author jschumacher
 * 
 */
public class ResultAccessor {

  /**
   * the search result.
   */
  private SearchResult _result;

  /**
   * create instance.
   * 
   * @param result
   *          the search result.
   */
  public ResultAccessor(SearchResult result) {
    _result = result;
  }

  /**
   * access original result.
   * 
   * @return the search result.
   */
  public SearchResult getResult() {
    return _result;
  }

  /**
   * @return name of pipeline that produced the search result.
   */
  public String getWorkflowName() {
    return _result.getWorkflowName();
  }

  /**
   * 
   * @return true if the search result contains an effective query object, null else.
   */
  public boolean hasQuery() {
    return _result.getQuery() != null;
  }

  /**
   * create a wrapper for the effective query record.
   * 
   * @return wrapper for query, or null, if no query is present.
   */
  public QueryRecordAccessor getQuery() {
    if (hasQuery()) {
      return new QueryRecordAccessor(_result.getQuery());
    }
    return null;
  }

  /**
   * 
   * @return true if the result contains a (possibly empty) records list. false, if no records list is present.
   */
  public boolean hasRecords() {
    return _result.getRecords() != null;
  }

  /**
   * @return number of result records.
   */
  public int recordsSize() {
    if (hasRecords()) {
      return _result.getRecords().length;
    }
    return 0;
  }

  /**
   * create a wrapper for the n'th result record.
   * 
   * @param index
   *          position in result list.
   * @return wrapper for the result record, or null if index in invalid.
   */
  public ResultRecordAccessor getResultRecord(int index) {
    if (index >= 0 && index < recordsSize()) {
      return new ResultRecordAccessor(_result.getRecords()[index]);
    }
    return null;
  }

  /**
   * create new QueryBuilder for same pipeline from effective query object of this result, use complete query object.
   * 
   * @return new query builder.
   */
  public QueryBuilder newQueryBuilder() {
    final QueryBuilder builder = new QueryBuilder(getWorkflowName(), _result.getQuery());
    return builder;
  }

  /**
   * create new QueryBuilder for same pipeline from effective query object of this result, keep only parts of query
   * object as described by record filter. parameters are always copied.
   * 
   * @param recordFilterName
   *          name of filter to apply to effective query record.
   * @return new query builder.
   */
  public QueryBuilder newQueryBuilder(String recordFilterName) {
    throw new UnsupportedOperationException("not yet implemented");
  }
}
