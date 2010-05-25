/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import java.util.Vector;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
@SuppressWarnings("unchecked")
public class DIntegerList extends DParameter implements Cloneable {

  private Vector _values = new Vector();

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void addValue(int value) {
    _values.addElement(new Integer(value));
  }

  public void addValues(int[] values) {
    for (int i = 0; i < values.length; i++) {
      this._values.addElement(new Integer(values[i]));
    }
  }

  @Override
  public Object clone() {
    final DIntegerList il = (DIntegerList) super.clone();
    il._values = new Vector();
    for (int i = 0; i < _values.size(); i++) {
      il.addValue(((Integer) _values.elementAt(i)).intValue());
    }
    return il;
  }

  public boolean equals(DIntegerList parameter) {
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

    if (!(obj instanceof DIntegerList)) {
      return false;
    }

    return equals((DIntegerList) obj);
  }

  /**
   * @return Integer
   */
  public int[] getValues() {
    final int[] array = new int[_values.size()];
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Integer) _values.elementAt(i)).intValue();
    }
    return array;
  }

}
