/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A collection of utility methods to work with request strings.
 * 
 */
public final class RequestUtil {

  /**
   * Dummy constructor.
   */
  private RequestUtil() {

  }

  /**
   * Builds a query string from a given map of parameters.
   * 
   * @param m
   *          A map of parameters
   * @param ampersand
   *          String to use for ampersands (e.g. "&" or "&amp;" )
   * @param encode
   *          Whether or not to encode non-ASCII characters
   * 
   * @return query string (with no leading "?")
   */
  public static StringBuffer createQueryStringFromMap(Map<?, ?> m, String ampersand, boolean encode) {
    final StringBuffer result = new StringBuffer("");
    final Set<?> entrySet = m.entrySet();
    final Iterator<?> entrySetIterator = entrySet.iterator();

    while (entrySetIterator.hasNext()) {
      final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entrySetIterator.next();
      final Object o = entry.getValue();

      if (o == null) {
        append(entry.getKey(), "", result, ampersand, encode);
      } else if (o instanceof String) {
        append(entry.getKey(), o, result, ampersand, encode);
      } else if (o instanceof String[]) {
        final String[] values = (String[]) o;

        for (int i = 0; i < values.length; i++) {
          append(entry.getKey(), values[i], result, ampersand, encode);
        }
      } else {
        append(entry.getKey(), o, result, ampersand, encode);
      }
    }

    return result;
  }

  /**
   * Builds a query string from a given map of parameters.
   * 
   * @param m
   *          A map of parameters
   * @param ampersand
   *          String to use for ampersands (e.g. "&" or "&amp;" )
   * 
   * @return query string (with no leading "?")
   */
  public static StringBuffer createQueryStringFromMap(Map<?, ?> m, String ampersand) {
    return createQueryStringFromMap(m, ampersand, true);
  }

  /**
   * Append parameters to base URI.
   * 
   * @param uri
   *          An address that is base for adding params
   * @param params
   *          A map of parameters
   * 
   * @return resulting URI
   */
  public static String appendParams(String uri, Map<?, ?> params) {
    String delim;
    if (uri.indexOf('?') == -1) {
      delim = "?";
    } else {
      delim = "&";
    }

    return uri + delim + RequestUtil.createQueryStringFromMap(params, "&").toString();
  }

  /**
   * Appends new key and value pair to query string.
   * 
   * @param key
   *          parameter name
   * @param value
   *          value of parameter
   * @param queryString
   *          existing query string
   * @param ampersand
   *          string to use for ampersand (e.g. "&" or "&amp;")
   * @param encode
   *          whether to encode value
   * 
   * @return query string (with no leading "?")
   */
  private static StringBuffer append(Object key, Object value, StringBuffer queryString, String ampersand,
    boolean encode) {
    if (queryString.length() > 0) {
      queryString.append(ampersand);
    }

    try {
      if (encode) {
        key = URLEncoder.encode(key.toString(), "UTF-8");
        value = URLEncoder.encode(value.toString(), "UTF-8");
      }
      queryString.append(key);
      queryString.append("=");
      queryString.append(value);
    } catch (UnsupportedEncodingException exception) {
      // do nothing
      ;
    }
    return queryString;
  }

}
