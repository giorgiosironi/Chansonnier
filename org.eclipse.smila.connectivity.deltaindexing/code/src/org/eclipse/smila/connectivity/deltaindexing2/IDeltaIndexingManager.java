/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing2;

import java.util.Map;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;

/**
 * The Interface DeltaIndexingManager.
 */
public interface IDeltaIndexingManager {

  /**
   * Initializes the internal state for an import of a dataSourceID and creates a session wherein it establishes a lock
   * to avoid that the same dataSourceID is initialized multiple times concurrently. It returns an object for the session
   * that a client has to use to gain access to the locked data source.
   * 
   * @param dataSourceID
   *          dataSourceID
   * 
   * @return the i delta indexing session
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  IDeltaIndexingSession createSession(final String dataSourceID) throws DeltaIndexingException;


  /**
   * Clears all entries of the DeltaIndexingManager including sessions.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void clear() throws DeltaIndexingException;

  /**
   * Unlock the given data source and removes the sessions.
   * 
   * @param dataSourceID
   *          the data source id
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void unlockDatasource(final String dataSourceID) throws DeltaIndexingException;

  /**
   * Unlock all data sources and removes all sessions.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void unlockDatasources() throws DeltaIndexingException;

  /**
   * Gets an overview what data sources are locked or unlocked.
   * 
   * @return a map containing the dataSoureId and the LockState
   */
  Map<String, LockState> getLockStates();

  /**
   * Checks if the entries for the given dataSourceId exist.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return true, if successful
   */
  boolean dataSourceExists(final String dataSourceId);

  /**
   * Get the number of delta indexing entries for the given dataSourceID.
   * 
   * @param dataSourceID
   *          the data source id
   * @return the number of entries
   */
  long getEntryCount(final String dataSourceID);

  /**
   * Get the number of delta indexing entries for all data sources.
   * 
   * @return a map of dataSoureIds and the entry counts
   */
  Map<String, Long> getEntryCounts();

  /**
   * An enumeration defining the lock states a data source in the DeltaIndexingManager.
   */
  public enum LockState {
    /**
     * The lock states.
     */
    LOCKED, UNLOCKED;
  }
}
