/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation Georg Schmidt (brox IT-Solutions GmbH) -
 * coding conventions and adaption to SMILA
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * A class for mapping mime types and file extensions.
 */
public class MimeTypeMapper {
  /**
   * Name of extension file.
   */
  private static final String DEFAULT_EXTENSION_FILE = "mime.types";

  /**
   * MIME type to extension mapping.
   */
  private HashMap<String, String> _mimeTypeExtensions = new HashMap<String, String>();

  /**
   * Extension to MIME type mapping.
   */
  private HashMap<String, String> _extensionMimeTypes = new HashMap<String, String>();

  /**
   * Create MimeTypeMapper by default resource file.
   * 
   * @throws IOException
   *           Resource file could not be loaded.
   */
  public MimeTypeMapper() throws IOException {
    parse(new BufferedReader(new InputStreamReader(
      MimeTypeMapper.class.getResourceAsStream(DEFAULT_EXTENSION_FILE), "ISO-8859-1")));
  }

  /**
   * Create MimeTypeMapper by input stream. The input stream is interpreted as ISO-8859-1 file.
   * 
   * @param input
   *          MIME type mapping as stream. Will be interpreted as ISO-8859-1.
   * @throws IOException
   *           Unable to parse stream.
   */
  public MimeTypeMapper(InputStream input) throws IOException {
    parse(new BufferedReader(new InputStreamReader(input, "ISO-8859-1")));
  }

  /**
   * Create MimeTypeMapper by input stream using the given encoding.
   * 
   * @param input
   *          MIME type mapping as stream
   * @param encoding
   *          encoding of the stream
   * @throws IOException
   *           Unable to parse stream.
   */
  public MimeTypeMapper(InputStream input, String encoding) throws IOException {
    parse(new BufferedReader(new InputStreamReader(input, encoding)));
  }

  /**
   * Get content type by extension.
   * 
   * @param extension
   *          Extension. Must not be null.
   * @return Content type.
   */
  public String getContentType(String extension) {

    if (extension == null) {
      throw new NullPointerException("parameter extension is null");
    }
    return _mimeTypeExtensions.get(extension.toLowerCase());
  }

  /**
   * Get extension by content type.
   * 
   * @param contentType
   *          Content type.
   * @return Extension.
   */
  public String getExtension(String contentType) {
    if (contentType == null) {
      throw new NullPointerException("parameter contentType is null");
    }
    return _extensionMimeTypes.get(contentType);
  }

  /**
   * Parse MIME type file.
   * 
   * @param reader
   *          MIME type mappings to parse.
   * @throws IOException
   *           Unable to parse MIME types.
   */
  private synchronized void parse(BufferedReader reader) throws IOException {
    if (reader == null) {
      throw new NullPointerException("reader");
    }

    final HashMap<String, String> mimeTypes = (HashMap<String, String>) _extensionMimeTypes.clone();
    final HashMap<String, String> extensions = (HashMap<String, String>) _mimeTypeExtensions.clone();
    int count = 0;
    String currentMimetype = null;
    String line;
    while ((line = reader.readLine()) != null) {

      if (currentMimetype != null) {
        currentMimetype = currentMimetype + line;
      } else {
        currentMimetype = line;
      }
      final int stringLength = currentMimetype.length();
      if (stringLength == 0) {
        currentMimetype = null;
      } else if (currentMimetype.charAt(stringLength - 1) != '\\') {
        count += parseMimeTypeExtension(currentMimetype, mimeTypes, extensions);
        currentMimetype = null;
      } else {
        currentMimetype = currentMimetype.substring(0, stringLength - 1);
      }
    }
    if (currentMimetype != null) {
      count += parseMimeTypeExtension(currentMimetype, mimeTypes, extensions);
    }
    if (count > 0) {
      _extensionMimeTypes = mimeTypes;
      _mimeTypeExtensions = extensions;
    }
  }

  /**
   * @param mimetype -
   * @param mimeTypes
   *          MIME type to extension map.
   * @param extensions
   *          Extension to MIME type map.
   * @return Amount of parsed MIME types.
   */
  protected int parseMimeTypeExtension(String mimetype, Map<String, String> mimeTypes,
    Map<String, String> extensions) {
    if (mimetype == null) {
      throw new NullPointerException("spec");
    }
    if (mimeTypes == null) {
      throw new NullPointerException("mimeTypes");
    }
    if (extensions == null) {
      throw new NullPointerException("extensions");
    }

    int count = 0;
    mimetype = mimetype.trim();
    if (mimetype.length() > 0 && mimetype.charAt(0) != '#') {
      final StringTokenizer tokens = new StringTokenizer(mimetype);
      final String type = tokens.nextToken();
      while (tokens.hasMoreTokens()) {
        String ext = tokens.nextToken();
        if (ext.length() != 0) {
          ext = ext.toLowerCase();
          extensions.put(ext, type);
          if (count++ == 0) {
            mimeTypes.put(type, ext);
          }
        }
      }
    }
    return count;
  }
}
