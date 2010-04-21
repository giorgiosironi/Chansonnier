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

import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.connectivity.framework.AgentState;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestAgentController.
 */
public class TestAgentController extends DeclarativeServiceTestCase {

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
   * Test {@link AgentController#hasActiveAgents()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testHasActiveAgents() throws Exception {
    final AgentController agentController = getService(AgentController.class);

    final boolean hasActiveAgents = agentController.hasActiveAgents();
    assertFalse(hasActiveAgents);
  }

  /**
   * Test {@link AgentController#getAgentTasksState()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetAgentTasksState() throws Exception {
    final AgentController agentController = getService(AgentController.class);

    final Map<String, AgentState> agentTasksState = agentController.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(0, agentTasksState.size());
  }

  /**
   * Test {@link AgentController#getAvailableConfigurations()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetAvailableConfigurations() throws Exception {
    final AgentController agentController = getService(AgentController.class);

    final Collection<String> configs = agentController.getAvailableConfigurations();
    assertNotNull(configs);
    assertEquals(1, configs.size());
  }

  /**
   * Test a Agent run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartAgent() throws Exception {
    final AgentController agentController = getService(AgentController.class);

    final String dataSourceId = "mockAgent";
    agentController.startAgent(dataSourceId);

    boolean hasActiveAgents = agentController.hasActiveAgents();
    assertTrue(hasActiveAgents);

    Map<String, AgentState> agentTasksState = agentController.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    AgentState agentState = agentTasksState.get(dataSourceId);
    assertNotNull(agentState);
    assertEquals(AgentThreadState.Running, agentState.getState());
    assertNull(agentState.getLastError());

    // stop agent
    agentController.stopAgent(dataSourceId);
    Thread.sleep(SLEEP_TIME);

    hasActiveAgents = agentController.hasActiveAgents();
    assertFalse(hasActiveAgents);

    agentTasksState = agentController.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    agentState = agentTasksState.get(dataSourceId);
    assertNotNull(agentState);
    assertEquals(AgentThreadState.Stopped, agentState.getState());
    assertNull(agentState.getLastError());
  }

}
