/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class DQueryCodec {

  protected static DQuery decode(Element element) throws DSearchException {

    final DQuery dQuery = new DQuery();

    dQuery.setIndexName(element.getAttribute("IndexName"));
    dQuery.setMaxHits(Integer.parseInt(element.getAttribute("MaxHits")));
    dQuery.setMinSimilarity(Integer.parseInt(element.getAttribute("MinSimilarity")));
    dQuery.setShowHitDistribution(Boolean.valueOf(element.getAttribute("ShowHitDistribution")).booleanValue());

    if (element.hasAttribute("TemplateSelectorName")) {
      dQuery.setTemplateSelectorName(element.getAttribute("TemplateSelectorName"));
    }

    if (element.hasAttribute("StartHits")) {
      dQuery.setStartHits(new Integer(element.getAttribute("StartHits")));
    }

    // get field informations
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Field".equals(nl.item(i).getLocalName())) {
        dQuery.addField(DFieldCodec.decode((Element) nl.item(i)));
      }
    }
    return dQuery;
  }

  protected static Element encode(DQuery dQuery, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DFieldCodec.NS, "Query");

    el.setAttribute("IndexName", dQuery.getIndexName());
    el.setAttribute("MaxHits", dQuery.getMaxHits() + "");
    el.setAttribute("MinSimilarity", dQuery.getMinSimilarity() + "");
    el.setAttribute("ShowHitDistribution", dQuery.getShowHitDistribution() ? "true" : "false");

    if (dQuery.getTemplateSelectorName() != null) {
      if (!dQuery.getTemplateSelectorName().trim().equals("")) {
        el.setAttribute("TemplateSelectorName", dQuery.getTemplateSelectorName());
      }
    }

    if (dQuery.getStartHits() != null) {
      el.setAttribute("StartHits", dQuery.getStartHits().toString());
    }

    // encode fields
    final Enumeration enumeration = dQuery.getFields();

    while (enumeration.hasMoreElements()) {
      DFieldCodec.encode((DField) enumeration.nextElement(), el);
    }

    element.appendChild(el);
    return el;
  }

} // End class def.

