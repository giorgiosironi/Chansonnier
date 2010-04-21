/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 

 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.parse;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for extractors.
 */
public final class ExtractUtils {

  /**
   * Pattern that checks if given string is URI (string begins and ends with word-char, contains no '>' or '<', has an
   * internal dot or slash).
   */
  private static final Pattern URI_PATTERN =
    Pattern.compile("(?:\\w|[\\.]{0,2}/)[\\S&&[^<>]]*(?:\\.|/)[\\S&&[^<>]]*(?:\\w|/)");

  /**
   * Default constructor.
   */
  private ExtractUtils() {

  }

  /**
   * Checks if given string is uri.
   * 
   * @param test
   *          string to check.
   * @return true if test matches URI_PATTERN, false otherwise.
   */
  public static boolean isUrl(String test) {
    final Matcher uriMatcher = URI_PATTERN.matcher(test);
    return uriMatcher.find();
  }
}
