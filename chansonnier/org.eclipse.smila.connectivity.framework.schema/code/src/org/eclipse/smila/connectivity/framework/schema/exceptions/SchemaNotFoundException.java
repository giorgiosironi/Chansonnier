/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.exceptions;

/**
 * The Class SchemaNotFoundException.
 */
public class SchemaNotFoundException extends Exception {

  /**
   * The s_class format.
   */
  private static final String PACKAGE_FORMAT = "JAXB Schema is not found in the bundle plug-in \"%s\"";

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new date parse exception.
   * 
   * @param message
   *          the message
   */
  public SchemaNotFoundException(final String message) {
    super(String.format(PACKAGE_FORMAT, message));
  }
}
