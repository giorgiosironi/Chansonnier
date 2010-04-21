/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import java.util.Map;

import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.framework.CrawlState;
import org.eclipse.smila.connectivity.framework.CrawlerController;
import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Test IO Exceptions of CrawlerController.
 * 
 */
public class TestCrawlerControllerExceptions extends DeclarativeServiceTestCase {

  /**
   * Constant for thread sleep time.
   */
  private static final int SLEEP_TIME = 3000;

  /**
   * the ConnectivityManager.
   */
  private CrawlerController _crawlerController;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _crawlerController = getService(CrawlerController.class);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _crawlerController = null;
  }

  /**
   * Test {@link CrawlerController#startCrawl(String)}. Should throw a NullPointerException if called with parameter
   * null. Should throw a ConnectivityException if called with a nonexisting dataSourceId.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartCrawl() throws Exception {
    try {
      assertNotNull(_crawlerController);
      _crawlerController.startCrawl(null);
      fail("expected NullPointerException");
    } catch (final NullPointerException e) {
      assertNotNull(e);
    }

    final String dataSourceId = "notExistingDataSource";
    try {
      assertNotNull(_crawlerController);
      _crawlerController.startCrawl(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test {@link CrawlerController#stopCrawl(String)}. Should throw a NullPointerException if called with parameter
   * null. Should throw a ConnectivityException if called with a nonexisting dataSourceId.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStopCrawl() throws Exception {
    try {
      assertNotNull(_crawlerController);
      _crawlerController.stopCrawl(null);
      fail("expected NullPointerException");
    } catch (final NullPointerException e) {
      assertNotNull(e);
    }

    final String dataSourceId = "notExistingDataSource";
    try {
      assertNotNull(_crawlerController);
      _crawlerController.startCrawl(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test parallel crawling.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testStartParallelCrawl() throws Exception {
    final CrawlerController crawlerController = getService(CrawlerController.class);

    final String dataSourceId = "FilesystemConfigExample.xml";
    crawlerController.startCrawl(dataSourceId);
    try {
      crawlerController.startCrawl(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
    // wait for crawl to finish
    Thread.sleep(SLEEP_TIME);
    Map<String, CrawlState> crawlStates = crawlerController.getCrawlerTasksState();
    while (CrawlThreadState.Running.equals(crawlStates.get(dataSourceId).getState())) {
      Thread.sleep(SLEEP_TIME);
      crawlStates = crawlerController.getCrawlerTasksState();
    }
  }

}
