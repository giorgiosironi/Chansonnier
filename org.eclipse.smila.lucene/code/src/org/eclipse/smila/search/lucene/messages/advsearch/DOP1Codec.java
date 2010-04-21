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
public abstract class DOP1Codec {

  /**
   * Constructor.
   */
  private DOP1Codec() {

  }

  /**
   * @param dOP1 -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DOP1 dOP1, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "OP_1"));

    // set children and atts
    me.setAttribute("Operation", dOP1.getOperation() + "");
    DTermCodec.encode(dOP1.getTerm(), me);

    return me;
  }

  public static DOP1 decode(Element element) throws AdvSearchException {

    // decode Term
    try {
      final DOP1 dOP1 = new DOP1();

      dOP1.setOperation(element.getAttribute("Operation"));

      final NodeList nl = element.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        if ("Term".equals(nl.item(i).getLocalName())) {
          dOP1.setTerm(DTermCodec.decode((Element) nl.item(i)));
        }
      }

      return dOP1;
    } catch (final AdvSearchException e) {
      throw e;
    } catch (final Exception e) {
      throw new AdvSearchException("Error in attributes to OP_1!");
    }

  }

}
