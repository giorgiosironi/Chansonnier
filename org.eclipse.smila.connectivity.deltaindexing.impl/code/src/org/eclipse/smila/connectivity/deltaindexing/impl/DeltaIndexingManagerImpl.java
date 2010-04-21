/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdHandlingException;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

/**
 * The Class DeltaIndexingManagerImpl. It was done only for testing purposes.
 */
public class DeltaIndexingManagerImpl implements DeltaIndexingManager {

  /**
   * The Constant BUNDLE_ID.
   */
  private static final String BUNDLE_ID = "org.eclipse.smila.connectivity.deltaindexing";

  /**
   * The Constant ERROR_DURING_GLOBAL_CLEAN.
   */
  private static final String ERROR_DURING_GLOBAL_CLEAR =
    "Error during cleaning datasource %s during global clear() operation";

  /**
   * The Constant ERROR_DURING_UNLOCK.
   */
  private static final String ERROR_DURING_UNLOCK = "Error during unlocking of datasource %s";

  /**
   * The Constant ERROR_DURING_GET_ENTRY_COUNTS.
   */
  private static final String ERROR_DURING_GET_ENTRY_COUNTS =
    "Error during getting the entry count for datasource %s";

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(DeltaIndexingManagerImpl.class);

  /**
   * Data sources index.
   */
  private final Map<String, DataSourceConnection> _dataSources =
    Collections.synchronizedMap(new HashMap<String, DataSourceConnection>());

  /**
   * The bundle - only for patch to avoid DS bug.
   */
  private Bundle _bundle;

  /**
   * Activate.
   * 
   * @param context
   *          the context
   */
  protected synchronized void activate(final ComponentContext context) {
    if (_log.isDebugEnabled()) {
      _log.debug("Activating " + getClass());
    }
    // TODO: remove it when Declarative Services will set it correctly
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    try {
      this.start();
      if (_log.isDebugEnabled()) {
        _log.debug("Activation of " + getClass() + " was successfull");
      }
    } catch (final Throwable e) {
      _log.error("Activation of " + getClass() + " was failed", e);
      throw new RuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }

  /**
   * OSGi Declarative Services service deactivation method. Shuts down BPEL engine.
   * 
   * @param context
   *          OSGi service component context.
   */
  protected synchronized void deactivate(final ComponentContext context) {
    if (_log.isDebugEnabled()) {
      _log.debug("Deactivating " + getClass());
    }
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    try {
      this.stop();
    } catch (final IOException e) {
      _log.error("Deactivation of " + getClass() + " was failed", e);
      throw new RuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }

  /**
   * Start.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   * @throws DeltaIndexingException
   *           the delta indexing exception
   */
  private void start() throws IOException, ClassNotFoundException, DeltaIndexingException {
    final File folder = WorkspaceHelper.createWorkingDir(BUNDLE_ID);
    this._bundle = Platform.getBundle(BUNDLE_ID);
    synchronized (_dataSources) {
      for (final File file : folder.listFiles()) {
        if (file.isFile()) {
          // load data source
          final FileInputStream fis = new FileInputStream(file);
          final ObjectInputStream in = new ObjectInputStream(fis);
          DataSourceConnection connection = null;
          try {
            connection = (DataSourceConnection) in.readObject();
          } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(fis);
          }
          connection.forceUnlockAndRollback();
          _dataSources.put(connection.getDataSourceId(), connection);
        }
      }
    }
    _log.info("Deltaindexing service was successfully started");
  }

  /**
   * Stop.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void stop() throws IOException {

    final File folder;
    if (_bundle != null) {
      folder = WorkspaceHelper.createWorkingDirByBundle(_bundle);
    } else {
      folder = WorkspaceHelper.createWorkingDir(BUNDLE_ID);
    }
    _bundle = null;
    // remove old files
    for (final File file : folder.listFiles()) {
      if (file.isFile()) {
        file.delete();
      }
    }
    // add
    synchronized (_dataSources) {
      for (final String dataSource : _dataSources.keySet()) {
        final DataSourceConnection connection = _dataSources.get(dataSource);
        final File file1 = new File(folder, convertDatasourceToFileName(dataSource));
        final FileOutputStream fos = new FileOutputStream(file1);
        final ObjectOutputStream out = new ObjectOutputStream(fos);
        try {
          out.writeObject(connection);
        } finally {
          IOUtils.closeQuietly(out);
          IOUtils.closeQuietly(fos);
        }
      }
    }
    _log.info("Deltaindexing service was successfully stopped");
  }

  /**
   * Convert datasource to file name.
   * 
   * @param dataSource
   *          the data source
   * 
   * @return the string
   */
  private String convertDatasourceToFileName(final String dataSource) {
    // TODO
    return dataSource;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see {@link org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#checkForUpdate(String, Id, String)}
   */
  public boolean checkForUpdate(final String sessionId, final Id id, final String hash)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }
    if (hash == null) {
      throw new DeltaIndexingException("parameter hash is null");
    }
    if (_log.isDebugEnabled()) {
      _log.debug("Checking for update " + id.getIdHash());
    }

    final DataSourceConnection dataSource = findDataSource(sessionId, id.getSource());
    final String value = dataSource.getHash(id);
    if (value == null) {
      return true;
    }

    final boolean result = !value.equals(hash);
    if (!result) {
      // mark as visited
      dataSource.put(id);
      visitSubCompounds(dataSource, id);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#finish(java.lang.String)
   */
  public void finish(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException {
    final DataSourceConnection dataSource = findDataSourceBySession(sessionId);
    dataSource.unlock();
    if (_log.isDebugEnabled()) {
      _log.debug("Finishing session " + sessionId + " and releasing datasource lock "
        + dataSource.getDataSourceId());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#init(java.lang.String)
   */
  public String init(final String dataSourceID) throws DeltaIndexingException {
    if (dataSourceID == null) {
      throw new DeltaIndexingException("parameter dataSourceID is null");
    }

    synchronized (_dataSources) {
      if (!_dataSources.containsKey(dataSourceID)) {
        final DataSourceConnection dataSource = new DataSourceConnection(dataSourceID);
        _dataSources.put(dataSourceID, dataSource);
      }

      final DataSourceConnection dataSource = _dataSources.get(dataSourceID);
      final String sessionId = UUID.randomUUID().toString();
      dataSource.lock(sessionId);
      return sessionId;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see {@link org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#visit(String, Id, String, boolean)}
   */
  public void visit(final String sessionId, final Id id, final String hash, boolean isCompound)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }
    if (hash == null) {
      throw new DeltaIndexingException("parameter hash is null");
    }

    if (_log.isDebugEnabled()) {
      _log.debug("Visiting " + id.getIdHash());
    }
    final DataSourceConnection dataSource = findDataSource(sessionId, id.getSource());
    dataSource.put(id, hash, isCompound);

    // check if this is a sub compound
    if (isCompound) {
      try {
        final Id parentId = id.createCompoundId();
        dataSource.addSubCompound(parentId, id);
      } catch (IdHandlingException e) {
        ; // nothing to do
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see {@link org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#obsoleteIdIterator(String, String)}
   */
  public Iterator<Id> obsoleteIdIterator(final String sessionId, final String dataSourceID)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    if (dataSourceID == null) {
      throw new DeltaIndexingException("parameter dataSourceID is null");
    }
    final DataSourceConnection dataSource = findDataSource(sessionId, dataSourceID);
    return dataSource.obsoleteIdIterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see {@link org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#rollback(String)}
   */
  public void rollback(final String sessionId) throws DeltaIndexingException {
    throw new UnsupportedOperationException("Not Implemented yet. Use case and workflow are not specified!");
    // sample implementation
    /*
     * if (dataSourceID == null) { throw new DeltaIndexingException("parameter dataSourceID is null"); } final
     * DataSourceConnection dataSource = findDataSource(dataSourceID); dataSource.rollback();
     */
  }

  /**
   * {@inheritDoc}
   * 
   * @throws DeltaIndexingException
   * 
   * @see {@link org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#delete(String, Id)}
   */
  public void delete(final String sessionId, final Id id) throws DeltaIndexingSessionException,
    DeltaIndexingException {
    if (id == null) {
      throw new DeltaIndexingException("parameter id is null");
    }

    final DataSourceConnection dataSource = findDataSource(sessionId, id.getSource());
    dataSource.delete(id);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager# obsoleteIdIterator(String, Id)
   */
  public Iterator<Id> obsoleteIdIterator(final String sessionId, final Id id) {
    throw new UnsupportedOperationException("Not Implemented because working with fragments is not clear!");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#unlockDatasource(String)
   */
  public void unlockDatasource(final String dataSourceID) throws DeltaIndexingException {
    synchronized (_dataSources) {
      try {
        final DataSourceConnection connection = _dataSources.get(dataSourceID);
        if (connection != null) {
          connection.forceUnlockAndRollback();
        }
      } catch (final Throwable ex) {
        _log.error(String.format(ERROR_DURING_UNLOCK, dataSourceID), ex);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#unlockDatasources()
   */
  public void unlockDatasources() {
    synchronized (_dataSources) {
      for (final String dataSourceId : _dataSources.keySet()) {
        try {
          final DataSourceConnection connection = _dataSources.get(dataSourceId);
          connection.forceUnlockAndRollback();
        } catch (final Throwable ex) {
          _log.error(String.format(ERROR_DURING_UNLOCK, dataSourceId), ex);
        }
      } // for
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getLockStates()
   */
  public Map<String, LockState> getLockStates() {
    final HashMap<String, LockState> lockMap = new HashMap<String, LockState>();
    for (final String dataSourceId : _dataSources.keySet()) {
      try {
        final DataSourceConnection connection = _dataSources.get(dataSourceId);
        LockState state = LockState.UNLOCKED;
        if (connection != null) {
          if (connection.getSessionId() != null) {
            state = LockState.LOCKED;
          }
        }
        lockMap.put(dataSourceId, state);
      } catch (final Throwable ex) {
        _log.error(String.format(ERROR_DURING_UNLOCK, dataSourceId), ex);
      }
    } // for
    return lockMap;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#clear()
   */
  public void clear() throws DeltaIndexingException {
    synchronized (_dataSources) {
      for (final String dataSourceId : _dataSources.keySet()) {
        try {
          final DataSourceConnection connection = _dataSources.get(dataSourceId);
          connection.forceUnlockAndClear();
        } catch (final Throwable ex) {
          _log.error(String.format(ERROR_DURING_GLOBAL_CLEAR, dataSourceId), ex);
        }
      }
      _dataSources.clear();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#clear(java.lang.String)
   */
  public void clear(final String sessionId) throws DeltaIndexingSessionException, DeltaIndexingException {
    final DataSourceConnection dataSource = findDataSourceBySession(sessionId);
    dataSource.clear();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#exists(java.lang.String)
   */
  public boolean exists(final String dataSourceId) {
    return _dataSources.containsKey(dataSourceId);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getEntryCount(String)
   */
  public long getEntryCount(final String dataSourceId) {
    final DataSourceConnection connection = _dataSources.get(dataSourceId);
    if (connection == null) {
      return 0;
    }
    return connection.getEntryCount();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager#getEntryCounts()
   */
  public Map<String, Long> getEntryCounts() {
    final HashMap<String, Long> entryCounts = new HashMap<String, Long>();
    for (final String dataSourceId : _dataSources.keySet()) {
      try {
        final DataSourceConnection connection = _dataSources.get(dataSourceId);
        long count = 0;
        if (connection != null) {
          count = connection.getEntryCount();
        }
        entryCounts.put(dataSourceId, count);
      } catch (final Throwable ex) {
        _log.error(String.format(ERROR_DURING_GET_ENTRY_COUNTS, dataSourceId), ex);
      }
    } // for
    return entryCounts;
  }

  /**
   * Find DataSourceConnection by sessionId.
   * 
   * @param sessionId
   *          the id of the session
   * @return the DataSourceConnection
   * @throws DeltaIndexingSessionException
   *           if the session is invalid
   */
  private DataSourceConnection findDataSourceBySession(final String sessionId) throws DeltaIndexingSessionException {
    if (sessionId == null) {
      throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
    }

    final Iterator<DataSourceConnection> it = _dataSources.values().iterator();
    while (it.hasNext()) {
      final DataSourceConnection dsc = it.next();
      if (dsc != null && sessionId.equals(dsc.getSessionId())) {
        return dsc;
      }
    }
    throw new DeltaIndexingSessionException("Invalid session id: " + sessionId);
  }

  /**
   * Find DataSourceConnection by sessionId.
   * 
   * @param sessionId
   *          the id of the session
   * @param dataSourceID
   *          the data source id
   * @return the DataSourceConnection
   * @throws DeltaIndexingSessionException
   *           if the session is invalid
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private DataSourceConnection findDataSource(final String sessionId, final String dataSourceID)
    throws DeltaIndexingSessionException, DeltaIndexingException {
    final DataSourceConnection dsc = findDataSourceBySession(sessionId);
    if (!dsc.getDataSourceId().equals(dataSourceID)) {
      throw new DeltaIndexingException("Invalid data source id " + dataSourceID + " for session id " + sessionId);
    }
    return dsc;
  }

  /**
   * Visits all SubCompounds of the given Id if any exist.
   * 
   * @param dataSource
   *          the DataSourceConnection
   * @param id
   *          the Id to visit the sub compounds for
   * @throws DeltaIndexingException
   *           if any error occurs
   */
  private void visitSubCompounds(final DataSourceConnection dataSource, final Id id) throws DeltaIndexingException {
    final Set<Id> subCompounds = dataSource.getSubCompounds(id);
    if (subCompounds != null) {
      for (Id subId : subCompounds) {
        // mark as visited
        dataSource.put(subId);
        visitSubCompounds(dataSource, subId);
      } // for
    } // if
  }
}
