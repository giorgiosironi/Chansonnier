/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 *               Sebastian Voigt (brox IT Solutions GmbH)
 *               Andreas Weber (empolis GmbH) - switch to XML 1.1 in header to make more documents readable.
 **********************************************************************************************************************/
package org.eclipse.smila.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class XmlHelper.
 */
public final class XmlHelper {

  /**
   * The BUILDING factory.
   */
  public static final DocumentBuilderFactory BUILDING_FACTORY =
    new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();

  // DocumentBuilderFactory.newInstance();

  /**
   * The Constant TRANSFORMER_FACTORY.
   */
  public static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

  /**
   * The Constant XML_HEADER_UTF8.
   */
  public static final String XML_HEADER_UTF8 = "<?xml version=\"1.1\" encoding=\"UTF-8\"?>";

  static {
    // System.out.println("XML Factory=" + BUILDING_FACTORY.getClass());
    BUILDING_FACTORY.setIgnoringComments(true);
    BUILDING_FACTORY.setNamespaceAware(true);
    BUILDING_FACTORY.setCoalescing(true);
    BUILDING_FACTORY.setIgnoringElementContentWhitespace(true);
  }

  /**
   * Does not instantiates a new xml helper.
   */
  private XmlHelper() {

  }

  /**
   * To string.
   *
   * @param document
   *          the document
   *
   * @return the string
   */
  public static String toString(final Document document) {
    if (document == null) {
      return null;
    }
    // final StringWriter xmlString = new StringWriter();
    // final OutputFormat format = new OutputFormat(document);
    // final XMLSerializer serializer = new XMLSerializer(xmlString, format);
    // serializer.serialize(document);
    // final String xml = xmlString.toString();
    // return xml;
    return toString(document, false);
  }

  /**
   * To string.
   *
   * @param document
   *          the document
   * @param xmlDeclaration
   *          the xml declaration
   *
   * @return the string
   */
  public static String toString(final Document document, final boolean xmlDeclaration) {
    if (document == null) {
      return null;
    }
    final StreamResult streamResult = new StreamResult(new StringWriter());
    transform(document, streamResult, xmlDeclaration);
    final String stringXml = streamResult.getWriter().toString();
    return stringXml;
  }

  /**
   * To byte array.
   *
   * @param document
   *          the document
   *
   * @return the byte[]
   */
  public static byte[] toByteArray(final Document document) {
    if (document == null) {
      return null;
    }
    // force to UTF-8, BOS is not used.
    try {
      final String string = XML_HEADER_UTF8 + toString(document);
      return string.getBytes("utf-8");
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Transform.
   *
   * @param document
   *          the document
   * @param streamResult
   *          the stream result
   * @param xmlDeclaration
   *          the xml declaration
   */
  public static void transform(final Document document, final StreamResult streamResult,
    final boolean xmlDeclaration) {
    if (document == null) {
      throw new IllegalArgumentException("document cannot be NULL!");
    }
    if (streamResult == null) {
      throw new IllegalArgumentException("streamResult cannot be NULL!");
    }
    Transformer transformer;
    try {
      transformer = TRANSFORMER_FACTORY.newTransformer();
    } catch (final TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
    transformer.setOutputProperty(OutputKeys.INDENT, "no");
    String strXmlDeclaration;
    if (xmlDeclaration) {
      strXmlDeclaration = "no";
    } else {
      strXmlDeclaration = "yes";
    }
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, strXmlDeclaration);
    final DOMSource source = new DOMSource(document);
    try {
      transformer.transform(source, streamResult);
    } catch (final TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Parses the.
   *
   * @param input
   *          the input
   *
   * @return the document
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws SAXException
   *           the SAX exception
   */
  public static Document parse(final String input) throws IOException, ParserConfigurationException, SAXException {
    if (input == null) {
      return null;
    }
    final StringReader reader = new StringReader(input);
    final InputSource inputSource = new InputSource(reader);
    final DocumentBuilder documentBuilder = BUILDING_FACTORY.newDocumentBuilder();
    return documentBuilder.parse(inputSource);
  }

  /**
   * Parses the.
   *
   * @param input
   *          the input
   *
   * @return the document
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ParserConfigurationException
   *           the parser configuration exception
   * @throws SAXException
   *           the SAX exception
   */
  public static Document parse(final byte[] input) throws IOException, ParserConfigurationException, SAXException {
    if (input == null) {
      return null;
    }
    final ByteArrayInputStream stream = new ByteArrayInputStream(input);
    final InputSource inputSource = new InputSource(stream);
    final DocumentBuilder documentBuilder = BUILDING_FACTORY.newDocumentBuilder();
    return documentBuilder.parse(inputSource);
  }

  /**
   * New document.
   *
   * @return the document
   *
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public static Document newDocument() throws ParserConfigurationException {
    return BUILDING_FACTORY.newDocumentBuilder().newDocument();
  }

  /**
   * New document.
   *
   * @param rootName
   *          the root name
   *
   * @return the document
   *
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  public static Document newDocument(final String rootName) throws ParserConfigurationException {
    final Document document = newDocument();
    final Element child = document.createElement(rootName);
    document.appendChild(child);
    return document;
  }

  /**
   * Creates the element.
   *
   * @param parent
   *          the parent
   * @param nodeName
   *          the node name
   *
   * @return the element
   */
  public static Element createElement(final Node parent, final String nodeName) {
    final Element child = parent.getOwnerDocument().createElement(nodeName);
    parent.appendChild(child);
    return child;
  }

  /**
   * Creates the text.
   *
   * @param parent
   *          the parent
   * @param text
   *          the text
   *
   * @return the text
   */
  public static Text createText(final Node parent, final String text) {
    final Text child = parent.getOwnerDocument().createTextNode(text);
    parent.appendChild(child);
    return child;
  }
}
