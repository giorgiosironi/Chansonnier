/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.datadictionary;

import java.util.Enumeration;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2000 Company: BROX IT-Solutions GmbH
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.0
 */
public abstract class DAnyFinderDataDictionaryCodec {

  public static final String NS = "http://www.anyfinder.de/DataDictionary";

  public static final String NS_CONNECTION = "http://www.anyfinder.de/DataDictionary/Connection";

  public static DAnyFinderDataDictionary decode(Element element) throws DDException {

    final DAnyFinderDataDictionary dAnyFinderDataDictionary = new DAnyFinderDataDictionary();

    // decode index information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Index".equals(nl.item(i).getLocalName())) {
        dAnyFinderDataDictionary.addIndex(DIndexCodec.decode((Element) nl.item(i)));
      }
    }
    return dAnyFinderDataDictionary;
  } // End Method decode

  public static Document encode(DAnyFinderDataDictionary dAnyFinderDataDictionary) throws DDException {

    final Document doc = XMLUtils.getDocument();
    final Element rootElement = doc.createElementNS(DAnyFinderDataDictionaryCodec.NS, "AnyFinderDataDictionary");

    Attr attr = null;
    attr = doc.createAttribute("xmlns:xsi");
    attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
    rootElement.setAttributeNode(attr);

    attr = doc.createAttribute("xsi:schemaLocation");
    attr.setValue(NS + " ../xml/AnyFinderDataDictionary.xsd");
    rootElement.setAttributeNode(attr);

    final Enumeration enumeration = dAnyFinderDataDictionary.getIndices();

    while (enumeration.hasMoreElements()) {
      DIndexCodec.encode((DIndex) enumeration.nextElement(), rootElement);
    }

    doc.appendChild(rootElement);
    return doc;
  }
} // End Class def.
