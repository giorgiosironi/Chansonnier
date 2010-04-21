/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.efs.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.eclipse.smila.binarystorage.BinaryStorageService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Binary storage with EFS persistence test class.
 *
 * @author Alexander Eliseyev
 */
public class TestBinaryStorageServiceEFS extends DeclarativeServiceTestCase {
  /** The logger. */
  private final Log _log = org.apache.commons.logging.LogFactory.getLog(TestBinaryStorageServiceEFS.class);

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
   *           the exception
   */
  public void testBinaryStorageService() throws Exception {

    // 1. Create storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Create storage file...");
    }
    _binaryStorageService.store(_attacmentIdKey, _initFileContent.getBytes());
    _binaryStorageService.store(_attacmentIdKey, _initFileContent.getBytes());

    // 2. Fetch storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Get storage file...");
    }
    final InputStream inputStream = _binaryStorageService.fetchAsStream(_attacmentIdKey);
    final String returnedContent = convertStreamToString(inputStream);
    inputStream.close();

    assertEquals(_initFileContent, returnedContent);

    // 3. Delete storage folder/file
    if (_log.isDebugEnabled()) {
      _log.debug("Delete storage file...");
    }
    _binaryStorageService.remove(_attacmentIdKey);
  }

  /**
   * Test Binary Storage Service API.
   *
   * @throws Exception
   *           the exception
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

  /**
   * Utility method to convert stream to string.
   *
   * @param inputStream
   *          the input stream
   *
   * @return String
   */
  public String convertStreamToString(final InputStream inputStream) {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    final StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (final IOException e) {
      _log.error("Error reading from stream", e);
    } finally {
      try {
        inputStream.close();
      } catch (final IOException e) {
        _log.error("Error closing stream", e);
      }
    }
    return sb.toString();
  }
}
