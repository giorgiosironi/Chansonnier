/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import java.text.ParseException;

import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DDateCodec {

  /**
   * Constructor.
   */
  private DDateCodec() {

  }

  /**
   * @param element -
   * @return DParameter
   * @throws ParameterException -
   */
  public static DParameter decode(Element element) throws ParameterException {

    final DDate dParameter = new DDate();

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      final Node node = nl.item(i);
      final Node textElement = node.getFirstChild();
      String text = null;
      if (textElement != null) {
        text = textElement.getNodeValue();
      }

      if ("Value".equals(node.getLocalName())) {
        try {
          dParameter.setValue(XMLUtils.decodeDate(text));
        } catch (final ParseException e) {
          throw new ParameterException("Illegal date format. Must be yyyy-MM-dd [" + text + "]");
        }
      }
    }

    return dParameter;
  } // End Method def.

  public static Element encode(DDate dParameter, Element element) throws ParameterException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DParameterSetCodec.NS, "Parameter");
    el.setAttribute("xsi:type", "Date");

    String value;
    value = XMLUtils.encodeDate(dParameter.getValue());
    final Element e = doc.createElementNS(DParameterSetCodec.NS, "Value");
    e.appendChild(doc.createTextNode(value));
    el.appendChild(e);

    element.appendChild(el);
    return el;
  }

}
