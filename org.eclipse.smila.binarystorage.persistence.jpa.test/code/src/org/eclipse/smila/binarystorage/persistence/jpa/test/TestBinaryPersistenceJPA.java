/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
//CHECKSTYLE:OFF
package org.eclipse.smila.binarystorage.persistence.jpa.test;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.smila.binarystorage.BinaryStorageException;
import org.eclipse.smila.binarystorage.config.BinaryStorageConfiguration;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistence;
import org.eclipse.smila.binarystorage.persistence.BinaryPersistenceFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * TestBinaryPersistenceJPA class.
 */
public class TestBinaryPersistenceJPA extends DeclarativeServiceTestCase {

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
      .setImplementationClass("org.eclipse.smila.binarystorage.persistence.jpa.JPABinaryPersistence");
    configuration.setMountPoint("default");
    configuration.setName("default1");
    configuration.setProvider("file");
    configuration.setTempFileName("dummy.dat");

    return configuration;
  }

  // CHECKSTYLE:ON

}
