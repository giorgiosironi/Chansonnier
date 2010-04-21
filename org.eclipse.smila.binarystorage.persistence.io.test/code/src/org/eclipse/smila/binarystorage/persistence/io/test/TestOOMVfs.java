/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io.test;
//CHECKSTYLE:OFF

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.digest.DigestHelper;

/**
 * Commons-vfs adds a listener for each sub-folder that points to a file ... memory consuming.
 * 
 * i.e. if need to store a file xyz under root_path/1A/2B/3C/xyz, then we will have 4 listeners (the three sub-folders
 * and the file)
 * 
 * <code>
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232, listner :org.apache.commons.vfs.util.WeakRefFileListener@1a58470, number of listeners :1
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232XXX/1A/2B/3C/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232, listner :org.apache.commons.vfs.util.WeakRefFileListener@b3b622, number of listeners :1
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232XXX/1A/2B/3C, listner :org.apache.commons.vfs.util.WeakRefFileListener@174353a, number of listeners :1
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232XXX/1A/2B, listner :org.apache.commons.vfs.util.WeakRefFileListener@a68e82, number of listeners :1
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232XXX/1A, listner :org.apache.commons.vfs.util.WeakRefFileListener@7d9406, number of listeners :1
 * Add listner for file file:///D:/eccenca/SMILA/eclipse.build.make/Application/SMILA-incubation-win32.win32.x86/workspace/.metadata/.plugins/org.eclipse.smila.binarystorage/storage/default/c37c128d18383ec07addf49e9e20f696f468d5e7478547d2b6a083807aa0b232XXX, listner :org.apache.commons.vfs.util.WeakRefFileListener@1a09c82, number of listeners :1
 *
 * </code>
 * 
 * @author mcimpean
 * 
 */
public class TestOOMVfs extends DeclarativeServiceTestCase {
  /** The logger. */
  private static final Log _log = org.apache.commons.logging.LogFactory.getLog(TestOOMVfs.class);

  /**
   * BinaryStorageService.
   */
  private BinaryStorageService _binaryStorageService;

  /**
   * Utility counter to simulate ID hashing.
   */
  private static int counter;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _binaryStorageService = super.getService(BinaryStorageService.class);
  }

  /**
   * Simple test for binary storage service (create, retrieve and delete).
   * 
   * @throws Exception
   */
  public void testBinaryStorageService() throws Exception {
    final String root = "D:/sampledata";
    traverse(new File(root));
  }

  public final void traverse(final File f) throws IOException, BinaryStorageException {
    counter++;
    if (f.isDirectory()) {
      onDirectory(f);
      final File[] childs = f.listFiles();
      for (final File child : childs) {
        traverse(child);
      }
      return;
    }
    onFile(f);
  }

  /**
   * Fire event on directory.
   * 
   * @param directory
   */
  public void onDirectory(final File directory) {
    _log.debug("directory :" + directory.getPath());
  }

  /**
   * Fire event on file.
   * 
   * @param file
   * @throws BinaryStorageException
   * @throws IOException
   */
  public void onFile(final File file) throws BinaryStorageException, IOException {
    _log.debug("file :" + file.getPath());
    final String id = getAttachmentId("" + counter, file.getPath());
    _binaryStorageService.store(id, readFile(file));
    final InputStream stream = _binaryStorageService.fetchAsStream(id);
    stream.close();
    _binaryStorageService.remove(id);
  }

  /**
   * Read file and return byte array
   * 
   * @param file
   * @return byte[] - file content
   * @throws IOException
   */
  public static byte[] readFile(File file) {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      final byte[] data = new byte[(int) file.length()];
      int offset = 0;
      int read = 0;
      while (offset < data.length && (read = inputStream.read(data, offset, data.length - offset)) >= 0) {
        offset += read;
      }
      if (offset < data.length) {
        _log.warn("Could not complete read from file :" + file.getPath());
      }
      inputStream.close();
      return data;

    } catch (final IOException ioe) {
      _log.error("Error accessing file :" + file.getPath(), ioe);
      throw new RuntimeException(ioe);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (final IOException ioe) {
          _log.error("Error closing stream for file :" + file.getPath(), ioe);
          throw new RuntimeException(ioe);
        }
      }
    } // finally
  }

  private String getAttachmentId(final String id, final String name) {
    return DigestHelper.calculateDigest(id + name);
  }

}

//CHECKSTYLE:ON
