/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.Iterator;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DConfigurationCodec {

  public static final String NS = "http://www.anyfinder.de/DataDictionary/Configuration";

  public static DConfiguration decode(Element element) throws ConfigurationException {

    final DConfiguration dConfiguration = new DConfiguration();

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("DefaultConfig".equals(nl.item(i).getLocalName())) {
        dConfiguration.setDefaultConfig(DDefaultConfigCodec.decode((Element) nl.item(i)));
      } else if ("NamedConfig".equals(nl.item(i).getLocalName())) {
        dConfiguration.addNamedConfig(DNamedConfigCodec.decode((Element) nl.item(i)));
      } else if ("QueryConstraints".equals(nl.item(i).getLocalName())) {
        dConfiguration.setQueryConstraints(DQueryConstraintsCodec.decode((Element) nl.item(i)));
      }
    }
    return dConfiguration;
  } // End Method decode

  public static Document encode(DConfiguration dConfiguration) throws ConfigurationException {

    final Document doc = XMLUtils.getDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "Configuration");
    doc.appendChild(el);

    Attr attr = null;
    attr = doc.createAttribute("xmlns:xsi");
    attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
    el.setAttributeNode(attr);

    attr = doc.createAttribute("xsi:schemaLocation");
    attr.setValue(NS + " ../xml/DataDictionaryConfiguration.xsd");
    el.setAttributeNode(attr);

    DDefaultConfigCodec.encode(dConfiguration.getDefaultConfig(), el);

    Iterator it = dConfiguration.getNamedConfigs();
    while (it.hasNext()) {
      DNamedConfigCodec.encode((DNamedConfig) it.next(), el);
    }

    if (dConfiguration.getQueryConstraints() != null) {
      DQueryConstraintsCodec.encode(dConfiguration.getQueryConstraints(), el);
    }

    return doc;
  }

  public static Element encode(DConfiguration dConfiguration, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "Configuration");
    element.appendChild(el);

    Attr attr = null;
    attr = doc.createAttribute("xmlns:xsi");
    attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
    el.setAttributeNode(attr);

    attr = doc.createAttribute("xsi:schemaLocation");
    attr.setValue(NS + " ../xml/DataDictionaryConfiguration.xsd");
    el.setAttributeNode(attr);

    DDefaultConfigCodec.encode(dConfiguration.getDefaultConfig(), el);

    Iterator it = dConfiguration.getNamedConfigs();
    while (it.hasNext()) {
      DNamedConfigCodec.encode((DNamedConfig) it.next(), el);
    }

    if (dConfiguration.getQueryConstraints() != null) {
      DQueryConstraintsCodec.encode(dConfiguration.getQueryConstraints(), el);
    }

    return el;
  }
}
