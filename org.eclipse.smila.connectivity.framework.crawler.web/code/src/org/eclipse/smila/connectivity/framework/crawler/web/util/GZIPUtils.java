/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 *  This File is based on the src/java/org/apache/nutch/util/GZIPUtils.java 
 * from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team
 **********************************************************************************************************************/
/** 
 * Copyright 2005 The Apache Software Foundation 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.eclipse.smila.connectivity.framework.crawler.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A collection of utility methods for working on GZIPed data.
 */
public final class GZIPUtils {

  /** The Constant LOG. */
  private static final Log LOG = LogFactory.getLog(GZIPUtils.class);

  /** The Constant EXPECTED_COMPRESSION_RATIO. */
  private static final int EXPECTED_COMPRESSION_RATIO = 5;

  /** The Constant BUF_SIZE. */
  private static final int BUF_SIZE = 4096;

  /**
   * Dummy constructor.
   */
  private GZIPUtils() {

  }

  /**
   * Returns an gunzipped copy of the input array. If the gzipped input has been truncated or corrupted, a best-effort
   * attempt is made to unzip as much as possible. If no data can be extracted <code>null</code> is returned.
   * 
   * @param in
   *          input byte array
   * @return byte array
   */
  public static byte[] unzipBestEffort(byte[] in) {
    return unzipBestEffort(in, Integer.MAX_VALUE);
  }

  /**
   * Returns an gunzipped copy of the input array, truncated to <code>sizeLimit</code> bytes, if necessary. If the
   * gzipped input has been truncated or corrupted, a best-effort attempt is made to unzip as much as possible. If no
   * data can be extracted <code>null</code> is returned.
   * 
   * @param in
   *          input byte array
   * @param sizeLimit
   *          value in bytes to truncate gunzziped copy of the input array
   * @return gunzipped byte array
   */
  public static byte[] unzipBestEffort(byte[] in, int sizeLimit) {
    try {
      // decompress using GZIPInputStream
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

      final GZIPInputStream inStream = new GZIPInputStream(new ByteArrayInputStream(in));

      final byte[] buf = new byte[BUF_SIZE];
      int written = 0;
      while (true) {
        try {
          final int size = inStream.read(buf);
          if (size <= 0) {
            break;
          }
          if ((written + size) > sizeLimit) {
            outStream.write(buf, 0, sizeLimit - written);
            break;
          }
          outStream.write(buf, 0, size);
          written += size;
        } catch (IOException e) {
          break;
        }
      }
      try {
        outStream.close();
      } catch (IOException exception) {
        // don't matter
        ;
      }

      return outStream.toByteArray();

    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Returns an gunzipped copy of the input array.
   * 
   * @param in
   *          input byte array
   * @return gunzipped copy of the input array
   * @throws IOException
   *           if the input cannot be properly decompressed
   */
  public static byte[] unzip(byte[] in) throws IOException {
    // decompress using GZIPInputStream
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

    final GZIPInputStream inStream = new GZIPInputStream(new ByteArrayInputStream(in));

    final byte[] buf = new byte[BUF_SIZE];
    while (true) {
      final int size = inStream.read(buf);
      if (size <= 0) {
        break;
      }
      outStream.write(buf, 0, size);
    }
    outStream.close();

    return outStream.toByteArray();
  }

  /**
   * Returns an gzipped copy of the input array.
   * 
   * @param in
   *          input byte array
   * @return byte gzipped copy of the input array
   */
  public static byte[] zip(byte[] in) {
    try {
      // compress using GZIPOutputStream
      final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(in.length / EXPECTED_COMPRESSION_RATIO);

      final GZIPOutputStream outStream = new GZIPOutputStream(byteOut);

      try {
        outStream.write(in);
      } catch (Exception exception) {
        LOG.error(exception.getMessage());
      }

      try {
        outStream.close();
      } catch (IOException e) {
        LOG.error(e.getMessage());
      }

      return byteOut.toByteArray();

    } catch (IOException e) {
      LOG.error(e.getMessage());
      return null;
    }
  }

}
