/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.exceptions;

/**
 * The Class SchemaRuntimeException.
 */
public class SchemaRuntimeException extends RuntimeException {

  /**
   * sid.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new date parse exception.
   * 
   * @param message
   *          the message
   */
  public SchemaRuntimeException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new index order schema runtime exception.
   * 
   * @param ex
   *          throwable
   */
  public SchemaRuntimeException(final Throwable ex) {
    super(ex);
  }

  /**
   * Instantiates a new index order schema runtime exception.
   * 
   * @param message
   *          the message
   * @param ex
   *          the ex
   */
  public SchemaRuntimeException(final String message, final Throwable ex) {
    super(message, ex);
  }

}
