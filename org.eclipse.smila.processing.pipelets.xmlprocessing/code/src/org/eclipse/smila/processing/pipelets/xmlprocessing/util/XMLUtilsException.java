/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

/**
 * XMLUtilsException.
 */
public class XMLUtilsException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public XMLUtilsException() {
    super();
  }

  /**
   * @param message
   *          Message.
   */
  public XMLUtilsException(String message) {
    super(message);
  }

  /**
   * @param message
   *          Message.
   * @param cause
   *          Cause.
   */
  public XMLUtilsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          Cause.
   */
  public XMLUtilsException(Throwable cause) {
    super(cause);
  }
}
