/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web;

import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceAgent;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class WebCrawlerPerformanceAgent.
 */
public class WebCrawlerPerformanceAgent extends CrawlerPerformanceAgent {

  /**
   * The _bytes.
   */
  private final PerformanceCounter _bytes = new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _pages.
   */
  private final PerformanceCounter _pages = new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _producer exceptions.
   */
  private final PerformanceCounter _producerExceptions =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _average http fetch time.
   */
  private final PerformanceCounter _averageHttpFetchTime =
    new PerformanceCounter(PerformanceCounterFormula.AVERAGE_FORMULA);

  /**
   * Gets the bytes.
   * 
   * @return the bytes
   */
  public PerformanceCounter getBytes() {
    return _bytes;
  }

  /**
   * Gets the pages.
   * 
   * @return the pages
   */
  public PerformanceCounter getPages() {
    return _pages;
  }

  /**
   * Gets the producer exceptions.
   * 
   * @return the producer exceptions
   */
  public PerformanceCounter getProducerExceptions() {
    return _producerExceptions;
  }

  /**
   * Gets the average http fetch time.
   * 
   * @return the average http fetch time
   */
  public PerformanceCounter getAverageHttpFetchTime() {
    return _averageHttpFetchTime;
  }
}
