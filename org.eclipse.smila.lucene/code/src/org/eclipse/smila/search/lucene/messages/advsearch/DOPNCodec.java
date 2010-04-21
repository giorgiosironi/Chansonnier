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
public abstract class DOPNCodec {

  /**
   * Constructor.
   */
  private DOPNCodec() {

  }

  /**
   * @param dOPN -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DOPN dOPN, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "OP_N"));

    // set children and atts
    me.setAttribute("Operation", dOPN.getOperation() + "");

    if (dOPN.getTermCount() < 2) {
      throw new AdvSearchException("Error: OP_N must have at least two terms");
    }
    for (int i = 0; i < dOPN.getTermCount(); i++) {
      DTermCodec.encode(dOPN.getTerm(i), me);
    }
    return me;
  }

  public static DOPN decode(Element element) throws AdvSearchException {

    // decode Term
    try {
      final DOPN dOPN = new DOPN();

      dOPN.setOperation(element.getAttribute("Operation"));

      final NodeList nl = element.getChildNodes();
      if (nl.getLength() < 2) {
        throw new AdvSearchException("Error: OP_N must have at least two terms");
      }
      for (int i = 0; i < nl.getLength(); i++) {
        if (i >= nl.getLength()) {
          break;
        }
        if ("Term".equals(nl.item(i).getLocalName())) {
          dOPN.addTerm(DTermCodec.decode((Element) nl.item(i)));
        }
      }

      return dOPN;
    } catch (final Exception e) {
      throw new AdvSearchException("Error in attributes to OP_N!");
    }

  }

}
