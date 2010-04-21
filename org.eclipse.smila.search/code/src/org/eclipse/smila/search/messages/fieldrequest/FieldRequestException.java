/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.messages.fieldrequest;

/**
 * Exception to be used in field request messages.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class FieldRequestException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public FieldRequestException() {
    super();
  }

  /**
   * @param message
   *          Message.
   */
  public FieldRequestException(String message) {
    super(message);
  }

  /**
   * @param message
   *          Message.
   * @param cause
   *          Cause.
   */
  public FieldRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          Cause.
   */
  public FieldRequestException(Throwable cause) {
    super(cause);
  }

}
