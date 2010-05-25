/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;

/**
 * The Interface Crawler.
 */
public interface Crawler {

  /**
   * Returns the ID of this Crawler.
   * 
   * @return a String containing the ID of this Crawler
   * 
   * @throws CrawlerException
   *           if any error occurs
   */
  String getCrawlerId() throws CrawlerException;

  /**
   * Returns an array of DataReference objects. The size of the returned array may vary from call to call. The maximum
   * size of the array is determined by configuration or by the implementation class.
   * 
   * @return an array of DataReference objects or null, if no more DataReference exist
   * 
   * @throws CrawlerException
   *           if any error occurs
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  DataReference[] getNext() throws CrawlerException, CrawlerCriticalException;

  /**
   * Initialize.
   * 
   * @param config
   *          the DataSourceConnectionConfig
   * 
   * @throws CrawlerException
   *           the crawler exception
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  void initialize(final DataSourceConnectionConfig config) throws CrawlerException, CrawlerCriticalException;

  /**
   * Ends crawl, allowing the Crawler implementation to close any open resources.
   * 
   * @throws CrawlerException
   *           if any error occurs
   */
  void close() throws CrawlerException;

}
