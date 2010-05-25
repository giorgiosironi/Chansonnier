/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The Digest Helper.
 */
public final class DigestHelper {

  /** bit mask to use for converting bytes to ints. */
  private static final int BYTE_MASK = 0xff;

  /**
   * Instantiates a new digest helper.
   */
  private DigestHelper() {
  }

  /**
   * Calculate digest.
   * 
   * @param bytes
   *          the bytes
   * 
   * @return hash
   */
  public static String calculateDigest(final byte[] bytes) {
    MessageDigest digest = null;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (final NoSuchAlgorithmException e1) {
      throw new RuntimeException("Cannot happen, SHA-256 MUST be available!");
    }
    final byte[] hash = digest.digest(bytes);
    final StringBuilder hexHash = new StringBuilder();
    for (int i = 0; i < hash.length; i++) {
      hexHash.append(Integer.toHexString(hash[i] & BYTE_MASK));
    }
    return hexHash.toString();
  }

  /**
   * Calculate digest.
   * 
   * @param value
   *          the value
   * 
   * @return hash
   */
  public static String calculateDigest(final String value) {
    if (value == null) {
      return null;
    }
    final byte[] bytes;
    try {
      bytes = value.getBytes("utf-8");
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException("Cannot happen, utf-8 is always known!");
    }
    return calculateDigest(bytes);
  }
}
