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

package org.eclipse.smila.processing;

/**
 * Thrown on errors when processing SMILA record.
 * 
 * @author jschumacher
 * 
 */
public class ProcessingException extends Exception {

  /**
   * exceptions are serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * constructur with message.
   * 
   * @param message
   *          detailed error message.
   */
  public ProcessingException(String message) {
    super(message);
  }

  /**
   * constructur with causing exception.
   * 
   * @param cause
   *          exception that caused the error.
   */
  public ProcessingException(Throwable cause) {
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
  public ProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

}
