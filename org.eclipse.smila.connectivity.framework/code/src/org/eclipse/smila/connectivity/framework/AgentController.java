/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Collection;
import java.util.Map;

import org.eclipse.smila.connectivity.ConnectivityException;

/**
 * Management interface for the AgentController.
 */
public interface AgentController {

  /**
   * The Performance AGENT location.
   */
  String PERFORMANCE_AGENT_LOCATION = "Agents/Total";

  /**
   * Starts an Agent using the given dataSourceId. This method creates a new Thread. If it is called for a dataSourceId
   * that is currently used by another agent a ConnectivityException is thrown. Returns an id for this job, the hashCode
   * of the agent instance used for performance counter.
   * 
   * @param dataSourceId
   *          the ID of the data source
   * @return - the jobId (hashcode of the agent instance as int value)
   * @throws ConnectivityException
   *           if any error occurs
   */
  int startAgent(String dataSourceId) throws ConnectivityException;

  /**
   * Stops an active agent using the given dataSourceId.
   * 
   * @param dataSourceId
   *          the ID of the data source
   * @throws ConnectivityException
   *           if any error occurs
   */
  void stopAgent(String dataSourceId) throws ConnectivityException;

  /**
   * Checks if there are any active agents.
   * 
   * @return true if there are active agents, false otherwise
   * @throws ConnectivityException
   *           if any error occurs
   */
  boolean hasActiveAgents() throws ConnectivityException;

  /**
   * Gets the status of all agent tasks as a map of data source id and agent state.
   * 
   * @return a map of data source id and agent state.
   */
  Map<String, AgentState> getAgentTasksState();

  /**
   * returns the AgentController known Agents.
   * 
   * @return Collection with Strings
   */
  Collection<String> getAvailableAgents();

  /**
   * returns all available Agent data source configurations.
   * 
   * @return List with Strings of all available Agent data source configurations
   */
  Collection<String> getAvailableConfigurations();
}
