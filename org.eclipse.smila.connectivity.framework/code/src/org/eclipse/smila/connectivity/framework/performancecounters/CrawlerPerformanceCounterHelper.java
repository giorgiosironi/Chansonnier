/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import java.lang.reflect.Constructor;

import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementAgentNotFoundException;
import org.eclipse.smila.management.ManagementRegistration;

/**
 * The Class CrawlerPerformanceCounterHelper.
 * 
 * @param <AgentType>
 *          real agent class
 */
public class CrawlerPerformanceCounterHelper<AgentType extends CrawlerPerformanceAgent> extends
  ConnectivityPerformanceCounterHelperBase<AgentType> {

  /**
   * Instantiates a new crawler performance counter helper.
   * 
   * @param configuration
   *          the configuration
   * @param hashCode
   *          the hash code
   * @param agentClass
   *          the agent class
   */
  public CrawlerPerformanceCounterHelper(final DataSourceConnectionConfig configuration, final int hashCode,
    final Class<AgentType> agentClass) {
    super(configuration, hashCode, agentClass);
  }

  /**
   * Inits the agent.
   * 
   * @param location
   *          the location
   * 
   * @return the agent type
   */
  @Override
  @SuppressWarnings("unchecked")
  protected AgentType initAgent(final ManagementAgentLocation location) {
    synchronized (ManagementRegistration.INSTANCE.getMonitor()) {
      try {
        return (AgentType) ManagementRegistration.INSTANCE.getAgent(location);
      } catch (final ManagementAgentNotFoundException e) {
        // register new
        try {
          final Constructor constructor = _agentClass.getConstructor();
          final AgentType agent = (AgentType) constructor.newInstance();
          ManagementRegistration.INSTANCE.registerAgent(location, agent);
          return agent;
        } catch (final Throwable e1) {
          throw new RuntimeException(e1);
        }
      }
    }
  }

}
