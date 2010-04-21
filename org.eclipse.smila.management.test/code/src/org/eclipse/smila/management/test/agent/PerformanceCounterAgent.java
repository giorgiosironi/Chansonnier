/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.test.agent;

import org.eclipse.smila.management.DeclarativeManagementAgent;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class PerformanceCounterAgent.
 */
public class PerformanceCounterAgent extends DeclarativeManagementAgent {

  /**
   * The _simple.
   */
  private final PerformanceCounter _simple = new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _rate.
   */
  private final PerformanceCounter _rate = new PerformanceCounter(PerformanceCounterFormula.RATE_FORMULA);

  /**
   * The _average.
   */
  private final PerformanceCounter _average = new PerformanceCounter(PerformanceCounterFormula.AVERAGE_FORMULA);

  /**
   * The _average timer.
   */
  private final PerformanceCounter _averageTimer =
    new PerformanceCounter(PerformanceCounterFormula.AVERAGE_TIMER_FORMULA);

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getCategory()
   */
  @Override
  protected String getCategory() {
    return "Test";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getName()
   */
  @Override
  protected String getName() {
    return "PerformanceCounterAgent";
  }

  /**
   * Gets the simple.
   * 
   * @return the simple
   */
  public PerformanceCounter getSimple() {
    return _simple;
  }

  /**
   * Gets the rate.
   * 
   * @return the rate
   */
  public PerformanceCounter getRate() {
    return _rate;
  }

  /**
   * Gets the average.
   * 
   * @return the average
   */
  public PerformanceCounter getAverage() {
    return _average;
  }

  /**
   * Gets the average timer.
   * 
   * @return the average timer
   */
  public PerformanceCounter getAverageTimer() {
    return _averageTimer;
  }

}
