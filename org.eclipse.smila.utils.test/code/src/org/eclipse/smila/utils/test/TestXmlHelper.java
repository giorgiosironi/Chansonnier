/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.eclipse.smila.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * The Class TestXmlHelper.
 */
public class TestXmlHelper extends TestCase {

  /**
   * Test factory.
   */
  public void testFactory() {
    assertNotNull(XmlHelper.BUILDING_FACTORY);
    assertTrue(XmlHelper.BUILDING_FACTORY.isIgnoringComments());
    assertTrue(XmlHelper.BUILDING_FACTORY.isNamespaceAware());
    assertTrue(XmlHelper.BUILDING_FACTORY.isCoalescing());
    assertTrue(XmlHelper.BUILDING_FACTORY.isIgnoringElementContentWhitespace());
  }

  /**
   * Test document.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SAXException
   *           the SAX exception
   */
  public void testDocument() throws ParserConfigurationException, IOException, SAXException {
    Document document = XmlHelper.newDocument("root");
    checkDocument(document);
    final String docString = XmlHelper.toString(document);
    assertNotNull(docString);
    assertTrue(docString.contains("root"));
    document = XmlHelper.parse(docString);
    checkDocument(document);
    document = XmlHelper.parse(docString.getBytes("utf-8"));
    checkDocument(document);
  }

  /**
   * Test to string null document.
   */
  public void testToStringNullDocument() {
    String docString = XmlHelper.toString(null);
    assertNull(docString);
    docString = XmlHelper.toString(null, true);
    assertNull(docString);
    docString = XmlHelper.toString(null, false);
    assertNull(docString);
  }

  /**
   * Test to byte array null document.
   */
  public void testToByteArrayNullDocument() {
    final byte[] bytes = XmlHelper.toByteArray(null);
    assertNull(bytes);
  }

  /**
   * Test parst null document.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws SAXException
   *           the SAX exception
   */
  public void testParstNullDocument() throws IOException, ParserConfigurationException, SAXException {
    Document document = XmlHelper.parse((String) null);
    assertNull(document);
    document = XmlHelper.parse((byte[]) null);
    assertNull(document);
  }

  /**
   * Check document.
   * 
   * @param document
   *          the document
   */
  public void checkDocument(final Document document) {
    assertNotNull(document);
    assertNotNull(document.getDocumentElement());
    assertEquals(document.getDocumentElement().getNodeName(), "root");
  }

  /**
   * Test transform ex null document.
   */
  public void testTransformExNullDocument() {
    // null document
    final Document document = null;
    final StringWriter sw = new StringWriter();
    final StreamResult streamResult = new StreamResult(sw);
    try {
      XmlHelper.transform(document, streamResult, true);
      throw new AssertionError("Should not be transformed");
    } catch (final IllegalArgumentException e) {
      ;// ok
    }
  }

  /**
   * Test transform ex null result.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public void testTransformExNullResult() throws ParserConfigurationException {
    // null document
    final Document document = XmlHelper.newDocument("root");
    final StreamResult streamResult = null;
    try {
      XmlHelper.transform(document, streamResult, true);
      throw new AssertionError("Should not be transformed");
    } catch (final IllegalArgumentException e) {
      ;// ok
    }
  }

  /**
   * Test transform.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public void testTransform() throws ParserConfigurationException {
    final Document document = XmlHelper.newDocument("root");
    final StringWriter sw = new StringWriter();
    final StreamResult streamResult = new StreamResult(sw);
    XmlHelper.transform(document, streamResult, true);
    assertTrue(sw.toString().contains("xml"));
  }

  /**
   * Test transform no declaration.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public void testTransformNoDeclaration() throws ParserConfigurationException {
    final Document document = XmlHelper.newDocument("root");
    final StringWriter sw = new StringWriter();
    final StreamResult streamResult = new StreamResult(sw);
    XmlHelper.transform(document, streamResult, false);
    assertFalse(sw.toString().contains("xml"));
  }

  /**
   * Test create element.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public void testCreateElement() throws ParserConfigurationException {
    final Document document = XmlHelper.newDocument("root");
    final Element root = document.getDocumentElement();
    final Element child = XmlHelper.createElement(root, "child");
    assertNotNull(child);
    assertEquals(1, root.getChildNodes().getLength());
    assertEquals("child", root.getFirstChild().getNodeName());
  }

  /**
   * Test create text.
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public void testCreateText() throws ParserConfigurationException {
    final Document document = XmlHelper.newDocument("root");
    final Element root = document.getDocumentElement();
    final Text text = XmlHelper.createText(root, "some text");
    assertNotNull(text);
    assertEquals(1, root.getChildNodes().getLength());
    assertEquals(root.getFirstChild().getTextContent(), "some text");
  }
}
