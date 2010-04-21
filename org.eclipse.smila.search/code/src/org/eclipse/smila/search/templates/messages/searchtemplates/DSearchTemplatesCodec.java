/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import java.util.Iterator;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DSearchTemplatesCodec {
  /**
   * NS.
   */
  public static final String NS = "http://www.anyfinder.de/SearchTemplates";

  /**
   * Constructor.
   */
  private DSearchTemplatesCodec() {

  }

  /**
   * @param dSearchTemplates -
   * @return Document
   * @throws DSearchTemplatesException -
   */
  public static Document encode(DSearchTemplates dSearchTemplates) throws DSearchTemplatesException {

    final Document doc = XMLUtils.getDocument();
    final Element el = doc.createElementNS(DSearchTemplatesCodec.NS, "SearchTemplates");
    doc.appendChild(el);

    el.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    el.setAttribute("xsi:schemaLocation", NS + " ../xml/SearchTemplates.xsd");

    el.setAttribute("IndexName", dSearchTemplates.getIndexName());

    final Iterator it = dSearchTemplates.getTemplates();
    while (it.hasNext()) {
      DTemplateCodec.encode((DTemplate) it.next(), el);
    }

    return doc;
  }

  public static DSearchTemplates decode(Element element) throws DSearchTemplatesException {

    final DSearchTemplates dSearchTemplates = new DSearchTemplates();

    dSearchTemplates.setIndexName(element.getAttribute("IndexName"));

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Template".equals(nl.item(i).getLocalName())) {
        dSearchTemplates.addTemplate(DTemplateCodec.decode((Element) nl.item(i)));
      }
    }

    return dSearchTemplates;
  }
}
