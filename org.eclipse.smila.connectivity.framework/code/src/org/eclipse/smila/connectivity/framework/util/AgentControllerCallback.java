/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util;

import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;

/**
 * Interface for callbacks on the AgentController. This interface is used by Agents to send add and delete requests and
 * to unregister an agent if a critical error occurred.
 */
public interface AgentControllerCallback extends ControllerCallback {

  /**
   * Add the given record.
   * 
   * @param sessionId
   *          the delta indexing session Id
   * @param deltaIndexingType
   *          the DeltaIndexingType
   * @param record
   *          the record to add
   * @param hash
   *          the hash value used for delta indexing
   */
  void add(final String sessionId, final DeltaIndexingType deltaIndexingType, final Record record, final String hash);

  /**
   * Delete the given id.
   * 
   * @param sessionId
   *          the delta indexing session Id
   * @param deltaIndexingType
   *          the DeltaIndexingType
   * @param id
   *          the id of the record to delete
   */
  void delete(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id id);

  /**
   * Removes the Agent using the given DataSourceId from the list of active Agents.
   * 
   * @param sessionId
   *          the delta indexing session Id
   * @param deltaIndexingType
   *          the DeltaIndexingType
   * @param dataSourceId
   *          the ID of the data source used by the crawl
   */
  void unregister(final String sessionId, final DeltaIndexingType deltaIndexingType, final String dataSourceId);
}
