/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.exceptions;

/**
 * The Class OperationException.
 */
public class OperationException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -7524113503263880375L;

  /**
   * Instantiates a new operation exception.
   */
  public OperationException() {
  }

  /**
   * Instantiates a new operation exception.
   * 
   * @param message
   *          the message
   */
  public OperationException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new operation exception.
   * 
   * @param cause
   *          the cause
   */
  public OperationException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new operation exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public OperationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
