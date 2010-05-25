/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import java.util.ArrayList;
import java.util.Iterator;

public class DSelector {
  private String _name;

  private ArrayList<DIndexField> _indexFields = new ArrayList<DIndexField>();

  public DSelector() {
  }

  public void setName(String name) {
    this._name = name;
  }

  public String getName() {
    return this._name;
  }

  public void addIndexField(DIndexField dIndexField) {
    _indexFields.add(dIndexField);
  }

  public void setIndexField(int index, DIndexField dIndexField) {
    _indexFields.set(index, dIndexField);
  }

  public DIndexField getIndexField(int index) {
    return _indexFields.get(index);
  }

  public Iterator<DIndexField> getIndexFields() {
    return _indexFields.iterator();
  }

  public int getIndexFieldCount() {
    return _indexFields.size();
  }

}
