/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.test;

import org.eclipse.smila.management.ManagementRegistration;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.test.agent.PerformanceCounterAgent;
import org.eclipse.smila.test.JmxTestCase;

/**
 * The Class PerformanceCounterTest.
 */
public class PerformanceCounterRemoteTest extends JmxTestCase {

  /**
   * The Constant AGENT_PATH.
   */
  private static final String AGENT_PATH = "Test/PerformanceCounterAgent";

  /**
   * The Constant D_5.
   */
  private static final double D_5 = 5.0;

  /**
   * The Constant I_5.
   */
  private static final int I_5 = 5;

  /**
   * The Constant D_10.
   */
  private static final double D_10 = 10.0;

  /**
   * The Constant I_10.
   */
  private static final int I_10 = 10;

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
    ManagementRegistration.INSTANCE.registerAgent(_agent);
    _agent = (PerformanceCounterAgent) ManagementRegistration.INSTANCE.getAgent(AGENT_PATH);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    ManagementRegistration.INSTANCE.unregisterAgent(_agent);
    _agent = null;
    super.tearDown();
  }

  /**
   * Test remote counter.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRemoteCounter() throws Exception {
    final PerformanceCounter counter = _agent.getSimple();
    double value = getRemoteValue();
    assertEquals(0.0, value);
    counter.increment();
    value = getRemoteValue();
    assertEquals(1.0, value);
    counter.decrement();
    value = getRemoteValue();
    assertEquals(0.0, value);
    counter.incrementBy(I_10);
    value = getRemoteValue();
    assertEquals(D_10, value);
    counter.decrementBy(I_5);
    value = getRemoteValue();
    assertEquals(D_5, value);
  }

  /**
   * Gets the remote value.
   * 
   * @return the remote value
   */
  private Double getRemoteValue() {
    return (Double) attribute(AGENT_PATH, "Simple");
  }
}
