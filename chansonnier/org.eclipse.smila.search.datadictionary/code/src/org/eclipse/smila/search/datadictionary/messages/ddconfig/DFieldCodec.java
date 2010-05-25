/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DFieldCodec {

  public static DField decode(Element element) throws ConfigurationException {

    final DField dField = new DField();

    dField.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("FieldConfig".equals(nl.item(i).getLocalName())) {
        dField.setFieldConfig(DFieldConfigCodec.decode((Element) nl.item(i)));
      }
    }

    return dField;
  } // End Method def.

  public static Element encode(DField dField, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "Field");

    el.setAttribute("FieldNo", "" + dField.getFieldNo());

    DFieldConfigCodec.encode(dField.getFieldConfig(), el);

    element.appendChild(el);
    return el;
  }

}
