/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.search.api.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.processing.parameters.SearchParameters.OrderMode;

/**
 * accessor for query records in search results to read out additional result properties that are attached to the
 * effective query object: term lists for query attributes, facets (aka categorization, grouping, questions), total
 * number of search results, etc.
 *
 * @author jschumacher
 *
 */
public class QueryRecordAccessor extends RecordAccessor {

  /**
   * service runtime parameters of effective query.
   */
  private ParameterAnnotation _parameters;

  /**
   * currently active orderby configuration. is cleared on changes in the above fields.
   */
  private LinkedHashMap<String, OrderMode> _orderByCache;

  /**
   * create instance for record.
   *
   * @param record
   *          a query record.
   */
  public QueryRecordAccessor(final Record record) {
    super(record);
    _parameters = new ParameterAnnotation(record);
  }

  /**
   *
   * @return textual query string.
   */
  public String getQuery() {
    return _parameters.getParameter(SearchParameters.QUERY);
  }

  /**
   * @return value of result size parameter used in query.
   */
  public Integer getResultSize() {
    return _parameters.getIntParameter(SearchParameters.RESULTSIZE);
  }

  /**
   * @return value of result offset parameter used in query.
   */
  public Integer getResultOffset() {
    return _parameters.getIntParameter(SearchParameters.RESULTOFFSET);
  }

  /**
   * @return value of threshod parameter used in query.
   */
  public Double getThreshold() {
    return _parameters.getFloatParameter(SearchParameters.THRESHOLD);
  }

  /**
   * @return value of language parameter used in query.
   */
  public String getLanguage() {
    return _parameters.getParameter(SearchParameters.LANGUAGE);
  }

  /**
   * @return value of index used in query
   */
  public String getIndexName() {
    return _parameters.getParameter(SearchParameters.INDEXNAME);
  }

  /**
   * get total number of hits for the search result. This is the number of all objects in the index that are at least as
   * relevant to the query as the threshold specifies and which match all filter attached to relevant query attributes.
   *
   * @return total number of hits.
   */
  public Integer getTotalHits() {
    return getResultAnnotationIntValue(SearchAnnotations.TOTAL_HITS);
  }

  /**
   * get total number of objects evaluated in this search. This is usually the size of the used index, hence the name.
   * Searches spanning multiple indexes could aggregate the various index sizes.
   *
   * @return nomber of evaluated objects.
   */
  public Integer getIndexSize() {
    return getResultAnnotationIntValue(SearchAnnotations.INDEX_SIZE);
  }

  /**
   * get runtime of search workflow in milliseconds.
   *
   * @return runtime of workflow.
   */
  public Integer getSearchRuntime() {
    return getResultAnnotationIntValue(SearchAnnotations.RUNTIME);
  }

  /**
   * gets a single valued parameter.
   *
   * @param name
   *          parameter name
   * @return current value or null.
   */
  public String getParameter(final String name) {
    return _parameters.getParameter(name);
  }

  /**
   * gets a multi values parameter.
   *
   * @param name
   *          parameter name
   * @return current values of parameter or an empty list.
   */
  public List<String> getParameters(final String name) {
    return _parameters.getParameters(name);
  }

  /**
   * get facets list for an attribute.
   *
   * @param attributeName
   *          name of attribute.
   * @return list of facets.
   */
  public Facets getFacets(final String attributeName) {
    return new Facets(attributeName, getAnnotations(attributeName, SearchAnnotations.FACETS));
  }

  /**
   * get iterator on names of attributes that have an order-by parameter set. Order of iteration is order of precedence
   * in ordering.
   *
   * @return names of attributes to be ordered.
   */
  public Iterator<String> getOrderByAttributeNames() {
    if (_orderByCache == null) {
      createOrderByCache();
    }
    return _orderByCache.keySet().iterator();
  }

  /**
   * get order mode for an attribute.
   *
   * @param attributeName
   *          attribute name
   * @return mode for attribute, if specified, or null else.
   */
  public OrderMode getOrderMode(final String attributeName) {
    if (_orderByCache == null) {
      createOrderByCache();
    }
    return _orderByCache.get(attributeName);
  }

  /**
   * return ranking annotation for current record or given attribute.
   *
   * @param attributeName
   *          name of attribute, or null for top-level ranking annotation.
   * @return ranking annotation, null if none exists.
   */
  public Annotation getRankingAnnotation(final String attributeName) {
    if (attributeName == null) {
      return getAnnotation(SearchAnnotations.RANKING);
    }
    return getAnnotation(attributeName, SearchAnnotations.RANKING);
  }

  /**
   * get boost factor for attribute.
   *
   * @param attributeName
   *          attribute name
   * @return boost factor if set, else null.
   */
  public Double getBoostFactor(final String attributeName) {
    final Annotation ranking = getRankingAnnotation(attributeName);
    if (ranking == null) {
      return null;
    }
    final String boost = ranking.getNamedValue(SearchAnnotations.RANKING_BOOST);
    if (boost != null) {
      try {
        return Double.valueOf(boost);
      } catch (Exception ex) {
        // shit happens.
        ex = null;
      }
    }
    return null;
  }

  /**
   * get ranking name for attribute or record.
   *
   * @param attributeName
   *          attribute name, null for record
   * @return record if set, else null.
   */
  public String getRankingName(final String attributeName) {
    final Annotation ranking = getRankingAnnotation(attributeName);
    if (ranking == null) {
      return null;
    }
    return ranking.getNamedValue(SearchAnnotations.RANKING_NAME);
  }

  /**
   * read orderby annotations and setup the cache for simpler access later.
   */
  private void createOrderByCache() {
    _orderByCache = new LinkedHashMap<String, OrderMode>();
    final Collection<Annotation> annotations = _parameters.getSubAnnotations(SearchParameters.ORDERBY);
    if (annotations != null) {
      for (final Annotation annotation : annotations) {
        final String attributeName = annotation.getNamedValue(SearchParameters.ORDERBY_ATTRIBUTE);
        final String orderModeValue = annotation.getNamedValue(SearchParameters.ORDERBY_MODE);
        final OrderMode orderMode = OrderMode.valueOf(orderModeValue);
        if (attributeName != null && orderMode != null) {
          _orderByCache.put(attributeName, orderMode);
        }
      }
    }
  }

}
