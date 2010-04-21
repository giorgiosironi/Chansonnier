/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.xalan.test;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

/**
 * The Class XsltTools used for XSLT extension.
 */
public final class XsltTools {

  /**
   * Instantiates a new xslt tools.
   */
  private XsltTools() {

  }

  /**
   * java:org.eclipse.smila.utils.xml.XsltTools.splitLongWords(., 25).
   * 
   * @param nodes
   *          List of nodes from XSLT.
   * @param maxWordLength
   *          Word length.
   * 
   * @return Splitted phrase.
   */
  public static String splitLongWords(final NodeIterator nodes, final int maxWordLength) {

    Node n = null;
    final StringBuffer sbText = new StringBuffer();
    while ((n = nodes.nextNode()) != null) {
      if (!(n instanceof Text)) {
        continue;
      }

      final Text text = (Text) n;

      if (text != null) {
        sbText.append(text.getData());
      }
    }

    final String[] strings = sbText.toString().split(" ");
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < strings.length; i++) {

      String s = strings[i];

      while (s.length() > maxWordLength) {
        sb.append(s.substring(0, maxWordLength) + " ");
        s = s.substring(maxWordLength);
      }

      sb.append(s + " ");
    }

    return sb.toString().trim();
  }

}
