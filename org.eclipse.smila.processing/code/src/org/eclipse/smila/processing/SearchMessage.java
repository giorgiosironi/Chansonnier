/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing;

import org.eclipse.smila.datamodel.id.Id;

/**
 * extension of {@link ProcessorMessage} for search processing. Adds a single query record to the list of records in
 * process.
 * 
 * @author jschumacher
 * 
 */
public class SearchMessage extends ProcessorMessage {

  /**
   * the query record.
   */
  private Id _query;

  /**
   * create empty instance.
   */
  public SearchMessage() {
  }

  /**
   * create instance from data.
   * 
   * @param query
   *          the query record.
   */
  public SearchMessage(Id query) {
    super();
    _query = query;
  }

  /**
   * create instance from data.
   * 
   * @param query
   *          the query record.
   * @param records
   *          records to process
   */
  public SearchMessage(Id query, Id[] records) {
    super(records);
    _query = query;
  }

  /**
   * 
   * @return the query record.
   */
  public Id getQuery() {
    return _query;
  }

  /**
   * 
   * @param query
   *          the query record.
   */
  public void setQuery(Id query) {
    _query = query;
  }

  /**
   * 
   * @return true if it contains a query Id. false if the Id is null.
   */
  public boolean hasQuery() {
    return _query != null;
  }

}
