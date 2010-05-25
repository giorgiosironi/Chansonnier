/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import java.util.Date;
import java.util.Vector;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class DDateList extends DParameter implements Cloneable {

  private Vector _values = new Vector();

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void addValue(Date value) {
    _values.addElement(value);
  }

  /**
   * Sets the value.
   * 
   * @param values
   *          The value to set
   */
  public void addValues(Date[] values) {
    for (int i = 0; i < values.length; i++) {
      this._values.addElement(values[i]);
    }
  }

  @Override
  public Object clone() {
    final DDateList dl = (DDateList) super.clone();
    dl._values = new Vector();
    for (int i = 0; i < _values.size(); i++) {
      dl.addValue((Date) ((Date) _values.elementAt(i)).clone());
    }
    return dl;
  }

  public boolean equals(DDateList parameter) {
    if (parameter == null) {
      return false;
    }

    if (!parameter.getName().equals(getName())) {
      return false;
    }

    if (!parameter.getType().equals(getType())) {
      return false;
    }

    if (parameter._values.size() != _values.size()) {
      return false;
    }
    for (int i = 0; i < parameter._values.size(); i++) {
      if (!parameter._values.get(i).equals(_values.get(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof DDateList)) {
      return false;
    }

    return equals((DDateList) obj);
  }

  /**
   * @return Date
   */
  public Date[] getValues() {
    final Date[] array = new Date[_values.size()];
    _values.copyInto(array);
    return array;
  }

}
