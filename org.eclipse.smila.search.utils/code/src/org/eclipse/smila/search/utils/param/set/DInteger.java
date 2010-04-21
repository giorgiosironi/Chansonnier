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
public class DInteger extends DParameter implements Cloneable {

  private int _value;

  public boolean equals(DInteger parameter) {
    if (parameter == null) {
      return false;
    }

    if (!parameter.getName().equals(getName())) {
      return false;
    }

    if (!parameter.getType().equals(getType())) {
      return false;
    }

    if (parameter.getValue() != getValue()) {
      return false;
    }

    return true;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof DInteger)) {
      return false;
    }

    return equals((DInteger) obj);
  }

  /**
   * @return Integer
   */
  public int getValue() {
    return _value;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void setValue(int value) {
    this._value = value;
  }

}
