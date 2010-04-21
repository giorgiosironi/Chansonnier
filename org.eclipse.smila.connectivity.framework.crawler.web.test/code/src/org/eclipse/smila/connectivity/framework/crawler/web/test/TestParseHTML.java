/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.DOMBuilder;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.XMLCharacterRecognizer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * The Class TestParseHTML.
 * 
 * @author Alexander Eliseyev
 */
public class TestParseHTML extends TestCase {

  // CHECKSTYLE:OFF
  /** The WHITESPAC e_ chars. */
  private static char[] WHITESPACE_CHARS = new char[] { ' ', (byte) 0x20, (byte) 0x09, (byte) 0xD, (byte) 0xA };

  /**
   * Test dom builder characters.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDomBuilderCharacters() throws Exception {
    final Document document = getDocument();
    final DOMBuilder builder = new DOMBuilder(document);
    try {
      builder.characters(new char[] { 't', 'e', 's', 't' }, 0, 4);
      fail("Must throw SAXException");
    } catch (final SAXException e) {
      assertTrue(e.getMessage().startsWith("Warning: can't output text before document element"));
    }
  }

  /**
   * Test comment.
   * 
   * @throws Exception
   *           the exception
   */
  public void testComment() throws Exception {
    final Document document = getDocument();
    final DOMBuilder builder = new DOMBuilder(document, document.appendChild(document.createElement("testRoot")));

    final Node node = document.getFirstChild();
    
    builder.comment(new char[] { 't', 'e', 's', 't', '1' }, 0, 4);
    
    final Comment comment = (Comment) node.getLastChild();
    assertNotNull(comment);
  }
  
  /**
   * Test cdata.
   * 
   * @throws Exception
   *           the exception
   */
  public void testCDATA() throws Exception {
    final Document document = getDocument();
    final DOMBuilder builder = new DOMBuilder(document, document.appendChild(document.createElement("testRoot")));

    final Node node = document.getFirstChild();
    
    builder.startCDATA();
    
    final CDATASection cdataSection = (CDATASection) node.getLastChild();
    assertNotNull(cdataSection);
    
    builder.endCDATA();

    builder.cdata(new char[] { 't', 'e', 's', 't', '1' }, 0, 5);
    assertEquals("test1", cdataSection.getData());
    
    final Comment comment = document.createComment("someComment-");
    node.appendChild(comment);

    builder.cdata(new char[] { 't', 'e', 's', 't', '2' }, 0, 5);
    assertEquals("someComment-test2", comment.getData());
  }
  
  /**
   * Test start end.
   * 
   * @throws Exception
   *           the exception
   */
  public void testStartEnd() throws Exception {
    final Document document = getDocument();
    final DOMBuilder builder = new DOMBuilder(document);
    try {
      builder.startEntity(null);
      builder.skippedEntity(null);
      builder.endEntity(null);
    } catch (final Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Gets the document.
   * 
   * @return the document
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  private Document getDocument() throws ParserConfigurationException {
    final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    final DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    return docBuilder.newDocument();
  }

  /**
   * Test xml character recognizer.
   * 
   * @throws Exception
   *           the exception
   */
  public void testIsWhiteSpace() throws Exception {
    final StringBuffer whitespaceStringBuffer = new StringBuffer();

    for (final char whitespaceChar : WHITESPACE_CHARS) {
      assertTrue(XMLCharacterRecognizer.isWhiteSpace(whitespaceChar));

      final char[] someSequence = new char[] { 'a', whitespaceChar, whitespaceChar, 'z' };
      assertTrue(XMLCharacterRecognizer.isWhiteSpace(someSequence, 1, 2));

      whitespaceStringBuffer.append(whitespaceChar);
    }

    assertTrue(XMLCharacterRecognizer.isWhiteSpace(whitespaceStringBuffer));
    assertTrue(XMLCharacterRecognizer.isWhiteSpace(whitespaceStringBuffer.toString()));

    whitespaceStringBuffer.append('a');

    assertFalse(XMLCharacterRecognizer.isWhiteSpace(whitespaceStringBuffer));
    assertFalse(XMLCharacterRecognizer.isWhiteSpace(whitespaceStringBuffer.toString()));

    assertFalse(XMLCharacterRecognizer.isWhiteSpace('a'));
    assertFalse(XMLCharacterRecognizer.isWhiteSpace(new char[] { 'a', 'b', 'c' }, 0, 1));
  }
  // CHECKSTYLE:TRUE

}
