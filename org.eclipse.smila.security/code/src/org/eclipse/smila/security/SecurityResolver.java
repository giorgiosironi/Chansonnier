/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The interface SecurityResolver.
 */
public interface SecurityResolver {

  /**
   * Resolves a given name to a full form principal (e.g. a distinguished name).
   * @param name the name of the principal
   * @return the full form principal
   * @throws SecurityException if any error occurs
   */
  String resolvePrincipal(String name) throws SecurityException;
  
  /**
   * Returns all properties of the given principal. The properties are a map of attribute names (String) and attribute
   * values (Collection of Strings).
   * 
   * @param principal
   *          the principal
   * @return all properties if the principal
   * @throws SecurityException
   *           if any error occurs
   */
  Map<String, Collection<String>> getProperties(String principal) throws SecurityException;

  /**
   * Returns all principals that are member to the given group, including any subgroups.
   * 
   * @param group
   *          the group principal
   * @return a set of all principals that are members of this group
   * @throws SecurityException
   *           if any error occurs
   */
  Set<String> resolveGroupMembers(String group) throws SecurityException;

  /**
   * Returns all groups the given principal is member of.
   * 
   * @param principal
   *          the principal
   * @return a set of group principals the principal is member of
   * @throws SecurityException
   *           if any error occurs
   */
  Set<String> resolveMembership(String principal) throws SecurityException;

  /**
   * Checks if the given principal is a group.
   * 
   * @param principal
   *          the principal
   * @return true if the principal is a group, false otherwise
   * @throws SecurityException
   *           if any error occurs
   */
  boolean isGroup(String principal) throws SecurityException;
}
