/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage;

/**
 * Base checked binary-storage application exceptions. All checked and intelligent exceptions must be warped to
 * <code>BinaryStorageException</code>.
 *
 * @author mcimpean
 */
@SuppressWarnings("serial")
public class BinaryStorageException extends Exception implements java.io.Serializable {

  /**
   * Initial exception.
   */
  private Throwable _exception = null;

  /**
   * Passed parameters.
   */
  private Object[] _params = null;

  /**
   * Default constructor
   */
  public BinaryStorageException() {

    super();
  }

  /**
   * Exception constructor with message.
   *
   * @param message
   */
  public BinaryStorageException(final String message) {

    super(message);
  }

  /**
   * Creates a new BinaryStorageException wrapping another exception.
   *
   * @param exception
   *          the wrapped exception.
   */
  public BinaryStorageException(final Throwable exception) {

    super();
    this._exception = exception;
  }

  /**
   * Creates a new BinaryStorageException wrapping another exception.
   *
   * @param exception
   * @param message
   */
  public BinaryStorageException(final Throwable exception, final String message) {

    super(message);
    this._exception = exception;
  }

  /**
   * Creates a new BinaryStorageException with given parameters.
   *
   * @param _exception
   *          the wrapped exception.
   */
  public BinaryStorageException(final Object[] oParam) {

    super();
    this._params = oParam;
  }

  /**
   * @param string
   * @param e
   */
  public BinaryStorageException(String message, Throwable t) {
    super(message, t);
  }

  /**
   * @return passed application parameter
   */
  public Object[] getParam() {

    return _params;
  }

  /**
   * Gets the wrapped exception.
   *
   * @return the wrapped exception.
   */
  public Throwable getException() {

    return _exception;
  }

  /**
   * Retrieves (recursively) the root cause exception.
   *
   * @return the root cause exception.
   */
  public Throwable getRootCause() {

    if (_exception instanceof BinaryStorageException) {
      return ((BinaryStorageException) _exception).getRootCause();
    }
    return _exception == null ? this : _exception;
  }

  @Override
  public String toString() {

    if (_exception instanceof BinaryStorageException) {
      return _exception.toString();
    }
    return _exception == null ? super.toString() : _exception.toString();
  }
}
