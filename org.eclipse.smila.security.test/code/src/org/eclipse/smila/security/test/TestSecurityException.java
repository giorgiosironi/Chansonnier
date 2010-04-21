/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.security.test;

import junit.framework.TestCase;

import org.eclipse.smila.security.SecurityException;

/**
 * The Class TestSecurityException.
 */
public class TestSecurityException extends TestCase {

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
   * Test creation of SecurityException.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testSecurityException() throws Exception {
    final String msg = "a test message";
    final Throwable cause = new Exception("a cause exception");

    SecurityException se = new SecurityException(msg);
    assertNotNull(se);
    assertEquals(msg, se.getMessage());
    assertNull(se.getCause());

    se = new SecurityException(cause);
    assertEquals(cause, se.getCause());
    assertEquals("java.lang.Exception: " + cause.getMessage(), se.getMessage());

    se = new SecurityException(msg, cause);
    assertNotNull(se);
    assertEquals(msg, se.getMessage());
    assertEquals(cause, se.getCause());
  }

}
