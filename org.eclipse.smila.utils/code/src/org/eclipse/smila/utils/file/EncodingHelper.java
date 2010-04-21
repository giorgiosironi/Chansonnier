/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.file;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to help with common encoding problems.
 */
public final class EncodingHelper {

  /**
   * Constant for the encoding UTF-32BE.
   */
  public static final String ENCODING_UTF_32BE = "UTF-32BE";

  /**
   * Constant for the encoding UTF-32LE.
   */
  public static final String ENCODING_UTF_32LE = "UTF-32LE";

  /**
   * Constant for the encoding UTF-8.
   */
  public static final String ENCODING_UTF_8 = "UTF-8";

  /**
   * Constant for the encoding UTF-16BE.
   */
  public static final String ENCODING_UTF_16BE = "UTF-16BE";

  /**
   * Constant for the encoding UTF-16LE.
   */
  public static final String ENCODING_UTF_16LE = "UTF-16LE";

  /**
   * Constant for the number 3.
   */
  private static final int NUMBER_3 = 3;

  /**
   * Constant for the number 4.
   */
  private static final int NUMBER_4 = 4;

  /**
   * Constant for the number 9.
   */
  private static final int NUMBER_9 = 9;

  /**
   * BOM element 0x00.
   */
  private static final byte BOM_00 = (byte) 0x00;

  /**
   * BOM element 0xBB.
   */
  private static final byte BOM_BB = (byte) 0xBB;

  /**
   * BOM element 0xBF.
   */
  private static final byte BOM_BF = (byte) 0xBF;

  /**
   * BOM element 0xEF.
   */
  private static final byte BOM_EF = (byte) 0xEF;

  /**
   * BOM element 0xFE.
   */
  private static final byte BOM_FE = (byte) 0xFE;

  /**
   * BOM element 0xFF.
   */
  private static final byte BOM_FF = (byte) 0xFF;

  /**
   * Maximum number of bytes used for encoding detection.
   */
  private static final int MAX_BYTES = 10000;

  /**
   * The LOG.
   */
  private static final Log LOG = LogFactory.getLog(EncodingHelper.class);

  /**
   * Default Constructor.
   */
  private EncodingHelper() {
    // make it private so it cannot be instantiated
  }

  /**
   * Converts a given byte[] to a String. The method tries to detect the bytes encoding by checking for a BOM and
   * checking for markup encoding information. If no encoding is detected or the detected encoding is invalid the method
   * tries to convert to String using encoding UTF-8. If this fails it tries to convert using the platforms default
   * encoding.
   *
   * @param bytes
   *          the bytes to convert to String
   * @return the converted String
   * @throws IOException
   *           if any error occurs
   */
  public static String convertToString(final byte[] bytes) throws IOException {
    if (bytes == null) {
      return null;
    }
    if (bytes.length == 0) {
      return "";
    }

    final String encoding = EncodingHelper.getEncoding(bytes);
    if (isSupportedEncoding(encoding)) {
      return IOUtils.toString(new ByteArrayInputStream(bytes), encoding);
    } else {
      try {
        // try UTF-8 encoding
        if (LOG.isDebugEnabled()) {
          if (encoding == null) {
            LOG.debug("no encoding detected, trying to convert bytes to String using encoding UTF-8");
          } else {
            LOG.debug("trying to convert bytes to String using encoding UTF-8");
          }
        } // if
        return IOUtils.toString(new ByteArrayInputStream(bytes), ENCODING_UTF_8);
      } catch (final IOException e) {
        // try platform default encoding
        if (LOG.isDebugEnabled()) {
          LOG.debug("converting bytes to String using encoding UTF-8 failed", e);
          LOG.debug("trying to convert bytes to String using default platform encoding.");
        }
        return IOUtils.toString(new ByteArrayInputStream(bytes));
      }
    }
  }

  /**
   * Checks if the given charset is supported by the current java VM.
   *
   * @param charset
   *          the name of the charset.
   * @return true if the charset is supported, false otherwise
   */
  public static boolean isSupportedEncoding(final String charset) {
    if (charset != null) {
      try {
        return Charset.isSupported(charset);
      } catch (final IllegalCharsetNameException e) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("detected charset " + charset + " is not supported");
        }
      }
    }
    return false;
  }

  /**
   * Read bytes and detect encoding based on potential BOM marks or xml or html encoding information.
   *
   * @param bytes
   *          the byte[] to detect a encoding in
   * @return the encoding of the bytes, or <code>null</code> if encoding could not be detected
   * @throws IOException
   *           if any error occur
   */
  public static String getEncoding(final byte[] bytes) throws IOException {
    String encoding = getEncodingFromBOM(bytes);
    if (encoding == null) {
      encoding = getEncodingFromContent(bytes);
    }
    return encoding;
  }

  /**
   * Read bytes and detect encoding based on potential BOM marks.
   *
   * @param bom
   *          the byte[] to detect a BOM in
   * @return the encoding of the bytes, or <code>null</code> if encoding could not be detected
   */
  public static String getEncodingFromBOM(final byte[] bom) {
    String encoding = null;
    if (bom != null && bom.length > NUMBER_3) {
      if ((bom[0] == BOM_EF) && (bom[1] == BOM_BB) && (bom[2] == BOM_BF)) {
        encoding = ENCODING_UTF_8;
      } else if ((bom[0] == BOM_FE) && (bom[1] == BOM_FF)) {
        encoding = ENCODING_UTF_16BE;
      } else if ((bom[0] == BOM_FF) && (bom[1] == BOM_FE)) {
        encoding = ENCODING_UTF_16LE;
      } else if ((bom[0] == BOM_00) && (bom[1] == BOM_00) && (bom[2] == BOM_FE)
        && (bom[NUMBER_3] == BOM_FF)) {
        encoding = ENCODING_UTF_32BE;
      } else if ((bom[0] == BOM_FF) && (bom[1] == BOM_FE) && (bom[2] == BOM_00)
        && (bom[NUMBER_3] == BOM_00)) {
        encoding = ENCODING_UTF_32LE;
      }
    }
    return encoding;
  }

  /**
   * Checks if the originalBytes contain a BOM and Removes the BOM from the byte array. The number of bytes removed
   * depend on if the encoding uses a BOM. If the encoding does not use a BOM the originalBytes are returned. Otherwise
   * the modified byte[]
   *
   * @param originalBytes
   *          the bytes to check for and remove the BOM
   * @return the originalBytes if no BOM was found and removed, otherwise the originalBytes without the BOM
   */
  public static byte[] removeBOM(final byte[] originalBytes) {
    final String encoding = getEncodingFromBOM(originalBytes);
    if (originalBytes != null && originalBytes.length >= 2 && encoding != null) {
      // determine BOM length
      int bomLength = 0;
      if (encoding.equalsIgnoreCase(ENCODING_UTF_32BE)) {
        bomLength = NUMBER_4;
      } else if (encoding.equalsIgnoreCase(ENCODING_UTF_32LE)) {
        bomLength = NUMBER_4;
      } else if (encoding.equalsIgnoreCase(ENCODING_UTF_8)) {
        bomLength = NUMBER_3;
      } else if (encoding.equalsIgnoreCase(ENCODING_UTF_16BE)) {
        bomLength = 2;
      } else if (encoding.equalsIgnoreCase(ENCODING_UTF_16LE)) {
        bomLength = 2;
      }

      if (bomLength >= 2) {
        try {
          final int size = originalBytes.length - bomLength;
          final byte[] modifiedBytes = new byte[size];
          System.arraycopy(originalBytes, bomLength, modifiedBytes, 0, size);
          return modifiedBytes;
        } catch (final Exception e) {
          ;// nothing has to happen
        }
      }
    }
    return originalBytes;
  }

  /**
   * Read bytes and detect encoding based on potential xml or html encoding information from tags. Returns encoding if
   * document is xml or html and if an encoding is defined; null otherwise Stops searching for an encoding. Does not
   * allow a BOM at the start of the bytes.
   *
   * @param bytes
   *          the byte[] to detect a encoding in
   * @return the encoding of the bytes, or <code>null</code> if encoding could not be detected
   * @throws IOException
   *           if any error occur
   */
  public static String getEncodingFromContent(final byte[] bytes) throws IOException {
    // check if bytes contains markup
    if (isMarkup(bytes)) {
      final StringBuffer buffer = new StringBuffer();
      BufferedReader inputReader = null;
      boolean isHTML = false;
      int xmlStart = -1;
      int xmlEnd = -1;
      try {
        inputReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        String line = inputReader.readLine();
        while (line != null && buffer.length() < MAX_BYTES) {
          buffer.append(line);

          if (!isHTML) {
            // check for xml file
            if (xmlStart == -1) {
              xmlStart = line.toLowerCase().indexOf("<?xml");
            }
            if (xmlStart > -1) {
              xmlEnd = line.toLowerCase().indexOf(">");
              if (xmlEnd > -1) {
                // get start and end in context of whole buffer
                xmlStart = buffer.toString().toLowerCase().indexOf("<?xml");
                xmlEnd = buffer.toString().toLowerCase().indexOf(">", xmlStart);
                return getEncodingFromXML(buffer.toString().substring(xmlStart, xmlEnd));
              }
            }

            // check for html file
            if (line.toLowerCase().indexOf("<html") > -1) {
              isHTML = true;
            }
          } else {
            if (line.toLowerCase().indexOf("</head") > -1 || line.toLowerCase().indexOf("<body") > -1) {
              return getEncodingFromHTML(buffer.toString());
            }
          }

          // read next line
          line = inputReader.readLine();
        } // while

        // end of buffer or MAX_BYTES was reached or, if isHTML, try to get encoding from bytes read
        if (isHTML) {
          return getEncodingFromHTML(buffer.toString());
        }
      } finally {
        IOUtils.closeQuietly(inputReader);
      }
    }
    return null;
  }

  /**
   * Checks if the given bytes array represents some kind of markup language (xml, html), by checking if the first non
   * whitespace character is a <. Does not allow a BOM at the start of the bytes.
   *
   * @param bytes
   *          the byte[] to check for markup content
   * @return true if the bytes contain xml or html markup, false otherwise
   * @throws IOException
   *           if any error occurs
   */
  public static boolean isMarkup(final byte[] bytes) throws IOException {
    if (bytes != null) {
      BufferedReader inputReader = null;
      try {
        // find first non whitespace character
        // markup should begin with a <
        inputReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        int buff = inputReader.read();
        while (buff != -1) {
          switch (buff) {
            case 0:
              break;
            case ' ':
              break;
            case '\r':
              break;
            case '\n':
              break;
            case '\t':
              break;
            case '<':
              return true;
            default:
              return false;
          }
          buff = inputReader.read();
        } // while
      } finally {
        IOUtils.closeQuietly(inputReader);
      }
    }
    return false;
  }

  /**
   * Extracts the encoding from an xml string. If no encoding can be detected, the default encoding for xml UTF-8 is
   * returned.
   *
   * @param xml
   *          the xml String to detect encoding in
   * @return the detected encoding or UTF-8
   */
  private static String getEncodingFromXML(final String xml) {
    String enoding = "UTF-8"; // XML files without explicit encoding are UTF-8 by spec

    int end = -1;
    int start = xml.toLowerCase().indexOf("encoding");
    if (start > -1) {
      final int start1 = xml.indexOf("\"", start + NUMBER_9) + 1;
      final int start2 = xml.indexOf("'", start + NUMBER_9) + 1;

      if (start2 > 0 && (start1 > start2 || start1 <= 0)) {
        start = start2;
        end = xml.indexOf("'", start2);
      } else if (start1 > 0) {
        start = start1;
        end = xml.indexOf("\"", start1);
      }

      if (end > -1) {
        enoding = xml.substring(start, end).trim();
      }
    }
    return enoding;
  }

  /**
   * Extracts the encoding from an html string. Searches for a meta tag containign charset information
   *
   * @param html
   *          the html String to detect encoding in
   * @return the detected encoding or null
   */
  private static String getEncodingFromHTML(final String html) {
    String encoding = null;

    // search for meta tags
    int startMeta = html.toLowerCase().indexOf("<meta");
    while (startMeta > -1) {
      final int endMeta = html.toLowerCase().indexOf(">", startMeta);
      if (endMeta > -1) {
        encoding = getEncodingFromMetaTag(html.substring(startMeta, endMeta));
        if (encoding != null) {
          return encoding;
        } else {
          startMeta = html.toLowerCase().indexOf("<meta", endMeta);
        }
      } else {
        encoding = getEncodingFromMetaTag(html.substring(startMeta));
        if (encoding != null) {
          return encoding;
        } else {
          startMeta = -1;
        }
      }
    }
    return encoding;
  }

  /**
   * Searches a meta tag for charset information.
   *
   * @param metaTag
   *          the metaTag to analyze
   * @return the detected encoding or null
   */
  private static String getEncodingFromMetaTag(final String metaTag) {
    String encoding = null;

    // check if meta tag contains content-type info
    int start = metaTag.toLowerCase().indexOf("content-type");
    if (start > -1) {
      start = metaTag.indexOf("charset");
      if (start > -1) {
        start = metaTag.indexOf("=", start);
        if (start > -1) {
          final int end1 = metaTag.indexOf("\"", start);
          final int end2 = metaTag.indexOf("'", start);

          if (end2 > -1 && (end1 > end2 || end1 == -1)) {
            encoding = metaTag.substring(start + 1, end2).trim();
          } else if (end1 > -1) {
            encoding = metaTag.substring(start + 1, end1).trim();
          }
        } // if
      } // if
    } // if

    return encoding;
  }

}
