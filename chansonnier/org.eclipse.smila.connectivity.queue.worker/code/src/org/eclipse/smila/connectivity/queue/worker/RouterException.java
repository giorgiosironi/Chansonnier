/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker;

/**
 * The Class RouterException.
 */
public class RouterException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new router exception.
   * 
   * @param message
   *          the message
   */
  public RouterException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new router exception.
   * 
   * @param cause
   *          the cause
   */
  public RouterException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new router exception.
   * 
   * * @param message the message
   * 
   * @param cause
   *          the cause
   */
  public RouterException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
