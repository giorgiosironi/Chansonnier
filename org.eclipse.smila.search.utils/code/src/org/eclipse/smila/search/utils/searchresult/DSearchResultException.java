/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

/**
 * Exception class for AnyFinderSearchResults.
 * 
 * @author brox IT-Solutions GmbH
 */
public class DSearchResultException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public DSearchResultException() {
    super();
  }

  /**
   * @param message
   *          Reason for exception.
   */
  public DSearchResultException(String message) {
    super(message);
  }

  /**
   * @param message
   *          Reason for exception.
   * @param cause
   *          Cause of exception.
   */
  public DSearchResultException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          Cause of exception.
   */
  public DSearchResultException(Throwable cause) {
    super(cause);
  }
}
