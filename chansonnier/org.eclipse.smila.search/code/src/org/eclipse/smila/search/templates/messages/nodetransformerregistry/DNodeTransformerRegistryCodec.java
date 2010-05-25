/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.nodetransformerregistry;

import java.util.Iterator;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DNodeTransformerRegistryCodec {

  /**
   * NS.
   */
  public static final String NS = "http://www.anyfinder.de/NodeTransformerRegistry";

  /**
   * Constructor.
   */
  private DNodeTransformerRegistryCodec() {

  }

  /**
   * @param dNodeTransformerRegistry -
   * @return Document.
   * @throws DNodeTransformerRegistryException -
   */
  public static Document encode(DNodeTransformerRegistry dNodeTransformerRegistry)
    throws DNodeTransformerRegistryException {

    final Document doc = XMLUtils.getDocument();
    final Element el = doc.createElementNS(NS, "NodeTransformerRegistry");
    doc.appendChild(el);

    el.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    el.setAttribute("xsi:schemaLocation", NS + " ../xml/NodeTransformerRegistry.xsd");

    final Iterator it = dNodeTransformerRegistry.getNodeTransformers();
    while (it.hasNext()) {
      DNodeTransformerCodec.encode((DNodeTransformer) it.next(), el);
    }

    return doc;
  }

  public static DNodeTransformerRegistry decode(Element element) throws DNodeTransformerRegistryException {

    final DNodeTransformerRegistry dNodeTransformerRegistry = new DNodeTransformerRegistry();

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("NodeTransformer".equals(nl.item(i).getLocalName())) {
        dNodeTransformerRegistry.addNodeTransformer(DNodeTransformerCodec.decode((Element) nl.item(i)));
      }
    }

    return dNodeTransformerRegistry;
  }
}
