/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.eclipse.smila.utils.file.EncodingHelper;

/**
 * The Class TestEncodingHelper.
 */
public class TestEncodingHelper extends TestCase {

  /**
   * Test isSupportedEncoding().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testIsSupportedEncoding() throws Exception {
    boolean isSupported = EncodingHelper.isSupportedEncoding(null);
    assertFalse(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("");
    assertFalse(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("blafasel");
    assertFalse(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("iso-1252");
    assertFalse(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("iso-8859-1; Macromedia Dreamweaver 4.0");
    assertFalse(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("iso-8859-1");
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding("Windows-1252");
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding(EncodingHelper.ENCODING_UTF_16BE);
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding(EncodingHelper.ENCODING_UTF_16LE);
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding(EncodingHelper.ENCODING_UTF_32BE);
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding(EncodingHelper.ENCODING_UTF_32LE);
    assertTrue(isSupported);

    isSupported = EncodingHelper.isSupportedEncoding(EncodingHelper.ENCODING_UTF_8);
    assertTrue(isSupported);
  }

  /**
   * Test convertToString().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testConvertToString() throws Exception {
    String convertedString = EncodingHelper.convertToString(null);
    assertNull(convertedString);

    convertedString = EncodingHelper.convertToString(new byte[] {});
    assertEquals(0, convertedString.length());

    byte[] loadedBytes = loadFile("test.html");
    convertedString = EncodingHelper.convertToString(loadedBytes);
    assertTrue(convertedString.length() > 0);

    loadedBytes = loadFile("test2.html");
    convertedString = EncodingHelper.convertToString(loadedBytes);
    assertTrue(convertedString.length() > 0);

    loadedBytes = loadFile("test3.html");
    convertedString = EncodingHelper.convertToString(loadedBytes);
    assertTrue(convertedString.length() > 0);
  }

  /**
   * Test removeBOM().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testRemoveBOM() throws Exception {
    byte[] bytes = EncodingHelper.removeBOM(null);
    assertNull(bytes);

    bytes = EncodingHelper.removeBOM(new byte[] {});
    assertEquals(0, bytes.length);

    byte[] loadedBytes = loadFile("UTF16-BE-BOM.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);
    loadedBytes = loadFile("UTF16-BE-BOM.txt");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);
    loadedBytes = loadFile("UTF16-BE-BOM.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);

    loadedBytes = loadFile("UTF16-LE-BOM.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);
    loadedBytes = loadFile("UTF16-LE-BOM.txt");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);
    loadedBytes = loadFile("UTF16-LE-BOM.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 2, bytes.length);

    loadedBytes = loadFile("UTF8-BOM.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 3, bytes.length);
    loadedBytes = loadFile("UTF8-BOM.txt");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 3, bytes.length);
    loadedBytes = loadFile("UTF8-BOM.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length - 3, bytes.length);

    loadedBytes = loadFile("UTF8-CHARSET.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);
    loadedBytes = loadFile("UTF8-CHARSET.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);

    loadedBytes = loadFile("WinDefault-CHARSET.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);
    loadedBytes = loadFile("WinDefault-CHARSET.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);

    loadedBytes = loadFile("WinDefault.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);
    loadedBytes = loadFile("WinDefault.txt");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);
    loadedBytes = loadFile("WinDefault.xml");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);

    loadedBytes = loadFile("test.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);

    loadedBytes = loadFile("test2.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);

    loadedBytes = loadFile("test3.html");
    bytes = EncodingHelper.removeBOM(loadedBytes);
    assertEquals(loadedBytes.length, bytes.length);
  }

  /**
   * Test isMarkup().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testIsMarkup() throws Exception {
    boolean isMarkup = EncodingHelper.isMarkup(null);
    assertFalse(isMarkup);

    isMarkup = EncodingHelper.isMarkup(new byte[] {});
    assertFalse(isMarkup);

    isMarkup = EncodingHelper.isMarkup("A simple text without valid markup but with <tags>".getBytes());
    assertFalse(isMarkup);

    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.html")));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.txt")));
    assertFalse(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.xml")));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.html")));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.txt")));
    assertFalse(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.xml")));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF8-BOM.html")));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF8-BOM.txt")));
    assertFalse(isMarkup);
    isMarkup = EncodingHelper.isMarkup(EncodingHelper.removeBOM(loadFile("UTF8-BOM.xml")));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("UTF8-CHARSET.html"));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(loadFile("UTF8-CHARSET.xml"));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("WinDefault-CHARSET.html"));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(loadFile("WinDefault-CHARSET.xml"));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("WinDefault.html"));
    assertTrue(isMarkup);
    isMarkup = EncodingHelper.isMarkup(loadFile("WinDefault.txt"));
    assertFalse(isMarkup);
    isMarkup = EncodingHelper.isMarkup(loadFile("WinDefault.xml"));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("test.html"));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("test2.html"));
    assertTrue(isMarkup);

    isMarkup = EncodingHelper.isMarkup(loadFile("test3.html"));
    assertTrue(isMarkup);
  }

  /**
   * Test getEncodingFromBOM().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testGetEncodingFromBOM() throws Exception {
    String encoding = EncodingHelper.getEncodingFromBOM(null);
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(new byte[] {});
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-BE-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-BE-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-BE-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-LE-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-LE-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF16-LE-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF8-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF8-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF8-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF8-CHARSET.html"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("UTF8-CHARSET.xml"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("WinDefault-CHARSET.html"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("WinDefault-CHARSET.xml"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("WinDefault.html"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("WinDefault.txt"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromBOM(loadFile("WinDefault.xml"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("test.html"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("test2.html"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromBOM(loadFile("test3.html"));
    assertNull(encoding);
  }

  /**
   * Test getEncodingFromContent().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testGetEncodingFromContent() throws Exception {
    String encoding = EncodingHelper.getEncodingFromContent(null);
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromContent(new byte[] {});
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.html")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.txt")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-BE-BOM.xml")));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.html")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.txt")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF16-LE-BOM.xml")));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF8-BOM.html")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF8-BOM.txt")));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(EncodingHelper.removeBOM(loadFile("UTF8-BOM.xml")));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("UTF8-CHARSET.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncodingFromContent(loadFile("UTF8-CHARSET.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("WinDefault-CHARSET.html"));
    assertEquals("Windows-1252", encoding);
    encoding = EncodingHelper.getEncodingFromContent(loadFile("WinDefault-CHARSET.xml"));
    assertEquals("Windows-1252", encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("WinDefault.html"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(loadFile("WinDefault.txt"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncodingFromContent(loadFile("WinDefault.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("test.html"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("test2.html"));
    assertEquals("iso-8859-1", encoding);

    encoding = EncodingHelper.getEncodingFromContent(loadFile("test3.html"));
    assertEquals("iso-8859-1", encoding);
  }

  /**
   * Test getEncoding().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testGetEncoding() throws Exception {
    String encoding = EncodingHelper.getEncoding(null);
    assertNull(encoding);

    encoding = EncodingHelper.getEncoding(new byte[] {});
    assertNull(encoding);

    encoding = EncodingHelper.getEncoding(loadFile("UTF16-BE-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF16-BE-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF16-BE-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_16BE, encoding);

    encoding = EncodingHelper.getEncoding(loadFile("UTF16-LE-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF16-LE-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF16-LE-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_16LE, encoding);

    encoding = EncodingHelper.getEncoding(loadFile("UTF8-BOM.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF8-BOM.txt"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF8-BOM.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncoding(loadFile("UTF8-CHARSET.html"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);
    encoding = EncodingHelper.getEncoding(loadFile("UTF8-CHARSET.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncoding(loadFile("WinDefault-CHARSET.html"));
    assertEquals("Windows-1252", encoding);
    encoding = EncodingHelper.getEncoding(loadFile("WinDefault-CHARSET.xml"));
    assertEquals("Windows-1252", encoding);

    encoding = EncodingHelper.getEncoding(loadFile("WinDefault.html"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncoding(loadFile("WinDefault.txt"));
    assertNull(encoding);
    encoding = EncodingHelper.getEncoding(loadFile("WinDefault.xml"));
    assertEquals(EncodingHelper.ENCODING_UTF_8, encoding);

    encoding = EncodingHelper.getEncoding(loadFile("test.html"));
    assertNull(encoding);

    encoding = EncodingHelper.getEncoding(loadFile("test2.html"));
    assertEquals("iso-8859-1", encoding);

    encoding = EncodingHelper.getEncoding(loadFile("test3.html"));
    assertEquals("iso-8859-1", encoding);
  }

  /**
   * Loads the file with the given name form the configuration/data folder.
   * 
   * @param name
   *          the file name
   * @return the bytes of the file
   * @throws IOException
   *           if any error occurs
   */
  private byte[] loadFile(final String name) throws IOException {
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream("configuration/data/" + name);
      return IOUtils.toByteArray(inputStream);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

}
