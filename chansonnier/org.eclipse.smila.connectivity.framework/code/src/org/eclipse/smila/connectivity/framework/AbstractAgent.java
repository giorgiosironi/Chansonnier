/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Dictionary;

import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;
import org.eclipse.smila.connectivity.framework.util.AgentThreadState;
import org.osgi.service.component.ComponentContext;

/**
 * Abstract base class for Agent implementations.
 */
public abstract class AbstractAgent implements Agent {
  /**
   * The agent ID.
   */
  private String _agentId;

  /**
   * Reference to the AgentControllerCallback.
   */
  private AgentControllerCallback _controllerCallback;

  /**
   * Reference to the DataSourceConnectionConfig.
   */
  private DataSourceConnectionConfig _config;

  /**
   * The delta indexing session.
   */
  private String _sessionId;

  /**
   * Flag if the agent thread should be / was stopped.
   */
  private boolean _stopThread;

  /**
   * Reference to the Agents thread.
   */
  private Thread _agentThread;

  /**
   * The state of the agent.
   */
  private AgentState _agentState;

  /**
   * Default Constructor.
   */
  public AbstractAgent() {
  }

  /**
   * Returns the Agent Id, which is the OSGi DecarativeService Component Name.
   * 
   * @return the AgentId
   * @throws AgentException
   *           if any error occurs
   */
  public String getAgentId() throws AgentException {
    return _agentId;
  }

  /**
   * Gets the AgentState.
   * 
   * @return the AgentState
   */
  public AgentState getAgentState() {
    return _agentState;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.Agent#start(AgentControllerCallback, AgentState,
   *      DataSourceConnectionConfig, String)
   */
  public void start(final AgentControllerCallback controllerCallback, final AgentState agentState,
    final DataSourceConnectionConfig config, final String sessionId) throws AgentException {
    if (controllerCallback == null) {
      throw new AgentException("parameter controllerCallback is null");
    }
    if (config == null) {
      throw new AgentException("parameter config is null");
    }
    _stopThread = false;
    _controllerCallback = controllerCallback;
    _config = config;
    _sessionId = sessionId;

    // initialize the AgentState
    _agentState = agentState;
    _agentState.setDataSourceId(config.getDataSourceID());
    _agentState.setState(AgentThreadState.Running);
    _agentState.setStartTime(System.currentTimeMillis());

    // initialize the agent (depends on implementation)
    initialize();

    // initialize the Agent thread
    _agentThread = new Thread(this);
    _agentThread.start();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.Agent#stop()
   */
  public void stop() throws AgentException {
    stopThread();
    _agentState.setState(AgentThreadState.Stopped);
  }

  /**
   * Returns the value of the flag _stopThread.
   * 
   * @return true if the thread is stopped, false otherwise
   */
  protected boolean isStopThread() {
    return _stopThread;
  }

  /**
   * Stops the agent thread.
   */
  protected void stopThread() {
    _agentState.setEndTime(System.currentTimeMillis());
    _stopThread = true;
    _agentThread = null;
    _controllerCallback.unregister(_sessionId, _config.getDeltaIndexing(), _config.getDataSourceID());
  }

  /**
   * Gets the AgentControllerCallback.
   * 
   * @return the AgentControllerCallback
   */
  protected final AgentControllerCallback getControllerCallback() {
    return _controllerCallback;
  }

  /**
   * Gets the DataSourceConnectionConfig.
   * 
   * @return the DataSourceConnectionConfig
   */
  protected final DataSourceConnectionConfig getConfig() {
    return _config;
  }

  /**
   * Returns the delta indexing session id.
   * 
   * @return the delta indexing session id
   */
  protected final String getSessionId() {
    return _sessionId;
  }

  /**
   * Activate the component.
   * 
   * @param context
   *          the ComponentContext
   */
  @SuppressWarnings("unchecked")
  protected void activate(final ComponentContext context) {
    final Dictionary<String, String> dictionary = context.getProperties();
    _agentId = dictionary.get("component.name");
  }

  /**
   * Deactivate the component.
   * 
   * @param context
   *          the ComponentContext
   */
  protected void deactivate(final ComponentContext context) {
    _agentId = null;
  }

  /**
   * Method to contain initialization of the agent. Must be implemented by subclasses. This method is called just before
   * the AgentThread is started.
   * 
   * @throws AgentException
   *           if any error occurs
   */
  protected abstract void initialize() throws AgentException;
}
