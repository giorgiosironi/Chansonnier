/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
//CHECKSTYLE:OFF
package org.eclipse.smila.binarystorage.persistence.efs.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistenceFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * TestBinaryPersistenceEFS class.
 *
 * @author Alexander Eliseyev
 */
public class TestBinaryPersistenceEFS extends DeclarativeServiceTestCase {

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
      binaryPersistence.storeBinary("3ea9a6a9d6894a29135f90d1724d69d3b6eea0c68d34161ea3c8cd18324874",
        new InputStream() {
          @Override
          public int read() throws IOException {
            throw new IOException("Test IOException");
          }
        });
      fail("Must throw BinaryStorageException");
    } catch (final BinaryStorageException e) {
      ; // ok
    }

    // (jschumacher) deactivated because it just doesn't fail on linux systems
    //    try {
    //      binaryPersistence.storeBinary("wrong://3ea9a6a9d68", new byte[] {});
    //      fail("Must throw BinaryStorageException");
    //    } catch (final BinaryStorageException e) {
    //      ; // ok
    //    }

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

    configuration
      .setImplementationClass("org.eclipse.smila.binarystorage.persistence.efs.EFSFlatManager");
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
    configuration
      .setImplementationClass("org.eclipse.smila.binarystorage.persistence.efs.EFSHierarchicalManager");
    configuration.setMountPoint("default");
    configuration.setName("default1");
    configuration.setProvider("file");
    configuration.setTempFileName("dummy.dat");
    String path, tempPath;

    File file =
      new File(WorkspaceHelper.createWorkingDir("org.eclipse.smila.binarystorage.persistence.efs.test"), "storage");

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

  // CHECKSTYLE:ON

}
