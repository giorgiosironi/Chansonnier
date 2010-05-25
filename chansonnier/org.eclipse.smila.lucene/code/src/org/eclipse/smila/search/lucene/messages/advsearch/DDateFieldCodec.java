/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

abstract class DDateFieldCodec {

  /**
   * Constructor.
   */
  private DDateFieldCodec() {

  }

  /**
   * @param dDateField -
   * @param parent -
   * @return Element
   * @throws AdvSearchException -
   */
  public static Element encode(DDateField dDateField, Element parent) throws AdvSearchException {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Field"));
    me.setAttribute("xsi:type", "DateField");

    // set children and atts
    me.setAttribute("FieldNo", dDateField.getFieldNo() + "");

    Element child = null;

    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date cal = null;

    cal = dDateField.getMin();
    child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Min"));
    if (cal != null) {
      child.appendChild(doc.createTextNode(formatter.format(cal)));
    } else {
      child.setAttribute("xsi:nil", "true");
    }

    cal = dDateField.getMax();
    child = (Element) me.appendChild(doc.createElementNS(DAnyFinderAdvSearchCodec.NS, "Max"));
    if (cal != null) {
      child.appendChild(doc.createTextNode(formatter.format(cal)));
    } else {
      child.setAttribute("xsi:nil", "true");
    }

    return me;
  }

  public static DDateField decode(Element eDateField) throws AdvSearchException {

    // decode Term
    try {
      final DDateField dDateField = new DDateField();

      dDateField.setFieldNo(Integer.parseInt(eDateField.getAttribute("FieldNo")));

      // load index fields
      final GregorianCalendar c = new GregorianCalendar();
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      final NodeList nl = eDateField.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        final NamedNodeMap attrs = nl.item(i).getAttributes();
        final Node nilItem = (attrs == null) ? null : attrs.getNamedItem("xsi:nil");
        final String xsiNil = (nilItem == null) ? null : nilItem.getNodeValue();
        final boolean isNil = (xsiNil != null) && (xsiNil.toLowerCase().equals("true") || xsiNil.equals("1"));
        if ("Min".equals(nl.item(i).getLocalName()) && !isNil) {
          if (nl.item(i).hasChildNodes()) {
            final String date = ((Text) nl.item(i).getFirstChild()).getData();
            try {
              c.setTime(sdf.parse(date));
            } catch (final ParseException e) {
              throw new AdvSearchException("Error in DateField: Illegal date format '" + date + "'.");
            }
            dDateField.setMin(c.getTime());
          }
        }
        if ("Max".equals(nl.item(i).getLocalName()) && !isNil) {
          if (nl.item(i).hasChildNodes()) {
            final String date = ((Text) nl.item(i).getFirstChild()).getData();
            try {
              c.setTime(sdf.parse(date));
            } catch (final ParseException e) {
              throw new AdvSearchException("Error in DateField: Illegal date format '" + date + "'.");
            }
            dDateField.setMax(c.getTime());
          }
        }
      }

      return dDateField;
    } catch (final Exception e) {
      throw new AdvSearchException("Error in attributes to DateField!", e);
    }

  }

}
