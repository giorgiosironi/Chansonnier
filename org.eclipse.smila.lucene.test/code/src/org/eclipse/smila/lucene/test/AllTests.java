/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.lucene.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.tools.MObjectHelper;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.lucene.LuceneIndexService;

/**
 * Test suite for org.eclipse.smila.lucene bundle.
 */
public final class AllTests {

  /**
   * Constant for attribute Title.
   */
  public static final String ATT_TITLE = "Title";

  /**
   * Constant for attribute Date.
   */
  public static final String ATT_DATE = "Date";

  /**
   * Constant for attachment Text.
   */
  public static final String ATTACHMENT_TEXT = "Content";

  /**
   * Private constructor.
   */
  private AllTests() {

  }

  /**
   * Creates test suite.
   * 
   * @return Test suite.
   */
  public static Test suite() {

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.lucene");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestLoadMappings.class);
    suite.addTestSuite(TestLuceneIndexService.class);
    suite.addTestSuite(TestLuceneSearchService.class);    
    // $JUnit-END$
    return suite;
  }

  /**
   * Creates a record.
   * 
   * @param idValue
   *          the value of the Id
   * @param title
   *          the title
   * @param date
   *          the date
   * @param text
   *          the text
   * @return a Record object
   * @throws InvalidTypeException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           on encoding/decoding problems
   */
  public static Record createRecord(String idValue, String title, Date date, String text)
    throws InvalidTypeException, UnsupportedEncodingException {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("testDataSource", idValue);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);
    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);
    MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, metadata,
      AllTests.ATT_TITLE, title);
    MObjectHelper.addSimpleLiteralAttribute(RecordFactory.DEFAULT_INSTANCE, metadata,
      AllTests.ATT_DATE, date);
    record.setAttachment(AllTests.ATTACHMENT_TEXT, text.getBytes());
    return record;
  }

  /**
   * Creates a list of records.
   * 
   * @param count
   *          the number of records to create
   * @return a Record[]
   * @throws InvalidTypeException
   *           if any error occurs
   * @throws UnsupportedEncodingException
   *           on encoding/decoding problems
   */
  public static Record[] createRecords(int count) throws InvalidTypeException, UnsupportedEncodingException {
    final String id = "testId";
    final String title = "test title";
    final Date date = new Date(System.currentTimeMillis());
    final String text = "This is a test document. It contains some sentences. But not too much.";

    final Record[] records = new Record[count];
    for (int i = 0; i < count; i++) {
      records[i] = createRecord(id + i, title + i, date, text);
    }
    return records;
  }

  /**
   * Sets the execution mode for a record.
   * 
   * @param blackboard
   *          the BlackBoardService
   * @param id
   *          the Id
   * @param execMode
   *          the executionMode
   * @param indexName
   *          the name of the index
   * 
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  public static void setAnnotations(final Blackboard blackboard, final Id id,
    final LuceneIndexService.ExecutionMode execMode, final String indexName) throws BlackboardAccessException {
    final Annotation anno = blackboard.createAnnotation(id);
    anno.setNamedValue(LuceneIndexService.EXECUTION_MODE, execMode.name());
    anno.setNamedValue(LuceneIndexService.INDEX_NAME, indexName);
    blackboard.setAnnotation(id, null, LuceneIndexService.class.getName(), anno);
  }
}
