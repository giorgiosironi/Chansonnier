/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.lucene.test;

import java.io.File;

import org.apache.lucene.index.IndexReader;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.lucene.LuceneIndexService;
import org.eclipse.smila.lucene.LuceneSearchService;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The Class TestLuceneIndexService.
 */
public class TestLuceneSearchService extends DeclarativeServiceTestCase {

  /** The Constant DOCS_QUANTITY. */
  private static final int DOCS_QUANTITY = 5;

  /**
   * Name of the test index.
   */
  private static final String TEST_INDEX_NAME = "test_index";

  /** the Blackboard. */
  private Blackboard _blackboard;

  /** the LuceneIndexService. */
  private LuceneIndexService _luceneIndexService;

  /** the LuceneSearchService. */
  private LuceneSearchService _luceneSearchService;

  /**
   * the test index directory.
   */
  private File _testIndexDir;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    final BlackboardFactory factory = getService(BlackboardFactory.class);
    assertNotNull("no BlackboardFactory service found.", factory);
    _blackboard = factory.createPersistingBlackboard();
    assertNotNull("no Blackboard created", _blackboard);
    _luceneIndexService = getService(LuceneIndexService.class);
    assertNotNull(_luceneIndexService);
    _testIndexDir = WorkspaceHelper.createWorkingDir(LuceneIndexService.BUNDLE_NAME, TEST_INDEX_NAME);
    assertNotNull(_testIndexDir);
    if (!_luceneIndexService.isIndexExists(TEST_INDEX_NAME)) {
      _luceneIndexService.createIndex(TEST_INDEX_NAME);
    }
    _luceneSearchService = getService(LuceneSearchService.class);
    assertNotNull(_luceneSearchService);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _blackboard = null;
    _luceneIndexService = null;
    _luceneSearchService = null;
  }

  /**
   * Test add and search documents.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testAddAndSearch() throws Exception {
    addDocument();
    searchDocuments();
  }

  /**
   * Add documents.
   * 
   * @throws Exception
   *           if any error occurs
   */
  private void addDocument() throws Exception {
    try {
      IndexReader indexReader = IndexReader.open(_testIndexDir);
      assertEquals(0, indexReader.numDocs());

      final Record[] records = AllTests.createRecords(5);
      assertEquals(DOCS_QUANTITY, records.length);
      final Id[] recordIds = new Id[DOCS_QUANTITY];
      for (int i = 0; i < records.length; i++) {
        recordIds[i] = records[i].getId();
        _blackboard.setRecord(records[i]);
        AllTests.setAnnotations(_blackboard, records[i].getId(), LuceneIndexService.ExecutionMode.ADD,
          TEST_INDEX_NAME);
      }
      final Id[] result = _luceneIndexService.process(_blackboard, recordIds);
      assertEquals(DOCS_QUANTITY, result.length);

      indexReader = indexReader.reopen();
      assertEquals(DOCS_QUANTITY, indexReader.numDocs());
      indexReader.close();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error(e);
      }
      throw e;
    }
  }

  /**
   * Search documents.
   * 
   * @throws Exception
   *           if any error occurs
   */
  private void searchDocuments() throws Exception {
    try {
      final Id query = IdFactory.DEFAULT_INSTANCE.createId("testDataSource", "queryid");
      _blackboard.create(query);
      final Annotation annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
      _blackboard.addAnnotation(query, null, SearchParameters.PARAMETERS, annotation);
      annotation.setNamedValue(SearchParameters.QUERY, "test");
      annotation.setNamedValue(SearchParameters.INDEXNAME, TEST_INDEX_NAME);
      annotation.setNamedValue(LuceneSearchService.SEARCH_ANNOTATION_QUERY_ATTRIBUTE, "Content");
      final SearchMessage message = new SearchMessage(query);

      // execute search
      final SearchMessage resultMessage = _luceneSearchService.process(_blackboard, message);
      assertNotNull(resultMessage);
      final Id[] results = resultMessage.getRecords();
      assertNotNull(results);
      assertEquals(DOCS_QUANTITY, results.length);
      for (Id id : results) {
        final Annotation highlightAnnotation =
          _blackboard.getAnnotation(id, new Path().add(AllTests.ATTACHMENT_TEXT, PathStep.ATTRIBUTE_ANNOTATION),
            SearchAnnotations.HIGHLIGHT);
        assertNotNull(highlightAnnotation);
        final String highlightedText = highlightAnnotation.getNamedValue(SearchAnnotations.HIGHLIGHT_TEXT);
        assertNotNull(highlightedText);
        assertTrue(highlightedText.indexOf("<b>test</b>") >= 0);
      }
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error(e);
      }
      e.printStackTrace();
      throw e;
    } catch (final Throwable e) {
      if (_log.isErrorEnabled()) {
        _log.error(e);
      }
      e.printStackTrace();
    }
  }

}
