/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation Sebastian Voigt (Brox IT-Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.smila.connectivity.framework.CrawlerControllerAgent;
import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class CrawlerControllerAgent.
 */
public class TestCrawlerControllerAgent extends DeclarativeServiceTestCase {

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
    forceStartBundle("org.eclipse.osgi.services");
    forceStartBundle("org.eclipse.update.configurator");
    forceStartBundle("org.eclipse.equinox.ds");
    forceStartBundle("org.eclipse.smila.blackboard");
    forceStartBundle("org.eclipse.smila.processing");
    forceStartBundle("org.eclipse.smila.processing.bpel");
    forceStartBundle("org.eclipse.smila.connectivity.queue.broker.main");
    forceStartBundle("org.eclipse.smila.jms");
    forceStartBundle("org.eclipse.smila.jms.activemq");
    forceStartBundle("org.eclipse.smila.connectivity.queue.worker.jms");
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
   * Test error messages.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testErrorMessages() throws Exception {
    final CrawlerControllerAgent cca = getService(CrawlerControllerAgent.class);
    assertNotNull(cca);

    final String dataSourceId = "dummy";

    String msg = cca.startCrawlerTask(dataSourceId);
    assertNotNull(msg);
    assertEquals("Error loading DataSource with DataSourceId '" + dataSourceId
      + "': org.eclipse.smila.utils.config.ConfigurationLoadException: Unable to find configuration resource "
      + dataSourceId + ".xml in the bundle org.eclipse.smila.connectivity.framework", msg);

    try {
      msg = cca.startCrawlerTask(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Parameter dataSourceId is null", e.getMessage());
    }

    msg = cca.stopCrawlerTask(dataSourceId);
    assertNotNull(msg);
    assertEquals("Could not stop crawl for DataSourceId '" + dataSourceId + "'. No CrawlThread exists.", msg);

    try {
      msg = cca.stopCrawlerTask(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Parameter dataSourceId is null", e.getMessage());
    }
  }

  /**
   * Test {@link CrawlerControllerAgent#getActiveAgentTaskStatus()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetActiveCrawlerTaskStatus() throws Exception {
    // an earlier test already started a crawl of a different data source, therefore we expect status for 2 taksks
    final CrawlerControllerAgent cca = getService(CrawlerControllerAgent.class);
    assertNotNull(cca);
    final String dataSourceId = "FilesystemConfigExample.xml";

    cca.startCrawlerTask(dataSourceId);
    Map<String, String> crawlerTasksState = cca.getCrawlerTasksState();
    assertNotNull(crawlerTasksState);
    assertEquals(2, crawlerTasksState.size());
    assertEquals(CrawlThreadState.Running.name(), crawlerTasksState.get(dataSourceId));

    cca.stopCrawlerTask(dataSourceId);
    while (CrawlThreadState.Running.name().equals(crawlerTasksState.get(dataSourceId))) {
      crawlerTasksState = cca.getCrawlerTasksState();
      Thread.sleep(SLEEP_TIME);
    }

    assertNotNull(crawlerTasksState);
    assertEquals(2, crawlerTasksState.size());
    assertEquals(CrawlThreadState.Stopped.name(), crawlerTasksState.get(dataSourceId));
  }

  /**
   * Test {@link CrawlerControllerAgent#getAvailableCrawlerTasks()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetAvailableConfigurations() throws Exception {
    final CrawlerControllerAgent cca = getService(CrawlerControllerAgent.class);
    assertNotNull(cca);

    final String[] configs = cca.getAvailableCrawlerTasks();
    assertNotNull(configs);
    assertEquals(2, configs.length);
  }

  /**
   * Test a Crawl run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartCrawl() throws Exception {
    testStartCrawl(false);
  }

  /**
   * Test a Crawl run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStopCrawl() throws Exception {
    testStartCrawl(true);
  }

  /**
   * Test a Crawl run.
   * 
   * @param interrupt
   *          the interrupt
   * 
   * @throws Exception
   *           the Exception
   */
  private void testStartCrawl(boolean interrupt) throws Exception {
    final CrawlerControllerAgent cca = getService(CrawlerControllerAgent.class);
    assertNotNull(cca);

    final String dataSourceId = "FilesystemConfigExample.xml";
    cca.startCrawlerTask(dataSourceId);

    Map<String, String> crawlStates = cca.getCrawlerTasksState();
    assertNotNull(crawlStates);
    assertEquals(CrawlThreadState.Running.toString(), crawlStates.get(dataSourceId));

    if (interrupt) {
      // stop crawler
      cca.stopCrawlerTask(dataSourceId);
      Thread.sleep(SLEEP_TIME);
    } else {
      // wait for crawl to finish
      Thread.sleep(SLEEP_TIME);
      while (CrawlThreadState.Running.toString().equals(crawlStates.get(dataSourceId))) {
        Thread.sleep(SLEEP_TIME);
        crawlStates = cca.getCrawlerTasksState();
      }
    }

    crawlStates = cca.getCrawlerTasksState();
    assertNotNull(crawlStates);
    if (interrupt) {
      assertEquals(CrawlThreadState.Stopped.toString(), crawlStates.get(dataSourceId));
    } else {
      assertEquals(CrawlThreadState.Finished.toString(), crawlStates.get(dataSourceId));
    }
  }

  /**
   * test the Function get the known Crawlers.
   * 
   * @throws Exception
   *           in case of weird problems
   */
  public void testAvailableCrawlers() throws Exception {
    final CrawlerControllerAgent cca = getService(CrawlerControllerAgent.class);
    final String[] availCrawlers = cca.getAvailableCrawlers();
    assert (availCrawlers.length == 2);
    final List<String> a = Arrays.asList(availCrawlers);
    assert (a.indexOf("FileSystemCrawler") > 0);
    assert (a.indexOf("WebCrawler") > 0);
  }

}
