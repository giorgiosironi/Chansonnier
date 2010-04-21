/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.search.api.test;

import java.util.UUID;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.record.dom.RecordBuilder;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.search.api.SearchResult;
import org.eclipse.smila.search.api.SearchService;
import org.eclipse.smila.search.api.helper.Facets;
import org.eclipse.smila.search.api.helper.HighlightInfo;
import org.eclipse.smila.search.api.helper.QueryBuilder;
import org.eclipse.smila.search.api.helper.QueryRecordAccessor;
import org.eclipse.smila.search.api.helper.ResultAccessor;
import org.eclipse.smila.search.api.helper.ResultRecordAccessor;
import org.eclipse.smila.search.api.helper.Terms;
import org.eclipse.smila.search.api.pipelet.TestSearchPipelet;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test search processing with pipelets. The pipeline contains a simple pipelet manipulating the query, then a search
 * pipelet that creates a search result by splitting the query object and finally the same simple pipelet than before
 * manipulates the result records.
 *
 * @author jschumacher
 *
 */
public class TestSearchPipeletPipeline extends DeclarativeServiceTestCase {
  /**
   * name of pipeline to test.
   */
  public static final String PIPELINE_NAME = "SearchPipeletPipeline";

  /**
   * name of attribute set by test pipelet in query record.
   */
  public static final String ATTRIBUTE_QUERY = TestSearchPipelet.QUERY.getName(0);

  /**
   * name of attribute set by test pipelet in result records.
   */
  public static final String ATTRIBUTE_TITLE = TestSearchPipelet.TITLE.getName(0);

  /**
   * search service.
   */
  private SearchService _search;

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.bpel.test.AWorkflowProcessorTest#getPipelineName()
   */
  protected String getPipelineName() {
    return PIPELINE_NAME;
  }

  /**
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _search = getService(SearchService.class);
    assertNotNull(_search);
  }

  /**
   *
   * @throws Exception
   *           test fails
   */
  public void testWithoutBuilder() throws Exception {
    final String queryString = "test basic";
    final Record query = RecordFactory.DEFAULT_INSTANCE.createRecord();
    final Annotation parameters = query.getFactory().createAnnotation();
    parameters.setNamedValue(SearchParameters.QUERY, queryString);
    query.getMetadata().setAnnotation(SearchParameters.PARAMETERS, parameters);
    final SearchResult result = _search.search(getPipelineName(), query);
    assertNotNull(result);
    assertNotNull(result.getQuery());
    assertNotNull(result.getQuery().getId());
    assertEquals(getPipelineName(), result.getQuery().getId().getSource());
    assertNotNull(UUID.fromString(result.getQuery().getId().getKey().getKey()));
    assertNotNull(result.getRecords());
    assertEquals(SearchParameters.DEFAULT_RESULTSIZE, result.getRecords().length);
  }

  /**
   * test with default size and offset.
   *
   * @throws Exception
   *           test fails
   */
  public void testDefault() throws Exception {
    final String queryString = "test default";
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    assertEquals(getPipelineName(), query.getWorkflowName());
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertNotNull(result.getResult());
    assertEquals(getPipelineName(), result.getWorkflowName());
    assertTrue(result.hasQuery());
    assertTrue(result.hasRecords());
    checkQuery(result.getQuery(), queryString, SearchParameters.DEFAULT_THRESHOLD);
    checkResult(result, queryString, SearchParameters.DEFAULT_RESULTSIZE, SearchParameters.DEFAULT_RESULTOFFSET,
      SearchParameters.DEFAULT_THRESHOLD);
  }

  /**
   * test with non-default size and offset.
   *
   * @throws Exception
   *           test fails
   */
  public void testNonDefault() throws Exception {
    final String queryString = "test non default";
    final int mySize = 2;
    final int myOffset = 4;
    final double myThreshold = 0.5;
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setResultSize(mySize).setResultOffset(myOffset).setThreshold(myThreshold).setIndexName("my-index");
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertNotNull(result.getResult());
    assertEquals(getPipelineName(), result.getWorkflowName());
    assertTrue(result.hasQuery());
    assertTrue(result.hasRecords());
    assertEquals("my-index", result.getQuery().getIndexName());
    checkQuery(result.getQuery(), queryString, myThreshold);
    checkResult(result, queryString, mySize, myOffset, myThreshold);
  }

  /**
   * test query record terms annotation.
   *
   * @throws Exception
   *           test fails
   */
  public void testTerms() throws Exception {
    final String queryString = "test some terms";
    final int mySize = 3;
    final int myOffset = 3;
    final double myThreshold = 0.1;
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setResultSize(mySize).setResultOffset(myOffset).setThreshold(myThreshold);
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertTrue(result.hasQuery());
    assertTrue(result.hasRecords());
    checkTerms(result.getQuery(), queryString);
  }

  /**
   * test query record terms annotation.
   *
   * @throws Exception
   *           test fails
   */
  public void testFacets() throws Exception {
    final String queryString = "test a lot of facets";
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertTrue(result.hasQuery());
    assertTrue(result.hasRecords());
    checkFacets(result.getQuery(), queryString);

    final QueryBuilder newQuery = result.newQueryBuilder();
    newQuery.addFacetFilter(result.getQuery().getFacets(ATTRIBUTE_QUERY), 0);
    assertEquals(queryString + " + test", newQuery.getParameters().getParameter(SearchParameters.QUERY));
  }

  /**
   * test query record terms annotation.
   *
   * @throws Exception
   *           test fails
   */
  public void testHighlight() throws Exception {
    final String queryString = "test pretty highlighting";
    final int mySize = 3;
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setResultSize(mySize);
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertTrue(result.hasQuery());
    assertTrue(result.hasRecords());
    assertEquals(mySize, result.recordsSize());
    double expectedQuality = 1.0;
    final double expectedQualityDecrement = 0.01;
    for (int i = 0; i < mySize; i++) {
      checkHighlight(result.getResultRecord(i), queryString, expectedQuality);
      expectedQuality -= expectedQualityDecrement;
    }
    checkFacets(result.getQuery(), queryString);
  }

  /**
   * test with attachment.
   *
   * @throws Exception
   *           test fails
   */
  public void testAttachment() throws Exception {
    final String queryString = "test attachment";
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setAttachment("query", queryString.getBytes());
    final ResultAccessor result = query.executeRequest(_search);
    assertNotNull(result);
    assertTrue(result.hasQuery());
    assertTrue(result.getQuery().getRecord().hasAttachment("query"));
    assertEquals(queryString, new String(result.getQuery().getRecord().getAttachment("query")));
  }

  /**
   * test search with XML results.
   *
   * @throws Exception
   *           test fails.
   */
  public void testXMLSearch() throws Exception {
    final String queryString = "test search with XML results";
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setResultSize(SearchParameters.DEFAULT_RESULTSIZE).setResultOffset(SearchParameters.DEFAULT_RESULTOFFSET)
      .setThreshold(SearchParameters.DEFAULT_THRESHOLD);
    final Document result = query.executeRequestXml(_search);
    assertNotNull(result);
    final Element elemResult = result.getDocumentElement();
    assertNotNull(elemResult);
    assertEquals(SearchService.TAG_SEARCHRESULT, elemResult.getLocalName());
    final NodeList resultChildren = elemResult.getChildNodes();
    assertEquals(2, resultChildren.getLength());
    final Node elemQuery = resultChildren.item(0);
    assertEquals(SearchService.TAG_QUERY, elemQuery.getLocalName());
    final NodeList queryChildren = elemQuery.getChildNodes();
    assertEquals(2, queryChildren.getLength());
    final Node elemWorkflow = queryChildren.item(0);
    assertEquals(SearchService.TAG_WORKFLOWNAME, elemWorkflow.getLocalName());
    assertEquals(getPipelineName(), elemWorkflow.getTextContent());
    final Node elemQueryRecord = queryChildren.item(1);
    assertEquals(RecordBuilder.TAG_RECORD, elemQueryRecord.getLocalName());
    final Node elemRecordList = resultChildren.item(1);
    assertEquals(RecordBuilder.TAG_RECORDLIST, elemRecordList.getLocalName());
    assertEquals(SearchParameters.DEFAULT_RESULTSIZE, elemRecordList.getChildNodes().getLength());
  }

  /**
   * test search with XML string results.
   *
   * @throws Exception
   *           test fails.
   */
  public void testXMLStringSearch() throws Exception {
    final String queryString = "test search with XML results";
    final QueryBuilder query = new QueryBuilder(getPipelineName()).setQuery(queryString);
    query.setResultSize(SearchParameters.DEFAULT_RESULTSIZE).setResultOffset(SearchParameters.DEFAULT_RESULTOFFSET)
      .setThreshold(SearchParameters.DEFAULT_THRESHOLD);
    final String result = query.executeRequestXmlString(_search);
    assertNotNull(result);
  }

  /**
   * check attribute values, terms and facets.
   *
   * @param query
   *          query accessor
   * @param queryString
   *          input query string
   * @param threshold
   *          threshold (used to compute totalHits
   */
  private void checkQuery(final QueryRecordAccessor query, final String queryString, final double threshold) {
    final Integer expectedTotalNoOfHits = Integer.valueOf((int) ((1 - threshold) * 100));
    assertEquals(expectedTotalNoOfHits, query.getTotalHits());
    assertTrue(query.hasLiterals(ATTRIBUTE_QUERY));
    assertEquals(queryString, query.getLiteral(ATTRIBUTE_QUERY).getStringValue());
    assertNotNull(query.getSearchRuntime());
    assertEquals(Integer.valueOf(TestSearchPipelet.INDEX_SIZE), query.getIndexSize());
    checkTerms(query, queryString);
    checkFacets(query, queryString);
  }

  /**
   * check terms.
   *
   * @param query
   *          query accessor
   * @param queryString
   *          input query string.
   */
  private void checkTerms(final QueryRecordAccessor query, final String queryString) {
    final String[] tokens = queryString.split(" ");
    final Terms terms = query.getTerms(ATTRIBUTE_QUERY);
    assertEquals(ATTRIBUTE_QUERY, terms.getAttributeName());
    assertEquals(tokens.length, terms.length());
    assertNotNull(terms.getSource());
    assertEquals(tokens.length, terms.getSource().size());
    int position = 0;
    for (int i = 0; i < tokens.length; i++) {
      assertEquals(tokens[i], terms.getToken(i));
      assertNull(terms.getIntProperty(i, SearchAnnotations.TERM_TOKEN));
      assertNull(terms.getFloatProperty(i, SearchAnnotations.TERM_TOKEN));
      assertEquals(tokens[i].toUpperCase(), terms.getConcept(i));
      assertEquals(Integer.valueOf(i), terms.getStartWordPos(i));
      assertEquals(Integer.valueOf(i), terms.getEndWordPos(i));
      assertEquals(Integer.valueOf(position), terms.getStartCharPos(i));
      position += tokens[i].length();
      assertEquals(Integer.valueOf(position), terms.getEndCharPos(i));
      position++;
      assertEquals("test", terms.getMethod(i));
      assertEquals("word", terms.getPartOfSpeech(i));
      assertEquals(ATTRIBUTE_TITLE, terms.getTargetAttributeName(i));
      assertEquals(1.0, terms.getQuality(i));
    }
    assertNull(terms.getProperty(tokens.length, SearchAnnotations.TERM_TOKEN));
    assertNull(terms.getIntProperty(tokens.length, SearchAnnotations.TERM_TOKEN));
    assertNull(terms.getFloatProperty(tokens.length, SearchAnnotations.TERM_TOKEN));
    assertNull(terms.getAnnotation(tokens.length, SearchAnnotations.FACET_FILTER));
    assertTrue(terms.getAnnotations(tokens.length, SearchAnnotations.FACET_FILTER).isEmpty());
  }

  /**
   * check facets.
   *
   * @param query
   *          query accessor
   * @param queryString
   *          input query string.
   */
  private void checkFacets(final QueryRecordAccessor query, final String queryString) {
    final String[] tokens = queryString.split(" ");
    final Facets facets = query.getFacets(ATTRIBUTE_QUERY);
    assertEquals(ATTRIBUTE_QUERY, facets.getAttributeName());
    assertEquals(tokens.length, facets.length());
    assertNotNull(facets.getSource());
    assertEquals(tokens.length, facets.getSource().size());
    for (int i = 0; i < tokens.length; i++) {
      assertEquals(tokens[i], facets.getName(i));
      assertNull(facets.getIntProperty(i, SearchAnnotations.FACET_NAME));
      assertNull(facets.getFloatProperty(i, SearchAnnotations.FACET_NAME));
      assertEquals(" + " + tokens[i], facets.getStringFilter(i));
      assertNull(facets.getObjectFilter(i));
      assertTrue(facets.getAnnotations(i, SearchAnnotations.FACET_FILTER).isEmpty());
      assertEquals(Integer.valueOf(i), facets.getCount(i));
      assertFalse(facets.hasSubFacets(i));
      assertNotNull(facets.getSubFacets(i));
      assertTrue(facets.getSubFacets(i).isEmpty());
    }
    assertNull(facets.getProperty(tokens.length, SearchAnnotations.FACET_NAME));
    assertNull(facets.getIntProperty(tokens.length, SearchAnnotations.FACET_NAME));
    assertNull(facets.getFloatProperty(tokens.length, SearchAnnotations.FACET_NAME));
    assertNull(facets.getAnnotation(tokens.length, SearchAnnotations.FACET_FILTER));
    assertTrue(facets.getAnnotations(tokens.length, SearchAnnotations.FACET_FILTER).isEmpty());
  }

  /**
   * simple result check.
   *
   * @param result
   *          search result
   * @param query
   *          query string
   * @param resultSize
   *          expected size
   * @param resultOffset
   *          expected offset
   * @param threshold
   *          expected threshold
   * @throws Exception
   *           check fails
   */
  private void checkResult(final ResultAccessor result, final String query, final int resultSize,
    final int resultOffset, final double threshold) throws Exception {
    final double expectedQualityDecrement = 0.01;
    final Id queryId = result.getQuery().getRecord().getId();
    assertEquals(resultSize, result.recordsSize());
    for (int i = 0; i < resultSize; i++) {
      final ResultRecordAccessor record = result.getResultRecord(i);
      final int position = resultOffset + i;
      final String key = TestSearchPipelet.PREFIX + position;
      assertNotNull(record);
      final Double expectedRelevance = Double.valueOf(1 - (position * expectedQualityDecrement));
      assertEquals(expectedRelevance, record.getRelevance());
      final Id id = record.getRecord().getId();
      assertEquals(queryId, id.createCompoundId());
      assertEquals(key, id.getFragmentNames().get(0));
      assertTrue(record.hasLiterals(ATTRIBUTE_TITLE));
      final String value = record.getLiteral(ATTRIBUTE_TITLE).getStringValue();
      assertEquals(key, value);
      checkHighlight(record, query, expectedRelevance);
    }
  }

  /**
   * check highlight info.
   *
   * @param record
   *          result record accessor.
   * @param query
   *          input query string.
   * @param expectedRelevance
   *          expected relevance (used as quality)
   */
  private void checkHighlight(final ResultRecordAccessor record, final String query, final Double expectedRelevance) {
    final HighlightInfo highlight = record.getHighlightInfo();
    assertNull(highlight.getAttributeName());
    assertEquals(query, highlight.getText());
    assertFalse(highlight.isHighlighted());
    final String[] tokens = query.split(" ");
    final int positionLength = highlight.positionLength();
    assertEquals(tokens.length, positionLength);
    int position = 0;
    for (int i = 0; i < positionLength; i++) {
      assertEquals(Integer.valueOf(i), highlight.getQueryGroup(i));
      assertEquals("test", highlight.getMethod(i));
      assertEquals(Double.valueOf(expectedRelevance), highlight.getQuality(i));
      assertEquals(Integer.valueOf(position), highlight.getStartPos(i));
      position += tokens[i].length();
      assertEquals(Integer.valueOf(position), highlight.getEndPos(i));
      position++;
    }
  }
}
