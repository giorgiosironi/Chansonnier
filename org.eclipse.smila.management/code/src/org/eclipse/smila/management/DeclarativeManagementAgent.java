/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;

/**
 * The Class DeclarativeManagementAgent.
 */
public abstract class DeclarativeManagementAgent extends LocatedManagementAgentBase {

  /**
   * The _log.
   */
  protected Log _log = LogFactory.getLog(getClass());

  /**
   * Activate.
   * 
   * @param context
   *          the context
   */
  protected synchronized void activate(final ComponentContext context) {
    if (_log.isInfoEnabled()) {
      _log.info(String.format("Registering agent for %s ...", getName()));
    }
    ManagementRegistration.INSTANCE.registerAgent(getLocation(), this);
    if (_log.isInfoEnabled()) {
      _log.info(String.format("Agent for %s was registered successfully", getName()));
    }
  }

  /**
   * Deactivate.
   * 
   * @param context
   *          the context
   */
  protected synchronized void deactivate(final ComponentContext context) {
    if (_log.isInfoEnabled()) {
      _log.info(String.format("Unregistering agent for %s ...", getName()));
    }
    ManagementRegistration.INSTANCE.unregisterAgent(getLocation());
    if (_log.isInfoEnabled()) {
      _log.info(String.format("Agent for %s was unregistered successfully", getName()));
    }
  }

}
