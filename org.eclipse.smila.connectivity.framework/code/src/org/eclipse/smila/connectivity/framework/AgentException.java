/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

/**
 * Standard Exception used by Agents.
 */
public class AgentException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -1165046234076776725L;

  /**
   * Creates a AgentException.
   */
  public AgentException() {
    super();
  }

  /**
   * Creates a AgentException.
   * 
   * @param message
   *          the detail message
   */
  public AgentException(final String message) {
    super(message);
  }

  /**
   * Creates a AgentException.
   * 
   * @param cause
   *          the cause
   */
  public AgentException(final Throwable cause) {
    super(cause);
  }

  /**
   * Creates a AgentException.
   * 
   * @param message
   *          the detail message
   * @param cause
   *          the cause
   */
  public AgentException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
