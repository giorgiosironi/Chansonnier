/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
abstract class DSelectorCodec {

  /**
   * Constructor.
   */
  private DSelectorCodec() {

  }

  /**
   * @param dSelector -
   * @param element -
   * @return Element
   * @throws DSearchTemplatesException -
   */
  protected static Element encode(DSelector dSelector, Element element) throws DSearchTemplatesException {

    final Document doc = element.getOwnerDocument();
    final Element el = (Element) element.appendChild(doc.createElementNS(DSearchTemplatesCodec.NS, "Selector"));

    if (dSelector.getName() != null) {
      if (!dSelector.getName().trim().equals("")) {
        el.setAttribute("Name", dSelector.getName());
      }
    }

    final Iterator it = dSelector.getIndexFields();
    while (it.hasNext()) {
      DIndexFieldCodec.encode((DIndexField) it.next(), el);
    }

    return el;
  }

  protected static DSelector decode(Element element) throws DSearchTemplatesException {

    final DSelector dSelector = new DSelector();

    final String name = element.getAttribute("Name");
    if (!name.trim().equals("")) {
      dSelector.setName(name);
    }

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("IndexField".equals(nl.item(i).getLocalName())) {
        dSelector.addIndexField(DIndexFieldCodec.decode((Element) nl.item(i)));
      }
    }

    return dSelector;
  }
}
