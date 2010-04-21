/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConfigurationException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public ConfigurationException() {
    super();
  }

  /**
   * @param message
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   */
  public ConfigurationException(Throwable cause) {
    super(cause);
  }
}
