/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.axes.NodeSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments.
 */
public final class XPathUtils {

  /**
   * Default Constructor.
   */
  private XPathUtils() {
  }

  /**
   * Queries for index field.
   * 
   * @param node
   *          the Node
   * @param xpath
   *          the XPath
   * @param namespaceNode
   *          the namespace
   * @param separator
   *          the seperator
   * @return a Object (String, Double, Boolean)
   */
  public static Object queryForIndexField(Node node, String xpath, Node namespaceNode, String separator) {
    final Log log = LogFactory.getLog(XPathUtils.class);
    try {
      final XObject xobj = XPathAPI.eval(node, xpath, namespaceNode);

      if (xobj instanceof NodeSequence) {
        final NodeList nlTemp = xobj.nodelist();

        final StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < nlTemp.getLength(); i++) {

          final XObject value = XPathAPI.eval(nlTemp.item(i), "string()");
          if ((i > 0) && (separator != null)) {
            sb.append(separator);
          }
          sb.append(value.str());
        }
        return sb.toString();
      } else if (xobj instanceof XBoolean) {
        return Boolean.valueOf(xobj.bool());
      } else if (xobj instanceof XNumber) {
        return new Double(xobj.num());
      } else if (xobj instanceof XRTreeFrag) {
        return xobj.str();
      } else if (xobj instanceof XString) {
        return xobj.str();
      } else {
        throw new Exception("unsupported xpath return type [" + xobj.getClass().getName() + "]");
      }
    } catch (final Exception e) {
      log.error("unkown error occured", e);
      return null;
    }
  }

  /**
   * Removes nodes by XPath.
   * 
   * @param node
   *          the Node
   * @param xpath
   *          the XPath
   * @param namespaceNode
   *          the namespace
   */
  public static void removeNodesByXPath(Node node, String xpath, Node namespaceNode) {

    final Log log = LogFactory.getLog(XPathUtils.class);

    try {

      final XObject xobj = XPathAPI.eval(node, xpath, namespaceNode);

      if (xobj instanceof NodeSequence) {
        final NodeList nlTemp = xobj.nodelist();

        for (int i = 0; i < nlTemp.getLength(); i++) {
          final Node parent = nlTemp.item(i).getParentNode();
          parent.removeChild(nlTemp.item(i));
        }
        /*
         * } else if (xobj instanceof XBoolean) { xobj.nodelist() return new Boolean(xobj.bool()); } else if (xobj
         * instanceof XNumber) { return new Double(xobj.num()); } else if (xobj instanceof XRTreeFrag) { return
         * xobj.str(); } else if (xobj instanceof XString) { return xobj.str();
         */
      } else {
        throw new Exception("unsupported xpath return type [" + xobj.getClass().getName() + "]");
      }
    } catch (final Exception e) {
      log.error("unkown error occured", e);
    }
  }
}
