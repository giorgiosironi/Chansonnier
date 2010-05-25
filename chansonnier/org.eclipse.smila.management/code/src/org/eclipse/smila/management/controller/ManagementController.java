/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.management.controller;

import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.RegistrationException;

/**
 * The Interface ManagementController.
 */
public interface ManagementController {

  /**
   * Register agent.
   * 
   * @param agent
   *          the agent
   * @param location
   *          the location
   * 
   * @throws RegistrationException
   *           the registration exception
   */
  void registerAgent(ManagementAgentLocation location, ManagementAgent agent) throws RegistrationException;

  /**
   * Unregister agent.
   * 
   * @param location
   *          the location
   * 
   * @throws RegistrationException
   *           the registration exception
   */
  void unregisterAgent(ManagementAgentLocation location) throws RegistrationException;

}
