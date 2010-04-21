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
public class DNamedConfigCodec {

  public static DNamedConfig decode(Element element) throws ConfigurationException {

    final DNamedConfig dNamedConfig = new DNamedConfig();

    dNamedConfig.setName(element.getAttribute("Name"));

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("FieldConfig".equals(nl.item(i).getLocalName())) {
        dNamedConfig.addFieldConfig(DFieldConfigCodec.decode((Element) nl.item(i)));
      }
    }

    return dNamedConfig;
  } // End Method decode

  public static Element encode(DNamedConfig dNamedConfig, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element rootElement = doc.createElementNS(DConfigurationCodec.NS, "NamedConfig");

    rootElement.setAttribute("Name", "" + dNamedConfig.getName());

    final DFieldConfig[] fieldConfig = dNamedConfig.getFieldConfig();
    for (int i = 0; i < fieldConfig.length; i++) {
      DFieldConfigCodec.encode(fieldConfig[i], rootElement);
    }

    element.appendChild(rootElement);
    return element;
  }

}
