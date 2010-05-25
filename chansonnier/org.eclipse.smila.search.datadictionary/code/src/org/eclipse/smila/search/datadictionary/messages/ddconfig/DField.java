/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DField {

  private int fieldNo;

  private DFieldConfig fieldConfig;

  /**
   * @return FieldConfig
   */
  public DFieldConfig getFieldConfig() {
    return fieldConfig;
  }

  /**
   * @return int
   */
  public int getFieldNo() {
    return fieldNo;
  }

  /**
   * Sets the fieldConfig.
   * 
   * @param fieldConfig
   *          The fieldConfig to set
   */
  public void setFieldConfig(DFieldConfig fieldConfig) {
    this.fieldConfig = fieldConfig;
  }

  /**
   * Sets the fieldNo.
   * 
   * @param fieldNo
   *          The fieldNo to set
   */
  public void setFieldNo(int fieldNo) {
    if (fieldNo < 0) {
      throw new IllegalArgumentException("FieldNo must be >= 0 in Field");
    }
    this.fieldNo = fieldNo;
  }

}
