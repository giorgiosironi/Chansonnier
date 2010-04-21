/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.common.mimetype.impl.test;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.smila.common.mimetype.impl.MimeTypeMapper;

/**
 * Test class for MimeTypeMapper.
 */
public class TestMimeTypeMapper extends TestCase {

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {

  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {

  }

  /**
   * Test the mimetype mapper.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testMapper() throws Exception {
    MimeTypeMapper mapper = new MimeTypeMapper();
    checkMappings(mapper);

    final InputStream input = MimeTypeMapper.class.getResourceAsStream("mime.types");
    mapper = new MimeTypeMapper();
    checkMappings(mapper);

    mapper = new MimeTypeMapper(input, "ISO-8859-1");
    checkMappings(mapper);
  }

  /**
   * Test exception handling.
   * 
   * @throws Exception
   *           if any error ocuurs
   */
  public void testExceptions() throws Exception {
    try {
      new MimeTypeMapper(null);
    } catch (NullPointerException e) {
      assertNotNull(e);
    }

    try {
      new MimeTypeMapper(null, "UTF-8");
    } catch (NullPointerException e) {
      assertNotNull(e);
    }

    try {
      final InputStream input = MimeTypeMapper.class.getResourceAsStream("mime.types");
      new MimeTypeMapper(input, null);
    } catch (NullPointerException e) {
      assertNotNull(e);
    }

    final MimeTypeMapper mapper = new MimeTypeMapper();
    assertNotNull(mapper);

    try {
      mapper.getContentType(null);
    } catch (NullPointerException e) {
      assertNotNull(e);
      assertEquals("parameter extension is null", e.getMessage());
    }

    try {
      mapper.getExtension(null);
    } catch (NullPointerException e) {
      assertNotNull(e);
      assertEquals("parameter contentType is null", e.getMessage());
    }
  }

  /**
   * Check input/output of the mimetype mapper.
   * 
   * @param mapper
   *          tee mapper to check
   */
  private void checkMappings(MimeTypeMapper mapper) {
    final String extension = "html";
    final String contentType = "text/html";

    assertNotNull(mapper);

    String detectedContentType = mapper.getContentType(extension);
    assertNotNull(detectedContentType);
    assertEquals(contentType, detectedContentType);
    detectedContentType = mapper.getContentType("dummy");
    assertNull(detectedContentType);

    String detectedExtension = mapper.getExtension(contentType);
    assertNotNull(detectedExtension);
    assertEquals(extension, detectedExtension);
    detectedExtension = mapper.getExtension("dummy");
    assertNull(detectedExtension);
  }
}
