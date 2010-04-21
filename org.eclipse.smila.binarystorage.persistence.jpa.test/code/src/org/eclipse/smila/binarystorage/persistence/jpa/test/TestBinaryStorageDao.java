/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.jpa.test;

import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.eclipse.smila.binarystorage.persistence.jpa.BinaryStorageDao;

/**
 * Test case for BinaryStorageDao.
 */
public class TestBinaryStorageDao extends TestCase {

  /**
   * Tests BinaryStorageDao.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testBinaryStorageDao() throws Exception {
    final String id = "0815";
    final String content = "This is some test data.";

    BinaryStorageDao dao = new BinaryStorageDao(id, content.getBytes("utf-8"));
    assertNotNull(dao);
    assertEquals(id, dao.getId());
    assertNotNull(dao.getBytes());
    assertEquals(content, new String(dao.getBytes()));
    assertNotNull(dao.getBytesAsStream());
    assertEquals(content, IOUtils.toString(dao.getBytesAsStream()));

    dao = new BinaryStorageDao(id, IOUtils.toInputStream(content, "utf-8"));
    assertNotNull(dao);
    assertEquals(id, dao.getId());
    assertNotNull(dao.getBytes());
    assertEquals(content, new String(dao.getBytes()));
    assertNotNull(dao.getBytesAsStream());
    assertEquals(content, IOUtils.toString(dao.getBytesAsStream()));
  }

  /**
   * Tests BinaryStorageDao Exceptions.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testExceptions() throws Exception {
    final String id = "0815";

    try {
      new BinaryStorageDao(null, (byte[]) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter id is null", e.getMessage());
    }

    try {
      new BinaryStorageDao(null, (InputStream) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter id is null", e.getMessage());
    }

    try {
      new BinaryStorageDao("", (byte[]) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter id is an empty String", e.getMessage());
    }

    try {
      new BinaryStorageDao("", (InputStream) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter id is an empty String", e.getMessage());
    }

    try {
      new BinaryStorageDao(id, (byte[]) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter data is null", e.getMessage());
    }

    try {
      new BinaryStorageDao(id, (InputStream) null);
    } catch (IllegalArgumentException e) {
      assertEquals("parameter input is null", e.getMessage());
    }
  }
}
