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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author gschmidt
 * 
 */
abstract class DNumFieldCodec {

  /**
   * Constructor.
   */
  private DNumFieldCodec() {

  }

  /**
   * @param dNumField -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DNumField dNumField, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Field"));
    me.setAttribute("xsi:type", "NumField");

    // set children and atts
    me.setAttribute("FieldNo", dNumField.getFieldNo() + "");

    Element child = null;

    long l = dNumField.getMin();
    child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Min"));
    child.appendChild(doc.createTextNode(l + ""));

    l = dNumField.getMax();
    child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Max"));
    child.appendChild(doc.createTextNode(l + ""));

    return me;
  }

  public static DNumField decode(Element eNumField) throws AdvSearchException {

    // decode Term
    try {
      final DNumField dNumField = new DNumField();

      dNumField.setFieldNo(Integer.parseInt(eNumField.getAttribute("FieldNo")));

      // load index fields
      final NodeList nl = eNumField.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        final NamedNodeMap attrs = nl.item(i).getAttributes();
        final Node nilItem = (attrs == null) ? null : attrs.getNamedItem("xsi:nil");
        final String xsiNil = (nilItem == null) ? null : nilItem.getNodeValue();
        final boolean isNil = (xsiNil != null) && (xsiNil.toLowerCase().equals("true") || xsiNil.equals("1"));
        if ("Min".equals(nl.item(i).getLocalName())) {
          if (nl.item(i).hasChildNodes()) {
            long l;
            if (isNil) {
              l = Long.MIN_VALUE;
            } else {
              final String data = ((Text) nl.item(i).getFirstChild()).getData();
              try {
                l = Long.parseLong(data);
              } catch (final NumberFormatException nfe) {
                throw new AdvSearchException("Error in NumField: Illegal number format '" + data + "'");
              }
              dNumField.setMin(l);
            }
          }
        }

        if ("Max".equals(nl.item(i).getLocalName())) {
          if (nl.item(i).hasChildNodes()) {
            long l;
            if (isNil) {
              l = Long.MAX_VALUE;
            } else {
              final String data = ((Text) nl.item(i).getFirstChild()).getData();
              try {
                l = Long.parseLong(data);
              } catch (final NumberFormatException nfe) {
                throw new AdvSearchException("Error in NumField: Illegal number format '" + data + "'");
              }
              dNumField.setMax(l);
            }
          }
        }
      }
      return dNumField;
    } catch (final Exception e) {
      throw new AdvSearchException("Error in attributes to NumField!", e);
    }

  }

}
