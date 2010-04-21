/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
// CHECKSTYLE:OFF
package org.eclipse.smila.binarystorage.persistence.io.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistenceFactory;
import org.eclipse.smila.binarystorage.persistence.io.BssIOUtils;
import org.eclipse.smila.binarystorage.persistence.io.IOFlatManager;
import org.eclipse.smila.binarystorage.persistence.io.IOHierarchicalManager;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * A factory for creating TestBinaryPersistence objects.
 *
 * @author Alexander Eliseyev
 */
public class TestBinaryPersistence extends DeclarativeServiceTestCase {

  /** IMPL_CLASS_IOHIERARCHY. */
  private static final String IMPL_CLASS_HIERARCHY = IOHierarchicalManager.class.getName();

  /** KEY. */
  private static final String KEY = "3ea9a6a9d6894a29135f90d1724d69d3b6eea0c68d34161ea3c8cd18324874";

  /** CONTENT_BYTES. */
  private static final byte[] CONTENT_BYTES = "content".getBytes();

  /** Constant file size for 64 MB */
  private final static int FILE_SIZE_64MB = 64 * 1024 * 1024;

  private static final String IMPL_CLASS_FLAT = IOFlatManager.class.getName();

  public void testRemoveEmptyFolderOnDelete() throws Exception {
    final BinaryStorageConfiguration configuration = prepareConfiguration();
    configuration.setImplementationClass(IMPL_CLASS_HIERARCHY);
    configuration.setImplementationClass(IMPL_CLASS_HIERARCHY);

    IOHierarchicalManager bss = (IOHierarchicalManager) BinaryPersistenceFactory.newImplInstance(configuration);
    assertNotNull(bss);

    bss.storeBinary(KEY, CONTENT_BYTES);
    bss.deleteBinary(KEY);
    String calcKey = bss.calculateDirectoryPath(KEY);
    File parentFile = BssIOUtils.getFile(calcKey).getParentFile();
    assertFalse("parent folder should not exist: " + parentFile, parentFile.exists());

  }

  public void testSpecialKeys() throws Exception {
    BinaryStorageService bss = super.getService(BinaryStorageService.class);

    // short key
    bssCrud(bss, "a");

    // special char keys
    String[] forbiddenIds = { "\\", "/", ":", ";", "", "  ", null };
    for (String id : forbiddenIds) {
      try {
        bssCrud(bss, id);
        bss.store(id, CONTENT_BYTES); // this is just to see if the file realy exists in storage
        fail("expected exception @ id: [" + id + "]");
      } catch (BinaryStorageException e) {
      }

    }

    // very long keys
    try {
      bssCrud(bss, StringUtils.repeat("a", 1024));
      fail();
    } catch (Exception e) {
    }

  }

  /**
   * @param bss
   * @param id
   * @throws BinaryStorageException
   */
  private void bssCrud(BinaryStorageService bss, String id) throws BinaryStorageException {
    bss.store(id, CONTENT_BYTES);
    bss.fetchSize(id);
    bss.remove(id);
  }

  /**
   * Test exception handle logic.
   *
   * @throws Exception
   *           the exception
   */
  public void testExceptions() throws Exception {
    final BinaryStorageConfiguration configuration = prepareConfiguration();
    final BinaryPersistence binaryPersistence = BinaryPersistenceFactory.newImplInstance(configuration);

    try {
      binaryPersistence.storeBinary(KEY, new InputStream() {
        @Override
        public int read() throws IOException {
          throw new IOException("Test IOException");
        }
      });
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      assertEquals("Test IOException", e.getRootCause().getMessage());
      ; // ok
    }

    final File tempFile = getTempExistingFile();
    FileInputStream fileInputStream = new FileInputStream(tempFile) {
      @Override
      public FileChannel getChannel() {
        return new DummyFileChannel() {
          @Override
          public long size() throws IOException {
            throw new IOException("Test IOException");
          }
        };
      }
    };
    try {
      binaryPersistence.storeBinary(KEY, fileInputStream);
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      assertEquals("Test IOException", e.getRootCause().getMessage());
    }

    fileInputStream = new FileInputStream(tempFile) {
      @Override
      public FileChannel getChannel() {
        return new DummyFileChannel() {
          @Override
          public long size() throws IOException {
            return FILE_SIZE_64MB + 1;
          }

          @Override
          public long transferTo(final long position, final long count, final WritableByteChannel target)
            throws IOException {
            throw new IOException("Test IOException");
          }
        };
      }
    };
    try {
      binaryPersistence.storeBinary(KEY, fileInputStream);
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      assertEquals("Test IOException", e.getRootCause().getMessage());
    }

    fileInputStream = new FileInputStream(tempFile) {
      @Override
      public FileChannel getChannel() {
        return new DummyFileChannel() {
          @Override
          protected void implCloseChannel() throws IOException {
            throw new IOException("Test IOException");
          }
        };
      }
    };
    try {
      binaryPersistence.storeBinary(KEY, fileInputStream);
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      assertEquals("Test IOException", e.getRootCause().getMessage());
    }

    // (jschumacher) deactivated because it just doesn't fail on linux systems
    // try {
    // binaryPersistence.storeBinary("wrong://3ea9a6a9d68", new byte[] {});
    // fail("Must throw BinaryStorageException");
    // } catch (final BinaryStorageException e) {
    // ; // ok
    // }

    try {
      binaryPersistence.fetchSize("wrong://3ea9a6a9d68");
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      ; // ok
    }

    try {
      binaryPersistence.loadBinaryAsInputStream("wrong://3ea9a6a9d68");
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      ; // ok
    }
  }

  /**
   * Test binary persistence factory.
   *
   * @throws Exception
   *           the exception
   */
  public void testBinaryPersistenceFactory() throws Exception {
    final BinaryStorageConfiguration configuration = prepareConfiguration();

    BinaryPersistence binaryPersistence = BinaryPersistenceFactory.newImplInstance(configuration);
    assertNotNull(binaryPersistence);

    configuration.setImplementationClass(IMPL_CLASS_FLAT);
    binaryPersistence = BinaryPersistenceFactory.newImplInstance(configuration);
    assertNotNull(binaryPersistence);

    configuration.setPath(null);
    try {
      binaryPersistence = BinaryPersistenceFactory.newImplInstance(configuration);
      fail("Must throw IllegalArgumentException on empty path");
    } catch (final IllegalArgumentException e) {
      ; // ok
    }
  }

  /**
   * Prepare configuration.
   *
   * @return the binary storage configuration
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private BinaryStorageConfiguration prepareConfiguration() throws IOException {
    final BinaryStorageConfiguration configuration = new BinaryStorageConfiguration();
    configuration.setImplementationClass(IMPL_CLASS_HIERARCHY);
    configuration.setMountPoint("default");
    configuration.setName("default1");
    configuration.setProvider("file");
    configuration.setTempFileName("dummy.dat");
    configuration.setPathDepth(2);
    String path, tempPath;

    File file = new File(WorkspaceHelper.createWorkingDir("org.eclipse.smila.binarystorage.impl"), "storage");

    if (!file.exists()) {
      file.mkdir();
    }
    path = file.getPath();
    file = new File(file.getParentFile(), "temp");
    if (!file.exists()) {
      file.mkdir();
    }
    tempPath = file.getPath();

    configuration.setPath(path);
    configuration.setTempPath(tempPath);

    return configuration;
  }

  /**
   * Gets the temp file.
   *
   * @return the temp file
   * @throws IOException
   */
  private File getTempExistingFile() throws IOException {
    File file =
      new File(WorkspaceHelper.createWorkingDir("org.eclipse.smila.binarystorage.persistence.io.test"), "storage");

    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getParentFile(), "temp" + new Random().nextInt());

    final BufferedWriter out = new BufferedWriter(new FileWriter(file));
    out.write("aString");
    out.close();

    return file;
  }

  /**
   * The Class DummyFileChannel.
   */
  private class DummyFileChannel extends FileChannel {
    private final static String EXCEPTION_MESSAGE = "";

    @Override
    public void force(final boolean metaData) throws IOException {
      throw new IOException();
    }

    @Override
    public FileLock lock(final long position, final long size, final boolean shared) throws IOException {
      return null;
    }

    @Override
    public MappedByteBuffer map(final MapMode mode, final long position, final long size) throws IOException {
      return null;
    }

    @Override
    public long position() throws IOException {
      return 0;
    }

    @Override
    public FileChannel position(final long newPosition) throws IOException {
      return null;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
      return 0;
    }

    @Override
    public int read(final ByteBuffer dst, final long position) throws IOException {
      return 0;
    }

    @Override
    public long read(final ByteBuffer[] dsts, final int offset, final int length) throws IOException {
      return 0;
    }

    @Override
    public long size() throws IOException {
      return 0;
    }

    @Override
    public long transferFrom(final ReadableByteChannel src, final long position, final long count)
      throws IOException {
      return 0;
    }

    @Override
    public long transferTo(final long position, final long count, final WritableByteChannel target)
      throws IOException {
      return 0;
    }

    @Override
    public FileChannel truncate(final long size) throws IOException {
      return null;
    }

    @Override
    public FileLock tryLock(final long position, final long size, final boolean shared) throws IOException {
      return null;
    }

    @Override
    public int write(final ByteBuffer src) throws IOException {
      return 0;
    }

    @Override
    public int write(final ByteBuffer src, final long position) throws IOException {
      throw new IOException(EXCEPTION_MESSAGE);
    }

    @Override
    public long write(final ByteBuffer[] srcs, final int offset, final int length) throws IOException {
      throw new IOException(EXCEPTION_MESSAGE);
    }

    @Override
    protected void implCloseChannel() throws IOException {
    }

  }

  // CHECKSTYLE:ON

}
