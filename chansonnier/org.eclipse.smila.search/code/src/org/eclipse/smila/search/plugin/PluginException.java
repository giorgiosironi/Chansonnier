/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.plugin;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PluginException extends Exception {

  private static final long serialVersionUID = 7526472295622776147L;

  /**
   * 
   */
  public PluginException() {
    super();
  }

  /**
   * @param message -
   */
  public PluginException(String message) {
    super(message);
  }

  /**
   * @param message -
   * @param cause -
   */
  public PluginException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause -
   */
  public PluginException(Throwable cause) {
    super(cause);
  }

}
