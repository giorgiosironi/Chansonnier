/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.searchresult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
abstract class DHighLightedCodec {

  public static final String NS = "http://www.anyfinder.de/SearchResult";

  protected static DHighLighted decode(Element element) throws DSearchResultException {

    final DHighLighted dHighLighted = new DHighLighted();

    if (element.hasChildNodes()) {
      dHighLighted.setText(element.getFirstChild().getNodeValue());
    }

    dHighLighted.setScore(Integer.decode(element.getAttribute("Score")).intValue());

    return dHighLighted;
  } // end Method decode

  protected static Element encode(DHighLighted dHighLighted, Element element) throws DSearchResultException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(NS, "HighLighted");

    if (dHighLighted.getText() != null) {
      el.appendChild(doc.createTextNode(dHighLighted.getText()));
    } else {
      // todo: evtl. Error handling
    }

    el.setAttribute("Score", Integer.toString(dHighLighted.getScore()));

    element.appendChild(el);
    return el;
  } // end method encode

  /**
   * 
   */
  private DHighLightedCodec() {
  }

} // End class def. DFieldCodec
