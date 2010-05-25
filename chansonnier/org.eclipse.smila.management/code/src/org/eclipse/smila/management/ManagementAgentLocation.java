/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;

/**
 * The Interface ManagementAgentLocation.
 */
public interface ManagementAgentLocation extends ManagementTreeNode {

  /**
   * Gets the name.
   * 
   * @return the name
   */
  String getName();

  /**
   * Gets the category.
   * 
   * @return the category
   */
  ManagementCategory getCategory();

  /**
   * Register.
   * 
   * @param agent
   *          the agent
   */
  void register(ManagementAgent agent);

  /**
   * Unregister.
   * 
   * @param agent
   *          the agent
   */
  void unregister(ManagementAgent agent);
}
