/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.datamodel.tools;

/**
 * The Class DatamodelDeserializationException.
 */
public class DatamodelDeserializationException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new datamodel deserialization exception.
   */
  public DatamodelDeserializationException() {
  }

  /**
   * Instantiates a new datamodel deserialization exception.
   * 
   * @param arg0
   *          the arg0
   */
  public DatamodelDeserializationException(final String arg0) {
    super(arg0);
  }

  /**
   * Instantiates a new datamodel deserialization exception.
   * 
   * @param arg0
   *          the arg0
   */
  public DatamodelDeserializationException(final Throwable arg0) {
    super(arg0);
  }

  /**
   * Instantiates a new datamodel deserialization exception.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   */
  public DatamodelDeserializationException(final String arg0, final Throwable arg1) {
    super(arg0, arg1);
  }

}
