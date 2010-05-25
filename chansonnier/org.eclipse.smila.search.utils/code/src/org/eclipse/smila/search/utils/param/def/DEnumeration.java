/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

import java.util.Vector;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class DEnumeration extends DParameter {

  private final Vector _defaults = new Vector();

  private final Vector _values = new Vector();

  private boolean _allowMultiple;

  /**
   * Sets the value.
   * 
   * @param defaultValue
   *          The value to set
   */
  public void addDefault(String defaultValue) {
    _defaultExists = true;
    _defaults.addElement(defaultValue);
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void addValue(String value) {
    _values.addElement(value);
  }

  /**
   * @return String
   */
  public String[] getDefaults() {
    final String[] array = new String[_defaults.size()];
    _defaults.copyInto(array);
    return array;
  }

  /**
   * @return String
   */
  public String[] getValues() {
    final String[] array = new String[_values.size()];
    _values.copyInto(array);
    return array;
  }

  /**
   * @return boolean
   */
  public boolean isAllowMultiple() {
    return _allowMultiple;
  }

  /**
   * Sets the allowMultiple.
   * 
   * @param allowMultiple
   *          The allowMultiple to set
   */
  public void setAllowMultiple(boolean allowMultiple) {
    this._allowMultiple = allowMultiple;
  }

}
