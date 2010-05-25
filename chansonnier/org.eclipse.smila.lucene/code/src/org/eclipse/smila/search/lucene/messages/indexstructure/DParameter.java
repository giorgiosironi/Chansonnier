/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

public class DParameter {

  private String _name;

  private String _value;

  public DParameter(String name, String value) {
    this._name = name;
    this._value = value;
  }

  public void setName(String name) {
    this._name = name;
  }

  public String getName() {
    return _name;
  }

  public void setValue(String value) {
    this._value = value;
  }

  public String getValue() {
    return _value;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DParameter)) {
      return false;
    }
    final DParameter o = (DParameter) obj;
    if (!o.getName().equals(getName())) {
      return false;
    }
    if (!o.getValue().equals(getValue())) {
      return false;
    }
    return true;
  }
}
