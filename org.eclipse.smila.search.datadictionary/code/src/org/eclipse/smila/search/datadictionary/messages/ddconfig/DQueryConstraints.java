/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.ArrayList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DQueryConstraints {

  private final ArrayList<DFieldConstraints> _fieldConstraints = new ArrayList<DFieldConstraints>();

  public void addFieldConstraints(DFieldConstraints FieldConstraints) {
    this._fieldConstraints.add(FieldConstraints);
  }

  public DFieldConstraints[] getFieldConstraints() {
    final DFieldConstraints[] confs = _fieldConstraints.toArray(new DFieldConstraints[0]);
    return confs;
  }

  public DFieldConstraints getFieldConstraints(int index) {
    return _fieldConstraints.get(index);
  }

  public int getFieldConstraintsLength() {
    return _fieldConstraints.size();
  }

  public void removeFieldConstraints(int index) {
    this._fieldConstraints.remove(index);
  }

  public void setFieldConstraints(DFieldConstraints[] FieldConstraints) {
    this._fieldConstraints.clear();
    for (int i = 0; i < FieldConstraints.length; i++) {
      this._fieldConstraints.add(FieldConstraints[i]);
    }
  }

}
