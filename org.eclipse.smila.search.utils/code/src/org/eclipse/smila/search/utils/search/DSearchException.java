/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

public class DSearchException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public DSearchException() {
    super();
  }

  /**
   * @param message
   */
  public DSearchException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public DSearchException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   */
  public DSearchException(Throwable cause) {
    super(cause);
  }
}
