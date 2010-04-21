/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.management.DeclarativeServiceManagementAgent;

/**
 * The Class CrawlerControllerAgent.
 */
public abstract class CrawlerControllerAgentBase extends DeclarativeServiceManagementAgent<CrawlerController>
  implements CrawlerControllerAgent {

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(CrawlerControllerAgentBase.class);

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
    return "CrawlerController";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerControllerAgent#startCrawlerTask(java.lang.String)
   */
  public String startCrawlerTask(final String dataSourceId) {
    try {
      final int hashcode = _service.startCrawl(dataSourceId);
      return "Crawler with the dataSourceId = " + dataSourceId + " and hashcode [" + hashcode + "]"
        + " successfully started!";

    } catch (final ConnectivityException exception) {
      if (_log.isErrorEnabled()) {
        _log.error(exception);
      }
      return getExceptionText(exception);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerControllerAgent#stopCrawlerTask(java.lang.String)
   */
  public String stopCrawlerTask(final String dataSourceId) {
    try {
      _service.stopCrawl(dataSourceId);
      return "Crawl with the dataSourceId = " + dataSourceId + " successfully stopped.";
    } catch (final ConnectivityException exception) {
      if (_log.isErrorEnabled()) {
        _log.error(exception);
      }
      return getExceptionText(exception);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerControllerAgent#getAgentTasksState()
   */
  public Map<String, String> getCrawlerTasksState() {
    final HashMap<String, String> states = new HashMap<String, String>();
    final Map<String, CrawlState> crawlStates = _service.getCrawlerTasksState();
    final Iterator<String> it = crawlStates.keySet().iterator();
    while (it.hasNext()) {
      final String dataSourceId = it.next();
      states.put(dataSourceId, crawlStates.get(dataSourceId).getState().name());
    }
    return states;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerControllerAgent#getAvailableCrawlers()
   */
  public String[] getAvailableCrawlers() {
    final Collection<String> availCrawlers = _service.getAvailableCrawlers();
    return availCrawlers.toArray(new String[availCrawlers.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.CrawlerControllerAgent#getAvailableCrawlerTasks()
   */
  public String[] getAvailableCrawlerTasks() {
    final Collection<String> configs = _service.getAvailableConfigurations();
    return configs.toArray(new String[configs.size()]);
  }

  /**
   * Returns the text of the exception plus any additional text from the exception's cause.
   * 
   * @param t
   *          the Throwable
   * @return the exception text
   */
  private String getExceptionText(final Throwable t) {
    String text = t.getMessage();
    final Throwable cause = t.getCause();
    if (cause != null) {
      text += ": " + cause.toString();
    }
    return text;
  }
}
