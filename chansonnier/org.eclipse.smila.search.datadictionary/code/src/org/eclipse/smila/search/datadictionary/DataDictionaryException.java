/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public class DataDictionaryException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public DataDictionaryException() {
    super();
  }

  /**
   * @param message
   *          Message.
   */
  public DataDictionaryException(String message) {
    super(message);
  }

  /**
   * @param message
   *          Message.
   * @param cause
   *          Cause.
   */
  public DataDictionaryException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          Cause.
   */
  public DataDictionaryException(Throwable cause) {
    super(cause);
  }
}
