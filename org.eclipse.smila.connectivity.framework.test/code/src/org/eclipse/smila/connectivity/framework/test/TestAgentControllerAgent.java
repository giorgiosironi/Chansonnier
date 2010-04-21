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

import org.eclipse.smila.connectivity.framework.AgentControllerAgent;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class AgentControllerAgent.
 */
public class TestAgentControllerAgent extends DeclarativeServiceTestCase {

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
    final AgentControllerAgent aca = getService(AgentControllerAgent.class);
    assertNotNull(aca);

    final String dataSourceId = "dummy";
    String msg = aca.startAgentTask(dataSourceId);
    assertNotNull(msg);
    assertEquals("Error loading DataSource with DataSourceId '" + dataSourceId
      + "': org.eclipse.smila.utils.config.ConfigurationLoadException: Unable to find configuration resource "
      + dataSourceId + ".xml in the bundle org.eclipse.smila.connectivity.framework", msg);

    try {
      msg = aca.startAgentTask(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Parameter dataSourceId is null", e.getMessage());
    }

    msg = aca.stopAgentTask(dataSourceId);
    assertNotNull(msg);
    assertEquals("Could not stop Agent for DataSourceId '" + dataSourceId + "'. No agent has been started for it.",
      msg);

    try {
      msg = aca.stopAgentTask(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      assertEquals("Parameter dataSourceId is null", e.getMessage());
    }
  }

  /**
   * Test {@link AgentControllerAgent#getActiveAgentTaskStatus()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetActiveAgentStatus() throws Exception {
    final AgentControllerAgent aca = getService(AgentControllerAgent.class);
    assertNotNull(aca);
    final String dataSourceId = "mockAgent";

    aca.startAgentTask(dataSourceId);
    Map<String, String> agentTasksState = aca.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    assertEquals(AgentThreadState.Running.name(), agentTasksState.get(dataSourceId));

    aca.stopAgentTask(dataSourceId);
    agentTasksState = aca.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    assertEquals(AgentThreadState.Stopped.name(), agentTasksState.get(dataSourceId));
  }

  /**
   * Test {@link AgentControllerAgent#getAvailableAgentTasks()}.
   * 
   * @throws Exception
   *           a Exception
   */
  public void testGetAvailableConfigurations() throws Exception {
    final AgentControllerAgent aca = getService(AgentControllerAgent.class);
    assertNotNull(aca);

    final String[] configs = aca.getAvailableAgentTasks();
    assertNotNull(configs);
    assertEquals(1, configs.length);
  }

  /**
   * Test a Agent run.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testStartAgent() throws Exception {
    final AgentControllerAgent aca = getService(AgentControllerAgent.class);
    assertNotNull(aca);

    final String dataSourceId = "mockAgent";
    aca.startAgentTask(dataSourceId);

    Map<String, String> agentTasksState = aca.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    String agentState = agentTasksState.get(dataSourceId);
    assertNotNull(agentState);
    assertEquals(AgentThreadState.Running.toString(), agentState);

    // stop agent
    aca.stopAgentTask(dataSourceId);
    Thread.sleep(SLEEP_TIME);

    agentTasksState = aca.getAgentTasksState();
    assertNotNull(agentTasksState);
    assertEquals(1, agentTasksState.size());
    agentState = agentTasksState.get(dataSourceId);
    assertNotNull(agentState);
    assertEquals(AgentThreadState.Stopped.toString(), agentState);
  }

  /**
   * test the Function get the known Agents.
   * 
   * @throws Exception
   *           in case of weird problems
   */
  public void testAvailableAgents() throws Exception {
    final AgentControllerAgent aca = getService(AgentControllerAgent.class);
    final String[] availAgents = aca.getAvailableAgents();
    assert (availAgents.length == 1);
    final List<String> a = Arrays.asList(availAgents);
    assert (a.indexOf("MockAgent") > 0);
  }

}
