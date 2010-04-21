/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DDefaultConfigCodec {

  public static DDefaultConfig decode(Element element) throws ConfigurationException {

    final DDefaultConfig dDefaultConfig = new DDefaultConfig();

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Field".equals(nl.item(i).getLocalName())) {
        dDefaultConfig.addField(DFieldCodec.decode((Element) nl.item(i)));
      }
    }

    return dDefaultConfig;
  } // End Method decode

  public static Element encode(DDefaultConfig dDefaultConfig, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element rootElement = doc.createElementNS(DConfigurationCodec.NS, "DefaultConfig");

    final Iterator fields = dDefaultConfig.getFields();
    while (fields.hasNext()) {
      DFieldCodec.encode((DField) fields.next(), rootElement);
    }

    element.appendChild(rootElement);
    return element;
  }
}
