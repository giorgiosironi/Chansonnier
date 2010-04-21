/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import org.eclipse.smila.connectivity.framework.util.AgentThreadState;

/**
 * Utility class that contains the state of a agent.
 */
public class AgentState extends State {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 7499001878583883888L;

 
  /**
   * State of the Agent thread.
   */
  private AgentThreadState _state;

  
  /**
   * Returns the state of the CrawlThread.
   * 
   * @return the state of the CrawlThread.
   */
  public AgentThreadState getState() {
    return _state;
  }

  /**
   * Set the state of the Agent thread.
   * 
   * @param state
   *          the state of the Agent thread.
   */
  public void setState(final AgentThreadState state) {
    _state = state;
  }


  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public AgentState clone() throws CloneNotSupportedException {
    super.clone();
    final AgentState agentState = new AgentState();
    agentState.setDataSourceId(getDataSourceId());
    agentState.setEndTime(getEndTime());
    agentState.setLastError(getLastError());
    agentState.setStartTime(getStartTime());
    agentState.setState(getState());
    agentState.setJobId(getJobId());

    return agentState;
  }
}
