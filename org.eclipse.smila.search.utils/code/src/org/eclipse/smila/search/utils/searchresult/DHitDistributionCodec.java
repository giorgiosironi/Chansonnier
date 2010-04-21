/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */

import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

abstract class DHitDistributionCodec {

  public static final String NS = "http://www.anyfinder.de/SearchResult";

  protected static DHitDistribution decode(Element element) throws DSearchResultException {

    final DHitDistribution dHitDistribution = new DHitDistribution();

    // decode result information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Hit".equals(nl.item(i).getLocalName())) {
        dHitDistribution.addHit(DHitCodec.decode((Element) nl.item(i)));
      }
    }

    return dHitDistribution;
  } // End Method decode

  protected static Element encode(DHitDistribution dHitDistribution, Element element) throws DSearchResultException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(NS, "HitDistribution");

    final Enumeration hits = dHitDistribution.getHits();
    while (hits.hasMoreElements()) {
      DHitCodec.encode((DHit) hits.nextElement(), el);
    }

    element.appendChild(el);
    return el;
  } // End Method encode

  /**
   * 
   */
  private DHitDistributionCodec() {
  }

} // End class def.
