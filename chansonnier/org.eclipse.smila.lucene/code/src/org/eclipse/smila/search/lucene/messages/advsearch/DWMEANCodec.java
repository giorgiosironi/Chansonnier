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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author gschmidt
 * 
 */
public abstract class DWMEANCodec {

  /**
   * Constructor.
   */
  private DWMEANCodec() {

  }

  /**
   * @param dWMEAN -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DWMEAN dWMEAN, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "WMEAN"));

    // set children and atts
    me.setAttribute("Operation", dWMEAN.getOperation() + "");

    Element child = null;
    if (dWMEAN.getTermCount() < 2) {
      throw new AdvSearchException("Error: WMEAN must have at least two terms");
    }
    for (int i = 0; i < dWMEAN.getTermCount(); i++) {

      child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Constraint"));
      child.appendChild(doc.createTextNode(dWMEAN.getConstraint(i)));
      child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Boost"));
      child.appendChild(doc.createTextNode(dWMEAN.getBoost(i) + ""));

      DTermCodec.encode(dWMEAN.getTerm(i), me);
    }

    return me;
  }

  public static DWMEAN decode(Element element) throws AdvSearchException {

    // decode Term
    try {
      final DWMEAN dWMEAN = new DWMEAN();

      dWMEAN.setOperation(element.getAttribute("Operation"));

      final NodeList nl = element.getChildNodes();
      if (nl.getLength() < 4) {
        throw new AdvSearchException("Error: WMEAN must have at least two terms");
      }
      for (int i = 0; i < nl.getLength(); i += 3) {
        if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
          i++; // skip text node caused by linefeed in XML
        }
        if (i >= nl.getLength()) {
          break;
        }
        if ("Constraint".equals(nl.item(i).getLocalName())) {
          String constraint = ((Text) nl.item(i).getFirstChild()).getData().trim();
          if (constraint == null || constraint.equals("")) {
            constraint = "optional";
          }
          float boost = 0;
          try {
            String data = null;
            if (nl.item(i + 1).getNodeType() == Node.TEXT_NODE) {
              i++; // skip text node caused by linefeed in XML
            }
            if ("Boost".equals(nl.item(i + 1).getLocalName())) {
              data = ((Text) nl.item(i + 1).getFirstChild()).getData().trim();
            } else {
              final String nullstring = null;
              nullstring.toString();
            }
            try {
              boost = Float.parseFloat(data);
            } catch (final NumberFormatException nfe) {
              throw new AdvSearchException("Error in WMEAN: Illegal number format '" + data + "' for Boost");
            }
            if (nl.item(i + 2).getNodeType() == Node.TEXT_NODE) {
              i++; // skip text node caused by linefeed in XML
            }
            if ("Term".equals(nl.item(i + 2).getLocalName())) {
              dWMEAN.addTerm(DTermCodec.decode((Element) nl.item(i + 2)), boost, constraint);
            } else {
              final String nullstring = null;
              nullstring.toString();
            }
            // NodeList.item() throws NullPointerException if index is out of bounds
          } catch (final NullPointerException npe) {
            throw new AdvSearchException(
              "Illegal structure below WMEAN: Need sequence of <Constraint>...</Constraint><Boost>...</Boost><Term>...</Term>");
          }
        }
      }

      return dWMEAN;
    } catch (final Exception e) {
      if (e instanceof AdvSearchException) {
        throw (AdvSearchException) e;
      } else {
        throw new AdvSearchException("Error in attributes to WMEAN: " + e);
      }
    }

  }

}
