/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel;

/**
 * Object for managing SimplePipelet instances.
 * 
 * @author jschumacher
 * 
 */
public class ProcessingServiceInvoker extends ExtensionAdapter {
  /**
   * serializable because of super class.
   */
  private static final long serialVersionUID = 1L;

  /**
   * processing service name given as service property.
   */
  private String _serviceName;

  /**
   * @return name of service.
   */
  public String getServiceName() {
    return _serviceName;
  }

  /**
   * @param serviceName
   *          new service name.
   */
  public void setServiceName(String serviceName) {
    _serviceName = serviceName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionAdapter#getPrintName()
   */
  @Override
  public String getPrintName() {
    return "service " + getServiceName();
  }
}
