/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.datamodel.id.Id;

/**
 * The Class DataSourceConnection.
 */
public class DataSourceConnection implements Serializable {

  /**
   * serial version id.
   */
  private static final long serialVersionUID = -2263628823851855201L;

  /**
   * The Constant EXCEPTION_DATA_SOURCE_LOCKED_BY_OTHER_SESSION.
   */
  private static final String EXCEPTION_DATA_SOURCE_LOCKED_BY_OTHER_SESSION =
    "Data source %s already locked by another session";

  /**
   * The Constant EXCEPTION_DATA_SOURCE_SHOULD_BE_LOCKED.
   */
  private static final String EXCEPTION_DATA_SOURCE_SHOULD_BE_LOCKED =
    "Data source %s should be locked before using!";

  /**
   * The _data source id.
   */
  private final String _dataSourceId;

  /**
   * The session id.
   */
  private String _sessionId;

  /**
   * The _lock.
   */
  // transient
  private ReentrantLock _lock = new ReentrantLock();

  /**
   * The _lock monitor. Just an object not used by anybody else.
   */
  private final Object _lockMonitor = new Object[0];

  /**
   * The _index.
   */
  private final Map<Id, String> _index = new HashMap<Id, String>();

  /**
   * The modified compounds.
   */
  private final transient Set<Id> _modified = new HashSet<Id>();

  /**
   * The _new index.
   */
  private transient Map<Id, String> _updated;

  /**
   * The _deleted.
   */
  private transient Set<Id> _deleted;

  /**
   * A mapping of Ids to their subCompound Ids (if any).
   */
  private final Map<Id, Set<Id>> _subCompounds = new HashMap<Id, Set<Id>>();

  /**
   * Instantiates a new data source connection.
   * 
   * @param dataSourceId
   *          the data source id
   */
  public DataSourceConnection(final String dataSourceId) {
    _dataSourceId = dataSourceId;
  }

  /**
   * Lock.
   * 
   * @param sessionId
   *          the id of the session
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void lock(final String sessionId) throws DeltaIndexingException {
    // added synchronization for entering lock to avoid sleeping
    synchronized (_lockMonitor) {
      if (_lock.isLocked()) {
        throw new DeltaIndexingException(String
          .format(EXCEPTION_DATA_SOURCE_LOCKED_BY_OTHER_SESSION, _dataSourceId));
      }
      _modified.clear();
      _lock.lock();
      _sessionId = sessionId;
    }
    rollback();
  }

  /**
   * Unlock.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void unlock() throws DeltaIndexingException {
    // commit changes
    checkLock();
    commit();
    rollback();
    synchronized (_lockMonitor) {
      _lock.unlock();
      _sessionId = null;
    }
  }

  /**
   * Put.
   * 
   * @param id
   *          the id
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void put(final Id id) throws DeltaIndexingException {
    checkLock();
    // get the hash from the _index.
    _updated.put(id, _index.get(id));
  }

  /**
   * Put.
   * 
   * @param id
   *          the id
   * @param hash
   *          the hash
   * @param isCompound
   *          boolean flag if the record identified by id is a compound record (true) or not (false)
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void put(final Id id, final String hash, final boolean isCompound) throws DeltaIndexingException {
    checkLock();
    _updated.put(id, hash);
    if (isCompound) {
      _modified.add(id);
    }
  }

  /**
   * Gets the hash.
   * 
   * @param id
   *          the id
   * 
   * @return the hash
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public String getHash(final Id id) throws DeltaIndexingException {
    checkLock();
    return _index.get(id);
  }

  /**
   * Obsolete id iterator.
   * 
   * @return the iterator< id>
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public Iterator<Id> obsoleteIdIterator() throws DeltaIndexingException {
    checkLock();
    return new IdIterator(_index.keySet(), _updated.keySet(), _modified);
  }

  /**
   * Delete.
   * 
   * @param id
   *          the id
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void delete(final Id id) throws DeltaIndexingException {
    checkLock();
    _deleted.add(id);
  }

  /**
   * Clear.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void rollback() throws DeltaIndexingException {
    checkLock();
    _updated = new HashMap<Id, String>();
    // it may be broken by force unlocking
    checkLock();
    _deleted = new HashSet<Id>();
  }

  /**
   * Clear.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void clear() throws DeltaIndexingException {
    rollback();
    _index.clear();
    _modified.clear();

    // clear mapped sets
    final Iterator<Set<Id>> it = _subCompounds.values().iterator();
    while (it.hasNext()) {
      final Set<Id> set = it.next();
      if (set != null) {
        set.clear();
      }
    }
    _subCompounds.clear();
  }

  /**
   * Commit changes.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  private void commit() throws DeltaIndexingException {
    for (final Id id : _updated.keySet()) {
      checkLock();
      _index.put(id, _updated.get(id));
    }
    for (final Id id : _deleted) {
      checkLock();
      _index.remove(id);
      removeSubCompound(id);
    }
  }

  /**
   * Check lock.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  private void checkLock() throws DeltaIndexingException {
    if (!_lock.isLocked()) {
      throw new DeltaIndexingException(String.format(EXCEPTION_DATA_SOURCE_SHOULD_BE_LOCKED, _dataSourceId));
    }
  }

  /**
   * Force unlock and rollback.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void forceUnlockAndRollback() throws DeltaIndexingException {
    final ReentrantLock lock = new ReentrantLock();
    lock.lock();
    _lock = lock;
    rollback();
    unlock();
  }

  /**
   * Force unlock and clear.
   * 
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  public void forceUnlockAndClear() throws DeltaIndexingException {
    final ReentrantLock lock = new ReentrantLock();
    lock.lock();
    _lock = lock;
    clear();
    unlock();
  }

  /**
   * Add a sub compound mapping.
   * 
   * @param parentId
   *          the parent Id
   * @param subCompoundId
   *          the sub compound id
   */
  public void addSubCompound(final Id parentId, final Id subCompoundId) {
    Set<Id> set = _subCompounds.get(parentId);
    if (set == null) {
      set = new HashSet<Id>();
    }
    set.add(subCompoundId);
    _subCompounds.put(parentId, set);
  }

  /**
   * Returns the Set of sub compounds or null if none exists.
   * 
   * @param id
   *          the id to get the sub compounds for
   * @return the Set of sub compounds or null if none exists
   */
  public Set<Id> getSubCompounds(final Id id) {
    return _subCompounds.get(id);
  }

  /**
   * Removes the mapping for an id, cleaning up the mapped set, too.
   * 
   * @param id
   *          the id to remove the mapping for
   */
  public void removeSubCompound(final Id id) {
    final Set<Id> set = _subCompounds.get(id);
    if (set != null) {
      set.clear();
    }
    _subCompounds.remove(id);
  }

  /**
   * Gets the data source id.
   * 
   * @return the data source id
   */
  public String getDataSourceId() {
    return _dataSourceId;
  }

  /**
   * Gets the session id.
   * 
   * @return the session id
   */
  public String getSessionId() {
    return _sessionId;
  }

  /**
   * Returns the number of entries in the index.
   * 
   * @return the number of entries in the index
   */
  public long getEntryCount() {
    final Set<Id> set = new HashSet<Id>();
    if (_index != null) {
      set.addAll(_index.keySet());
    }
    if (_updated != null) {
      set.addAll(_updated.keySet());
    }
    if (_modified != null) {
      set.addAll(_modified);
    }
    if (_deleted != null) {
      set.removeAll(_deleted);
    }
    return set.size();
  }
}
