/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype;

/**
 * The Class MimeTypeParseException.
 */
public class MimeTypeParseException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new mime type parse exception.
   */
  public MimeTypeParseException() {
  }

  /**
   * Instantiates a new mime type parse exception.
   * 
   * @param arg0
   *          the arg0
   */
  public MimeTypeParseException(final String arg0) {
    super(arg0);
  }

  /**
   * Instantiates a new mime type parse exception.
   * 
   * @param ex
   *          the ex
   */
  public MimeTypeParseException(final Throwable ex) {
    super(ex);
  }
  
  /**
   * Instantiates a new mime type parse exception.
   * 
   * @param arg0
   *          the arg0
   * @param ex
   *          the ex          
   */
  public MimeTypeParseException(final String arg0, final Throwable ex) {
    super(arg0, ex);
  }
}
