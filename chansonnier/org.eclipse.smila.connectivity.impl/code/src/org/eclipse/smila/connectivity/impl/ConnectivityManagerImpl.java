/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * The Class ConnectivityManagerImpl.
 */
public class ConnectivityManagerImpl implements ConnectivityManager {

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(ConnectivityManagerImpl.class);

  /**
   * The record factory.
   */
  private final RecordFactory _recordFactory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * reference to the RouterService.
   */
  private Router _router;

  /**
   * Default Constructor.
   */
  public ConnectivityManagerImpl() {
    if (_log.isTraceEnabled()) {
      _log.trace("Creating ConnectivityManagerImpl");
    }
  }

  /**
   * Sets the RouterService. Used by OSGi.
   * 
   * @param router
   *          the RouterService to set
   */
  public void setRouterService(final Router router) {
    if (_log.isTraceEnabled()) {
      _log.trace("Binding RouterService");
    }
    _router = router;
  }

  /**
   * Unsets the RouterService. Used by OSGi.
   * 
   * @param router
   *          the RouterService to unset
   */
  public void unsetRouterService(final Router router) {
    if (_log.isTraceEnabled()) {
      _log.trace("Unbinding RouterService");
    }
    if (_router == router) {
      _router = null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.ConnectivityManager#add(org.eclipse.smila.datamodel.record.Record[])
   */
  public int add(final Record[] records) throws ConnectivityException {
    int count = 0;
    if (records != null) {
      try {
        // pass records to router
        final Map<Id, Exception> exceptionMap = _router.route(records, Operation.ADD);
        count = records.length - exceptionMap.size();
      } catch (final Exception e) {
        final String msg = "Error while adding records";
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
        throw new ConnectivityException(msg, e);
      }
    } // if
    return count;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.ConnectivityManager#delete(org.eclipse.smila.datamodel.id.ID[])
   */
  public int delete(final Id[] ids) throws ConnectivityException {
    int count = 0;
    if (ids != null) {
      // build array of Records
      final Record[] records = new Record[ids.length];
      for (int i = 0; i < ids.length; i++) {
        records[i] = _recordFactory.createRecord();
        records[i].setId(ids[i]);
      } // for
      count = delete(records);
    } // if

    return count;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.ConnectivityManager#delete(org.eclipse.smila.datamodel.id.ID[])
   */
  public int delete(final Record[] records) throws ConnectivityException {
    int count = 0;
    if (records != null && records.length > 0) {
      try {
        // pass records to router
        final Map<Id, Exception> exceptionMap = _router.route(records, Operation.DELETE);
        count = records.length - exceptionMap.size();
      } catch (final Exception e) {
        final String msg = "Error while deleting records";
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
        throw new ConnectivityException(msg, e);
      }
    } // if

    return count;
  }
}
