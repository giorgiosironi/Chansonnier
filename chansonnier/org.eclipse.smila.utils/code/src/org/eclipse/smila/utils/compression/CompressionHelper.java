/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

/**
 * The Compression Helper.
 */
public final class CompressionHelper {

  /**
   * Does not instantiates a new IO helper.
   */
  private CompressionHelper() {
  }

  /**
   * /** Unzip.
   * 
   * @param dest
   *          the dest
   * @param inputStream
   *          the input stream
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void unzip(final File dest, final InputStream inputStream) throws IOException {
    ZipInputStream zis = null;
    FileOutputStream fileOutputStream = null;
    BufferedOutputStream bufferedOutputStream = null;
    try {
      zis = new ZipInputStream(inputStream);
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.isDirectory()) {
          new File(dest, entry.getName()).mkdir();
          continue;
        }
        final File destFile = new File(dest, entry.getName());
        if (!destFile.getParentFile().exists()) {
          destFile.getParentFile().mkdirs();
        }
        try {
          fileOutputStream = new FileOutputStream(destFile);
          bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
          IOUtils.copy(zis, bufferedOutputStream);
        } finally {
          IOUtils.closeQuietly(fileOutputStream);
          IOUtils.closeQuietly(bufferedOutputStream);
        }
        destFile.setLastModified(entry.getTime());
      }
    } finally {
      IOUtils.closeQuietly(zis);
    }
  }

}
