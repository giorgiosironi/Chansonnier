/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DField {

  private String name;

  private int fieldNo;

  /**
   * Constructor
   */
  public DField() {
  }

  public DField(int fieldNo, String name) {
    setFieldNo(fieldNo);
    setName(name);
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }// End Method equals

  public int getFieldNo() {
    return this.fieldNo;
  }

  public String getName() {
    return this.name;
  }

  public void setFieldNo(int fieldNo) {
    this.fieldNo = fieldNo;
  }

  public void setName(String value) {
    this.name = value;
  }

  @Override
  public String toString() {
    try {
      final Element el = DFieldCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End class def.

