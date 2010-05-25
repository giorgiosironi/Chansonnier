/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing;

import java.util.Map;

/**
 * The Interface DeltaIndexingManagerAgent.
 */
public interface DeltaIndexingManagerAgent {

  /**
   * Clear all entries of the DeltaIndexingManager for the given dataSourceID. You cannot execute this command on
   * an already locked data source. If you want to clear a locked data source you have to unlock it first.
   * 
   * @param dataSourceID
   *          the data source id
   * @return a message saying either "OK" or describing an error cause.
   */
  String clear(final String dataSourceID);

  /**
   * Clears all entries of the DeltaIndexingManager including entries of any active sessions! Note that this may cause
   * errors in clients currently using any of the closed sessions.
   * 
   * @return a message saying either "OK" or describing an error cause.
   */
  String clearAll();

  /**
   * Unlock the given data source and removes the sessions. Note that this may cause exceptions in a client currently
   * using the closed sessions.
   * 
   * @param dataSourceID
   *          the data source id
   * @return a message saying either "OK" or describing an error cause.
   */
  String unlock(final String dataSourceID);

  /**
   * Unlock all data sources and removes all sessions. Note that this may cause exceptions in clients currently using
   * any of the closed sessions.
   * 
   * @return a message saying either "OK" or describing an error cause.
   */
  String unlockAll();

  /**
   * Get an overview what data sources are locked or unlocked.
   * 
   * @return a map containing the dataSoureId and the LockState
   */
  Map<String, String> getLockStates();

  /**
   * Get the number of delta indexing entries for the given dataSourceID.
   * 
   * @param dataSourceID
   *          the data source id
   * @return either a Long giving the number of entries or a String with an error message.
   */
  Object getEntryCount(final String dataSourceID);

  /**
   * Get the number of delta indexing entries for all data sources.
   * 
   * @return a map containing the dataSoureId and the entry count
   */
  Map<String, Long> getEntryCounts();
}
