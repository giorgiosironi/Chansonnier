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

import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementCategory;
import org.eclipse.smila.management.ManagementRegistration;

/**
 * The Class ManagementAgentLocationImpl.
 */
public class ManagementAgentLocationImpl extends ManagementTreeNodeImpl implements ManagementAgentLocation {

  /**
   * The _category.
   */
  private final ManagementCategory _category;

  /**
   * The _name.
   */
  private final String _name;

  /**
   * Instantiates a new management agent location impl.
   * 
   * @param category
   *          the category
   * @param name
   *          the name
   */
  public ManagementAgentLocationImpl(final ManagementCategory category, final String name) {
    if (name == null) {
      throw new IllegalArgumentException("Location name cannot be null!");
    }
    final Pattern pattern = Pattern.compile("[^\\w]", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(name);
    if (matcher.matches()) {
      throw new IllegalArgumentException("Name should contains only words symbols,  digits or underscore");
    }
    _category = category;
    _name = name;
    if (_category == null) {
      _path = _name;
    } else {
      _path = String.format("%s/%s", _category.getPath(), _name);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementAgentLocation#getCategory()
   */
  public ManagementCategory getCategory() {
    return _category;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementAgentLocation#getName()
   */
  public String getName() {
    return _name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementAgentLocation#register(org.eclipse.smila.management.ManagementAgent)
   */
  public void register(final ManagementAgent agent) {
    ManagementRegistration.INSTANCE.registerAgent(this, agent);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementAgentLocation#unregister(org.eclipse.smila.management.ManagementAgent)
   */
  public void unregister(final ManagementAgent agent) {
    ManagementRegistration.INSTANCE.unregisterAgent(this);
  }
}
