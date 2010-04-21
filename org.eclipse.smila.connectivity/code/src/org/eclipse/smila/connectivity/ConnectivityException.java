/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity;

/**
 * Standard Exception used by ConnectivityManager.
 * 
 */
public class ConnectivityException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 7477562780407019015L;

  /**
   * Creates a ConnectivityException.
   */
  public ConnectivityException() {
    super();
  }

  /**
   * Creates a ConnectivityException.
   * 
   * @param message
   *          the detail message
   */
  public ConnectivityException(final String message) {
    super(message);
  }

  /**
   * Creates a ConnectivityException.
   * 
   * @param cause
   *          the cause
   */
  public ConnectivityException(final Throwable cause) {
    super(cause);
  }

  /**
   * Creates a ConnectivityException.
   * 
   * @param message
   *          the detail message
   * @param cause
   *          the cause
   */
  public ConnectivityException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
