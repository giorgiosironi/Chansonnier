/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.recordstorage.RecordStorageException;
import org.eclipse.smila.recordstorage.util.RecordDao;
import org.eclipse.smila.recordstorage.util.RecordIterator;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestRecordStore.
 */
public class TestUtilities extends DeclarativeServiceTestCase {

  /**
   * Test creation of RecordStorageException.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testRecordStorageException() throws Exception {
    // test constructor
    final RecordStorageException e1 = new RecordStorageException();
    assertNotNull(e1);
    final RecordStorageException e2 = new RecordStorageException("test");
    assertNotNull(e2);
    assertEquals("test", e2.getMessage());
    final UnsupportedEncodingException uee = new UnsupportedEncodingException();
    final RecordStorageException e3 = new RecordStorageException(uee);
    assertNotNull(e3);
    assertTrue(e3.getCause() instanceof UnsupportedEncodingException);
    final RecordStorageException e4 = new RecordStorageException(uee, "test");
    assertNotNull(e4);
    assertEquals("test", e4.getMessage());
    assertTrue(e4.getCause() instanceof UnsupportedEncodingException);
  }

  /**
   * Test RecordDao.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testRecordDao() throws Exception {

    try {
      new RecordDao(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("parameter record is null", e.getMessage());
    }

    try {
      final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
      new RecordDao(record);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("parameter record has not Id set", e.getMessage());
    }

    final Record record = TestRecordStorage.createRecord("0815", "source", "title", new Date(), "text", 2);
    final RecordDao dao = new RecordDao(record);
    assertNotNull(dao);
    final Record deserializedRecord = dao.toRecord();
    assertNotNull(deserializedRecord);
    assertEquals(record.getId(), deserializedRecord.getId());
    assertEquals(record.getMetadata().hasAttributes(), deserializedRecord.getMetadata().hasAttributes());
    assertEquals(record.getMetadata().size(), deserializedRecord.getMetadata().size());
  }

  /**
   * Test RecordIterator.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testRecordIterator() throws Exception {
    try {
      new RecordIterator(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("parameter daoIterator is null", e.getMessage());
    }

    final Record record1 = TestRecordStorage.createRecord("0815", "source", "title", new Date(), "text", 2);
    final Record record2 = TestRecordStorage.createRecord("12345", "source", "title", new Date(), "text", 2);
    final Record record3 = TestRecordStorage.createRecord("666", "source", "title", new Date(), "text", 2);
    final ArrayList<RecordDao> list = new ArrayList<RecordDao>();
    list.add(new RecordDao(record1));
    list.add(new RecordDao(record2));
    list.add(new RecordDao(record3));
    final RecordIterator it = new RecordIterator(list.iterator());

    assertTrue(it.hasNext());
    final Record r1 = it.next();
    assertNotNull(r1);
    assertEquals(record1.getId(), r1.getId());
    assertEquals(3, list.size());
    it.remove();
    assertEquals(2, list.size());

    assertTrue(it.hasNext());
    final Record r2 = it.next();
    assertNotNull(r2);
    assertEquals(record2.getId(), r2.getId());
    assertEquals(2, list.size());
    it.remove();
    assertEquals(1, list.size());

    assertTrue(it.hasNext());
    final Record r3 = it.next();
    assertNotNull(r3);
    assertEquals(record3.getId(), r3.getId());
    assertEquals(1, list.size());
    it.remove();
    assertEquals(0, list.size());

    assertFalse(it.hasNext());
  }
}
