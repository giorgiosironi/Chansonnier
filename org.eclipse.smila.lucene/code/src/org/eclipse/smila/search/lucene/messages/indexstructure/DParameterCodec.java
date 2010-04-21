/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class DParameterCodec {

  /**
   * Constructor.
   */
  private DParameterCodec() {

  }

  /**
   * @param dParameter -
   * @param parent -
   * @return Element
   */
  public static Element encode(DParameter dParameter, Element parent) {
    // prepare
    final Document doc = parent.getOwnerDocument();
    final Element me = (Element) parent.appendChild(doc.createElementNS(DIndexStructureCodec.NS, "Parameter"));

    Element child = null;

    child = (Element) me.appendChild(doc.createElementNS(DIndexStructureCodec.NS, "Name"));
    child.appendChild(doc.createTextNode(dParameter.getName()));
    child = (Element) me.appendChild(doc.createElementNS(DIndexStructureCodec.NS, "Value"));
    child.appendChild(doc.createTextNode(dParameter.getValue()));

    return me;
  }

  /**
   * @param element -
   * @return DParameter
   */
  public static DParameter decode(Element element) {
    final Log log = LogFactory.getLog(DParameterCodec.class);
    String paramName = null;
    String paramValue = null;
    // decode Term
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      /*
       * if (log.isInfoEnabled()) { log.info("DParameterCodec: node " + nl.item(i)); }
       */
      if ("Name".equals(nl.item(i).getLocalName())) {
        paramName = ((Text) nl.item(i).getFirstChild()).getData().trim();
      }

      if ("Value".equals(nl.item(i).getLocalName())) {
        final NamedNodeMap attrs = nl.item(i).getAttributes();
        final Node nilItem = (attrs == null) ? null : attrs.getNamedItem("xsi:nil");
        final String xsiNil = (nilItem == null) ? null : nilItem.getNodeValue();
        final boolean isNil = (xsiNil != null) && (xsiNil.toLowerCase().equals("true") || xsiNil.equals("1"));
        if (isNil) {
          paramValue = null;
        } else {
          paramValue = ((Text) nl.item(i).getFirstChild()).getData().trim();
        }
      }

    }

    final DParameter dParameter = new DParameter(paramName, paramValue);
    return dParameter;
  }

}
