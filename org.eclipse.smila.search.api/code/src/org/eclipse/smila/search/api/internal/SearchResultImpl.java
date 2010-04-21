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
package org.eclipse.smila.search.api.internal;

import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.search.api.SearchResult;

/**
 * standard implementation of search result interface.
 * 
 * @author jschumacher
 * 
 */
public class SearchResultImpl implements SearchResult {

  /**
   * pipeline name that produced the result.
   */
  private String _workflowName;

  /**
   * effective query record (optional).
   */
  private Record _query;

  /**
   * result record list (optional).
   */
  private Record[] _records;

  /**
   * create instance.
   * 
   * @param workflowName
   *          pipeline name
   * @param query
   *          effective query (can be null)
   */
  public SearchResultImpl(String workflowName, Record query) {
    setWorkflowName(workflowName);
    setQuery(query);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.api.SearchResult#getQuery()
   */
  public Record getQuery() {
    return _query;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.api.SearchResult#getRecords()
   */
  public Record[] getRecords() {
    return _records;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.api.SearchResult#getWorkflowName()
   */
  public String getWorkflowName() {
    return _workflowName;
  }

  /**
   * set pipeline name.
   * 
   * @param workflowName
   *          pipeline name.
   */
  protected void setWorkflowName(String workflowName) {
    _workflowName = workflowName;
  }

  /**
   * set effective query record.
   * 
   * @param query
   *          effective query record
   */
  protected void setQuery(Record query) {
    _query = query;
  }

  /**
   * set result records list.
   * 
   * @param records
   *          result records list.
   */
  protected void setRecords(Record[] records) {
    _records = records;
  }

}
