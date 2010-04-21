/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Collection;
import java.util.Map;

import org.eclipse.smila.connectivity.ConnectivityException;

/**
 * Management interface for the CrawlerController.
 */
public interface CrawlerController {

  /**
   * The Performance AGENT location.
   */
  String PERFORMANCE_AGENT_LOCATION = "Crawlers/Total";

  /**
   * Starts a crawl of the given dataSourceId. This method creates a new Thread. If it is called for a dataSourceId that
   * is currently crawled a ConnectivityException is thrown. Returns an id for this job, the hashCode of the crawler
   * instance used for performance counter.
   * 
   * @param dataSourceId
   *          the ID of the data source to crawl
   * @return - the jobId (hashcode of the crawler instance as int value)
   * @throws ConnectivityException
   *           if any error occurs
   */
  int startCrawl(String dataSourceId) throws ConnectivityException;

  /**
   * Stops an active crawl of the given dataSourceId.
   * 
   * @param dataSourceId
   *          the ID of the data source to stop the crawl
   * @throws ConnectivityException
   *           if any error occurs
   */
  void stopCrawl(String dataSourceId) throws ConnectivityException;

  /**
   * Checks if there are any active crawls.
   * 
   * @return true if there are active crawls, false otherwise
   * @throws ConnectivityException
   *           if any error occurs
   */
  boolean hasActiveCrawls() throws ConnectivityException;

  /**
   * Gets the status of all crawler tasks as a map of data source id and crawler state.
   * 
   * @return a map of data source id and crawler state.
   */
  Map<String, CrawlState> getCrawlerTasksState();

  /**
   * returns the CrawlerController known Crawlers.
   * 
   * @return Collection with Strings
   */
  Collection<String> getAvailableCrawlers();

  /**
   * returns all available Crawler data source configurations.
   * 
   * @return List with Strings of all available Crawler data source configurations
   */
  Collection<String> getAvailableConfigurations();
}
