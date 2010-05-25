/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public final class DSelectorCodec {

  /**
   * 
   */
  private DSelectorCodec() {
    super();
  }

  /**
   * Encode DSelector object into XML.
   * 
   * @param dSelector
   *          Object to encode.
   * @param parent
   *          Parent of object.
   * @return Encoded element.
   * @throws DFieldTemplatesException
   *           Unable to encode object.
   */
  public static org.w3c.dom.Element encode(final DSelector dSelector, final org.w3c.dom.Element parent)
    throws DFieldTemplatesException {

    if (parent == null) {
      throw new DFieldTemplatesException("parameter must not be null [parent]");
    }

    if (dSelector == null) {
      throw new DFieldTemplatesException("parameter must not be null [dSelector]");
    }
    final org.w3c.dom.Document doc = parent.getOwnerDocument();
    final org.w3c.dom.Element el = doc.createElementNS(DFieldTemplatesCodec.NS, "Selector");

    // set custom attributes
    el.setAttribute("FieldNo", XMLUtils.encodeInteger(dSelector.getFieldNo()));

    if (dSelector.getName() != null) {
      el.setAttribute("Name", dSelector.getName());
    }

    // create elements
    if (dSelector.getFilterExpression() != null) {
      final Element elTemp = doc.createElementNS(DFieldTemplatesCodec.NS, "FilterExpression");
      elTemp.appendChild(doc.createCDATASection(dSelector.getFilterExpression()));
      el.appendChild(elTemp);
    }

    // insert into dom tree
    parent.appendChild(el);
    return el;
  }

  /**
   * Decode XML element into DSelector.
   * 
   * @param element
   *          Element to decode.
   * @return Decoded element as DSelector.
   * @throws DFieldTemplatesException
   *           Unable to decode XML element.
   */
  public static DSelector decode(final org.w3c.dom.Element element) throws DFieldTemplatesException {

    if (element == null) {
      throw new DFieldTemplatesException("parameter must not be null [element]");
    }

    final DSelector dSelector = new DSelector();

    // resolve custom attributes
    dSelector.setFieldNo(XMLUtils.decodeInteger(element.getAttribute("FieldNo")));
    dSelector.setName(element.getAttribute("Name"));

    // resolve custom elements
    final org.w3c.dom.NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (!(nl.item(i) instanceof org.w3c.dom.Element)) {
        continue;
      }
      final org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);

      if ("FilterExpression".equals(el.getLocalName())) {
        dSelector.setFilterExpression(el.getTextContent());
      }
    }

    return dSelector;
  }

}
