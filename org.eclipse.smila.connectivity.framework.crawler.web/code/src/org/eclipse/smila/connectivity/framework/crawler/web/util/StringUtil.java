/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt(Brox IT Solutions GmbH) - inital creator
 * 
 * This File is based on the src/java/org/apache/nutch/util/StringUtil.java 
 * from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team
 **********************************************************************************************************************/
// CHECKSTYLE:OFF
// Reason: 3rd party adopted class
/**
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.eclipse.smila.connectivity.framework.crawler.web.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of String processing utility methods.
 */
public class StringUtil {

  /**
   * Parse the character encoding from the specified content type header. If the content type is null, or there is no
   * explicit character encoding, <code>null</code> is returned. <br />
   * This method was copy from org.apache.catalina.util.RequestUtil is licensed under the Apache License, Version 2.0
   * (the "License").
   * 
   * @param contentType
   *          a content type header
   */
  public static String parseCharacterEncoding(String contentType) {
    if (contentType == null) {
      return (null);
    }
    final int start = contentType.indexOf("charset=");
    if (start < 0) {
      return (null);
    }
    String encoding = contentType.substring(start + 8);
    final int end = encoding.indexOf(';');
    if (end >= 0) {
      encoding = encoding.substring(0, end);
    }
    encoding = encoding.trim();
    if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
      encoding = encoding.substring(1, encoding.length() - 1);
    }
    return (encoding.trim());

  }

  private static Map<String, String> encodingAliases = new HashMap<String, String>();

  /**
   * the following map is not an alias mapping table, but maps character encodings which are often used in mislabelled
   * documents to their correct encodings. For instance, there are a lot of documents labelled 'ISO-8859-1' which
   * contain characters not covered by ISO-8859-1 but covered by windows-1252. Because windows-1252 is a superset of
   * ISO-8859-1 (sharing code points for the common part), it's better to treat ISO-8859-1 as synonymous with
   * windows-1252 than to reject, as invalid, documents labelled as ISO-8859-1 that have characters outside ISO-8859-1.
   */
  static {
    encodingAliases.put("ISO-8859-1", "windows-1252");
    encodingAliases.put("EUC-KR", "x-windows-949");
    encodingAliases.put("x-EUC-CN", "GB18030");
    encodingAliases.put("GBK", "GB18030");
    // encodingAliases.put("Big5", "Big5HKSCS");
    // encodingAliases.put("TIS620", "Cp874");
    // encodingAliases.put("ISO-8859-11", "Cp874");

  }

  public static String resolveEncodingAlias(String encoding) {
    if (!Charset.isSupported(encoding)) {
      return null;
    }
    final String canonicalName = new String(Charset.forName(encoding).name());
    if (encodingAliases.containsKey(canonicalName)) {
      return encodingAliases.get(canonicalName);
    } else {
      return canonicalName;
    }
  }

}
// CHECKSTYLE:OFF
