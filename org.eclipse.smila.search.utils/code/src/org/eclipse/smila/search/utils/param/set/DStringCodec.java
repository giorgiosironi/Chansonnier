/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import org.eclipse.smila.search.utils.param.ParameterException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class DStringCodec {

  /**
   * Constructor.
   */
  private DStringCodec() {

  }

  /**
   * @param element -
   * @return DParameter
   * @throws ParameterException -
   */
  public static DParameter decode(Element element) throws ParameterException {

    final DString dParameter = new DString();

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      final Node node = nl.item(i);
      final Node textElement = node.getFirstChild();
      String text = null;
      if (textElement != null) {
        text = textElement.getNodeValue();
      }

      if ("Value".equals(node.getLocalName())) {
        dParameter.setValue((text == null) ? "" : text);
      }
    }

    return dParameter;
  } // End Method def.

  /**
   * @param dParameter -
   * @param element -
   * @return Element
   * @throws ParameterException -
   */
  public static Element encode(DString dParameter, Element element) throws ParameterException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DParameterSetCodec.NS, "Parameter");
    el.setAttribute("xsi:type", "String");

    final String value = "" + dParameter.getValue();
    final Element e = doc.createElementNS(DParameterSetCodec.NS, "Value");
    e.appendChild(doc.createTextNode(value));
    el.appendChild(e);

    element.appendChild(el);
    return el;
  }

}
