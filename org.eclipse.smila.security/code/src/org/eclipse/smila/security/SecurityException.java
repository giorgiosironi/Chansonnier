/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.security;

/**
 * SecurityException. Thrown on errors when processing SMILA security annotations. 
 */
public class SecurityException extends Exception {

  /**
   * exceptions are serializable.
   */
  private static final long serialVersionUID = 213413606804432526L;

  /**
   * constructur with message.
   * 
   * @param message
   *          detailed error message.
   */
  public SecurityException(String message) {
    super(message);
  }

  /**
   * constructur with causing exception.
   * 
   * @param cause
   *          exception that caused the error.
   */
  public SecurityException(Throwable cause) {
    super(cause);
  }

  /**
   * constructur with mesage and causing exception.
   * 
   * @param message
   *          detailed error message.
   * @param cause
   *          exception that caused the error.
   */
  public SecurityException(String message, Throwable cause) {
    super(message, cause);
  }

}
