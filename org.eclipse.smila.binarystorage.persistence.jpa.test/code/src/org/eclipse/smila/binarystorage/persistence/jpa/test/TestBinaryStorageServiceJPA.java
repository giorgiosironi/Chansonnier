/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.jpa.test;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Binary storage with EFS persistence test class.
 * 
 * @author Alexander Eliseyev
 */
public class TestBinaryStorageServiceJPA extends DeclarativeServiceTestCase {
  /** CONTENT_SIZE_LARGE.*/
  private static final int CONTENT_SIZE_5MB = 5 * 1024 * 1024;

  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(TestBinaryStorageServiceJPA.class);

  /** Test key shared between test methods. */
  private final String _attachmentIdKey = "3ea9a6a9d6894a29135f90d1724d69d3b6eea0c68d34161ea3c8cd18324874";

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
   *           the exception
   */
  public void testBinaryStorageService_StoreNFetch_Bytes() throws Exception {

    // 1. Create storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Create storage file...");
    }
    byte[] inBytes = _initFileContent.getBytes();
    _binaryStorageService.store(_attachmentIdKey, inBytes);

    // 2. Fetch storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Get storage file...");
    }
    byte[] outBytes = _binaryStorageService.fetchAsByte(_attachmentIdKey);

    assertTrue("content bytes", Arrays.equals(inBytes, outBytes));
  }

  /**
   * Simple test for binary storage service (create, retrieve and delete).
   * 
   * @throws Exception
   *           the exception
   */
  public void testBinaryStorageService_StoreNFetch_Streams() throws Exception {

    // 1. Create storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Create storage file...");
    }
    _binaryStorageService.store(_attachmentIdKey, _initFileContent.getBytes());

    // 2. Fetch storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Get storage file...");
    }
    final InputStream inputStream = _binaryStorageService.fetchAsStream(_attachmentIdKey);
    final String returnedContent = IOUtils.toString(inputStream);
    inputStream.close();

    assertEquals(_initFileContent, returnedContent);

  }

  /**
   * Tests overwriting of content.
   * 
   * @throws Exception
   *           the exception
   */
  public void testOverwriteContent() throws Exception {
    String newContent = _initFileContent + "overwrite";

    _binaryStorageService.store(_attachmentIdKey, _initFileContent.getBytes());
    _binaryStorageService.store(_attachmentIdKey, newContent.getBytes());

    byte[] outBytes = _binaryStorageService.fetchAsByte(_attachmentIdKey);
    assertTrue(Arrays.equals(newContent.getBytes(), outBytes));

  }

  public void testStore_5MB() throws Exception {

    byte[] blob = new byte[CONTENT_SIZE_5MB];
    Arrays.fill(blob, "a".getBytes()[0]);
    _binaryStorageService.store(_attachmentIdKey, blob);

    _binaryStorageService.fetchSize(_attachmentIdKey);
  }

  public void testDelete() throws Exception {

    _binaryStorageService.store(_attachmentIdKey, _initFileContent.getBytes());

    _binaryStorageService.remove(_attachmentIdKey);

    try {
      _binaryStorageService.fetchAsByte(_attachmentIdKey);
      fail();
    } catch (Exception e) {
      ;
    }

  }

  public void testFetchSize() throws Exception {

    _binaryStorageService.store(_attachmentIdKey, _initFileContent.getBytes());

    long fetchSize = _binaryStorageService.fetchSize(_attachmentIdKey);

    assertEquals(_initFileContent.getBytes().length, fetchSize);
  }

  /**
   * Test Binary Storage Service API.
   * 
   * @throws Exception
   *           the exception
   */
  public void _testBinaryStorageServiceAPI() throws Exception {

    _binaryStorageService.store(_attachmentIdKey, _initFileContent.getBytes());

    final InputStream stream = _binaryStorageService.fetchAsStream(_attachmentIdKey);
    final String newAttacmentIdKey = _attachmentIdKey + "0000";
    _binaryStorageService.store(newAttacmentIdKey, stream);
    stream.close();

    final byte[] recordByte = _binaryStorageService.fetchAsByte(newAttacmentIdKey);
    final String returnedContent = new String(recordByte);
    assertEquals(_initFileContent, returnedContent);

    final long sizeInitial = _binaryStorageService.fetchSize(_attachmentIdKey);
    final long sizeFinal = _binaryStorageService.fetchSize(newAttacmentIdKey);
    assertEquals(sizeInitial, sizeFinal);

    _binaryStorageService.remove(_attachmentIdKey);
    _binaryStorageService.remove(newAttacmentIdKey);
  }
}
