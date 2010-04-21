/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DFieldConstraintsCodec {

  public static DFieldConstraints decode(Element element) throws ConfigurationException {

    final DFieldConstraints dFieldConstraints = new DFieldConstraints();

    dFieldConstraints.setFieldNo(Integer.parseInt(element.getAttribute("FieldNo")));
    if (element.hasAttribute("Occurrence")) {
      dFieldConstraints.setOccurrence(element.getAttribute("Occurrence"));
    }

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      final Node node = nl.item(i);
      final Node textElement = node.getFirstChild();
      String text = null;
      if (textElement != null) {
        text = textElement.getNodeValue();
      }

      if ("FieldTemplate".equals(node.getLocalName())) {
        dFieldConstraints.addFieldTemplate(text);      
      } else if ("NodeTransformer".equals(node.getLocalName())) {
        dFieldConstraints.addNodeTransformer(text);
      } else if ("Constraint".equals(node.getLocalName())) {
        dFieldConstraints.addConstraint(text);
      }
    }

    return dFieldConstraints;
  } // End Method def.

  public static Element encode(DFieldConstraints dFieldConstraints, Element element) throws ConfigurationException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "FieldConstraints");

    el.setAttribute("FieldNo", "" + dFieldConstraints.getFieldNo());
    if (dFieldConstraints.getOccurrence() != null) {
      el.setAttribute("Occurrence", dFieldConstraints.getOccurrence());
    }

    final String[] fieldTemplates = dFieldConstraints.getFieldTemplates();
    for (int i = 0; i < fieldTemplates.length; i++) {
      final Element e = doc.createElementNS(DConfigurationCodec.NS, "FieldTemplate");
      e.appendChild(doc.createTextNode(fieldTemplates[i]));
      el.appendChild(e);
    }

    final String[] nodeTransformers = dFieldConstraints.getNodeTransformers();
    for (int i = 0; i < nodeTransformers.length; i++) {
      final Element e = doc.createElementNS(DConfigurationCodec.NS, "NodeTransformer");
      e.appendChild(doc.createTextNode(nodeTransformers[i]));
      el.appendChild(e);
    }

    final String[] constraints = dFieldConstraints.getConstraints();
    for (int i = 0; i < constraints.length; i++) {
      final Element e = doc.createElementNS(DConfigurationCodec.NS, "Constraint");
      e.appendChild(doc.createTextNode(constraints[i]));
      el.appendChild(e);
    }

    element.appendChild(el);
    return el;
  }

}
