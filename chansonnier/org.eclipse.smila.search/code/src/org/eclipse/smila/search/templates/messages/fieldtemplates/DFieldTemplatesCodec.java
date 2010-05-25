/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

import java.util.Iterator;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DFieldTemplatesCodec {
  /**
   * NS.
   */
  public static final String NS = "http://www.anyfinder.de/FieldTemplates";

  /**
   * Constructor.
   */
  private DFieldTemplatesCodec() {

  }

  /**
   * @param dSearchTemplates -
   * @return Document
   * @throws DFieldTemplatesException -
   */
  public static Document encode(DFieldTemplates dSearchTemplates) throws DFieldTemplatesException {

    final Document doc = XMLUtils.getDocument();
    final Element el = doc.createElementNS(DFieldTemplatesCodec.NS, "FieldTemplates");
    doc.appendChild(el);

    el.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    el.setAttribute("xsi:schemaLocation", NS + " ../xml/FieldTemplates.xsd");

    el.setAttribute("IndexName", dSearchTemplates.getIndexName());

    final Iterator it = dSearchTemplates.getTemplates();
    while (it.hasNext()) {
      DFieldTemplateCodec.encode((DFieldTemplate) it.next(), el);
    }

    return doc;
  }

  public static DFieldTemplates decode(Element element) throws DFieldTemplatesException {

    final DFieldTemplates dSearchTemplates = new DFieldTemplates();

    dSearchTemplates.setIndexName(element.getAttribute("IndexName"));

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("FieldTemplate".equals(nl.item(i).getLocalName())) {
        dSearchTemplates.addTemplate(DFieldTemplateCodec.decode((Element) nl.item(i)));
      }
    }

    return dSearchTemplates;
  }
}
