/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.indexstructure;

import java.util.Iterator;

import org.eclipse.smila.search.utils.search.DField;

public abstract class DIndexStructure {

  private String _name;

  public abstract DIndexField addField(DIndexField dIndexField);

  public abstract boolean doesFieldTypeMatch(DField field);

  public boolean equals(DIndexStructure indexStructure) {
    try {
      return equalsStructure(indexStructure, false);
    } catch (final ISException e) {
      return false;
    }
  }

  public abstract boolean equalsStructure(DIndexStructure indexStructure, boolean throwException)
    throws ISException;

  public abstract DIndexField getField(int fieldNo);

  public abstract int getFieldCount();

  public abstract Iterator getFields();

  /**
   * @return String
   */
  public String getName() {
    return this._name;
  }

  public abstract boolean hasField(int fieldNo);

  public abstract DIndexField removeField(int fieldNo);

  /**
   * Sets the name.
   * 
   * @param name
   *          The name to set
   */
  public void setName(String name) {
    this._name = name;
  }

} // End class def.
