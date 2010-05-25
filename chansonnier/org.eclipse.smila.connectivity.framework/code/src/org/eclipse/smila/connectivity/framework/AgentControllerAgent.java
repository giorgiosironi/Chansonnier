/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Map;

/**
 * The Interface AgentControllerAgent.
 */
public interface AgentControllerAgent {

  /**
   * Start agent.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return the string
   */
  String startAgentTask(final String dataSourceId);

  /**
   * Stop agent.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return the string
   */
  String stopAgentTask(final String dataSourceId);

  /**
   * returns all Agents that have connected to the AgentController.
   * 
   * @return List with Strings of all available Agents
   */
  String[] getAvailableAgents();

  /**
   * returns all available Agent data source configurations.
   * 
   * @return List with Strings of all available Agent data source configurations
   */
  String[] getAvailableAgentTasks();

  /**
   * Gets the status of all agent tasks as a map of data source id and agent state.
   * 
   * @return a map of data source id and agent state.
   */
  Map<String, String> getAgentTasksState();

}
