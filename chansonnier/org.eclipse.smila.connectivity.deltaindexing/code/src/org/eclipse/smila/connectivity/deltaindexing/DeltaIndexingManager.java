/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;

/**
 * The Interface DeltaIndexingManager. To start a processing session, call:
 * <ol>
 * <li>{@link #init(String)}<br>
 * to start a new session</li>
 * <li>for all items in this run call
 * <ol>
 * <li>{@link #checkForUpdate(String, Id, String)} <br>
 * to test if the given item has changed since the last run</li>
 * <li>{@link #visit(String, Id, String, boolean)}<br>
 * to mark this item as visted</li>
 * </ol>
 * </li>
 * <li>{@link #obsoleteIdIterator(String, Id)}<br>
 * to get all ids that haven been visited this run and {@link #delete(String, Id)} them</li>
 * 
 * <li>{@link #finish(String)}<br>
 * to commit all changes from this run and releaase the lock</li>
 * </ol>
 * 
 * @see http://wiki.eclipse.org/SMILA/Documentation/DeltaIndexingManager#Configuration
 * @see http://wiki.eclipse.org/SMILA/Specifications/DeltaIndexingAndConnectivtyDiscussion09
 */
public interface DeltaIndexingManager {

  /**
   * Initializes the internal state for an import of a dataSourceID and creates a session wherein it establishes a lock
   * to avoid that the same. dataSourceID is initialized multiple times concurrently. It returns a unique Id for the
   * session that a client has to use to gain access to the locked data source.
   * 
   * @param dataSourceID
   *          dataSourceID
   * @return a String containing the sessionId
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  String init(final String dataSourceID) throws DeltaIndexingException;

  /**
   * checks if the hash of the current id is new or has changed (true) or not (false). //
   * 
   * to reduce method calls mark entry as visited on return value false
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @param id
   *          the id
   * @param hash
   *          the hash
   * 
   * @return true, if check for update
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  boolean checkForUpdate(final String sessionId, final Id id, final String hash)
    throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Creates or updates the delta indexing entry. this is THE method to make the record known to DI. It sets the hash,
   * the isCompound flag and marks this id as visited.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @param id
   *          the id
   * @param hash
   *          the hash
   * @param isCompound
   *          boolean flag if the record identified by id is a compound record (true) or not (false)
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void visit(final String sessionId, final Id id, final String hash, final boolean isCompound)
    throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Obsolete id iterator.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @param dataSourceID
   *          the data source id
   * 
   * @return the iterator< id>
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  Iterator<Id> obsoleteIdIterator(final String sessionId, final String dataSourceID)
    throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Obsolete id iterator for id fragments of compounds.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @param id
   *          the id
   * 
   * @return the iterator< id>
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  Iterator<Id> obsoleteIdIterator(final String sessionId, final Id id) throws DeltaIndexingSessionException,
    DeltaIndexingException;

  /**
   * Clear all entries for the given sessionId. In order to call clear you first have to initialize a session calling
   * init(). This is to avoid clearing of any locked data sources.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void clear(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * rolls back changes that were made in the current session between init() and finish(), it should be called before
   * finishing process.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void rollback(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Delete.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @param id
   *          the id
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void delete(final String sessionId, final Id id) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Finish this delta indexing session and remove the lock.
   * 
   * @param sessionId
   *          the id of the delta indexing session
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void finish(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException;

  /* methods that don't need a session */

  /**
   * Clears all entries of the DeltaIndexingManager including any active sessions! Note that this may cause exceptions
   * in clients currently using any of the closed sessions.
   * 
   * @admin this an administrative management function to be called manually and not part of the normal workflow.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void clear() throws DeltaIndexingException;

  /**
   * Unlock the given data source and removes the sessions. Note that this may cause exceptions in a client currently
   * using the closed sessions.
   * 
   * @admin this an administrative management function to be called manually and not part of the normal workflow.
   * 
   * @param dataSourceID
   *          the data source id
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void unlockDatasource(final String dataSourceID) throws DeltaIndexingException;

  /**
   * Unlock all data sources and removes all sessions. Note that this may cause exceptions in clients currently using
   * any of the closed sessions.
   * 
   * @admin this an administrative management function to be called manually and not part of the normal workflow.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void unlockDatasources() throws DeltaIndexingException;

  /**
   * Get an overview what data sources are locked or unlocked.
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
  boolean exists(final String dataSourceId);

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
