/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import java.util.Iterator;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DAnyFinderAdvSearchCodec {

  /**
   * Namespace.
   */
  public static final String NS = "http://www.anyfinder.de/AdvancedSearch";

  /**
   * Constructor.
   */
  private DAnyFinderAdvSearchCodec() {

  }

  /**
   * Encodes this object's content recursivly into an XML-Document.
   * 
   * @param dAnyFinderAdvSearch -
   * @throws AdvSearchException -
   * @return Document -
   * 
   */
  public static Document encode(DAnyFinderAdvSearch dAnyFinderAdvSearch) throws AdvSearchException {

    final Document doc = XMLUtils.getDocument();
    final Element root = doc.createElementNS(NS, "AnyFinderAdvancedSearch");
    root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    root.setAttribute("xsi:schemaLocation", NS + " ../xml/AnyFinderAdvancedSearch.xsd");
    if (dAnyFinderAdvSearch.getVersion() != null) {
      root.setAttribute("Version", dAnyFinderAdvSearch.getVersion());
    }

    final Iterator it = dAnyFinderAdvSearch.getQueryExpressions();
    while (it.hasNext()) {
      DQueryExpressionCodec.encode((DQueryExpression) it.next(), root);
    }

    doc.appendChild(root);
    return doc;
  }

  /**
   * Constructs a DAnyFinderAdvSearch object from an XML structure. The resulting XML element will be inserted below a
   * given root element.
   * 
   * @param dAnyFinderAdvSearch
   *          object to be encoded to XML.
   * @param element
   *          XML node below which the resulting element will be inserted as an additional child.
   * @throws AdvSearchException -
   * @return Element -
   */
  public static Element encode(DAnyFinderAdvSearch dAnyFinderAdvSearch, Element element) throws AdvSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(NS, "AnyFinderAdvancedSearch");

    if (dAnyFinderAdvSearch.getVersion() != null) {
      el.setAttribute("Version", dAnyFinderAdvSearch.getVersion());
    }

    final Iterator it = dAnyFinderAdvSearch.getQueryExpressions();
    while (it.hasNext()) {
      DQueryExpressionCodec.encode((DQueryExpression) it.next(), el);
    }

    element.appendChild(el);
    return el;
  } // End Method encode(DAnyFinderAdvSearch, Element)

  /**
   * Creates a DAnyFinderAdvSearch object from an XML structure.
   * 
   * @param eAdvSearch
   *          element from which to construct the object.
   * @throws AdvSearchException
   *           if there's an error in the XML structure.
   * @return DAnyFinderAdvSearch
   */
  public static DAnyFinderAdvSearch decode(Element eAdvSearch) throws AdvSearchException {

    // decode query information
    if (!"AnyFinderAdvancedSearch".equals(eAdvSearch.getLocalName())) {
      throw new AdvSearchException("Illegal element: Expected <AnyFinderAdvancedSearch>.");
    }

    // QueryExpressions
    final DAnyFinderAdvSearch dAnyFinderAdvSearch = new DAnyFinderAdvSearch();
    final String version = eAdvSearch.getAttribute("Version");
    if (version != null && !version.equals("")) {
      dAnyFinderAdvSearch.setVersion(version);
    }

    final NodeList nl = eAdvSearch.getChildNodes();
    int i = 0;
    for (i = 0; i < nl.getLength(); i++) {
      if ("QueryExpression".equals(nl.item(i).getLocalName())) {
        dAnyFinderAdvSearch.addQueryExpression(DQueryExpressionCodec.decode((Element) nl.item(i)));
      }
    }
    if (i < 1) {
      throw new AdvSearchException("Mandatory child QueryExpression missing in AnyFinderAdvancedSearch.");
    }

    return dAnyFinderAdvSearch;
  }
}
