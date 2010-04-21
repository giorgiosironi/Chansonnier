/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.jpa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * JPA Binary Storage persistence layer.
 */
public class JPABinaryPersistence extends BinaryPersistence {

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.binarystorage.persistence.jpa";

  /**
   * Constant for the eclipseLink persistence unit name.
   */
  public static final String PERSISTENCE_UNIT_NAME = "SmilaBinaryObject";

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_FILE = "persistence.properties";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(JPABinaryPersistence.class);

  /**
   * service methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * configuration properties.
   */
  private Properties _properties;

  /**
   * the EntityManagerFactory.
   */
  private EntityManagerFactory _emf;

  /**
   * Basic constructor.
   *
   * @param binaryStorageConfig
   *          the BinaryStorageConfiguration
   * @throws BinaryStorageException
   *           if any error occurs during initialization
   */
  public JPABinaryPersistence(final BinaryStorageConfiguration binaryStorageConfig) throws BinaryStorageException {
    if (_log.isTraceEnabled()) {
      _log.trace("creating instance of RecordStorageImpl");
    }
    init(binaryStorageConfig);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#storeBinary(java.lang.String,
   *      byte[])
   */
  @Override
  public void storeBinary(final String key, final byte[] content) throws BinaryStorageException {
    if (key == null) {
      throw new BinaryStorageException("parameter key is null");
    }
    if (content == null) {
      throw new BinaryStorageException("parameter content is null");
    }
    store(new BinaryStorageDao(key, content));
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#storeBinary(java.lang.String,
   *      java.io.InputStream)
   */
  @Override
  public void storeBinary(final String key, final InputStream stream) throws BinaryStorageException {
    if (key == null) {
      throw new BinaryStorageException("parameter key is null");
    }
    if (stream == null) {
      throw new BinaryStorageException("parameter stream is null");
    }

    try {
      store(new BinaryStorageDao(key, stream));
    } catch (final IOException e) {
      throw new BinaryStorageException(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#deleteBinary(java.lang.String)
   */
  @Override
  public void deleteBinary(final String key) throws BinaryStorageException {
    if (key == null) {
      throw new BinaryStorageException("parameter key is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final BinaryStorageDao dao = findBinaryStorageDao(em, key);
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
            throw new BinaryStorageException(e, "error removing record id: " + key);
          }
        } else {
          if (_log.isDebugEnabled()) {
            _log.debug("could not remove id: " + key + ". no binary object with this id exists.");
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
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#fetchSize(java.lang.String)
   */
  @Override
  public long fetchSize(final String key) throws BinaryStorageException {
    if (key == null) {
      throw new BinaryStorageException("parameter key is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final BinaryStorageDao dao = findBinaryStorageDao(em, key);
        if (dao != null) {
          return dao.getBytes().length;
        } else {
          throw new BinaryStorageException("could not fetch size for id: " + key
            + ". no binary object with this id exists.");
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
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#
   *      loadBinaryAsByteArray(java.lang.String)
   */
  @Override
  public byte[] loadBinaryAsByteArray(final String key) throws BinaryStorageException {
    return load(key).getBytes();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#
   *      loadBinaryAsInputStream(java.lang.String)
   */
  @Override
  public InputStream loadBinaryAsInputStream(final String key) throws BinaryStorageException {
    return load(key).getBytesAsStream();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.binarystorage.internal.impl.persistence.BinaryPersistence#cleanup()
   */
  @Override
  public void cleanup() throws BinaryStorageException {
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
   * Initialize JPABinaryPersistence.
   *
   * @param binaryStorageConfig
   *          BinaryStorageConfiguration
   *
   * @throws BinaryStorageException
   *           if any error occurs
   */
  private void init(final BinaryStorageConfiguration binaryStorageConfig) throws BinaryStorageException {
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
      throw new BinaryStorageException("error activating JPABinaryPersistence", e);
    } finally {
      closeEntityManager(em);
    }
    if (_log.isTraceEnabled()) {
      _log.trace("started JPABinaryPersistence");
    }
  }

  /**
   * Load the BinaryStorageDao with the given key.
   *
   * @param key
   *          the key
   * @return the BinaryStorageDao
   * @throws BinaryStorageException
   *           if any error occurs or no BinaryStorageDao was found
   */
  private BinaryStorageDao load(final String key) throws BinaryStorageException {
    if (key == null) {
      throw new BinaryStorageException("parameter key is null");
    }
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      try {
        final BinaryStorageDao dao = findBinaryStorageDao(em, key);
        if (dao != null) {
          return dao;
        }
        throw new BinaryStorageException("error loading id: " + key + ". no binary object with this id exists.");
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * Stores the given BinaryStorageDao, updating an existing one or creating a new one.
   *
   * @param dao
   *          the BinaryStorageDao to store
   * @throws BinaryStorageException
   *           if any error occurs
   */
  // TODO: don't know if this synchronize is good, was needed to pass the JUNit test TestConcurrentBSSAccessJPA
  private synchronized void store(final BinaryStorageDao dao) throws BinaryStorageException {
    _lock.readLock().lock();
    try {
      final EntityManager em = createEntityManager();
      final EntityTransaction transaction = em.getTransaction();
      try {
        transaction.begin();
        if (findBinaryStorageDao(em, dao.getId()) == null) {
          em.persist(dao);
        } else {
          em.merge(dao);
        }
        transaction.commit();
        if (_log.isTraceEnabled()) {
          _log.trace("stored content of id:" + dao.getId());
        }
      } catch (final Exception e) {
        if (transaction.isActive()) {
          transaction.rollback();
        }
        throw new BinaryStorageException(e, "error storing record id: " + dao.getId());
      } finally {
        closeEntityManager(em);
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * Internal method to find a BinaryStorageDao object by id.
   *
   * @param em
   *          the EntityManager to use
   * @param id
   *          the id of the BinaryStorageDao
   * @return the RecordDao object or null
   */
  private BinaryStorageDao findBinaryStorageDao(final EntityManager em, final String id) {
    return em.find(BinaryStorageDao.class, id);
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
   * @throws BinaryStorageException
   *           service is not active currently (probably deactivated has been called already).
   */
  private EntityManager createEntityManager() throws BinaryStorageException {
    if (_emf == null) {
      throw new BinaryStorageException(
        "BinaryStorage PJA Persistence is not active anymore. Maybe this system is shutting down?");
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
