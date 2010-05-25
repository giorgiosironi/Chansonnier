/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDefaultConfig {

  private final Hashtable<Integer, DField> _fields = new Hashtable<Integer, DField>();

  public DDefaultConfig() {
  }

  public void addField(DField field) {
    _fields.put(new Integer(field.getFieldNo()), field);
  }

  public DField getField(int fieldNo) {
    return _fields.get(new Integer(fieldNo));
  }

  public int getFieldCount() {
    return _fields.size();
  }

  /**
   * @return Iterator
   */
  public Iterator getFields() {
    return _fields.values().iterator();
  }

  /**
   * Sets the fields.
   * 
   * @param fields
   *          The fields to set
   */
  public void setFields(DField[] fields) {
    this._fields.clear();
    for (int i = 0; i < fields.length; i++) {
      this._fields.put(new Integer(fields[i].getFieldNo()), fields[i]);
    }
  }

}
