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
public class DQueryConstraintsCodec {

  public static final String NS = "http://www.anyfinder.de/DataDictionary/Configuration";

  public static DQueryConstraints decode(Element element) throws ConfigurationException {

    final DQueryConstraints dQueryConstraints = new DQueryConstraints();

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("FieldConstraints".equals(nl.item(i).getLocalName())) {
        dQueryConstraints.addFieldConstraints(DFieldConstraintsCodec.decode((Element) nl.item(i)));
      }
    }

    return dQueryConstraints;
  } // End Method decode

  public static Element encode(DQueryConstraints dQueryConstraints, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element rootElement = doc.createElementNS(DConfigurationCodec.NS, "QueryConstraints");

    final DFieldConstraints[] fieldConstraints = dQueryConstraints.getFieldConstraints();
    for (int i = 0; i < fieldConstraints.length; i++) {
      DFieldConstraintsCodec.encode(fieldConstraints[i], rootElement);
    }

    element.appendChild(rootElement);
    return element;
  }

}
