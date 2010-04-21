/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

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
public abstract class DParameterCodec {

  /**
   * Constructor.
   */
  private DParameterCodec() {

  }

  /**
   * @param element -
   * @return DParameter
   * @throws ParameterException -
   */
  public static DParameter decode(Element element) throws ParameterException {

    String type = element.getAttribute("xsi:type");
    type = XMLUtils.getLocalPart(type);

    DParameter dParameter = null;

    if ("Float".equals(type)) {
      dParameter = DFloatCodec.decode(element);
    } else if ("Date".equals(type)) {
      dParameter = DDateCodec.decode(element);
    } else if ("String".equals(type)) {
      dParameter = DStringCodec.decode(element);
    } else if ("Integer".equals(type)) {
      dParameter = DIntegerCodec.decode(element);
    } else if ("Boolean".equals(type)) {
      dParameter = DBooleanCodec.decode(element);
    } else if ("FloatList".equals(type)) {
      dParameter = DFloatListCodec.decode(element);
    } else if ("DateList".equals(type)) {
      dParameter = DDateListCodec.decode(element);
    } else if ("StringList".equals(type)) {
      dParameter = DStringListCodec.decode(element);
    } else if ("IntegerList".equals(type)) {
      dParameter = DIntegerListCodec.decode(element);
    } else if ("Enumeration".equals(type)) {
      dParameter = DEnumerationCodec.decode(element);
    } else {
      throw new ParameterException("Unknown parameter type [" + type + "]");
    }

    dParameter.setName(element.getAttribute("Name"));
    dParameter.setConstraint(element.getAttribute("Constraint"));
    dParameter.setType(type);

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      final Node node = nl.item(i);
      final Node textElement = node.getFirstChild();
      String text = null;
      if (textElement != null) {
        text = textElement.getNodeValue();
      }

      if ("Description".equals(node.getLocalName())) {
        dParameter.setDescription(text);
      }
    }

    return dParameter;
  } // End Method def.

  public static Element encode(DParameter dParameter, Element element) throws ParameterException {

    Element el = null;

    if (dParameter instanceof DFloat) {
      el = DFloatCodec.encode((DFloat) dParameter, element);
    } else if (dParameter instanceof DDate) {
      el = DDateCodec.encode((DDate) dParameter, element);
    } else if (dParameter instanceof DString) {
      el = DStringCodec.encode((DString) dParameter, element);
    } else if (dParameter instanceof DInteger) {
      el = DIntegerCodec.encode((DInteger) dParameter, element);
    } else if (dParameter instanceof DBoolean) {
      el = DBooleanCodec.encode((DBoolean) dParameter, element);
    } else if (dParameter instanceof DFloatList) {
      el = DFloatListCodec.encode((DFloatList) dParameter, element);
    } else if (dParameter instanceof DDateList) {
      el = DDateListCodec.encode((DDateList) dParameter, element);
    } else if (dParameter instanceof DStringList) {
      el = DStringListCodec.encode((DStringList) dParameter, element);
    } else if (dParameter instanceof DIntegerList) {
      el = DIntegerListCodec.encode((DIntegerList) dParameter, element);
    } else if (dParameter instanceof DEnumeration) {
      el = DEnumerationCodec.encode((DEnumeration) dParameter, element);
    } else {
      throw new ParameterException("Unknown parameter type [" + dParameter.getClass() + "]");
    }

    el.setAttribute("Name", dParameter.getName());
    el.setAttribute("xsi:type", dParameter.getType());
    if (dParameter.getConstraint() != null) {
      el.setAttribute("Constraint", "" + dParameter.getConstraint());
    }

    element.appendChild(el);
    return el;
  }

  public static void encodeStandardValues(DParameter dParameter, Element el, Document doc) {

    final Element eDescription = doc.createElementNS(DParameterDefinitionCodec.NS, "Description");
    eDescription.appendChild(doc.createTextNode(dParameter.getDescription()));
    el.appendChild(eDescription);

  }

}
