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
public class DFloatList extends DParameter {

  private final Vector _values = new Vector();

  /**
   * Sets the value.
   * 
   * @param value
   *          The value to set
   */
  public void addDefault(float value) {
    _defaultExists = true;
    _values.addElement(new Float(value));
  }

  /**
   * @return Float
   */
  public float[] getDefaults() {
    final float[] array = new float[_values.size()];
    for (int i = 0; i < array.length; i++) {
      array[i] = ((Float) _values.elementAt(i)).floatValue();
    }
    return array;
  }

}
