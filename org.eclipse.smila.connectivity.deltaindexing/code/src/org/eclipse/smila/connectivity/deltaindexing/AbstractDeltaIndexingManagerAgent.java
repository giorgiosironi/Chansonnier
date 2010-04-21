/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager.LockState;
import org.eclipse.smila.management.DeclarativeServiceManagementAgent;

/**
 * An abstract base class for DeltaIndexingManagerAgent implementations.
 */
public abstract class AbstractDeltaIndexingManagerAgent extends
  DeclarativeServiceManagementAgent<DeltaIndexingManager> implements DeltaIndexingManagerAgent {

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getCategory()
   */
  @Override
  protected String getCategory() {
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getName()
   */
  @Override
  protected String getName() {
    return "DeltaIndexing";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#clear(java.lang.String)
   */
  public String clear(final String dataSourceID) {
    try {
      checkDataSource(dataSourceID);
      final String sessionId = _service.init(dataSourceID);
      _service.clear(sessionId);
      _service.finish(sessionId);
      return "OK";
    } catch (final Throwable e) {
      _log.error(e);
      return getErrorMessage(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#clearAll()
   */
  public String clearAll() {
    try {
      _service.clear();
      return "OK";
    } catch (final Throwable e) {
      _log.error(e);
      return getErrorMessage(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#unlock(String)
   */
  public String unlock(final String dataSourceID) {
    try {
      checkDataSource(dataSourceID);
      _service.unlockDatasource(dataSourceID);
      return "OK";
    } catch (final Throwable e) {
      _log.error(e);
      return getErrorMessage(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#unlockAll()
   */
  public String unlockAll() {
    try {
      _service.unlockDatasources();
      return "OK";
    } catch (final Throwable e) {
      _log.error(e);
      return getErrorMessage(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#getLockStates()
   */
  public Map<String, String> getLockStates() {
    try {
      final HashMap<String, String> states = new HashMap<String, String>();
      final Map<String, LockState> lockStates = _service.getLockStates();
      final Iterator<String> it = lockStates.keySet().iterator();
      while (it.hasNext()) {
        final String dataSourceId = it.next();
        states.put(dataSourceId, lockStates.get(dataSourceId).name());
      }
      return states;
    } catch (final Throwable e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#getEntryCount(String)
   */
  public Object getEntryCount(final String dataSourceID) {
    try {
      checkDataSource(dataSourceID);
      return _service.getEntryCount(dataSourceID);
    } catch (final Throwable e) {
      _log.error(e);
      return getErrorMessage(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManagerAgent#getEntryCounts()
   */
  public Map<String, Long> getEntryCounts() {
    try {
      return _service.getEntryCounts();
    } catch (final Throwable e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * check if DeltaIndexingManager knows about the given data source.
   *
   * @param dataSourceID
   *          the data source id
   * @throws DeltaIndexingException
   *           if no such data source exists.
   */
  protected void checkDataSource(final String dataSourceID) throws DeltaIndexingException {
    if (!_service.exists(dataSourceID)) {
      throw new DeltaIndexingException(String.format("Unable to find index for datasource [%s]", dataSourceID));
    }
  }

  /**
   *
   * @param ex
   *          an exception
   * @return error description to return to client.
   */
  protected String getErrorMessage(final Throwable ex) {
    String message = ex.getMessage();
    if (message == null || message.length() == 0) {
      message = ex.toString();
    }
    return "Error: " + message;
  }
}
