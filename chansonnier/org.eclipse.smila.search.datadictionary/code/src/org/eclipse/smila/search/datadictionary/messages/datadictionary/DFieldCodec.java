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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract class DFieldCodec {

  protected static DField decode(Element element) throws DDException {

    final DField dField = new DField();

    // get field index information
    dField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));

    // get field name information
    dField.setName(element.getAttribute("Name"));

    return dField;
  } // End Method decode

  protected static Element encode(DField dField, Element element) throws DDException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DAnyFinderDataDictionaryCodec.NS, "Field");

    Attr attr = null;

    attr = doc.createAttribute("FieldNo");
    attr.setValue(dField.getFieldNo() + "");
    el.setAttributeNode(attr);

    attr = doc.createAttribute("Name");
    attr.setValue(dField.getName());
    el.setAttributeNode(attr);

    element.appendChild(el);
    return el;
  } // End Method encode

} // End Class def.
