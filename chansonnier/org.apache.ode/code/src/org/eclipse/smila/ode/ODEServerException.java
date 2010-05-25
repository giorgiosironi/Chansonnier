/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.ode;

/**
 * Exception thrown by ODEServer on errors during invocation or similar.
 * 
 * @author jschumacher
 * 
 */
public class ODEServerException extends Exception {

  /**
   * exceptions are serializable, so ...
   */
  private static final long serialVersionUID = 1L;

  /**
   * create execption with message and cause.
   * 
   * @param message
   *          error message
   * @param cause
   *          causing exception
   */
  public ODEServerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * create exception with message only.
   * 
   * @param message
   *          error message
   */
  public ODEServerException(String message) {
    super(message);
  }

}
