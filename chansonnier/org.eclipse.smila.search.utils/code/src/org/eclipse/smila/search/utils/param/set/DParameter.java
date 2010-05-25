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
public abstract class DParameter implements Cloneable {

  private String _name;

  private String _type;

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DParameter", e);
    }
  }

  @Override
  public abstract boolean equals(Object obj);

  /**
   * @return String
   */
  public String getName() {
    return _name;
  }

  /**
   * @return String
   */
  public String getType() {
    return _type;
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
