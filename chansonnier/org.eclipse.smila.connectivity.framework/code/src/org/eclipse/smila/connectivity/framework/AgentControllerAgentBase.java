/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.management.DeclarativeServiceManagementAgent;

/**
 * The Class AgentControllerAgent.
 */
public abstract class AgentControllerAgentBase extends DeclarativeServiceManagementAgent<AgentController> implements
  AgentControllerAgent {

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(AgentControllerAgentBase.class);

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getCategory()
   */
  @Override
  protected String getCategory() {
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getName()
   */
  @Override
  protected String getName() {
    return "AgentController";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentControllerAgent#startAgentTask(java.lang.String)
   */
  public String startAgentTask(final String dataSourceId) {
    try {
      final int hashcode = _service.startAgent(dataSourceId);
      return "Agent with the dataSourceId = " + dataSourceId + " and hashcode [" + hashcode + "]"
        + " successfully started!";

    } catch (final ConnectivityException exception) {
      if (_log.isErrorEnabled()) {
        _log.error(exception);
      }
      return getExceptionText(exception);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentControllerAgent#stopAgentTask(java.lang.String)
   */
  public String stopAgentTask(final String dataSourceId) {
    try {
      _service.stopAgent(dataSourceId);
      return "Agent with the dataSourceId = " + dataSourceId + " successfully stopped.";
    } catch (final ConnectivityException exception) {
      if (_log.isErrorEnabled()) {
        _log.error(exception);
      }
      return getExceptionText(exception);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentControllerAgent#getAvailableAgents()
   */
  public String[] getAvailableAgents() {
    final Collection<String> availAgents = _service.getAvailableAgents();
    return availAgents.toArray(new String[availAgents.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentControllerAgent#getAvailableAgentTasks()
   */
  public String[] getAvailableAgentTasks() {
    final Collection<String> configs = _service.getAvailableConfigurations();
    return configs.toArray(new String[configs.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentControllerAgent#getAgentTasksState()
   */
  public Map<String, String> getAgentTasksState() {
    final HashMap<String, String> states = new HashMap<String, String>();
    final Map<String, AgentState> agentStates = _service.getAgentTasksState();
    final Iterator<String> it = agentStates.keySet().iterator();
    while (it.hasNext()) {
      final String dataSourceId = it.next();
      states.put(dataSourceId, agentStates.get(dataSourceId).getState().name());
    }
    return states;
  }

  /**
   * Returns the text of the exception plus any additional text from the exception's cause.
   * 
   * @param t
   *          the Throwable
   * @return the exception text
   */
  private String getExceptionText(final Throwable t) {
    String text = t.getMessage();
    final Throwable cause = t.getCause();
    if (cause != null) {
      text += ": " + cause.toString();
    }
    return text;
  }
}
