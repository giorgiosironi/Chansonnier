/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseData;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseStatus;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

//CHECKSTYLE:OFF

/**
 * The Class TestParseData.
 * 
 * @author Alexander Eliseyev
 */
public class TestParseData extends TestCase {

  /** The Constant BASE_URL. */
  private static final String BASE_URL = "http://www.eclipse/smila/";

  /**
   * Test parse status.
   * 
   * @throws Exception
   *           the exception
   */
  public void testParseStatus() throws Exception {
    final ParseStatus emptyStatus = new ParseStatus();
    assertEquals(0, emptyStatus.getMajorCode());
    assertNull(emptyStatus.getMessage());
    assertEquals("notparsed(0,0)", emptyStatus.toString());

    final ParseStatus status = new ParseStatus(ParseStatus.SUCCESS, "OK");
    assertEquals(ParseStatus.SUCCESS, status.getMajorCode());
    assertEquals("success(" + ParseStatus.SUCCESS + ",0)message=OK", status.toString());
    assertTrue(status.isSuccess());

    final ParseStatus status1 = new ParseStatus(ParseStatus.SUCCESS, 0, "OK");
    assertEquals(status1, status);

    status.setMessage(null);
    assertFalse(status.equals(status1));

    status.setMajorCode((byte) (1 + 1 + 1));
    assertEquals("UNKNOWN!(" + (1 + 1 + 1) + ",0)", status.toString());

    final ParseStatus failStatus = new ParseStatus(new RuntimeException("testException"));
    assertEquals("java.lang.RuntimeException: testException", failStatus.getMessage());
    assertEquals(ParseStatus.FAILED, failStatus.getMajorCode());
    assertFalse(failStatus.isSuccess());
  }

  /**
   * Test parse impl.
   * 
   * @throws Exception
   *           the exception
   */
  public void testParseImpl() throws Exception {
    final ParseData parseData =
      new ParseData(new ParseStatus(ParseStatus.SUCCESS, "OK"), "testTitle", new Outlink[] {}, new Metadata());
    final Parse parse = new ParseImpl(parseData);
    assertEquals(parseData, parse.getData());
  }

  /**
   * Test get default port number.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetDefaultPortNumber() throws Exception {
    assertEquals(80, Outlink.getDefaultPortNumber("http"));
    assertEquals(443, Outlink.getDefaultPortNumber("https"));
  }
  
  /**
   * Test content.
   * 
   * @throws Exception
   *           the exception
   */
  public void testContent() throws Exception {
    final String url = BASE_URL;
    final String base = BASE_URL;
    final byte[] contentBytes = "TestContent".getBytes();
    final String contentType = "text/html";
    final Metadata metadata = new Metadata();
    metadata.add("name1", "value1");

    Content content = new Content();
    content.setContent(contentBytes);
    content.setContentType(contentType);
    content.setMetadata(metadata);
    assertEquals(contentBytes, content.getContent());
    assertEquals(contentType, content.getContentType());
    assertEquals(metadata, content.getMetadata());

    content = new Content(url, base, contentBytes, contentType);
    assertEquals(contentBytes, content.getContent());
    assertEquals(base, content.getBaseUrl());
    assertEquals(url, content.getUrl());
    assertEquals(new Metadata(), content.getMetadata());
    assertEquals(url.hashCode(), content.hashCode());

    content = new Content(url, base, contentBytes, contentType, metadata);
    assertEquals(contentBytes, content.getContent());
    assertEquals(base, content.getBaseUrl());
    assertEquals(url, content.getUrl());
    assertEquals(metadata, content.getMetadata());

    final String contentStr = content.toString();
    assertTrue(contentStr.contains(BASE_URL));
    assertTrue(contentStr.contains(contentType));
    assertTrue(contentStr.contains("TestContent"));

    try {
      new Content(null, base, contentBytes, contentType, metadata);
      fail("Must throw IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      ; // ok
    }

    try {
      new Content(url, null, contentBytes, contentType, metadata);
      fail("Must throw IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      ; // ok
    }

    try {
      new Content(url, base, null, contentType, metadata);
      fail("Must throw IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      ; // ok
    }

    try {
      new Content(url, base, contentBytes, contentType, null);
      fail("Must throw IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      ; // ok
    }
  }

  /**
   * Test parse data.
   * 
   * @throws Exception
   *           the exception
   */
  public void testParseData() throws Exception {
    final ParseStatus status = new ParseStatus(ParseStatus.SUCCESS, ParseStatus.SUCCESS_REDIRECT);
    final String title = "test-title";
    final Outlink[] outlinks = new Outlink[] { new Outlink(BASE_URL, BASE_URL, new Configuration()) };
    final Metadata contentMeta = new Metadata();
    contentMeta.add("name1", "value1");
    final Metadata parseMeta = new Metadata();
    parseMeta.add("name2", "value2");
    final HTMLMetaTags htmlMetaTags = new HTMLMetaTags();

    ParseData data = new ParseData();
    assertNull(data.getParseMeta());
    assertNull(data.getTitle());

    data = new ParseData(status, title, outlinks, contentMeta);
    assertNotNull(data.getParseMeta());
    assertEquals("test-title", data.getTitle());
    assertEquals(outlinks, data.getOutlinks());
    assertEquals(contentMeta, data.getContentMeta());
    assertEquals(title.hashCode(), data.hashCode());

    final ParseData data1 = new ParseData(status, title, outlinks, contentMeta, htmlMetaTags);
    assertNotNull(data1.getParseMeta());
    assertEquals("test-title", data1.getTitle());
    assertEquals(outlinks, data1.getOutlinks());
    assertEquals(contentMeta, data1.getContentMeta());
    assertEquals(htmlMetaTags, data1.getHtmlMetaTags());

    assertEquals(data1, data);

    final String dataToString = data.toString();
    assertTrue(dataToString.contains("success(1,100)"));
    assertTrue(dataToString.contains("test-title"));
    assertTrue(dataToString.contains("http://www.eclipse/smila/"));
    assertTrue(dataToString.contains("name1=value1"));

    data.setParseMeta(parseMeta);
    assertTrue(data.toString().contains("name2=value2"));

    assertFalse(data1.equals(data));

    assertEquals("value1", data.getMeta("name1"));
    assertEquals("value2", data.getMeta("name2"));
  }

}

//CHECKSTYLE:ON
