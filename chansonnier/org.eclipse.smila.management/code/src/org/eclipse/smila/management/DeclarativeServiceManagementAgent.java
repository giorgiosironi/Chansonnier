/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;


/**
 * The Class AbstractDeclarativeServiceManagementAgent.
 * 
 * @param <ServiceType>
 *          service type
 */
public abstract class DeclarativeServiceManagementAgent<ServiceType> extends DeclarativeManagementAgent {

  /**
   * The _service.
   */
  protected ServiceType _service;

  /**
   * Sets the service.
   * 
   * @param service
   *          the new service
   */
  protected synchronized void setService(final ServiceType service) {
    _service = service;
  }

  /**
   * Unset service.
   * 
   * @param service
   *          the service
   */
  protected synchronized void unsetService(final ServiceType service) {
    _service = null;
  }

}
