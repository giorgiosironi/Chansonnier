/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.util.test;

import java.io.FileNotFoundException;

import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XMLUtils;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.XslTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TestXslTransformer.
 */
public class TestXslTransformer extends AXMLUnitTestCase {

  /**
   * @throws Exception
   *           Exception.
   */
  public void testExceptions() throws Exception {
    final XslTransformer xslTransformer = new XslTransformer();
    try {
      final Document document = XMLUtils.newDocument();
      xslTransformer.transform(document, "fileNotExists.xsl");
      fail("exception expected...");
    } catch (final FileNotFoundException exception) {
      assertNotNull(exception);
      _log.debug("Expected exception: " + exception.getMessage());
    }
  }

  /**
   * @throws Exception
   *           Exception.
   */
  public void testTransform() throws Exception {
    final Document document = XMLUtils.newDocument();
    assertNotNull(document);

    final Element root = document.createElement("root");
    final Element child1 = document.createElement("child");
    child1.setTextContent("child1");
    final Element child2 = document.createElement("child");
    child2.setTextContent("child2");
    document.appendChild(root);
    root.appendChild(child1);
    root.appendChild(child2);

    final Document resultDocument = XMLUtils.newDocument();
    assertNotNull(resultDocument);

    final Element newRoot = resultDocument.createElement("newRoot");
    final Element newChild1 = resultDocument.createElement("newChild");
    newChild1.setTextContent("child1");
    final Element newChild2 = resultDocument.createElement("newChild");
    newChild2.setTextContent("child2");
    resultDocument.appendChild(newRoot);
    newRoot.appendChild(newChild1);
    newRoot.appendChild(newChild2);

    final XslTransformer transformer = new XslTransformer();
    Document result = transformer.transform(document, "./configuration/data/xslFile.xsl");
    _log.debug("expected  result: " + XMLUtils.documentToString(resultDocument));
    assertNotNull(result);
    _log.debug("transform result: " + XMLUtils.documentToString(result));
    assertTrue(compareXmlDocuments(resultDocument, result));

    result = transformer.transform(document, "./configuration/data/xslFile.xsl");
    _log.debug("expected  result: " + XMLUtils.documentToString(resultDocument));
    assertNotNull(result);
    _log.debug("transform result: " + XMLUtils.documentToString(result));
    assertTrue(compareXmlDocuments(resultDocument, result));
  }
}
