/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.search.api.pipelet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * Pipelet simulating a search process.
 *
 * @author jschumacher
 *
 */
public class TestSearchPipelet implements SearchPipelet, SearchProcessingService {
  /**
   * size of simulated index.
   */
  public static final int INDEX_SIZE = 1234;

  /**
   * attribute to set in query to query string and attach terms to.
   */
  public static final Path QUERY = new Path("query");

  /**
   * path to set attribute annotations on query attribute.
   */
  public static final Path QUERY_ANNOTATION = new Path().add("query", PathStep.ATTRIBUTE_ANNOTATION);

  /**
   * attribute to set in results to $PREFIX + index (0 <= index < resultSize).
   */
  public static final Path TITLE = new Path("title");

  /**
   * prefix for fragment name and attribute value.
   */
  public static final String PREFIX = "Result #";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * pipelet config.
   */
  private PipeletConfiguration _configuration;

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    _configuration = configuration;
  }

  /**
   * simulate an index retrieval. [{@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public SearchMessage process(final Blackboard blackboard, final SearchMessage message)
    throws ProcessingException {
    if (message.hasQuery()) {
      final ParameterAccessor parameters = new ParameterAccessor(blackboard, message.getQuery());
      parameters.setPipeletConfiguration(_configuration);
      final String query = parameters.getQuery();
      final int resultSize = parameters.getResultSize();
      final int resultOffset = parameters.getResultOffset();
      final double threshold = parameters.getThreshold();
      final double qualityDecrement = 0.01;
      final List<Id> results = new ArrayList<Id>(resultSize);
      for (int i = 0; i < resultSize; i++) {
        final int position = resultOffset + i;
        final String value = PREFIX + position;
        final double relevance = 1 - (position * qualityDecrement);
        if (relevance >= threshold) {
          try {
            final Id result = blackboard.split(message.getQuery(), value);
            final Literal literal = blackboard.createLiteral(result);
            literal.setStringValue(value);
            blackboard.addLiteral(result, TITLE, literal);
            setRelevance(blackboard, result, relevance);
            setRecordHighlighting(blackboard, result, query, relevance);
            results.add(result);
          } catch (final BlackboardAccessException ex) {
            _log.error(ex);
          }
        }
      }
      final int totalHits = (int) ((1 - threshold) * 100);
      try {
        setTotalHits(blackboard, message.getQuery(), totalHits);
        setTerms(blackboard, message.getQuery(), query);
        setFacets(blackboard, message.getQuery(), query);
        setIndexSize(blackboard, message.getQuery(), INDEX_SIZE);
      } catch (final BlackboardAccessException ex) {
        _log.error(ex);
      }
      message.setRecords(results);
    }
    return message;
  }

  // helper code ... to be moved to a general helper/builder class!
  /**
   * set totalHits query result annotation.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          query record Id
   * @param noOfHits
   *          number of hits
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setTotalHits(final Blackboard blackboard, final Id record, final int noOfHits)
    throws BlackboardAccessException {
    final Annotation resultAnno = ensureRecordAnnotation(blackboard, record, SearchAnnotations.RESULT);
    resultAnno.setNamedValue(SearchAnnotations.TOTAL_HITS, Integer.toString(noOfHits));
  }

  /**
   * set index size query result annotation.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          query record Id
   * @param indexSize
   *          index size
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setIndexSize(final Blackboard blackboard, final Id record, final int indexSize)
    throws BlackboardAccessException {
    final Annotation resultAnno = ensureRecordAnnotation(blackboard, record, SearchAnnotations.RESULT);
    resultAnno.setNamedValue(SearchAnnotations.INDEX_SIZE, Integer.toString(indexSize));
  }

  /**
   * set relevance result record annotation.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          result record Id
   * @param relevance
   *          relevance of hit
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setRelevance(final Blackboard blackboard, final Id record, final double relevance)
    throws BlackboardAccessException {
    final Annotation resultAnno = ensureRecordAnnotation(blackboard, record, SearchAnnotations.RESULT);
    resultAnno.setNamedValue(SearchAnnotations.RELEVANCE, Double.toString(relevance));
  }

  /**
   * create a mockup terms annotation for the query attribute.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          query record id
   * @param text
   *          text to use for mocking
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setTerms(final Blackboard blackboard, final Id record, final String text)
    throws BlackboardAccessException {
    if (text != null) {
      final Literal queryLit = blackboard.createLiteral(record);
      queryLit.setStringValue(text);
      blackboard.setLiteral(record, QUERY, queryLit);
      final String[] tokens = text.split(" ");
      int position = 0;
      for (int i = 0; i < tokens.length; i++) {
        final Annotation term = blackboard.createAnnotation(record);
        term.setNamedValue(SearchAnnotations.TERM_TOKEN, tokens[i]);
        term.setNamedValue(SearchAnnotations.TERM_CONCEPT, tokens[i].toUpperCase());
        term.setNamedValue(SearchAnnotations.TERM_STARTWORD, Integer.toString(i));
        term.setNamedValue(SearchAnnotations.TERM_ENDWORD, Integer.toString(i));
        term.setNamedValue(SearchAnnotations.TERM_START, Integer.toString(position));
        position += tokens[i].length();
        term.setNamedValue(SearchAnnotations.TERM_END, Integer.toString(position));
        position++;
        term.setNamedValue(SearchAnnotations.TERM_PARTOFSPEECH, "word");
        term.setNamedValue(SearchAnnotations.TERM_METHOD, "test");
        term.setNamedValue(SearchAnnotations.TERM_TARGET, TITLE.getName(0));
        term.setNamedValue(SearchAnnotations.TERM_QUALITY, "1.0");
        blackboard.addAnnotation(record, QUERY_ANNOTATION, SearchAnnotations.TERMS, term);
      }
    }
  }

  /**
   * create a mockup facets annotation on query record.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          query record id
   * @param text
   *          text to use for mocking
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setFacets(final Blackboard blackboard, final Id record, final String text)
    throws BlackboardAccessException {
    if (text != null) {
      final String[] tokens = text.split(" ");
      for (int i = 0; i < tokens.length; i++) {
        final Annotation facet = blackboard.createAnnotation(record);
        facet.setNamedValue(SearchAnnotations.FACET_NAME, tokens[i]);
        facet.setNamedValue(SearchAnnotations.FACET_FILTER, " + " + tokens[i]);
        facet.setNamedValue(SearchAnnotations.FACET_COUNT, Integer.toString(i));
        blackboard.addAnnotation(record, QUERY_ANNOTATION, SearchAnnotations.FACETS, facet);
      }
    }
  }

  /**
   * create a mockup result highlight annotation.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          result record id
   * @param text
   *          text to use for mocking
   * @param quality
   *          value to use for quality of highlight terms
   *
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private void setRecordHighlighting(final Blackboard blackboard, final Id record, final String text,
    final double quality) throws BlackboardAccessException {
    if (text != null) {
      final Annotation highlight = ensureRecordAnnotation(blackboard, record, SearchAnnotations.HIGHLIGHT);
      highlight.setNamedValue(SearchAnnotations.HIGHLIGHT_TEXT, text);
      final String[] tokens = text.split(" ");
      int position = 0;
      for (int i = 0; i < tokens.length; i++) {
        final Annotation highlightPos = blackboard.createAnnotation(record);
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_START, Integer.toString(position));
        position += tokens[i].length();
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_END, Integer.toString(position));
        position++;
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_QUALITY, Double.toString(quality));
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_METHOD, "test");
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_GROUP, Integer.toString(i));
        highlight.addAnnotation(SearchAnnotations.HIGHLIGHT_POSITIONS, highlightPos);
      }
    }
  }

  /**
   * create an attribute annotation on a record, or reuse an existing one.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          record id
   * @param attribute
   *          path to attribute to annotate.
   * @param name
   *          annotation name
   * @return annotation instance
   * @throws BlackboardAccessException
   *           error accessing record or creating annotation
   */
  private Annotation ensureAttributeAnnotation(final Blackboard blackboard, final Id record,
    final Path attribute, final String name) throws BlackboardAccessException {
    Annotation annotation = blackboard.getAnnotation(record, attribute, name);
    if (annotation == null) {
      annotation = blackboard.createAnnotation(record);
      blackboard.setAnnotation(record, attribute, name, annotation);
    }
    return annotation;
  }

  /**
   * create a top-level annotation on a record, or reuse an existing one.
   *
   * @param blackboard
   *          Blackboard service to use.
   * @param record
   *          record id
   * @param name
   *          annotation name
   * @return annotation instance
   * @throws BlackboardAccessException
   *           error accessing record or creating annotation
   */
  private Annotation ensureRecordAnnotation(final Blackboard blackboard, final Id record, final String name)
    throws BlackboardAccessException {
    Annotation annotation = blackboard.getAnnotation(record, null, name);
    if (annotation == null) {
      annotation = blackboard.createAnnotation(record);
      blackboard.setAnnotation(record, null, name, annotation);
    }
    return annotation;
  }

}
