/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.recordstorage.RecordStorage;
import org.eclipse.smila.recordstorage.RecordStorageException;
import org.eclipse.smila.recordstorage.util.RecordDao;
import org.eclipse.smila.recordstorage.util.RecordIterator;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.service.component.ComponentContext;

/**
 * Implementation of a RecordStorage using eclipseLink and JPA. providing limited search access.
 */
public class RecordStorageImpl implements RecordStorage {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.recordstorage.impl";

  /**
   * Constant for the eclipseLink persistence unit name.
   */
  public static final String PERSISTENCE_UNIT_NAME = "SmilaRecord";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_FILE = "persistence.properties";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(RecordStorageImpl.class);

  /**
   * service methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * configuraton properties.
   */
  private Properties _properties;

  /**
   * the EntityManagerFactory.
   */
  private EntityManagerFactory _emf;

  /**
   * Default Constructor.
   */
  public RecordStorageImpl() {
    if (_log.isTraceEnabled()) {
      _log.trace("creating instance of RecordStorageImpl");
    }
  }

  /**
   * DS activate method.
   *
   * @param context
   *          ComponentContext
   *
   * @throws Exception
   *           if any error occurs
   */
  protected void activate(final ComponentContext context) throws Exception {
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
        _log.error("error activating RecordStorageImpl service", e);
      }
      throw e;
    } finally {
      closeEntityManager(em);
    }
    if (_log.isTraceEnabled()) {
      _log.trace("started RecordStorageImpl service");
    }
  }

  /**
   * DS deactivate method.
   *
   * @param context
   *          the ComponentContext
   *
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    // close EntityManagerFactory
    _lock.writeLock().lock();
    try {
      try {
        if (_emf != null) {
          _emf.close();
        }
      } catch (final Exception e) {
        if (_log.isErrorEnabled()) {
          _log.error("error closing EntityManagerFactory", e);
        }
      }
      _emf = null;

      // _properties _
      if (_properties != null) {
        _properties.clear();
        _properties = null;
      }

      if (_log.isTraceEnabled()) {
        _log.trace("deactivated RecordStorageImpl service");
      }
    } finally {
      _lock.writeLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.recordstorage.RecordStorage#loadRecord(org.eclipse.smila.datamodel.id.Id)
   */
  public Record loadRecord(final Id id) throws RecordStorageException {
    if (id == null) {
      throw new RecordStorageException("parameter id is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final RecordDao dao = findRecordDao(em, id);
        if (dao != null) {
          return dao.toRecord();
        }
        return null;
      } catch (final Exception e) {
        throw new RecordStorageException(e, "error loading record id: " + id);
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
   * @see org.eclipse.smila.recordstorage.RecordStorage#storeRecord(org.eclipse.smila.datamodel.record.Record)
   */
  public void storeRecord(final Record record) throws RecordStorageException {
    if (record == null) {
      throw new RecordStorageException("parameter record is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        final RecordDao dao = new RecordDao(record);
        transaction.begin();
        if (findRecordDao(em, record.getId()) == null) {
          em.persist(dao);
        } else {
          em.merge(dao);
        }
        transaction.commit();
        if (_log.isTraceEnabled()) {
          _log.trace("stored record Id:" + record.getId());
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new RecordStorageException(e, "error storing record id: " + record.getId());
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
   * @see org.eclipse.smila.recordstorage.RecordStorage#removeRecord(org.eclipse.smila.datamodel.id.Id)
   */
  public void removeRecord(final Id id) throws RecordStorageException {
    if (id == null) {
      throw new RecordStorageException("parameter id is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final RecordDao dao = findRecordDao(em, id);
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
            throw new RecordStorageException(e, "error removing record id: " + id);
          }
        } else {
          if (_log.isDebugEnabled()) {
            _log.debug("could not remove record id: " + id + ". no record with this id exists.");
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
   * @see org.eclipse.smila.recordstorage.RecordStorage#existsRecord(org.eclipse.smila.datamodel.id.Id)
   */
  public boolean existsRecord(final Id id) throws RecordStorageException {
    if (id == null) {
      throw new RecordStorageException("parameter id is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final RecordDao dao = findRecordDao(em, id);
        if (dao != null) {
          return true;
        }
        return false;
      } catch (final Exception e) {
        throw new RecordStorageException(e, "error checking existence of record id: " + id);
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
   * @see org.eclipse.smila.recordstorage.RecordStorage#findRecordsBySource(java.lang.String)
   */
  public Iterator<Record> loadRecords(final String source) throws RecordStorageException {
    if (source == null) {
      throw new RecordStorageException("parameter source is null");
    }
    if (source.trim().length() == 0) {
      throw new RecordStorageException("parameter source is an empty String");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final Query query = em.createNamedQuery(RecordDao.NAMED_QUERY_FIND_BY_SOURCE);
        final List<RecordDao> daos = query.setParameter(RecordDao.NAMED_QUERY_PARAM_SOURCE, source).getResultList();
        if (daos.isEmpty() && _log.isInfoEnabled()) {
          _log.info("loadRecords could not find any records for source: " + source);
        }
        return new RecordIterator(daos.iterator());
      } catch (final Exception e) {
        throw new RecordStorageException(e, "error executing loadRecords with source: " + source);
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * Internal method to find a RecordDao object by Record Id.
   *
   * @param em
   *          the EntityManager to use
   * @param id
   *          the Id of the Record
   * @return the RecordDao object or null
   */
  private RecordDao findRecordDao(final EntityManager em, final Id id) {
    return em.find(RecordDao.class, id.getIdHash());
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
   * @throws RecordStorageException
   *           service is not active currently (probably deactivated has been called already).
   */
  private EntityManager createEntityManager() throws RecordStorageException {
    if (_emf == null) {
      throw new RecordStorageException("RecordStorage is not active anymore. Maybe this system is shutting down?");
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
