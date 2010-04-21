/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH) - initial API and implementation (based on aperture test by DS)
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype.impl.test;

import junit.framework.TestCase;

import org.eclipse.smila.common.mimetype.MimeTypeParseException;
import org.eclipse.smila.common.mimetype.impl.SimpleMimeTypeIdentifier;

/**
 * The Class TestConnectivity.
 */
public class TestMimeTypeIdentifier extends TestCase{

  /** the MimeTypeIdentifier. */
  private org.eclipse.smila.common.mimetype.MimeTypeIdentifier _mdi;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _mdi = new SimpleMimeTypeIdentifier();
    assertNotNull(_mdi);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _mdi = null;
  }

  /**
   * Tests Exceptions.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testExceptions() throws Exception {

    try {
      _mdi.identify((byte[]) null);
      fail("expected MimeTypeParseException");
    } catch (final MimeTypeParseException e) {
      assertNotNull(e);
    } catch (final Exception e) {
      fail("expected MimeTypeParseException");
    }

    try {
      _mdi.identify((String) null);
      fail("expected MimeTypeParseException");
    } catch (final MimeTypeParseException e) {
      assertNotNull(e);
    } catch (final Exception e) {
      fail("expected MimeTypeParseException");
    }

    try {
      _mdi.identify(null, null);
      fail("expected MimeTypeParseException");
    } catch (final MimeTypeParseException e) {
      assertNotNull(e);
    } catch (final Exception e) {
      fail("expected MimeTypeParseException");
    }

    try {
      _mdi.identify(null, ".txt");
      fail("expected MimeTypeParseException");
    } catch (final MimeTypeParseException e) {
      assertNotNull(e);
    } catch (final Exception e) {
      fail("expected MimeTypeParseException");
    }
  }

  /**
   * Test identify.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testIdentify() throws Exception {
    final String extension = "txt";
    final String expectedMimeType = "text/plain";

    final String detectedMimeType = _mdi.identify(extension);
    assertEquals(expectedMimeType, detectedMimeType);
  }

  /**
   * Test txt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testTXT() throws Exception {
    executeTest("txt", "text/plain");
  }

  /**
   * Test utf-8 txt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testUTF8TXT() throws Exception {
    executeTest("txt", "text/plain");
  }

  /**
   * Test html.
   * 
   * @throws Exception
   *           the exception
   */
  public void testHTML() throws Exception {
    executeTest("html", "text/html");
  }

  /**
   * Test pdf.
   * 
   * @throws Exception
   *           the exception
   */
  public void testPDF() throws Exception {
    executeTest("pdf", "application/pdf");
  }

  /**
   * Test rtf.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRTF() throws Exception {
    executeTest("rtf", "text/rtf");
  }

  /**
   * Test xml.
   * 
   * @throws Exception
   *           the exception
   */
  public void testXML() throws Exception {
    executeTest("xml", "text/xml");
  }

  /**
   * Test msoffic e2003 doc.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003DOC() throws Exception {
    executeTest("doc", "application/vnd.ms-word");
  }

  /**
   * Test msoffic e2003 ppt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003PPT() throws Exception {
    executeTest("ppt", "application/vnd.ms-powerpoint");
  }

  /**
   * Test msoffic e2003 xls.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2003XLS() throws Exception {
    executeTest("xls", "application/vnd.ms-excel");
  }

  /**
   * Test msoffic e2007 docx.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMSOFFICE2007DOCX() throws Exception {
    executeTest("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
  }

  /**
   * Test openoffic e24 odp.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE24ODP() throws Exception {
    executeTest("odp", "application/vnd.oasis.opendocument.presentation");
  }

  /**
   * Test openoffic e22 ods.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE22ODS() throws Exception {
    executeTest("ods", "application/vnd.oasis.opendocument.spreadsheet");
  }

  /**
   * Test openoffic e22 odt.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOPENOFFICE22ODT() throws Exception {
    executeTest("odt", "application/vnd.oasis.opendocument.text");
  }

  /**
   * Helper meethod to execute tests.
   * 
   * @param extension
   *          the file extension
   * @param expectedMimeType
   *          the expected mime type
   * @throws Exception
   *           if any error occurs
   */
  protected void executeTest(String extension, String expectedMimeType) throws Exception {
    identifyAndCompare(null, extension, expectedMimeType);
  }

  /**
   * Identifies the mime type with the given parameters and compares the result.
   * 
   * @param data
   *          the data
   * @param extension
   *          the file extension
   * @param expectedMimeType
   *          the expected mime type
   * @throws Exception
   *           if any error occurs
   */
  private void identifyAndCompare(byte[] data, String extension, String expectedMimeType) throws Exception {
    try {
      final String detectedMimeType = _mdi.identify(data, extension);
      assertEquals(expectedMimeType, detectedMimeType);
    } catch (final Exception e) {
      throw e;
    }
  }
}
