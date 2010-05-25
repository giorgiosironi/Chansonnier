/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util;

/**
 * Interface for callbacks on the CrawlerController. This interface is used by CrawlThreads to unregister finished
 * CrawlThreads.
 * 
 */
public interface CrawlerControllerCallback extends ControllerCallback {

  /**
   * Removes a CrawlThread for the given DataSourceId from the list of active CrawlThreads.
   * 
   * @param dataSourceId
   *          the ID of the data source to crawl
   */
  void unregister(String dataSourceId);
}
