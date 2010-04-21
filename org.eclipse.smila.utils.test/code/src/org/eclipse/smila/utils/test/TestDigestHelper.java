/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.smila.utils.digest.DigestHelper;

/**
 * The Class TestDigest.
 */
public class TestDigestHelper extends TestCase {

  /**
   * The Constant INPUT.
   */
  private static final String INPUT = "input";

  /**
   * The Constant OUTPUT - SHA-256 digest of INPUT.
   */
  private static final String OUTPUT = "c96c6d5be8d08a12e7b5cdc1b27fa6b2430974c86803d8891675e76fd992c20";

  /**
   * Test string.
   */
  public void testString() {
    final String digest = DigestHelper.calculateDigest(INPUT);
    assertEquals(digest, OUTPUT);
  }

  /**
   * Test bytes.
   * 
   * @throws UnsupportedEncodingException
   *           the unsupported encoding exception
   */
  public void testBytes() throws UnsupportedEncodingException {
    final byte[] bytes = INPUT.getBytes("utf-8");
    final String digest = DigestHelper.calculateDigest(bytes);
    assertEquals(digest, OUTPUT);
  }

  /**
   * Test null.
   */
  public void testNull() {
    final String digest = DigestHelper.calculateDigest((String) null);
    assertNull(digest);
  }
}
