/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author gschmidt
 * 
 */
public abstract class DQueryExpressionCodec {

  /**
   * Constructor.
   */
  private DQueryExpressionCodec() {

  }

  /**
   * Encodes this object's content recursivly into an XML-Document.
   * 
   * @param dQueryExpression
   *          QueryExpression to be encoded
   * @param parent
   *          element below which the resulting document will be inserted
   * @return XML element to be encoded
   * @throws AdvSearchException -
   */
  public static Element encode(DQueryExpression dQueryExpression, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me =
      (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "QueryExpression"));

    // set children and atts
    me.setAttribute("IndexName", dQueryExpression.getIndexName());
    me.setAttribute("MaxHits", dQueryExpression.getMaxHits() + "");
    me.setAttribute("MinSimilarity", dQueryExpression.getMinSimilarity() + "");
    me.setAttribute("ShowHitDistribution", (dQueryExpression.getShowHitDistribution() ? "true" : "false"));

    if (dQueryExpression.getStartHits() != null) {
      me.setAttribute("StartHits", dQueryExpression.getStartHits().intValue() + "");
    }
    DTermCodec.encode((DTerm) dQueryExpression.getTerm(), me);

    return me;
  }

  public static DQueryExpression decode(Element element) throws AdvSearchException {

    final DQueryExpression dQueryExpression = new DQueryExpression();

    // set attributes
    dQueryExpression.setIndexName(element.getAttribute("IndexName"));
    dQueryExpression.setMaxHits(Integer.parseInt(element.getAttribute("MaxHits")));
    dQueryExpression.setMinSimilarity(Integer.parseInt(element.getAttribute("MinSimilarity")));
    dQueryExpression.setShowHitDistribution(Boolean.valueOf(element.getAttribute("ShowHitDistribution"))
      .booleanValue());

    if (element.hasAttribute("StartHits")) {
      dQueryExpression.setStartHits(new Integer(element.getAttribute("StartHits")));
    }

    // decode Term
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Term".equals(nl.item(i).getLocalName())) {
        dQueryExpression.setTerm(DTermCodec.decode((Element) nl.item(i)));
      }
    }

    return dQueryExpression;
  }

}
