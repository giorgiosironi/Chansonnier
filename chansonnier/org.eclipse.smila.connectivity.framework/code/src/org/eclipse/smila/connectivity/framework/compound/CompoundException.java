/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound;

/**
 * Standard Exception used by CompoundManager and CompoundHandlers.
 */
public class CompoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -1165046234076776725L;

  /**
   * Creates a CompoundException.
   */
  public CompoundException() {
    super();
  }

  /**
   * Creates a CompoundException.
   * 
   * @param message
   *          the detail message
   */
  public CompoundException(final String message) {
    super(message);
  }

  /**
   * Creates a CompoundException.
   * 
   * @param cause
   *          the cause
   */
  public CompoundException(final Throwable cause) {
    super(cause);
  }

  /**
   * Creates a CompoundException.
   * 
   * @param message
   *          the detail message
   * @param cause
   *          the cause
   */
  public CompoundException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
