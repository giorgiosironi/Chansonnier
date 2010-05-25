/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.jpa.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.service.component.ComponentContext;

/**
 * The Class DeltaIndexingManagerImpl.
 */
public class DeltaIndexingManagerImpl implements DeltaIndexingManager {

  /**
   * Constant for the eclipseLink persistence unit name.
   */
  public static final String PERSISTENCE_UNIT_NAME = "DeltaIndexing";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_FILE = "persistence.properties";

  /**
   * The Constant BUNDLE_NAME.
   */
  private static final String BUNDLE_NAME = "org.eclipse.smila.connectivity.deltaindexing.jpa.impl";

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(DeltaIndexingManagerImpl.class);

  /**
   * configuration properties.
   */
  private Properties _properties;

  /**
   * the EntityManagerFactory.
   */
  private EntityManagerFactory _emf;

  /**
   * service methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * Activate.
   *
   * @param context
   *          the context
   * @throws Exception
   *           if any error occurs
   */
  protected synchronized void activate(final ComponentContext context) throws Exception {
    EntityManager em = null;
    try {
      readConfiguration();
      if (!_properties.containsKey("eclipselink.logging.file")) {
        final File workingDir = WorkspaceHelper.createWorkingDir(BUNDLE_NAME);
        final File logfile = new File(workingDir, "jpa.log");
        _properties.put("eclipselink.logging.file", logfile.getAbsolutePath());
      }
      // set up eclipseLink
      _emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, _properties);

      // create an initial EntityManager to create the database
      em = _emf.createEntityManager();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error activating " + getClass(), e);
      }
      throw e;
    } finally {
      closeEntityManager(em);
    }
    if (_log.isTraceEnabled()) {
      _log.trace("Activating " + getClass());
    }
  }

  /**
   * OSGi Declarative Services service deactivation method. Shuts down BPEL engine.
   *
   * @param context
   *          OSGi service component context
   * @throws Exception
   *           if any error occurs
   */
  protected synchronized void deactivate(final ComponentContext context) throws Exception {
    // close EntityManagerFactory
    _lock.writeLock().lock();
    try {

      try {
        if (_emf != null) {
          _emf.close();
        }
      } catch (final Exception e) {
        if (_log.isErrorEnabled()) {
          _log.error("error deactivating " + getClass(), e);
        }
      }
      _emf = null;

      // _properties _
      if (_properties != null) {
        _properties.clear();
        _properties = null;
      }
      if (_log.isTraceEnabled()) {
        _log.trace("Deactivating " + getClass());
      }
    } finally {
      _lock.writeLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#init(java.lang.String)
   */
  public String init(final String dataSourceID) throws DeltaIndexingException {
    if (dataSourceID == null) {
      throw new DeltaIndexingException("parameter dataSourceID is null");
    }

    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        final DataSourceDao dao = findDataSourceDao(em, dataSourceID);
        if (dao != null && dao.getSessionId() != null) {
          throw new DeltaIndexingException("data source " + dataSourceID + " is already locked by another session");
        }

        final String sessionId = UUID.randomUUID().toString();

        final DataSourceDao lockedDao = new DataSourceDao(dataSourceID, sessionId);
        // lock the data source
        if (dao == null) {
          em.persist(lockedDao);
        } else {
          em.merge(lockedDao);
        }

        // reset visited and modified flags
        resetFlags(em, dataSourceID);

        transaction.commit();
        if (_log.isTraceEnabled()) {
          _log.trace("created session " + sessionId + " for data source: " + dataSourceID);
        }
        return sessionId;
      } catch (final DeltaIndexingException e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw e;
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException("error initializing delta indexing for data source: " + dataSourceID, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#checkForUpdate(String, Id, String)
   */
  public boolean checkForUpdate(final String sessionId, final Id id, final String hash)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }
    if (hash == null) {
      throw new DeltaIndexingException("parameter hash is null");
    }
    _lock.readLock().lock();
    try {
      assertSession(sessionId, id.getSource());
      final EntityManager em = createEntityManager();
      try {
        final DeltaIndexingDao dao = findDeltaIndexingDao(em, id);
        if (dao == null || !hash.equals(dao.getHash())) {
          return true;
        } else {
          final EntityTransaction transaction = em.getTransaction();
          try {
            transaction.begin();
            visitUnchangedDaos(em, dao);
            transaction.commit();
          } catch (final Exception e) {
            if (transaction.isActive()) {
              transaction.rollback();
            }
            throw new DeltaIndexingException("error visiting id: " + id, e);
          }
          return false;
        }
      } catch (final Exception e) {
        throw new DeltaIndexingException("error checking for update for id: " + id, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#visit(String, Id, String, boolean)
   */
  public void visit(final String sessionId, final Id id, final String hash, final boolean isCompound)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }
    if (hash == null) {
      throw new DeltaIndexingException("parameter hash is null");
    }
    _lock.readLock().lock();
    try {
      assertSession(sessionId, id.getSource());
      final EntityManager em = createEntityManager();
      try {
        final DeltaIndexingDao dao = findDeltaIndexingDao(em, id);
        visitNewOrChangedDao(em, dao, id, hash, isCompound);
      } catch (final DeltaIndexingException e) {
        throw e;
      } catch (final Exception e) {
        throw new DeltaIndexingException("error visiting id: " + id, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#obsoleteIdIterator(String, String)
   */
  public Iterator<Id> obsoleteIdIterator(final String sessionId, final String dataSourceID)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (dataSourceID == null) {
      throw new DeltaIndexingException("parameter dataSourceID is null");
    }
    _lock.readLock().lock();
    try {
      assertSession(sessionId, dataSourceID);
      final EntityManager em = createEntityManager();
      try {
        final Query query = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_FIND_OBSOLETE_IDS_BY_SOURCE);
        final List<DeltaIndexingDao> daos =
          query.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_SOURCE, dataSourceID).getResultList();
        if (daos.isEmpty() && _log.isInfoEnabled()) {
          _log.info("obsoleteIdIterator could not find any obsolete ids for source: " + dataSourceID);
        }
        return new IdIterator(daos.iterator());
      } catch (final Exception e) {
        throw new DeltaIndexingException("error executing loadRecords with source: " + dataSourceID, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#obsoleteIdIterator(String, Id)
   */
  public Iterator<Id> obsoleteIdIterator(final String sessionId, final Id id) throws DeltaIndexingSessionException,
    DeltaIndexingException {
    throw new UnsupportedOperationException("Not Implemented because working with fragments is not clear!");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#clear()
   */
  public void clear() throws DeltaIndexingException {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        // delete delta indexing entries
        final Query diQuery = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_DELETE_ALL);
        diQuery.executeUpdate();
        // delete source
        final Query dsQuery = em.createNamedQuery(DataSourceDao.NAMED_QUERY_DELETE_SOURCES);
        dsQuery.executeUpdate();

        transaction.commit();
        if (_log.isInfoEnabled()) {
          _log.info("cleared delta indexing");
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException("error clearing delta indexing", e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#clear(String)
   */
  public void clear(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException {
    _lock.readLock().lock();
    try {
      final DataSourceDao dao = assertSession(sessionId);

      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        // delete delta indexing entries
        final Query diQuery = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_DELETE_BY_SOURCE);
        diQuery.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_SOURCE, dao.getDataSourceId()).executeUpdate();

        transaction.commit();
        if (_log.isInfoEnabled()) {
          _log.info("cleared delta indexing for sessionId: " + sessionId + " with data source "
            + dao.getDataSourceId());
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException("error clearing delta indexing for session id: " + sessionId, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#rollback(java.lang.String)
   */
  public void rollback(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException {
    throw new UnsupportedOperationException("Not Implemented yet. Use case and workflow are not specified!");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#delete(String, Id)
   */
  public void delete(final String sessionId, final Id id) throws DeltaIndexingSessionException,
    DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }
    _lock.readLock().lock();
    try {
      assertSession(sessionId, id.getSource());
      final EntityManager em = createEntityManager();
      try {
        final DeltaIndexingDao dao = findDeltaIndexingDao(em, id);
        if (dao != null) {
          final EntityTransaction transaction = em.getTransaction();
          try {
            transaction.begin();
            em.remove(dao);
            transaction.commit();
          } catch (final Exception e) {
            if (transaction.isActive()) {
              transaction.rollback();
            }
            throw new DeltaIndexingException("error deleting id: " + id, e);
          }
        } else {
          if (_log.isDebugEnabled()) {
            _log.debug("could not delete id: " + id + ". Id does not exist.");
          }
        }
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#finish(String)
   */
  public void finish(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException {
    _lock.readLock().lock();
    try {
      final DataSourceDao dao = assertSession(sessionId);
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        final DataSourceDao unlockedDao = new DataSourceDao(dao.getDataSourceId(), null);
        em.merge(unlockedDao);
        transaction.commit();
        if (_log.isTraceEnabled()) {
          _log.trace("finished session " + sessionId + " with data source: " + dao.getDataSourceId());
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException(
          "error finishing delta indexing for data source: " + dao.getDataSourceId(), e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#unlockDatasource(String)
   */
  public void unlockDatasource(final String dataSourceID) throws DeltaIndexingException {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        final Query query = em.createNamedQuery(DataSourceDao.NAMED_QUERY_KILL_SESSION);
        query.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_SOURCE, dataSourceID);
        query.executeUpdate();
        transaction.commit();
        if (_log.isInfoEnabled()) {
          _log.info("removed delta indexing sessions and unlocked data source " + dataSourceID);
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException("error unlocking delta indexing data source " + dataSourceID, e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#unlockDatasources()
   */
  public void unlockDatasources() throws DeltaIndexingException {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        final Query query = em.createNamedQuery(DataSourceDao.NAMED_QUERY_KILL_ALL_SESSIONS);
        query.executeUpdate();
        transaction.commit();
        if (_log.isInfoEnabled()) {
          _log.info("removed all delta indexing sessions and unlocked all data sources");
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new DeltaIndexingException("error unlocking delta indexing data sources", e);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getLockStates()
   */
  public Map<String, LockState> getLockStates() {
    final HashMap<String, LockState> lockStates = new HashMap<String, LockState>();
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final Query query = em.createNamedQuery(DataSourceDao.NAMED_QUERY_SELECT_ALL);
        final List<DataSourceDao> list = query.getResultList();
        for (final DataSourceDao dao : list) {
          LockState state = LockState.UNLOCKED;
          if (dao.getSessionId() != null) {
            state = LockState.LOCKED;
          }
          lockStates.put(dao.getDataSourceId(), state);
        }
      } finally {
        closeEntityManager(em);
      }
    } catch (final DeltaIndexingException ex) {
      if (_log.isErrorEnabled()) {
        _log.error("error getting lock states for all data source ids", ex);
      }
    } finally {
      _lock.readLock().unlock();
    }

    return lockStates;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#exists(String)
   */
  public boolean exists(final String dataSourceId) {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        if (findDataSourceDao(em, dataSourceId) != null) {
          return true;
        }
        return false;
      } finally {
        closeEntityManager(em);
      }
    } catch (final DeltaIndexingException ex) {
      if (_log.isErrorEnabled()) {
        _log.error("error checking if data source id " + dataSourceId + " exists", ex);
      }
      return false;
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getEntryCount(String)
   */
  public long getEntryCount(final String dataSourceId) {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final Query query = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_COUNT_ENTRIES_BY_SOURCE);
        final Long count =
          (Long) query.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_SOURCE, dataSourceId).getSingleResult();
        if (count == null) {
          return 0;
        }
        return count.longValue();
      } finally {
        closeEntityManager(em);
      }
    } catch (final DeltaIndexingException ex) {
      if (_log.isErrorEnabled()) {
        _log.error("error getting entry count for data source id " + dataSourceId, ex);
      }
      return 0;
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getEntryCounts()
   */
  public Map<String, Long> getEntryCounts() {
    final HashMap<String, Long> entryCounts = new HashMap<String, Long>();
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final Query query = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_COUNT_ENTRIES);
        final List<Object[]> list = query.getResultList();
        for (final Object[] entryCount : list) {
          entryCounts.put((String) entryCount[0], (Long) entryCount[1]);
        }
      } finally {
        closeEntityManager(em);
      }
    } catch (final DeltaIndexingException ex) {
      if (_log.isErrorEnabled()) {
        _log.error("error getting entry counts for all data source ids", ex);
      }
    } finally {
      _lock.readLock().unlock();
    }

    return entryCounts;
  }

  /**
   * Assures that the given sessionId exists and is used for the given data source.
   *
   * @param sessionId
   *          the sessionId
   * @param dataSourceId
   *          the data source
   * @throws DeltaIndexingSessionException
   *           if the sessionId does not exist
   * @throws DeltaIndexingException
   *           if the given dataSourceId does not match the dataSourceId of the session
   */
  private void assertSession(final String sessionId, final String dataSourceId)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (sessionId == null) {
      throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
    }

    final EntityManager em = createEntityManager();
    try {
      final DataSourceDao dao = findDataSourceDaoBySession(em, sessionId);
      if (dao == null) {
        throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
      }
      if (!dao.getDataSourceId().equals(dataSourceId)) {
        throw new DeltaIndexingException("Invalid data source id " + dataSourceId + " for session id " + sessionId);
      }
    } finally {
      closeEntityManager(em);
    }
  }

  /**
   * Assures that the given sessionId exists and returns the found DataSourceDao object.
   *
   * @param sessionId
   *          session Id
   * @return the DataSourceDao
   * @throws DeltaIndexingSessionException
   *           if the sessionId does not exist
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private DataSourceDao assertSession(final String sessionId) throws DeltaIndexingSessionException,
    DeltaIndexingException {
    if (sessionId == null) {
      throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
    }

    final EntityManager em = createEntityManager();
    try {
      final DataSourceDao dao = findDataSourceDaoBySession(em, sessionId);
      if (dao == null) {
        throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
      }
      return dao;
    } finally {
      closeEntityManager(em);
    }
  }

  /**
   * Creates or updates an entry in the delta indexing database and sets the visited flag.
   *
   * @param em
   *          the EntityManager
   * @param dao
   *          the DeltaIndexingDao if a former entry exists or null
   * @param id
   *          the id of the record (just for logging)
   * @param hash
   *          the delta indexing hash
   * @param isCompound
   *          boolean flag if the record identified by id is a compound record (true) or not (false)
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private void visitNewOrChangedDao(final EntityManager em, DeltaIndexingDao dao, final Id id, final String hash,
    final boolean isCompound) throws DeltaIndexingException {
    final EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();
      if (dao == null) {
        dao = new DeltaIndexingDao(id, hash, isCompound, true);
        em.persist(dao);
        if (_log.isTraceEnabled()) {
          _log.trace("created and visited id: " + id);
        }
      } else {
        dao.modifyAndVisit();
        em.merge(dao);
        if (_log.isTraceEnabled()) {
          _log.trace("visited Id:" + id);
        }
      }
      transaction.commit();
    } catch (final Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new DeltaIndexingException("error visiting id: " + id, e);
    }
  }

  /**
   * Sets the visited flags of a unchanged dao object. The modified flag is NOT set !!! Sub compounds of compounds are
   * also set to visited.
   *
   * @param em
   *          the EntityManager
   * @param dao
   *          the DeltaIndexingDao
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private void visitUnchangedDaos(final EntityManager em, final DeltaIndexingDao dao) throws DeltaIndexingException {
    // visit dao if it's a compound or if it has no parent
    dao.visit();
    em.merge(dao);
    if (_log.isTraceEnabled()) {
      _log.trace("visited Id with hash:" + dao.getIdHash());
    }

    // check if dao is a compound and visit all sub compounds
    if (dao.isCompound()) {
      final Query query = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_FIND_SUB_COMPOUNDS);
      final List<DeltaIndexingDao> daos =
        query.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_PARENT_ID_HASH, dao.getIdHash()).getResultList();
      if (daos != null) {
        for (final DeltaIndexingDao subDao : daos) {
          visitUnchangedDaos(em, subDao);
        } // for
      } // if
    }
  }

  /**
   * Resets the visited and modified flags of a data source. Must be used with an entity manager.
   *
   * @param em
   *          the EntityManager
   * @param dataSourceId
   *          the id of the data source
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private void resetFlags(final EntityManager em, final String dataSourceId) throws DeltaIndexingException {
    try {
      final Query query = em.createNamedQuery(DeltaIndexingDao.NAMED_QUERY_RESET_FLAGS);
      query.setParameter(DeltaIndexingDao.NAMED_QUERY_PARAM_SOURCE, dataSourceId).executeUpdate();
      if (_log.isInfoEnabled()) {
        _log.info("reset visited flags for data source id: " + dataSourceId);
      }
    } catch (final Exception e) {
      throw new DeltaIndexingException("error resetting visited flags for data source id: " + dataSourceId, e);
    }
  }

  /**
   * Internal method to find a DataSourceDao object by dataSourceId.
   *
   * @param em
   *          the EntityManager to use
   * @param dataSourceId
   *          the data source id
   * @return the RecordDao object or null
   */
  private DataSourceDao findDataSourceDao(final EntityManager em, final String dataSourceId) {
    return em.find(DataSourceDao.class, dataSourceId);
  }

  /**
   * Internal method to find a DataSourceDao object by sessionId.
   *
   * @param em
   *          the EntityManager
   * @param sessionId
   *          the id of the session
   * @return the found DataSourceDao or null if none exists
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private DataSourceDao findDataSourceDaoBySession(final EntityManager em, final String sessionId)
    throws DeltaIndexingException {
    try {
      final Query query = em.createNamedQuery(DataSourceDao.NAMED_QUERY_FIND_BY_SESSION_ID);
      final List<DataSourceDao> daos =
        query.setParameter(DataSourceDao.NAMED_QUERY_PARAM_SESSION_ID, sessionId).getResultList();
      if (daos != null && !daos.isEmpty()) {
        return daos.get(0);
      }
      return null;
    } catch (final Exception e) {
      throw new DeltaIndexingException("error searching for sessionId " + sessionId, e);
    }

  }

  /**
   * Internal method to find a DeltaIndexingDao object by id.
   *
   * @param em
   *          the EntityManager to use
   * @param id
   *          the id
   * @return the RecordDao object or null
   */
  private DeltaIndexingDao findDeltaIndexingDao(final EntityManager em, final Id id) {
    return em.find(DeltaIndexingDao.class, id.getIdHash());
  }

  /**
   * read configuration property file.
   *
   * @throws IOException
   *           error reading configuration file
   */
  private void readConfiguration() throws IOException {
    _properties = new Properties();
    InputStream configurationFileStream = null;
    try {
      configurationFileStream = ConfigUtils.getConfigStream(BUNDLE_NAME, CONFIGURATION_FILE);
      _properties.load(configurationFileStream);
    } catch (final IOException ex) {
      throw new IOException("Could not read configuration property file " + CONFIGURATION_FILE + ": "
        + ex.toString());
    } finally {
      IOUtils.closeQuietly(configurationFileStream);
    }
  }

  /**
   * @return new entity manager
   * @throws DeltaIndexingException
   *           service is not active currently (probably deactivated has been called already).
   */
  private EntityManager createEntityManager() throws DeltaIndexingException {
    if (_emf == null) {
      throw new DeltaIndexingException("DeltaIndexing is not active anymore. Maybe this system is shutting down?");
    }
    return _emf.createEntityManager();
  }

  /**
   * Closes an EntityManager.
   *
   * @param em
   *          the EntityManager
   */
  private void closeEntityManager(final EntityManager em) {
    try {
      if (em != null) {
        em.close();
      }
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error closing local EntityManager", e);
      }
    }
  }
}
