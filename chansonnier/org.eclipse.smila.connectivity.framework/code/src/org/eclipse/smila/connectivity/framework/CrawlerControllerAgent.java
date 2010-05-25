/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Map;

/**
 * The Interface CrawlerControllerAgent.
 */
public interface CrawlerControllerAgent {

  /**
   * Start a new crawler task.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return the string
   */
  String startCrawlerTask(final String dataSourceId);

  /**
   * Stop a crawler task.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return the string
   */
  String stopCrawlerTask(final String dataSourceId);
    
  /**
   * Gets the status of all crawler tasks as a map of data source id and crawler state.
   * 
   * @return a map of data source id and crawler state.
   */
  Map<String, String> getCrawlerTasksState();

  /**
   * returns all Crawlers that have connected to the CrawlerController.
   * 
   * @return List with Strings of all available Crawlers
   */
  String[] getAvailableCrawlers();

  /**
   * returns all available Crawler data source configurations.
   * 
   * @return List with Strings of all available Crawler data source configurations
   */
  String[] getAvailableCrawlerTasks();
}
