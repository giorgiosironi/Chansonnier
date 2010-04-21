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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract class DHitCodec {

  public static final String NS = "http://www.anyfinder.de/SearchResult";

  protected static DHit decode(Element element) throws DSearchResultException {

    final DHit dHit = new DHit();
    dHit.setHits(Integer.parseInt(element.getAttribute("Hits")));
    dHit.setScore(Integer.parseInt(element.getAttribute("Score")));

    return dHit;
  } // End Method decode

  protected static Element encode(DHit dHit, Element element) throws DSearchResultException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(NS, "Hit");
    el.setAttribute("Hits", dHit.getHits() + "");
    el.setAttribute("Score", dHit.getScore() + "");

    element.appendChild(el);
    return el;
  } // End Method encode

  /**
   * 
   */
  private DHitCodec() {
  }

} // End class def.
