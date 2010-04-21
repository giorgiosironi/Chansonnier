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
package org.eclipse.smila.search.api;

import org.eclipse.smila.datamodel.record.Record;

/**
 * Result of a search process.
 * 
 * @author jschumacher
 * 
 */
public interface SearchResult {
  /**
   * 
   * @return name of the pipeline that produced this result.
   */
  String getWorkflowName();

  /**
   * The "effective query" record. This is the record which was actually used for the index search. This can be an
   * enriched version of the initial query record that went into the search process.
   * 
   * @return "effective query" record.
   */
  Record getQuery();

  /**
   * 
   * @return result record list.
   */
  Record[] getRecords();
}
