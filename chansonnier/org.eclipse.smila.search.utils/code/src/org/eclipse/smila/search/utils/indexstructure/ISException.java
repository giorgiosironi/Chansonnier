/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.indexstructure;

/**
 * **************************************************************************** Title: Any Finder Description:
 * Copyright: Copyright (c) 2000 Company: BROX IT-Solutions GmbH.
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.3.0
 */
public class ISException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public ISException() {
    super();
  }

  /**
   * @param message
   *          -
   */
  public ISException(String message) {
    super(message);
  }

  /**
   * @param message
   *          -
   * @param cause
   *          -
   */
  public ISException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          -
   */
  public ISException(Throwable cause) {
    super(cause);
  }
}
