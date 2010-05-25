/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: tmenzel (brox IT Solution GmbH )- initial creator
 *******************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing2;

import java.util.Iterator;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.datamodel.id.Id;

/**
 * The Interface IDeltaIndexingSession.
 * 
 * @author tmenzel
 */
public interface IDeltaIndexingSession {

  /**
   * Clear all entries of the given sessionId.
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void clear() throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Finish this delta indexing session and remove the lock.
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void commit() throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Delete.
   * 
   * @param id
   *          the id
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void delete(final Id id) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Delete untouched ids. rather than calling {@link #delete(Id)} by the controller when iterating thru the ids, the
   * implementation may do so internally for all untouched ids in one go more efficiently.
   * 
   * @param id
   *          the id
   * 
   * @return the number of deleted ids
   * 
   * @throws DeltaIndexingSessionException
   *           the delta indexing session exception
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  long deleteUntouchedIds() throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Obsolete id iterator.
   * 
   * 
   * @return the iterator< id>
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  Iterator<Id> getUntouchedIds() throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Obsolete id iterator for id fragments.
   * 
   * @param id
   *          the id
   * 
   * @return the iterator< id>
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  Iterator<Id> getUntouchedIds(final Id id) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * checks if the hash of the current id is new or has changed (true) or not (false). //
   * 
   * to reduce method calls mark entry as visited on return value false
   * 
   * @param id
   *          the id
   * @param hash
   *          the hash
   * 
   * @return true, if checks for changed
   * 
   * @throws DeltaIndexingSessionException
   *           the delta indexing session exception
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  boolean hasChanged(final Id id, final String hash) throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * rolls back changes that were made in the curreent session between init() and finish(), it should be called before
   * finishing process.
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void rollback() throws DeltaIndexingSessionException, DeltaIndexingException;

  /**
   * Creates or updates the delta indexing entry. this is THE method to make the record known to DI. It sets the hash,
   * the isCompound flag and marks this id as visited.
   * 
   * @param id
   *          the id
   * @param hash
   *          the hash
   * @param isCompound
   *          boolean flag if the record identified by id is a compound record (true) or not (false)
   * 
   * @throws DeltaIndexingSessionException
   *           if the sessionId is invalid
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  void touch(final Id id, final String hash, final boolean isCompound) throws DeltaIndexingSessionException,
    DeltaIndexingException;

  /**
   * this is a combination of {@link #hasChanged(Id, String)} and {@link #touch(Id, String, boolean)} in one step.
   * <p>
   * It has a perf. gain over calling the methods seperatly but has the drawback, that the record is always touched
   * independently of an exception that occurs before putting the record into the Q. on the other hand, this matters not
   * much as the subsequent processing may also cause errors which arent reflected in the "touch" state.
   * 
   * @param id
   *          the id
   * @param hash
   *          the hash
   * @param isCompound
   *          the is compound
   * 
   * @return true, if successful
   * 
   * @throws DeltaIndexingSessionException
   *           the delta indexing session exception
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  boolean checkAndTouch(final Id id, final String hash, final boolean isCompound)
    throws DeltaIndexingSessionException, DeltaIndexingException;

}
