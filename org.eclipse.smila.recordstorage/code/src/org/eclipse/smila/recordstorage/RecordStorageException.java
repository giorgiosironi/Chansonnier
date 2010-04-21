/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage;

/**
 * The class RecordStorageException. Used by RecordStorage for error handling.
 */
public class RecordStorageException extends Exception implements java.io.Serializable {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 4822259560441871369L;

  /**
   * Default constructor.
   */
  public RecordStorageException() {
    super();
  }

  /**
   * Exception constructor with message.
   * 
   * @param message
   *          a message string
   */
  public RecordStorageException(String message) {
    super(message);
  }

  /**
   * Creates a new BinaryStorageException wrapping another exception.
   * 
   * @param exception
   *          the nested exception.
   */
  public RecordStorageException(Throwable exception) {
    super(exception);
  }

  /**
   * Creates a new BinaryStorageException wrapping another exception.
   * 
   * @param exception
   *          a message string
   * @param message
   *          the nested exception.
   */
  public RecordStorageException(Throwable exception, String message) {
    super(message, exception);
  }

}
