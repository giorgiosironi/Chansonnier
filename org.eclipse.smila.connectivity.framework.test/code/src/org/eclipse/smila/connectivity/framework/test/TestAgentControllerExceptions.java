/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Test IO Exceptions of AgentController.
 * 
 */
public class TestAgentControllerExceptions extends DeclarativeServiceTestCase {

  /**
   * Constant for thread sleep time.
   */
  private static final int SLEEP_TIME = 3000;

  /**
   * the ConnectivityManager.
   */
  private AgentController _agentController;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _agentController = getService(AgentController.class);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _agentController = null;
  }

  /**
   * Test {@link AgentController#startAgent(String)}. Should throw a NullPointerException if called with parameter null.
   * Should throw a ConnectivityException if called with a nonexisting dataSourceId.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartAgent() throws Exception {
    try {
      assertNotNull(_agentController);
      _agentController.startAgent(null);
      fail("expected NullPointerException");
    } catch (final NullPointerException e) {
      assertNotNull(e);
    }

    final String dataSourceId = "notExistingDataSource";
    try {
      assertNotNull(_agentController);
      _agentController.startAgent(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test {@link AgentController#stopAgent(String)}. Should throw a NullPointerException if called with parameter null.
   * Should throw a ConnectivityException if called with a nonexisting dataSourceId.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStopAgent() throws Exception {
    try {
      assertNotNull(_agentController);
      _agentController.stopAgent(null);
      fail("expected NullPointerException");
    } catch (final NullPointerException e) {
      assertNotNull(e);
    }

    final String dataSourceId = "notExistingDataSource";
    try {
      assertNotNull(_agentController);
      _agentController.startAgent(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test parallel agent execution.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testStartParallelAgent() throws Exception {
    final AgentController agentController = getService(AgentController.class);

    final String dataSourceId = "mockAgent";
    agentController.startAgent(dataSourceId);
    try {
      agentController.startAgent(dataSourceId);
      fail("expected ConnectivityException");
    } catch (final ConnectivityException e) {
      assertNotNull(e);
    }
    // wait and stop agent
    Thread.sleep(SLEEP_TIME);
    agentController.stopAgent(dataSourceId);
  }

}
