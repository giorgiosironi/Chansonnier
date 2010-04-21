/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.framework.CrawlState;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.CrawlerController;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;
import org.eclipse.smila.connectivity.framework.util.CrawlerControllerCallback;
import org.osgi.service.component.ComponentContext;

/**
 * Basic Implementation of a CrawlerController.
 */
public class CrawlerControllerImpl extends AbstractController implements CrawlerController,
  CrawlerControllerCallback {

  /** The Constant BUNDLE_ID. */
  private static final String BUNDLE_ID = "org.eclipse.smila.connectivity.framework";

  /**
   * Maximum time in milliseconds to wait for a CrawlThread to join during deactivation.
   */
  private static final long CRAWL_THREAD_JOIN_TIME = 5000;

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(CrawlerControllerImpl.class);

  /**
   * A Map of active CrawlThreads.
   */
  private final java.util.Map<String, CrawlThread> _crawlThreads;

  /**
   * A Map of CrawlStates.
   */
  private final java.util.Map<String, CrawlState> _crawlStates;

  /**
   * Default Constructor.
   */
  public CrawlerControllerImpl() {
    if (_log.isTraceEnabled()) {
      _log.trace("Creating CrawlerControllerImpl");
    }
    _crawlThreads = new HashMap<String, CrawlThread>();
    _crawlStates = new HashMap<String, CrawlState>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#startCrawl(java.lang.String)
   */
  public int startCrawl(final String dataSourceId) throws ConnectivityException {
    // check parameters
    if (dataSourceId == null) {
      final String msg = "Parameter dataSourceId is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }

    // check if data source is already crawled
    if (_crawlThreads.containsKey(dataSourceId)) {
      throw new ConnectivityException("Can't start a new crawl for DataSourceId '" + dataSourceId
        + "'. It is already crawled by another process.");
    }

    try {
      final DataSourceConnectionConfig configuration = getConfiguration(BUNDLE_ID, dataSourceId);
      final Crawler crawler = createInstance(Crawler.class, configuration.getDataConnectionID().getId());
      final int jobId = crawler.hashCode();

      // initialize the CrawlState
      final CrawlState crawlState = new CrawlState();
      crawlState.setDataSourceId(dataSourceId);
      crawlState.setState(CrawlThreadState.Running);
      crawlState.setStartTime(System.currentTimeMillis());
      crawlState.setJobId(Integer.toString(jobId));
      _crawlStates.put(dataSourceId, crawlState);

      // initialize the CrawlThread
      final CrawlThread crawlThread =
        new CrawlThread(this, crawlState, getConnectivityManager(), getDeltaIndexingManager(),
          getCompoundManager(), crawler, configuration);
      _crawlThreads.put(dataSourceId, crawlThread);

      // start CrawlThread
      crawlThread.start();
      return jobId;
    } catch (final ConnectivityException e) {
      throw e;
    } catch (final Exception e) {
      final String msg = "Error during executeCrawl of DataSourceId '" + dataSourceId + "'";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new ConnectivityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#stopCrawl(java.lang.String)
   */
  public void stopCrawl(final String dataSourceId) throws ConnectivityException {
    // check parameters
    if (dataSourceId == null) {
      final String msg = "Parameter dataSourceId is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }

    final CrawlThread crawlThread = _crawlThreads.get(dataSourceId);
    if (crawlThread == null) {
      final String msg = "Could not stop crawl for DataSourceId '" + dataSourceId + "'. No CrawlThread exists.";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new ConnectivityException(msg);
    }

    crawlThread.stopCrawl();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#hasActiveCrawls()
   */
  public boolean hasActiveCrawls() throws ConnectivityException {
    return !_crawlThreads.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#getAgentTasksState()
   */
  public Map<String, CrawlState> getCrawlerTasksState() {
    final HashMap<String, CrawlState> states = new HashMap<String, CrawlState>();
    states.putAll(_crawlStates);
    return states;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.CrawlerControllerCallback#unregister(java.lang.String)
   */
  public void unregister(final String dataSourceId) {
    _crawlThreads.remove(dataSourceId);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#getAvailableCrawlers()
   */
  public Collection<String> getAvailableCrawlers() {
    return getAvailableFactories();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerController#getAvailableConfigurations()
   */
  public Collection<String> getAvailableConfigurations() {
    return getConfigurations(BUNDLE_ID, DataConnectionType.CRAWLER);
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
    if (_log.isInfoEnabled()) {
      _log.info("Deactivating CrawlerController");
    }
    _lock.writeLock().lock();
    try {
      // stop CrawlThreads
      Iterator<Map.Entry<String, CrawlThread>> it = _crawlThreads.entrySet().iterator();
      while (it.hasNext()) {
        final Map.Entry<String, CrawlThread> entry = it.next();
        try {
          if (entry.getValue() != null) {
            entry.getValue().stopCrawl();
          }
        } catch (Exception e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error stopping CrawlThread for data source " + entry.getKey(), e);
          }
        }
      }
      // stop CrawlThreads
      it = _crawlThreads.entrySet().iterator();
      while (it.hasNext()) {
        final Map.Entry<String, CrawlThread> entry = it.next();
        try {
          if (entry.getValue() != null) {
            entry.getValue().join(CRAWL_THREAD_JOIN_TIME);
          }
        } catch (Exception e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error joining CrawlThread for data source " + entry.getKey(), e);
          }
        }
      }
    } finally {
      // Thread.sleep(5000);
      _lock.writeLock().unlock();
    }
  }
}
