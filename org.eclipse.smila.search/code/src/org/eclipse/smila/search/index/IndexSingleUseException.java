/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

/**
 * @author August Georg Schmidt (BROX)
 */
public class IndexSingleUseException extends IndexException {

  /**
   * 
   */
  private static final long serialVersionUID = 7526472295622776147L;

  /**
   * 
   */
  public IndexSingleUseException() {
    super();
  }

  /**
   * @param message
   *          Message.
   */
  public IndexSingleUseException(String message) {
    super(message);
  }

  /**
   * @param message
   *          Message.
   * @param cause
   *          Cause.
   */
  public IndexSingleUseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   *          Cause.
   */
  public IndexSingleUseException(Throwable cause) {
    super(cause);
  }
}
