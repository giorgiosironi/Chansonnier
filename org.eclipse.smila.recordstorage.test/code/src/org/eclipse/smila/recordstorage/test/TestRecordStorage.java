/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.recordstorage.RecordStorage;
import org.eclipse.smila.recordstorage.RecordStorageException;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestRecordStore.
 */
public class TestRecordStorage extends DeclarativeServiceTestCase {

  /**
   * Constant for attribute Title.
   */
  public static final String ATT_TITLE = "Title";

  /**
   * Constant for attribute Date.
   */
  public static final String ATT_DATE = "Date";

  /**
   * Constant for attribute Date.
   */
  public static final String ATT_SIZE = "Size";

  /**
   * Constant for attachment Text.
   */
  public static final String ATTACHMENT_TEXT = "Content";

  /**
   * Constant for annotation globalAnnotation.
   */
  public static final String ANON_GLOBAL = "globalAnnotation";

  /**
   * Constant for annotation value value_1.
   */
  public static final String ANON_VALUE_1 = "value_1";

  /**
   * Constant for annotation titleAnnotation.
   */
  public static final String ANON_TITLE = "titleAnnotation";

  /**
   * Constant for annotation value value_2.
   */
  public static final String ANON_VALUE_2 = "value_2";

  /** the RecordStore. */
  private RecordStorage _recordStorage;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _recordStorage = getService(RecordStorage.class);
    assertNotNull(_recordStorage);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _recordStorage = null;
  }

  /**
   * Test add and delete document.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testCreateLoadDelete() throws Exception {
    final String idValue = "12345";
    final String source = "testDataSource";
    final String title = "test title";
    final String text = "test text";
    final int size = 666;
    final Date date = new Date(System.currentTimeMillis());
    final Record record = createRecord(idValue, source, title, date, text, size);

    assertFalse(_recordStorage.existsRecord(record.getId()));
    _recordStorage.storeRecord(record);
    assertTrue(_recordStorage.existsRecord(record.getId()));

    Record loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNotNull(loadedRecord);
    assertEquals(record.getId(), loadedRecord.getId());
    assertEquals(record.getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue(), loadedRecord
      .getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue());
    assertEquals(record.getMetadata().getAttribute(ATT_DATE).getLiteral().getDateValue(), loadedRecord
      .getMetadata().getAttribute(ATT_DATE).getLiteral().getDateValue());
    assertEquals(record.getMetadata().getAttribute(ATT_SIZE).getLiteral().getIntValue(), loadedRecord.getMetadata()
      .getAttribute(ATT_SIZE).getLiteral().getIntValue());
    assertTrue(record.hasAttachment(ATTACHMENT_TEXT));

    _recordStorage.removeRecord(record.getId());
    loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNull(loadedRecord);
  }

  /**
   * Test modification of a Record.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testModify() throws Exception {
    final String idValue = "0815";
    final String source = "testDataSource";
    final String title = "test title";
    final String text = "test text";
    final int size = 666;
    final Date date = new Date(System.currentTimeMillis());
    final Record record = createRecord(idValue, source, title, date, text, size);

    assertFalse(_recordStorage.existsRecord(record.getId()));
    _recordStorage.storeRecord(record);
    assertTrue(_recordStorage.existsRecord(record.getId()));

    Record loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNotNull(loadedRecord);
    assertEquals(record.getId(), loadedRecord.getId());
    assertEquals(record.getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue(), loadedRecord
      .getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue());
    assertEquals(record.getMetadata().getAttribute(ATT_DATE).getLiteral().getDateValue(), loadedRecord
      .getMetadata().getAttribute(ATT_DATE).getLiteral().getDateValue());
    assertEquals(record.getMetadata().getAttribute(ATT_SIZE).getLiteral().getIntValue(), loadedRecord.getMetadata()
      .getAttribute(ATT_SIZE).getLiteral().getIntValue());
    assertTrue(loadedRecord.hasAttachment(ATTACHMENT_TEXT));

    // modify record, change size, remove date
    record.getMetadata().removeAttribute(ATT_DATE);
    MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, record.getMetadata(),
      ATT_SIZE, 999);
    _recordStorage.storeRecord(record);

    loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNotNull(loadedRecord);
    assertEquals(record.getId(), loadedRecord.getId());
    assertEquals(record.getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue(), loadedRecord
      .getMetadata().getAttribute(ATT_TITLE).getLiteral().getStringValue());
    assertEquals(record.getMetadata().getAttribute(ATT_SIZE).getLiteral().getIntValue(), loadedRecord.getMetadata()
      .getAttribute(ATT_SIZE).getLiteral().getIntValue());
    assertFalse(loadedRecord.getMetadata().hasAttribute(ATT_DATE));
    assertTrue(loadedRecord.hasAttachment(ATTACHMENT_TEXT));

    _recordStorage.removeRecord(record.getId());
    loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNull(loadedRecord);
  }

  /**
   * Test record annotations.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testAnnotations() throws Exception {
    final String idValue = "0815";
    final String source = "testDataSource";
    final String title = "test title";
    final String text = "test text";
    final int size = 666;
    final Date date = new Date(System.currentTimeMillis());
    final Record record = createRecord(idValue, source, title, date, text, size);

    // add annotations
    final Annotation globalAnnotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
    globalAnnotation.addAnonValue(ANON_VALUE_1);
    record.getMetadata().addAnnotation(ANON_GLOBAL, globalAnnotation);

    final Annotation titleAnnotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
    titleAnnotation.addAnonValue(ANON_VALUE_2);
    record.getMetadata().getAttribute(ATT_TITLE).addAnnotation(ANON_TITLE, titleAnnotation);

    _recordStorage.storeRecord(record);
    Record loadedRecord = _recordStorage.loadRecord(record.getId());

    // check Annotations
    assertTrue(loadedRecord.getMetadata().hasAnnotations());
    assertTrue(loadedRecord.getMetadata().hasAnnotation(ANON_GLOBAL));
    assertEquals(record.getMetadata().getAnnotation(ANON_GLOBAL).getAnonValues().iterator().next(), loadedRecord
      .getMetadata().getAnnotation(ANON_GLOBAL).getAnonValues().iterator().next());
    assertTrue(loadedRecord.getMetadata().getAttribute(ATT_TITLE).hasAnnotation(ANON_TITLE));
    assertEquals(record.getMetadata().getAttribute(ATT_TITLE).getAnnotation(ANON_TITLE).getAnonValues().iterator()
      .next(), loadedRecord.getMetadata().getAttribute(ATT_TITLE).getAnnotation(ANON_TITLE).getAnonValues()
      .iterator().next());

    _recordStorage.removeRecord(record.getId());
    loadedRecord = _recordStorage.loadRecord(record.getId());
    assertNull(loadedRecord);
  }

  /**
   * Test loadRecords().
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testLoadRecords() throws Exception {
    final String idValue = "12345";
    final String source = "testDataSource";
    final String title = "test title";
    final String text = "test text";
    final int size = 666;
    final Date date = new Date(System.currentTimeMillis());
    final Record record = createRecord(idValue, source, title, date, text, size);
    _recordStorage.storeRecord(record);

    // create 2nd record with equal attribute values
    final Record record2 = createRecord("0815", source, title, date, text, size);
    _recordStorage.storeRecord(record2);

    // create 3rd record with equal attribute values but different source
    final Record record3 = createRecord("0815", "otherSource", title, date, text, size);
    _recordStorage.storeRecord(record3);

    // load all records of the same source
    final Iterator<Record> sourceRecords = _recordStorage.loadRecords("testDataSource");
    assertNotNull(sourceRecords);
    int counter = 0;
    while (sourceRecords.hasNext()) {
      final Record sourceRecord = sourceRecords.next();
      assertNotNull(sourceRecord);
      assertEquals("testDataSource", sourceRecord.getId().getSource());
      counter++;
    }
    assertEquals(2, counter);

    _recordStorage.removeRecord(record3.getId());
    _recordStorage.removeRecord(record2.getId());
    _recordStorage.removeRecord(record.getId());
  }

  /**
   * Test exception handling.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testExceptions() throws Exception {
    try {
      _recordStorage.loadRecord(null);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter id is null", e.getMessage());
    }

    try {
      _recordStorage.storeRecord(null);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter record is null", e.getMessage());
    }

    try {
      _recordStorage.removeRecord(null);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter id is null", e.getMessage());
    }

    try {
      _recordStorage.existsRecord(null);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter id is null", e.getMessage());
    }

    try {
      _recordStorage.loadRecords(null);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter source is null", e.getMessage());
    }

    try {
      _recordStorage.loadRecords("");
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("parameter source is an empty String", e.getMessage());
    }

    try {
      final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
      _recordStorage.storeRecord(record);
      fail("expected RecordStorageException");
    } catch (RecordStorageException e) {
      assertEquals("error storing record id: null", e.getMessage());
    }
  }

  /**
   * Utility method to create Record objects used in the tests.
   * 
   * @param idValue
   *          the id value
   * @param source
   *          the data source id
   * @param title
   *          the title
   * @param date
   *          the date
   * @param text
   *          the text
   * @param size
   *          the size
   * @return a Record
   * @throws InvalidTypeException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           if any error occurs
   */
  public static Record createRecord(String idValue, String source, String title, Date date, String text, int size)
    throws InvalidTypeException, UnsupportedEncodingException {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, "testKeyName", idValue);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);
    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);
    if (title != null) {
      MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, metadata, ATT_TITLE,
        title);
    }
    if (date != null) {
      MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, metadata, ATT_DATE, date);
    }
    if (size >= 0) {
      MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, metadata, ATT_SIZE, size);
    }
    record.setAttachment(ATTACHMENT_TEXT, text.getBytes());
    return record;
  }
}
