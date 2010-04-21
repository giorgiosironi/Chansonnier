/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.DOMContentUtils;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.js.JavascriptParserImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

//CHECKSTYLE:OFF

/**
 * The Class TestHTMLMetaProcessor.
 * 
 * @author Alexander Eliseyev
 */
public class TestHTMLMetaProcessor extends TestCase {

  /** The Constant BASE_URL. */
  private static final String BASE_URL = "http://www.eclipse/smila/";

  /** The Constant CORRECT_URLS. */
  private static final String[] CORRECT_URLS = new String[] {
    "http://www.google.com/",
    "http://www.somesite.com/",
    "http://www.eclipse/smila/somepage.html",
    "http://somesite.com/"
  };
  
  /** The content utils. */
  private DOMContentUtils _contentUtils;
  
  /**
   * Gets the node.
   * 
   * @param name
   *          the name
   * @param httpEquiv
   *          the http equiv
   * @param content
   *          the content
   * 
   * @return the node
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  private Node getMetaNode(String name, String httpEquiv, String content) throws ParserConfigurationException {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    final Document document = builder.newDocument();
    final Element metaNode = (Element) document.appendChild(document.createElement("meta"));

    if (name != null) {
      final Attr nameAttr = document.createAttribute("name");
      nameAttr.setValue(name);
      metaNode.setAttributeNode(nameAttr);
    }

    if (httpEquiv != null) {
      final Attr httpEquivAttr = document.createAttribute("http-equiv");
      httpEquivAttr.setValue(httpEquiv);
      metaNode.setAttributeNode(httpEquivAttr);
    }

    if (content != null) {
      final Attr contentAttr = document.createAttribute("content");
      contentAttr.setValue(content);
      metaNode.setAttributeNode(contentAttr);
    }

    return metaNode;
  }

  /**
   * Gets the base node.
   * 
   * @return the base node
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  private Node getBaseNode(String href) throws ParserConfigurationException {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    final Document document = builder.newDocument();
    final Element baseNode = (Element) document.appendChild(document.createElement("base"));

    baseNode.setAttribute("href", href);
    
    return baseNode;
  }
  
  /**
   * Test html meta processor.
   * 
   * @throws Exception
   *           the exception
   */
  public void testHTMLMetaProcessor() throws Exception {
    final HTMLMetaTags metaTags = new HTMLMetaTags();
    final URL currURL = new URL(BASE_URL);

    Node node = getMetaNode(null, "refresh", "30;URL=http://www.google.com");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertTrue(metaTags.getRefresh());
    assertEquals(30, metaTags.getRefreshTime());
    assertEquals(new URL("http://www.google.com"), metaTags.getRefreshHref());
    
    node = getMetaNode("robots", "refresh", "none");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertTrue(metaTags.getNoFollow());
    assertTrue(metaTags.getNoIndex());
    
    node = getMetaNode("robots", "refresh", "all");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertFalse(metaTags.getNoFollow());
    assertFalse(metaTags.getNoIndex());
    
    node = getMetaNode("robots", "refresh", "noindex,nofollow");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertTrue(metaTags.getNoFollow());
    assertTrue(metaTags.getNoIndex());
    final String metaStr = metaTags.toString();
    assertTrue(metaStr.contains("noindex,nofollow"));
    assertTrue(metaStr.contains("noCache=false"));
    assertTrue(metaStr.contains("noIndex=true"));
    assertTrue(metaStr.contains("base=null"));
    
    node = getMetaNode("robots", "refresh", "nofollow");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertTrue(metaTags.getNoFollow());
    assertFalse(metaTags.getNoIndex());
    
    node = getMetaNode(null, "pragma", "no-cache");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertTrue(metaTags.getNoCache());    
    
    node = getBaseNode("http://www.google.com");
    HTMLMetaProcessor.getMetaTags(metaTags, node, currURL);
    assertEquals(new URL("http://www.google.com"), metaTags.getBaseHref());    
  }

  /**
   * Test get base.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetBase() throws Exception {
    URL url = _contentUtils.getBase(getBaseNode("http://www.google.com"));
    assertEquals(new URL("http://www.google.com"), url);

    url = _contentUtils.getBase(getBaseNode(null));
    assertNull(url);

    try {
      url = _contentUtils.getBase(getBaseNode("wrongprotocol://google.com"));
      assertNull(url);
    } catch (final Exception e) {
      fail(e.getMessage());
    }

    // Not a base tag at all
    url = _contentUtils.getBase(getMetaNode(null, null, null));
    assertNull(url);
  }
  
  /**
   * Test get javascript outlinks.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetJavascriptOutlinks() throws Exception {    
    final List<Outlink> outlinks = new ArrayList<Outlink>();
    final String base = BASE_URL;
    
    _contentUtils.getJavascriptOutlinks(base, outlinks, getScriptNodeWithJSOutlinks());
    assertCorrectOutlinks(outlinks);
    
    outlinks.clear();
    
    _contentUtils.getJavascriptOutlinks(base, outlinks, getANodeWithJSOutlinks());
    assertCorrectOutlinks(outlinks);
  }

  /**
   * Gets the node with js outlinks.
   * 
   * @return the node with js outlinks
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  private Node getScriptNodeWithJSOutlinks() throws ParserConfigurationException {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document document = builder.newDocument();
    final Element scriptNode = (Element) document.appendChild(document.createElement("script"));
    
    final CDATASection scriptCDATASection = document.createCDATASection(getJSContent());
    scriptNode.appendChild(scriptCDATASection);    
    
    return scriptNode;
  }
  
  /**
   * Gets the node with js outlinks.
   * 
   * @return the node with js outlinks
   * 
   * @throws ParserConfigurationException
   *           the parser configuration exception
   */
  private Node getANodeWithJSOutlinks() throws ParserConfigurationException {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document document = builder.newDocument();
    final Element aNode = (Element) document.appendChild(document.createElement("a"));
    
    aNode.setAttribute("onClick", "alert('www.google.com');\n");
    aNode.setAttribute("onDrag", "alert(\"www.somesite.com\");\n");
    aNode.setAttribute("onMove", "alert('somepage.html');\n");
    aNode.setAttribute("onResize", "alert('notAnUrl');\n");
    aNode.setAttribute("onMove", "alert('somepage.html');\n");
    aNode.setAttribute("href", "javascript:alert('http://somesite.com');\n");    
    
    return aNode;
  }
  
  /**
   * Gets the JS content. 
   * @return the JS content
   */  
  private String getJSContent() {
    final StringBuilder jsCode = new StringBuilder();
    
    jsCode.append("alert('www.google.com');\n");
    jsCode.append("alert(\"www.somesite.com\");\n");
    jsCode.append("alert('somepage.html');\n");
    jsCode.append("alert('notAnUrl');\n");
    jsCode.append("alert('http://somesite.com');\n");
    jsCode.append("alert('badprorocol://somesite.com');\n");
    
    return jsCode.toString();
  }
  
  /**
   * Assert correct outlinks.
   * 
   * @param outlinks
   *          the outlinks
   */
  private static void assertCorrectOutlinks(List<Outlink> outlinks) {
    assertNotNull(outlinks);
    
    final List<String> urls = new ArrayList<String>();
    for (final Outlink outlink : outlinks) {
      urls.add(outlink.getUrlString());
    }
    
    for (final String correctUrl : CORRECT_URLS) {
      assertTrue(urls.contains(correctUrl));
    }
  }
  
  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception {
    super.setUp();
    final Configuration configuration = new Configuration();
    _contentUtils = new DOMContentUtils(configuration);
    final JavascriptParserImpl parserImpl = new JavascriptParserImpl();
    parserImpl.setConf(configuration);
    _contentUtils.setJavascriptParser(parserImpl);
  }
  
}

//CHECKSTYLE:ON
