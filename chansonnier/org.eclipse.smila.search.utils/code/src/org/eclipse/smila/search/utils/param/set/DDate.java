/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import java.util.Date;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDate extends DParameter implements Cloneable {

  private Date _value;

  @Override
  public Object clone() {
    final DDate obj = (DDate) super.clone();

    if (_value != null) {
      obj.setValue((Date) _value.clone());
    }

    return obj;
  }

  public boolean equals(DDate parameter) {

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

    if (!(obj instanceof DDate)) {
      return false;
    }

    return equals((DDate) obj);
  }

  /**
   * @return Date
   */
  public Date getValue() {
    return _value;
  }

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void setValue(Date value) {
    this._value = value;
  }

}
