/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.ArrayList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DNamedConfig extends DFieldConfig {

  private String _name;

  private final ArrayList<DFieldConfig> _fieldConfig = new ArrayList<DFieldConfig>();

  public void addFieldConfig(DFieldConfig fieldConfig) {
    this._fieldConfig.add(fieldConfig);
  }

  public DFieldConfig[] getFieldConfig() {
    final DFieldConfig[] confs = _fieldConfig.toArray(new DFieldConfig[0]);
    return confs;
  }

  public DFieldConfig getFieldConfig(int index) {
    return _fieldConfig.get(index);
  }

  public DFieldConfig getFieldConfig(org.eclipse.smila.search.utils.search.DField field) {
    final DFieldConfig[] fcs = getFieldConfig();
    for (int i = 0; i < fcs.length; i++) {
      if ((field instanceof org.eclipse.smila.search.utils.search.DTextField)
        && (fcs[i] instanceof DTextField)) {
        return fcs[i];
      }
      if ((field instanceof org.eclipse.smila.search.utils.search.DNumberField)
        && (fcs[i] instanceof DNumberField)) {
        return fcs[i];
      }
      if ((field instanceof org.eclipse.smila.search.utils.search.DDateField)
        && (fcs[i] instanceof DDateField)) {
        return fcs[i];
      }
    }
    return null;
  }

  public int getFieldConfigLength() {
    return _fieldConfig.size();
  }

  /**
   * @return String
   */
  public String getName() {
    return _name;
  }

  public void removeFieldConfig(int index) {
    this._fieldConfig.remove(index);
  }

  public void setFieldConfig(DFieldConfig[] fieldConfig) {
    this._fieldConfig.clear();
    for (int i = 0; i < fieldConfig.length; i++) {
      this._fieldConfig.add(fieldConfig[i]);
    }
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          The name to set
   */
  public void setName(String name) {
    this._name = name;
  }

}
