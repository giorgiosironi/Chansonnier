/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io.test;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Binary storage test class.
 *
 * @author mcimpean
 */
public class TestBinaryStorageService extends DeclarativeServiceTestCase {
  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(TestBinaryStorageService.class);

  /** Test key shared between test methods. */
  private final String _attacmentIdKey = "3ea9a6a9d6894a29135f90d1724d69d3b6eea0c68d34161ea3c8cd18324874";

  /** Text record. */
  private final String _initFileContent = "Dummy text file for testing the binary storage service.\n";

  /**
   * BinaryStorageService.
   */
  private BinaryStorageService _binaryStorageService;

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
   *           Exception
   */
  public void testBinaryStorageService() throws Exception {

    // 1. Create storage folder/file
    _log.debug("Create storage file...");

    _binaryStorageService.store(_attacmentIdKey, _initFileContent.getBytes());
    _binaryStorageService.store(_attacmentIdKey, _initFileContent.getBytes());

    // 2. Fetch storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Get storage file...");
    }
    final InputStream inputStream = _binaryStorageService.fetchAsStream(_attacmentIdKey);
    String returnedContent;
    try {
      returnedContent = IOUtils.toString(inputStream);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }

    assertEquals(_initFileContent, returnedContent);

    _log.debug("Delete storage file...");
    _binaryStorageService.remove(_attacmentIdKey);
  }

  /**
   * Test Binary Storage Service API.
   *
   * @throws Exception
   *           Exception
   */
  public void testBinaryStorageServiceAPI() throws Exception {

    _binaryStorageService.store(_attacmentIdKey, _initFileContent.getBytes());
    final InputStream stream = _binaryStorageService.fetchAsStream(_attacmentIdKey);
    final String newAttacmentIdKey = _attacmentIdKey + "0000";
    _binaryStorageService.store(newAttacmentIdKey, stream);
    stream.close();

    final byte[] recordByte = _binaryStorageService.fetchAsByte(newAttacmentIdKey);
    final String returnedContent = new String(recordByte);
    assertEquals(_initFileContent, returnedContent);

    final long sizeInitial = _binaryStorageService.fetchSize(_attacmentIdKey);
    final long sizeFinal = _binaryStorageService.fetchSize(newAttacmentIdKey);
    assertEquals(sizeInitial, sizeFinal);

    _binaryStorageService.remove(_attacmentIdKey);
    _binaryStorageService.remove(newAttacmentIdKey);
  }
}
