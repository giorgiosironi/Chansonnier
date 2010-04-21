/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.exceptions;

/**
 * The Class EvaluationException.
 */
public class EvaluationException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -5296994831254770184L;

  /**
   * Instantiates a new evaluation exception.
   */
  public EvaluationException() {
  }

  /**
   * Instantiates a new evaluation exception.
   * 
   * @param message
   *          the message
   */
  public EvaluationException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new evaluation exception.
   * 
   * @param cause
   *          the cause
   */
  public EvaluationException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new evaluation exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public EvaluationException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
