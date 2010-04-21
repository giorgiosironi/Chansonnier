/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

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
public abstract class DEnumerationCodec {

  /**
   * Constructor.
   */
  private DEnumerationCodec() {

  }

  /**
   * @param element -
   * @return DParameter.
   * @throws ParameterException -
   */
  public static DParameter decode(Element element) throws ParameterException {

    final DEnumeration dParameter = new DEnumeration();

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      final Node node = nl.item(i);
      final Node textElement = node.getFirstChild();
      String text = null;
      if (textElement != null) {
        text = textElement.getNodeValue();
      }

      if ("Default".equals(node.getLocalName())) {
        dParameter.addDefault((text == null) ? "" : text);
      }
      if ("Value".equals(node.getLocalName())) {
        dParameter.addValue((text == null) ? "" : text);
      }
      if ("AllowMultiple".equals(node.getLocalName())) {
        dParameter.setAllowMultiple(Boolean.valueOf(text).booleanValue());
      }
    }

    return dParameter;
  } // End Method def.

  public static Element encode(DEnumeration dParameter, Element element) throws ParameterException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DParameterDefinitionCodec.NS, "Parameter");

    DParameterCodec.encodeStandardValues(dParameter, el, doc);

    if (dParameter.hasDefault()) {
      final String[] defaults = dParameter.getDefaults();
      for (int i = 0; i < defaults.length; i++) {
        final String value = defaults[i];
        final Element e = doc.createElementNS(DParameterDefinitionCodec.NS, "Default");
        e.appendChild(doc.createTextNode(value));
        el.appendChild(e);
      }
    }

    final String[] values = dParameter.getValues();
    for (int i = 0; i < values.length; i++) {
      final String value = values[i];
      final Element e = doc.createElementNS(DParameterDefinitionCodec.NS, "Value");
      e.appendChild(doc.createTextNode(value));
      el.appendChild(e);
    }

    if (dParameter.isAllowMultiple()) {
      final Element e = doc.createElementNS(DParameterDefinitionCodec.NS, "AllowMultiple");
      e.appendChild(doc.createTextNode(dParameter.isAllowMultiple() + ""));
      el.appendChild(e);
    }

    element.appendChild(el);
    return el;
  }

}
