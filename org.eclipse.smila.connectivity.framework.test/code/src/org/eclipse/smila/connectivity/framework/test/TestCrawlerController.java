/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import java.util.Collection;
import java.util.Map;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.framework.CrawlState;
import org.eclipse.smila.connectivity.framework.CrawlerController;
import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestCrawlerController.
 */
public class TestCrawlerController extends DeclarativeServiceTestCase {

  /**
   * Constant for thread sleep time.
   */
  private static final int SLEEP_TIME = 3000;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    /*forceStartBundle("org.eclipse.osgi.services");
    forceStartBundle("org.eclipse.update.configurator");
    forceStartBundle("org.eclipse.equinox.ds");
    forceStartBundle("org.eclipse.smila.blackboard");
    forceStartBundle("org.eclipse.smila.processing");
    forceStartBundle("org.eclipse.smila.processing.bpel");
    forceStartBundle("org.eclipse.smila.connectivity.queue.broker.main");
    forceStartBundle("org.eclipse.smila.jms");
    forceStartBundle("org.eclipse.smila.jms.activemq");
    forceStartBundle("org.eclipse.smila.connectivity.queue.worker.jms");*/
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
  }

  /**
   * Test {@link CrawlerController#hasActiveCrawls()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testHasActiveCrawls() throws Exception {
    final CrawlerController crawlerController = getService(CrawlerController.class);

    final boolean hasActiveCrawls = crawlerController.hasActiveCrawls();
    assertFalse(hasActiveCrawls);
  }

  /**
   * Test {@link CrawlerController#getAgentTasksState()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetCrawlerTasksState() throws Exception {
    final CrawlerController crawlerController = getService(CrawlerController.class);

    final Map<String, CrawlState> crawlerTasksState = crawlerController.getCrawlerTasksState();
    assertNotNull(crawlerTasksState);
    assertEquals(0, crawlerTasksState.size());
  }

  /**
   * Test {@link CrawlerController#getAvailableConfigurations()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetAvailableConfigurations() throws Exception {
    final CrawlerController crawlerController = getService(CrawlerController.class);

    final Collection<String> configs = crawlerController.getAvailableConfigurations();
    assertNotNull(configs);
    assertEquals(2, configs.size());
  }

  /**
   * Test a Crawl run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartCrawl() throws Exception {
    final String dataSourceId = "FilesystemConfigExample.xml";
    testStartCrawl(dataSourceId, false);
  }

  /**
   * Test a Crawl run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStopCrawl() throws Exception {
    final String dataSourceId = "FilesystemConfigExample.xml";
    testStartCrawl(dataSourceId, true);
  }

  /**
   * Test a Crawl run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartCrawlCompounds() throws Exception {
    final String dataSourceId = "ZipConfigExample.xml";
    testStartCrawl(dataSourceId, false);
  }

  /**
   * Test a Crawl run.
   * 
   * @param dataSourceId
   *          the id of the data source
   * @param interrupt
   *          the interrupt
   * 
   * @throws Exception
   *           the Exception
   */
  private void testStartCrawl(final String dataSourceId, boolean interrupt) throws Exception {
    /*final DeltaIndexingManager deltaIndexingManager = getService(DeltaIndexingManager.class);
    deltaIndexingManager.unlockDatasources();*/
    
    final CrawlerController crawlerController = getService(CrawlerController.class);
    crawlerController.startCrawl(dataSourceId);

    boolean hasActiveCrawls = crawlerController.hasActiveCrawls();
    assertTrue(hasActiveCrawls);

    Map<String, CrawlState> crawlStates = crawlerController.getCrawlerTasksState();
    assertNotNull(crawlStates);
    CrawlState crawlState = crawlStates.get(dataSourceId);
    assertNotNull(crawlState);
    assertEquals(CrawlThreadState.Running, crawlState.getState());
    assertNull(crawlState.getLastError());

    if (interrupt) {
      // stop crawler
      crawlerController.stopCrawl(dataSourceId);
      Thread.sleep(SLEEP_TIME);
    } else {
      // wait for crawl to finish
      Thread.sleep(SLEEP_TIME);
      while (CrawlThreadState.Running.equals(crawlStates.get(dataSourceId).getState())) {
        Thread.sleep(SLEEP_TIME);
        crawlStates = crawlerController.getCrawlerTasksState();
      }
    }

    hasActiveCrawls = crawlerController.hasActiveCrawls();
    assertFalse(hasActiveCrawls);

    crawlStates = crawlerController.getCrawlerTasksState();
    assertNotNull(crawlStates);
    crawlState = crawlStates.get(dataSourceId);
    assertNotNull(crawlState);
    if (interrupt) {
      assertEquals(CrawlThreadState.Stopped, crawlState.getState());
    } else {
      assertEquals(CrawlThreadState.Finished, crawlState.getState());
    }
    assertNull(crawlState.getLastError());
  }
}
