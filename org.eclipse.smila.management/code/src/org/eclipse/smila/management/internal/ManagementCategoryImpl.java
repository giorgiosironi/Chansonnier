/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementCategory;

/**
 * The Class ManagementCategoryImpl.
 */
public class ManagementCategoryImpl extends ManagementTreeNodeImpl implements ManagementCategory {

  /**
   * Instantiates a new management category impl.
   * 
   * @param name
   *          the name
   */
  public ManagementCategoryImpl(final String name) {
    this(name, null);
  }

  /**
   * Instantiates a new management category impl.
   * 
   * @param name
   *          the name
   * @param parentCategory
   *          the parent category
   */
  public ManagementCategoryImpl(String name, final ManagementCategory parentCategory) {
    if (name != null) {
      final Pattern pattern = Pattern.compile("[^\\w\\/]", Pattern.CASE_INSENSITIVE);
      final Matcher matcher = pattern.matcher(name);
      if (matcher.matches()) {
        throw new IllegalArgumentException("Name should contains only words symbols,  digits, underscore or /");
      }
      name = name.replaceAll("^/+", "");
      name = name.replaceAll("/+$", "");
    }
    String path;
    if (parentCategory != null) {
      path = parentCategory.getPath();
    } else {
      path = "";
    }
    if (name != null) {
      if (path != null && !"".equals(path)) {
        path += "/";
      }
      path += name;
    }
    _path = path;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementCategory#getCategory(java.lang.String)
   */
  public ManagementCategory getCategory(final String name) {
    return new ManagementCategoryImpl(name, this);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementCategory#getLocation(java.lang.String)
   */
  public ManagementAgentLocation getLocation(final String name) {
    return new ManagementAgentLocationImpl(this, name);
  }

}
