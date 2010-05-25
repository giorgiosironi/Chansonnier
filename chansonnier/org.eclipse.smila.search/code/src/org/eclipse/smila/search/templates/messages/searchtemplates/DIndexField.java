/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

public class DIndexField {
  private String _name;

  private int _fieldNo = -1;

  public DIndexField() {
  }

  public void setName(String name) {
    this._name = name;
  }

  public String getName() {
    return this._name;
  }

  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  public int getFieldNo() {
    return this._fieldNo;
  }

}
