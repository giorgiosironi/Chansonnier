/***********************************************************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.security;

/**
 * Constants for names and values of record security annotations commonly used in crawlers and search pipelines. Here is
 * a short example how the annotation structure could look like:
 * 
 * <pre>
 * ACCESS_RIGHTS
 * |
 * |-READ
 * | |
 * | |- PRINCIPALS 
 * | |  + user1 
 * | |  + user2 
 * | |
 * | |- GROUPS 
 * |    + group1 
 * |    + group2 
 * |     
 * |-WRITE
 *   |
 *   |- PRINCIPALS 
 *      + userX 
 *   ...
 * </pre>
 */
public final class SecurityAnnotations {

  /**
   * Constant for the base annotation ACCESS_RIGHTS.
   */
  public static final String ACCESS_RIGHTS = "ACCESS_RIGHTS";

  /**
   * predefined sub annotations of annotation "ACCESS_RIGHTS".
   * 
   */
  public enum AccessRightType {
    /**
     * type name of access right READ.
     */
    READ,
    /**
     * type name of access right WRITE.
     */
    WRITE
  }

  /**
   * predefined sub annotations of any annotation of enum AccessRightType.
   * 
   */
  public enum EntityType {
    /**
     * type name of principal PRINCIPALS.
     */
    PRINCIPALS,
    /**
     * type name of principal GROUPS.
     */
    GROUPS
  }

  /**
   * prevent instance creation.
   */
  private SecurityAnnotations() {
    // prevent instance creation
  }
}
