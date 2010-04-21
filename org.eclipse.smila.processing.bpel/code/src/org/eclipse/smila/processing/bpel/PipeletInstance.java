/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.processing.bpel;

import org.eclipse.smila.processing.IPipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * Object for managing SimplePipelet instances.
 * 
 * @author jschumacher
 */
public class PipeletInstance extends ExtensionAdapter {

  /**
   * serializable because of super class.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Java class name of pipelet.
   */
  private String _className;

  /**
   * parsed pipelet configuration.
   */
  private PipeletConfiguration _configuration;

  /**
   * pipelet instance object.
   */
  private IPipelet _pipelet;

  /**
   * create instance.
   */
  public PipeletInstance() {
    super();
  }

  /**
   * @return Java class name of pipelet.
   */
  public String getClassName() {
    return _className;
  }

  /**
   * @param className
   *          set Java class name of pipelet.
   */
  public void setClassName(String className) {
    this._className = className;
  }

  /**
   * 
   * @return get parsed pipelet configuration.
   */
  public PipeletConfiguration getConfiguration() {
    return _configuration;
  }

  /**
   * 
   * @param configuration
   *          set parsed pipelet configuration.
   */
  public void setConfiguration(PipeletConfiguration configuration) {
    this._configuration = configuration;
  }

  /**
   * 
   * @return the pipelet instance, if the class is available already.
   */
  public IPipelet getPipelet() {
    return _pipelet;
  }

  /**
   * 
   * @param pipelet
   *          a new pipelet instance.
   */
  public void setPipelet(IPipelet pipelet) {
    this._pipelet = pipelet;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionAdapter#getPrintName()
   */
  @Override
  public String getPrintName() {
    return "pipelet " + getClassName();
  }
}
