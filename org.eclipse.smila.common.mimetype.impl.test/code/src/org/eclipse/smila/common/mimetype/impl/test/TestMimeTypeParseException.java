/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.common.mimetype.impl.test;


import junit.framework.TestCase;

import org.eclipse.smila.common.mimetype.MimeTypeParseException;


/**
 * Test class for MimeTypeParseException.
 */
public class TestMimeTypeParseException extends TestCase {

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
   * Test exception handling.
   */
  public void testExceptions() {
    final String msg = "a test error message";
    final Throwable cause = new Exception("a cause exception");

    MimeTypeParseException xsle = new MimeTypeParseException();
    assertNotNull(xsle);
    assertNull(xsle.getCause());

    xsle = new MimeTypeParseException(msg);
    assertNotNull(xsle);
    assertEquals(msg, xsle.getMessage());
    assertNull(xsle.getCause());

    xsle = new MimeTypeParseException(cause);
    assertNotNull(xsle);
    assertEquals("java.lang.Exception: " + cause.getMessage(), xsle.getMessage());
    assertNotNull(xsle.getCause());
    assertEquals(cause, xsle.getCause());

    xsle = new MimeTypeParseException(msg, cause);
    assertNotNull(xsle);
    assertEquals(msg, xsle.getMessage());
    assertNotNull(xsle.getCause());
    assertEquals(cause, xsle.getCause());
  }
}
