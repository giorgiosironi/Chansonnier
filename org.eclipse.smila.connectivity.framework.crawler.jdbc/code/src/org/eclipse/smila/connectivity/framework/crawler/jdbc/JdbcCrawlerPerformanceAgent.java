/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc;

import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceAgent;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class JdbcCrawlerPerformanceAgent.
 */
public class JdbcCrawlerPerformanceAgent extends CrawlerPerformanceAgent {

  /**
   * The _database rows.
   */
  private final PerformanceCounter _databaseRows =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _producer exceptions.
   */
  private final PerformanceCounter _producerExceptions =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _producer critical exceptions.
   */
  private final PerformanceCounter _producerCriticalExceptions =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _data refs created.
   */
  private final PerformanceCounter _dataRefsCreated =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _records created.
   */
  private final PerformanceCounter _recordsCreated =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _data refs retrieved by client.
   */
  private final PerformanceCounter _dataRefsRetrievedByClient =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * Gets the database rows.
   * 
   * @return the database rows
   */
  public PerformanceCounter getDatabaseRows() {
    return _databaseRows;
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
   * Gets the producer critical exceptions.
   * 
   * @return the producer critical exceptions
   */
  public PerformanceCounter getProducerCriticalExceptions() {
    return _producerCriticalExceptions;
  }

  /**
   * Gets the data refs created.
   * 
   * @return the data refs created
   */
  public PerformanceCounter getDataRefsCreated() {
    return _dataRefsCreated;
  }

  /**
   * Gets the records created.
   * 
   * @return the records created
   */
  public PerformanceCounter getRecordsCreated() {
    return _recordsCreated;
  }

  /**
   * Gets the data refs retrieved by client.
   * 
   * @return the data refs retrieved by client
   */
  public PerformanceCounter getDataRefsRetrievedByClient() {
    return _dataRefsRetrievedByClient;
  }

}
