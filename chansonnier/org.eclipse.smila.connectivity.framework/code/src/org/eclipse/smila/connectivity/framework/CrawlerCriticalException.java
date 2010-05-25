/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

/**
 * The Class CrawlerCriticalException.
 */
public class CrawlerCriticalException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new crawler critical exception.
   * 
   * @param message
   *          the message
   */
  public CrawlerCriticalException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new crawler critical exception.
   * 
   * @param cause
   *          the cause
   */
  public CrawlerCriticalException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new crawler critical exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public CrawlerCriticalException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
