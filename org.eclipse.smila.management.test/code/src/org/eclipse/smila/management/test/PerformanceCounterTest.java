/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.test;

import junit.framework.TestCase;

import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceSample;
import org.eclipse.smila.management.test.agent.PerformanceCounterAgent;

/**
 * The Class PerformanceCounterTest.
 */
public class PerformanceCounterTest extends TestCase {

  /**
   * The Constant C_15_0.
   */
  private static final double C_15_0 = 15.0;

  /**
   * The Constant C_30_0.
   */
  private static final double C_30_0 = 30.0;

  /**
   * The Constant C_20.
   */
  private static final int C_20 = 20;

  /**
   * The Constant C_3000.
   */
  private static final int C_3000 = 3000;

  /**
   * The Constant C_2000.
   */
  private static final int C_2000 = 2000;

  /**
   * The Constant C_10.
   */
  private static final int C_10 = 10;

  /**
   * The Constant C_1000.
   */
  private static final int C_1000 = 1000;

  /**
   * The _agent.
   */
  private PerformanceCounterAgent _agent;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _agent = new PerformanceCounterAgent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _agent = null;
    super.tearDown();
  }

  /**
   * Test simple count formula.
   */
  public void testSimpleCountFormula() {
    final PerformanceCounter counter = _agent.getSimple();
    counter.incrementBy(C_10);
    counter.incrementBy(C_20);
    counter.getNextPerformanceSample();
    assertEquals(C_30_0, counter.getNextValue());

    // additional tests with that counter
    counter.decrement();
    counter.getNextPerformanceSample();
    assertEquals(C_30_0 - 1, counter.getNextValue());
    counter.setRawValue(C_10);
    assertEquals((double) C_10, counter.getNextValue());
  }

  /**
   * Test average formula.
   */
  public void testAverageFormula() {
    final PerformanceCounter counter = _agent.getAverage();
    counter.incrementBy(C_10);
    counter.incrementBy(C_20);
    counter.getNextPerformanceSample();
    assertEquals(counter.getNextValue(), C_15_0);
  }

  /**
   * Test rate formula.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testRateFormula() throws InterruptedException {
    final PerformanceCounter counter = _agent.getRate();
    counter.incrementBy(C_1000);
    Thread.sleep(C_10);
    counter.incrementBy(C_2000);
    final PerformanceSample sample2 = counter.getNextPerformanceSample();
    assertEquals(sample2.getBaseValue(), 2);
    assertEquals(sample2.getRawValue(), C_3000);
  }

  /**
   * Test average timer formula.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testAverageTimerFormula() throws InterruptedException {
    final PerformanceCounter counter = _agent.getAverageTimer();
    counter.incrementBy(C_1000);
    Thread.sleep(C_10);
    counter.incrementBy(C_2000);
    final PerformanceSample sample2 = counter.getNextPerformanceSample();
    assertEquals(sample2.getBaseValue(), 2);
    assertEquals(sample2.getRawValue(), C_3000);
  }

  /**
   * Test counter illegal arguments exceptions.
   */
  public void testCounterConstructorIllegalArgumentsEx() {
    PerformanceCounter counter = null;
    try {
      counter = new PerformanceCounter(null);
      throw new AssertionError();
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
    }
    assertNull(counter);
  }

}
