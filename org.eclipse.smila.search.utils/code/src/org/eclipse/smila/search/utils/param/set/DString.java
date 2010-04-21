/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DString extends DParameter implements Cloneable {

  private String _value;

  public boolean equals(DString parameter) {
    if (parameter == null) {
      return false;
    }

    if (!parameter.getName().equals(getName())) {
      return false;
    }

    if (!parameter.getType().equals(getType())) {
      return false;
    }

    if (!parameter.getValue().equals(getValue())) {
      return false;
    }

    return true;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof DString)) {
      return false;
    }

    return equals((DString) obj);
  }

  /**
   * @return String
   */
  public String getValue() {
    return _value;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void setValue(String value) {
    this._value = value;
  }

}
