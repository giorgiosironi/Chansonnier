/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;

/**
 * The Interface ManagementCategory.
 */
public interface ManagementCategory extends ManagementTreeNode {

  /**
   * Gets the category.
   * 
   * @param name
   *          the name
   * 
   * @return the category
   */
  ManagementCategory getCategory(String name);

  /**
   * Gets the location.
   * 
   * @param name
   *          the name
   * 
   * @return the location
   */
  ManagementAgentLocation getLocation(String name);

}
