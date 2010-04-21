/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.smila.utils.compression.CompressionHelper;

/**
 * The Class TestCompression.
 */
public class TestCompression extends TestCase {

  /**
   * The Constant FS_DELAY.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  /**
   * Test compression.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testCompression() throws IOException {
    File tempFolder = File.createTempFile("SMILA", "testCompression");
    try {
      final String path = tempFolder.getPath();
      tempFolder.delete();
      tempFolder = new File(path);
      // sleepFS();
      InputStream inputStream = null;
      try {
        inputStream = TestCompression.class.getResourceAsStream("TestCompression.zip");
        CompressionHelper.unzip(tempFolder, inputStream);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
      // sleepFS();
      assertTrue(tempFolder.exists());
      final File[] files = tempFolder.listFiles();
      assertEquals(files.length, 1);
      assertEquals(files[0].getName(), "about.html");
    } finally {
      FileUtils.deleteQuietly(tempFolder);
    }
  }
}
