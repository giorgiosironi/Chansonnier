/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import org.eclipse.smila.connectivity.framework.performancecounters.ConnectivityPerformanceAgentBase;
import org.eclipse.smila.management.LocatedManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;
import org.osgi.service.component.ComponentContext;

/**
 * Used default connectivity agent.
 */
public class CrawlerControllerPerformanceAgentImpl extends ConnectivityPerformanceAgentBase implements
  LocatedManagementAgent {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgent#getLocation()
   */
  public ManagementAgentLocation getLocation() {
    return ManagementRegistration.INSTANCE.getCategory("Crawlers").getLocation("Total");
  }

  /**
   * Activate.
   * 
   * @param context
   *          the context
   */
  protected synchronized void activate(final ComponentContext context) {
    ManagementRegistration.INSTANCE.registerAgent(getLocation(), this);
  }

  /**
   * Deactivate.
   * 
   * @param context
   *          the context
   */
  protected synchronized void deactivate(final ComponentContext context) {
    ManagementRegistration.INSTANCE.unregisterAgent(getLocation());
  }

}
