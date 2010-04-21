/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;

/**
 * The Class ManagementAgentNotFoundException.
 */
public class ManagementAgentNotFoundException extends RuntimeException {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new management agent not found exception.
   * 
   * @param location
   *          the location
   */
  public ManagementAgentNotFoundException(final ManagementAgentLocation location) {
    super(String.format("Management agent [%s] is not found", location));
  }

}
