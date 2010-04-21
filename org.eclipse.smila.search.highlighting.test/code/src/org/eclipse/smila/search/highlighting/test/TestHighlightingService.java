/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT-Solutions GmbH) - initial API and implementation (based on aperture test by DS)
 **********************************************************************************************************************/
package org.eclipse.smila.search.highlighting.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.search.highlighting.HighlightingService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestConnectivity.
 */
// TODO: the test cases could be improved by checking the effects of the parameters results.
public class TestHighlightingService extends DeclarativeServiceTestCase {

  /**
   * The default score.
   */
  public static final int SCORE = 250;

  /**
   * Constant for the markup overhead length. The bold start and end tags, 7 characters.
   */
  public static final int MARKUP_LENGTH = 7;

  /**
   * Constant for the preceding character overhead length. 3 characters.
   */
  public static final int PRECEDING_CHARS_LENGTH = 3;

  /** the BlackboardService. */
  private Blackboard _blackboard;

  /** the HighlightingService. */
  private HighlightingService _hls;

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
    _hls =
      (HighlightingService) getService(SearchProcessingService.class,
        "(smila.processing.service.name=HighlightingService)");
    assertNotNull(_hls);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _blackboard = null;
    _hls = null;
  }

  /**
   * Test the HighlightingService with the Sentence HighlighingTransformer.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testSentence() throws Exception {
    // prepare query
    final String highlightingTransformer = "Sentence";
    final String queryId = "queryId";
    final String highlightAttribute = "Content";
    final HashMap<String, String> properties = new HashMap<String, String>();
    properties.put("MaxSucceedingCharacters", "30");
    properties.put("TextHandling", "ReturnSnipplet");
    properties.put("MarkupSuffix", "</b>");
    properties.put("SortAlgorithm", "Occurrence");
    properties.put("MarkupPrefix", "<b>");
    properties.put("MaxHLElements", "999");
    properties.put("MaxLength", "300");
    properties.put("SucceedingCharacters", "...");
    final Record query = createQuery(queryId, highlightingTransformer, highlightAttribute, properties);

    // prepare result
    final String resultId = "resultId";
    final String originalText =
      "This is a simple test document. It contains no special format just some test data.";
    final ArrayList<HighlightInfo> highlightInfos = new ArrayList<HighlightInfo>();
    final int startFirst = 17;
    final int endFirst = 21;
    final int startSecond = 72;
    final int endSecond = 76;
    highlightInfos.add(new HighlightInfo(startFirst, endFirst, SCORE));
    highlightInfos.add(new HighlightInfo(startSecond, endSecond, SCORE));
    final Record result = createResult(resultId, highlightAttribute, originalText, highlightInfos);

    // execute highlighting
    final String highlightedText = executeHighlightingService(query, result, highlightAttribute);
    assertNotSame(originalText, highlightedText);

    // check if test is highlighted
    final int firstPos = highlightedText.indexOf("<b>");
    assertEquals(startFirst, firstPos);
    final int secondPos = highlightedText.indexOf("<b>", firstPos + 1);
    assertEquals(startSecond + MARKUP_LENGTH, secondPos);
  }

  /**
   * Test the HighlightingService with the MaxTextLength HighlighingTransformer.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testMaxTextLength() throws Exception {
    final int maxLength = 70;
    // prepare query
    final String highlightingTransformer = "MaxTextLength";
    final String queryId = "queryId";
    final String highlightAttribute = "Content";
    final HashMap<String, String> properties = new HashMap<String, String>();
    properties.put("MarkupSuffix", "</b>");
    properties.put("MarkupPrefix", "<b>");
    properties.put("MaxLength", Integer.toString(maxLength));
    final Record query = createQuery(queryId, highlightingTransformer, highlightAttribute, properties);

    // prepare result
    final String resultId = "resultId";
    final String originalText =
      "This is a simple test document. It contains no special format just some test data.";
    final ArrayList<HighlightInfo> highlightInfos = new ArrayList<HighlightInfo>();
    final int startFirst = 17;
    final int endFirst = 21;
    final int startSecond = 72;
    final int endSecond = 76;
    highlightInfos.add(new HighlightInfo(startFirst, endFirst, SCORE));
    highlightInfos.add(new HighlightInfo(startSecond, endSecond, SCORE));
    final Record result = createResult(resultId, highlightAttribute, originalText, highlightInfos);

    // execute highlighting
    final String highlightedText = executeHighlightingService(query, result, highlightAttribute);
    assertNotSame(originalText, highlightedText);
    assertEquals(maxLength + MARKUP_LENGTH, highlightedText.length());

    // check if test is highlighted
    final int firstPos = highlightedText.indexOf("<b>");
    assertEquals(startFirst, firstPos);
    // no second pos because of maxLength
    final int secondPos = highlightedText.indexOf("<b>", firstPos + 1);
    assertEquals(-1, secondPos);
  }

  /**
   * Test the HighlightingService with the ComplexHLResultAggregation HighlighingTransformer.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testComplexHLResultAggregation() throws Exception {
    // prepare query
    final String highlightingTransformer = "ComplexHLResultAggregation";
    final String queryId = "queryId";
    final String highlightAttribute = "Content";
    final HashMap<String, String> properties = new HashMap<String, String>();
    properties.put("MaxSucceedingCharacters", "30");
    properties.put("TextHandling", "ReturnSnipplet");
    properties.put("MarkupSuffix", "</b>");
    properties.put("SortAlgorithm", "Occurrence");
    properties.put("MarkupPrefix", "<b>");
    properties.put("MaxHLElements", "999");
    properties.put("MaxLength", "300");
    properties.put("SucceedingCharacters", "...");
    properties.put("MaxPrecedingCharacters", "30");
    properties.put("PrecedingCharacters", "...");
    properties.put("HLElementFilter", "true");
    properties.put("SortAlgorithm", "Score");
    properties.put("TextHandling", "ReturnFullText");
    final Record query = createQuery(queryId, highlightingTransformer, highlightAttribute, properties);

    // prepare result
    final String resultId = "resultId";
    final String originalText =
      "This is a simple test document. It contains no special format just some test data.";
    final ArrayList<HighlightInfo> highlightInfos = new ArrayList<HighlightInfo>();
    final int startFirst = 17;
    final int endFirst = 21;
    final int startSecond = 72;
    final int endSecond = 76;
    highlightInfos.add(new HighlightInfo(startFirst, endFirst, SCORE));
    highlightInfos.add(new HighlightInfo(startSecond, endSecond, SCORE));
    final Record result = createResult(resultId, highlightAttribute, originalText, highlightInfos);

    // execute highlighting
    final String highlightedText = executeHighlightingService(query, result, highlightAttribute);
    assertNotSame(originalText, highlightedText);

    // check if test is highlighted
    final int firstPos = highlightedText.indexOf("<b>");
    assertEquals(startFirst + PRECEDING_CHARS_LENGTH, firstPos);
    final int secondPos = highlightedText.indexOf("<b>", firstPos + 1);
    assertTrue(secondPos <= -1);
  }

  /**
   * Execute the executeHighlightingService.
   * 
   * @param query
   *          the query Record
   * @param result
   *          the Result Record
   * @param highlightAttribute
   *          the name of the attribute to highlight
   * @return the highlighted text
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws ProcessingException
   *           if any error occurs
   */
  private String executeHighlightingService(final Record query, final Record result, final String highlightAttribute)
    throws BlackboardAccessException, ProcessingException {
    _blackboard.setRecord(query);
    _blackboard.setRecord(result);

    final Id[] recordIds = new Id[] { result.getId() };
    final SearchMessage searchMessage = new SearchMessage(query.getId(), recordIds);

    final SearchMessage searchResult = _hls.process(_blackboard, searchMessage);
    assertNotNull(searchResult);
    assertNotNull(searchResult.getQuery());
    assertEquals(query.getId(), searchResult.getQuery());
    assertNotNull(searchResult.getRecords());
    assertEquals(recordIds.length, searchResult.getRecords().length);

    final Annotation highlight =
      result.getMetadata().getAttribute(highlightAttribute).getAnnotation(SearchAnnotations.HIGHLIGHT);
    assertNotNull(highlight);
    final String highlightedText = highlight.getNamedValue(SearchAnnotations.HIGHLIGHT_TEXT);
    assertNotNull(highlightedText);
    return highlightedText;
  }

  /**
   * Creates a query record.
   * 
   * @param idValue
   *          the id
   * @param transformerName
   *          the name of the highlihtingTransformer
   * @param highlightAttribute
   *          the name of the attribute to highlight
   * @param properties
   *          highlihtingTransformer parameters
   * @return a Record
   */
  private Record createQuery(final String idValue, final String transformerName, final String highlightAttribute,
    final Map<String, String> properties) {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("testDataSource", idValue);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);

    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);

    final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    metadata.setAttribute(highlightAttribute, attribute);

    final Annotation highlight = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
    attribute.setAnnotation(SearchAnnotations.HIGHLIGHT, highlight);

    final Annotation highlightingTransformer = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
    highlightingTransformer.setNamedValue(HighlightingService.HIGHLIGHTING_PARAMETER_NAME, transformerName);
    highlight.setAnnotation(HighlightingService.HIGHLIGHTING_TRANSFORMER, highlightingTransformer);

    final Iterator<String> it = properties.keySet().iterator();
    while (it.hasNext()) {
      final String name = it.next();
      final Annotation annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
      annotation.setNamedValue(HighlightingService.HIGHLIGHTING_PARAMETER_VALUE, properties.get(name));
      highlightingTransformer.addAnnotation(name, annotation);
    }

    return record;
  }

  /**
   * Creates a result record.
   * 
   * @param idValue
   *          the id
   * @param highlightAttribute
   *          the name of the attribute to highlight
   * @param text
   *          the original text
   * @param highlightInfos
   *          highlight positions
   * @return a Record
   */
  private Record createResult(final String idValue, final String highlightAttribute, final String text,
    final List<HighlightInfo> highlightInfos) {
    final Id id = IdFactory.DEFAULT_INSTANCE.createId("testDataSource", idValue);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);

    final MObject metadata = RecordFactory.DEFAULT_INSTANCE.createMetadataObject();
    record.setMetadata(metadata);

    final Attribute attribute = RecordFactory.DEFAULT_INSTANCE.createAttribute();
    metadata.setAttribute(highlightAttribute, attribute);

    final Annotation highlight = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
    highlight.setNamedValue(SearchAnnotations.HIGHLIGHT_TEXT, text);
    attribute.setAnnotation(SearchAnnotations.HIGHLIGHT, highlight);

    for (HighlightInfo highlightInfo : highlightInfos) {
      final Annotation annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
      annotation.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_START, Integer.toString(highlightInfo._start));
      annotation.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_END, Integer.toString(highlightInfo._end));
      annotation.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_QUALITY, Integer.toString(highlightInfo._score));
      highlight.addAnnotation(SearchAnnotations.HIGHLIGHT_POSITIONS, annotation);
    }

    return record;
  }

  /**
   * Helper class to model highlight positions.
   */
  private class HighlightInfo {

    /**
     * The start position.
     */
    private int _start;

    /**
     * The end position.
     */
    private int _end;

    /**
     * The score.
     */
    private int _score;

    /**
     * Constructor.
     * 
     * @param start
     *          the start position
     * @param end
     *          the end position
     * @param score
     *          the score
     */
    protected HighlightInfo(final int start, final int end, final int score) {
      _start = start;
      _end = end;
      _score = score;
    }

  }
}
