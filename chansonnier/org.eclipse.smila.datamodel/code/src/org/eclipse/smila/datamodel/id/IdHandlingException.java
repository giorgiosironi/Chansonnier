/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.id;

/**
 * Exception thrown by illegal Id operations, e.g. trying to create a new element Id from an already fragmented Id.
 * 
 * @author scum36
 */
public class IdHandlingException extends Exception {

  /**
   * exceptions are serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * constructor with message and cause.
   * 
   * @param message
   *          message describing the error.
   * @param cause
   *          exception that has caused this error.
   */
  public IdHandlingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * constructor with message.
   * 
   * @param message
   *          message describing the error.
   */
  public IdHandlingException(String message) {
    super(message);
  }
}
