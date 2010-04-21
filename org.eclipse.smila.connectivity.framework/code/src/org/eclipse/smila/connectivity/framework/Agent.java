/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;

/**
 * The Interface Agent.
 */
public interface Agent extends Runnable {

  /**
   * Returns the ID of this Agent.
   * 
   * @return a String containing the ID of this Agent
   * 
   * @throws AgentException
   *           if any error occurs
   */
  String getAgentId() throws AgentException;

  /**
   * Starts the agent using the given configuration, creating a new internal thread.
   * 
   * @param controllerCallback
   *          reference to the interface AgentControllerCallback
   * @param agentState
   *          the AgentState
   * @param config
   *          the DataSourceConnectionConfig
   * @param sessionId
   *          the delta indexing session id
   * 
   * @throws AgentException
   *           if any error occurs
   */
  void start(final AgentControllerCallback controllerCallback, final AgentState agentState,
    final DataSourceConnectionConfig config, final String sessionId) throws AgentException;

  /**
   * Stops the agent.
   * 
   * @throws AgentException
   *           if any error occurs
   */
  void stop() throws AgentException;
}
