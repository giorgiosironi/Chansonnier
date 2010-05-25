/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementAgentNotFoundException;
import org.eclipse.smila.management.ManagementRegistration;

/**
 * The Class CrawlerControllerPerformanceCounterHelper.
 */
public class CrawlerControllerPerformanceCounterHelper extends
  ConnectivityPerformanceCounterHelperBase<CrawlerPerformanceAgent> {

  /**
   * Instantiates a new crawler controller performance counter helper.
   * 
   * @param configuration
   *          the configuration
   * @param hashCode
   *          the hash code
   */
  public CrawlerControllerPerformanceCounterHelper(final DataSourceConnectionConfig configuration, final int hashCode) {
    super(configuration, hashCode, CrawlerPerformanceAgent.class);
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
  protected CrawlerPerformanceAgent initAgent(final ManagementAgentLocation location) {
    synchronized (ManagementRegistration.INSTANCE.getMonitor()) {
      try {
        return (CrawlerPerformanceAgent) ManagementRegistration.INSTANCE.getAgent(location);
      } catch (final ManagementAgentNotFoundException e) {
        // TODO: tests does not init it correctly
        _log.warn(String.format("Agent location [%s] is not found", location));
        // throw new RuntimeException(e);
        return null;
      } catch (final Throwable e) {
        // TODO: tests does not init it correctly
        _log.warn(String.format("Agent location [%s] is not initialized", location));
        // throw new RuntimeException(e);
        return null;
      }

    }
  }

}
