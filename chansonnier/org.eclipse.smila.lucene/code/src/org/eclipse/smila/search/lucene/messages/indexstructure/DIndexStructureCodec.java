/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import java.util.Iterator;

import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public abstract class DIndexStructureCodec {

  /**
   * Constructor.
   */
  private DIndexStructureCodec() {

  }

  /**
   * NS.
   */
  public static final String NS = "http://www.anyfinder.de/IndexStructure";

  /**
   * @param dIS -
   * @param element -
   * @return Element
   * @throws ISException -
   */
  public static Element encode(DIndexStructure dIS, Element element) throws ISException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DIndexStructureCodec.NS, "IndexStructure");

    Attr attr = null;

    if (dIS.getName() != null) {
      attr = doc.createAttribute("Name");
      attr.setValue(dIS.getName());
      el.setAttributeNode(attr);
    } else {
      // todo : error handling
    }

    DAnalyzerCodec.encode(dIS.getAnalyzer(), el);

    // persist IndexStructure
    final Iterator it = dIS.getFields();
    while (it.hasNext()) {
      DIndexFieldCodec.encode((DIndexField) it.next(), el);
    }

    element.appendChild(el);
    return el;
  } // End Method endoce

  public static DIndexStructure decode(Element element) throws ISException {

    final DIndexStructure dIndexStructure = new DIndexStructure();

    dIndexStructure.setName(element.getAttribute("Name"));

    // load index fields and analyzer
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof Element)) {
        continue;
      }
      final Element node = (Element) nl.item(i);
      if (node.getLocalName().equals("Analyzer")) {
        dIndexStructure.setAnalyzer(DAnalyzerCodec.decode(node));
      } else if (node.getLocalName().equals("IndexField")) {
        dIndexStructure.addField(DIndexFieldCodec.decode(node));
      }
    }
    return dIndexStructure;
  }
}
