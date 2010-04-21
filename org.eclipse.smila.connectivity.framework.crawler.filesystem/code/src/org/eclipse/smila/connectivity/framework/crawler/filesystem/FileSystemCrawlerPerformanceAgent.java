/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.filesystem;

import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceAgent;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class FileSystemCrawlerPerformanceAgent.
 */
public class FileSystemCrawlerPerformanceAgent extends CrawlerPerformanceAgent {

  /**
   * The _files.
   */
  private final PerformanceCounter _files = new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _folders.
   */
  private final PerformanceCounter _folders =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _producer exceptions.
   */
  private final PerformanceCounter _producerExceptions =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * Gets the files.
   * 
   * @return the files
   */
  public PerformanceCounter getFiles() {
    return _files;
  }

  /**
   * Gets the folders.
   * 
   * @return the folders
   */
  public PerformanceCounter getFolders() {
    return _folders;
  }

  /**
   * Gets the producer exceptions.
   * 
   * @return the producer exceptions
   */
  public PerformanceCounter getProducerExceptions() {
    return _producerExceptions;
  }

}
