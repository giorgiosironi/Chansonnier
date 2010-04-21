/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.management.test.agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.DeclarativeManagementAgent;

/**
 * The Class ConfigurationAgent.
 */
public class ConfigurationAgent extends DeclarativeManagementAgent {

  /**
   * sid.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * The _int property.
   */
  private Integer _intProperty = 0;

  /**
   * The _properties.
   */
  private final List<String> _properties = new ArrayList<String>();

  /**
   * Gets the properties.
   * 
   * @return the properties
   */
  public List<String> getProperties() {
    return _properties;
  }

  /**
   * Adds the property.
   * 
   * @param property
   *          the property
   */
  public void addProperty(final String property) {
    log("Invoked adding property \"" + property + "\"");
    _properties.add(property);
  }

  /**
   * Gets the int property.
   * 
   * @return the int property
   */
  public Integer getIntProperty() {
    log("Invoked getter for IntProperty");
    return _intProperty;
  }

  /**
   * Sets the int property.
   * 
   * @param intProperty
   *          the new int property
   */
  public void setIntProperty(final Integer intProperty) {
    log("Invoked setter for IntProperty(" + intProperty + ")");
    _intProperty = intProperty;
  }

  /**
   * Log.
   * 
   * @param text
   *          the text
   */
  private void log(final String text) {
    _log.info(text);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getName()
   */
  @Override
  public String getName() {
    return "sampleAgent";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getCategory()
   */
  @Override
  protected String getCategory() {
    return null;
  }
}
