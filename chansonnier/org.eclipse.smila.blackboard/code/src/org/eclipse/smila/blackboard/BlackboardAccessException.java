/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.blackboard;

/**
 * The Class BlackboardAccessException.
 */
public class BlackboardAccessException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 454527874180148082L;

  /**
   * Instantiates a new blackboard access exception.
   *
   * @param cause
   *          the cause
   */
  public BlackboardAccessException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new blackboard access exception.
   *
   * @param message
   *          the message
   */
  public BlackboardAccessException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new blackboard access exception.
   *
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public BlackboardAccessException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
