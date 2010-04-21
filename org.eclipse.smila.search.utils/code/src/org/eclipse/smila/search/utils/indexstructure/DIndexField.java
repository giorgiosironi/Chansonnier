/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.indexstructure;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DIndexField {

  private int _fieldNo = -1;

  private String _name;

  private String _type;

  private boolean _storeText;

  /**
   * 
   */
  public DIndexField() {
    super();
  }

  /**
   * @param fieldNo
   *          The fieldNo to set
   * @param name
   *          The name to set
   */
  public DIndexField(int fieldNo, String name) {
    super();

    this._fieldNo = fieldNo;
    this._name = name;
  }

  public abstract boolean equals(DIndexField dIF);

  public abstract boolean equalsStructure(DIndexField dIF, boolean throwException) throws ISException;

  /**
   * @return int
   */
  public int getFieldNo() {
    return _fieldNo;
  }

  /**
   * @return String
   */
  public String getName() {
    return _name;
  }

  public boolean getStoreText() {
    return this._storeText;
  }

  /**
   * @return String
   */
  public String getType() {
    return _type;
  }

  /**
   * Sets the fieldNo.
   * 
   * @param fieldNo
   *          The fieldNo to set
   */
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
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

  public void setStoreText(boolean storeText) {
    this._storeText = storeText;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          The type to set
   */
  public void setType(String type) {
    this._type = type;
  }

}
