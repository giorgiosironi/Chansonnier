/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.processing.parameters;

/**
 * Constants for names and values of record and attribute annotations commonly used in search pipelines.
 * 
 * @author jschumacher
 * 
 */
public final class SearchAnnotations {
  /**
   * name of "filter" attribute and facet annotations for defining hard search criteria on top of fuzzy searches.
   */
  public static final String FILTER = "filter";

  /**
   * name of "type" property of "filter" annotations. For predefiend values see {@link FilterType}.
   */
  public static final String FILTER_TYPE = "type";

  /**
   * name of "mode" property of "filter" annotations. For predefined values set {@link FilterMode}.
   */
  public static final String FILTER_MODE = "mode";

  /**
   * name of "min" property of range "filter" annotations: lower bound value.
   */
  public static final String FILTER_MIN = "min";

  /**
   * name of "max" property of range "filter" annotations: upper bound value.
   */
  public static final String FILTER_MAX = "max";

  /**
   * name of "ranking" record or attribute annotation for specifying ranking criteria.
   */
  public static final String RANKING = "ranking";

  /**
   * name of "name" property of "ranking" annotations: Use a named predefined ranking criterium.
   */
  public static final String RANKING_NAME = "name";

  /**
   * name of "boost" property of "ranking" annotations: Specifies a boost factor.
   */
  public static final String RANKING_BOOST = "boost";

  /**
   * name of "result" annotation of effective query.
   */
  public static final String RESULT = "result";

  /**
   * name of "totalHits" property of "result" annotation of query.
   */
  public static final String TOTAL_HITS = "totalHits";

  /**
   * number of objects in index. May be set by retrieval engine in result annotation on query record.
   */
  public static final String INDEX_SIZE = "indexSize";

  /**
   * execution runtime of workflow, set by search service in result annotation on query record.
   */
  public static final String RUNTIME = "runtime";

  /**
   * name of "relevance" property of "result" annotation of results.
   */
  public static final String RELEVANCE = "relevance";

  /**
   * name of "facets" attribute annoation of effective query.
   */
  public static final String FACETS = "facets";

  /**
   * name of "name" property of facet annotations.
   */
  public static final String FACET_NAME = "name";

  /**
   * name of "count" property of facet annotations.
   */
  public static final String FACET_COUNT = "count";

  /**
   * name of "filter" annotation of facet annotations.
   */

  public static final String FACET_FILTER = "filter";

  /**
   * name of "terms" attribute annotation.
   */
  public static final String TERMS = "terms";

  /**
   * name of "concept" property of term annotations.
   */
  public static final String TERM_CONCEPT = "concept";

  /**
   * name of "token" property of term annotations.
   */
  public static final String TERM_TOKEN = "token";

  /**
   * name of "target" property of term annotations.
   */
  public static final String TERM_TARGET = "target";

  /**
   * name of "start" property of term annotations.
   */
  public static final String TERM_START = "start";

  /**
   * name of "end" property of term annotations.
   */
  public static final String TERM_END = "end";

  /**
   * name of "startWord" property of term annotations.
   */
  public static final String TERM_STARTWORD = "startWord";

  /**
   * name of "endWord" property of term annotations.
   */
  public static final String TERM_ENDWORD = "endWord";

  /**
   * name of "pos" property of term annotations.
   */
  public static final String TERM_PARTOFSPEECH = "pos";

  /**
   * name of "method" property of term annotations.
   */
  public static final String TERM_METHOD = "method";

  /**
   * name of "quality" property of term annotations.
   */
  public static final String TERM_QUALITY = "quality";

  /**
   * name of "highlight" record and attribute annotations.
   */
  public static final String HIGHLIGHT = "highlight";

  /**
   * name of "text" property of highlight annotations.
   */
  public static final String HIGHLIGHT_TEXT = "text";

  /**
   * name of "positions" subannotations of highlight annotations.
   */
  public static final String HIGHLIGHT_POSITIONS = "positions";

  /**
   * name of "start" property of highlight position annotations.
   */
  public static final String HIGHLIGHT_POS_START = "start";

  /**
   * name of "end" property of highlight position annotations.
   */
  public static final String HIGHLIGHT_POS_END = "end";

  /**
   * name of "quality" property of highlight position annotations.
   */
  public static final String HIGHLIGHT_POS_QUALITY = "quality";

  /**
   * name of "group" property of highlight position annotations.
   */
  public static final String HIGHLIGHT_POS_GROUP = "group";

  /**
   * name of "method" property of highlight position annotations.
   */
  public static final String HIGHLIGHT_POS_METHOD = "method";

  /**
   * predefined values of "type" property of filter annotations.
   * 
   */
  public enum FilterType {
    /**
     * type name of enumeration filter.
     */
    ENUMERATION,
    /**
     * type name of range filter.
     */
    RANGE
  }

  /**
   * predefined values of "mdoe" property of filter annotations.
   */
  public enum FilterMode {
    /**
     * mode of "any" filters.
     */
    ANY,
    /**
     * mode of "all" filters.
     */
    ALL,
    /**
     * mode of "none" filters.
     */
    NONE,
    /**
     * mode of "only" filters.
     */
    ONLY
  };

  /**
   * prevent instance creation.
   */
  private SearchAnnotations() {
    // prevent instance creation
  }

}
