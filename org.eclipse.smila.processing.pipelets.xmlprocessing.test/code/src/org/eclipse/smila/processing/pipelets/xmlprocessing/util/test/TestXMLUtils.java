/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util.test;

import junit.framework.TestCase;

import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TestCase for XmlUtils.
 */
public class TestXMLUtils extends TestCase {

  /**
   * Simple test case.
   * 
   * @throws Exception
   *           Unable to perform test.
   */
  public void testNewDocument() throws Exception {
    final Document document = XMLUtils.newDocument();
    assertNotNull(document);
  }

  /**
   * Simple test case.
   * 
   * @throws Exception
   *           Unable to perform test.
   */
  public void testDocumentToString() throws Exception {
    final Document document = XMLUtils.newDocument();
    assertNotNull(document);
    String result = XMLUtils.documentToString(document);

    final String compareValue1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    assertEquals("document compare 1 failed", compareValue1, result);

    final Element root = document.createElement("root");
    final Element child = document.createElement("child");
    document.appendChild(root);
    root.appendChild(child);
    result = XMLUtils.documentToString(document);
    final String compareValue2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child/></root>";
    assertEquals("document compare 2 failed", compareValue2, result);
  }
}
