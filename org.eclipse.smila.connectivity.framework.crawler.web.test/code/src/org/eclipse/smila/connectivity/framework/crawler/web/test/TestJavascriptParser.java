/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.js.JavascriptParserImpl;

//CHECKSTYLE:OFF

/**
 * The Class TestJavascriptParser.
 * 
 * @author Alexander Eliseyev
 */
public class TestJavascriptParser extends TestCase {

  /** The _javascript parser. */
  private JavascriptParserImpl _javascriptParser;
  
  /** The Constant CORRECT_URLS. */
  private static final String[] CORRECT_URLS = new String[] {
    "http://www.google.com/",
    "http://www.somesite.com/",
    "http://www.eclipse/smila/somepage.html",
    "http://somesite.com/"
  };
  
  /** The Constant BASE_URL. */
  private static final String BASE_URL = "http://www.eclipse/smila/";
  
  /**
   * Test get outlinks.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetOutlinks() throws Exception {
    final String jsContent = new String(getJSContent());
    final Outlink[] outlinks = _javascriptParser.getOutlinks(jsContent, null, BASE_URL);
    
    assertCorrectOutlinks(outlinks);    
  }
  
  /**
   * Assert correct outlinks.
   * 
   * @param outlinks
   *          the outlinks
   */
  private static void assertCorrectOutlinks(Outlink[] outlinks) {
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
   * Test get parse.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetParse() throws Exception {
    final Content content = new Content(BASE_URL, BASE_URL, getJSContent(), "text/javascript");
    final Parse parse = _javascriptParser.getParse(content);
    assertNotNull(parse);
    
    assertCorrectOutlinks(parse.getData().getOutlinks());
    assertEquals("", parse.getText());
  }
  
  /**
   * Gets the JS content.
   * 
   * @return the JS content
   */
  private byte[] getJSContent() {
    final StringBuilder jsCode = new StringBuilder();
    
    jsCode.append("alert('www.google.com');\n");
    jsCode.append("alert(\"www.somesite.com\");\n");
    jsCode.append("alert('somepage.html');\n");
    jsCode.append("alert('notAnUrl');\n");
    jsCode.append("alert('http://somesite.com');\n");
    jsCode.append("alert('badprorocol://somesite.com');\n");
    
    return jsCode.toString().getBytes();
  }

  /**
   * {@inheritDoc}
   */
  protected void setUp() throws Exception {
    super.setUp();
    _javascriptParser = new JavascriptParserImpl();
    _javascriptParser.setConf(new Configuration());
  }
  
}

//CHECKSTYLE:ON
