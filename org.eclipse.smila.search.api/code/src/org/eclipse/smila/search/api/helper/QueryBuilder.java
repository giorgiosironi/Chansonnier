/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.search.api.helper;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.LiteralFormatHelper;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.processing.parameters.SearchAnnotations.FilterMode;
import org.eclipse.smila.processing.parameters.SearchParameters.OrderMode;
import org.eclipse.smila.search.api.SearchResult;
import org.eclipse.smila.search.api.SearchService;
import org.w3c.dom.Document;

/**
 * Builder for Query objects to send to a search service. Most methods just return the QueryBuilder they were called on
 * to make it easy to chain several methods call in a single code line.
 *
 * @author jschumacher
 *
 */
public class QueryBuilder {

  /**
   * IP address of host we are running on. Used as source of query record Ids.
   */
  private static String s_localhostIP;

  static {
    try {
      s_localhostIP = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception ex) {
      // ignore
      ex = null;
    }
  }

  /**
   * name of pipeline to use for search.
   */
  private String _workflowName;

  /**
   * query object.
   */
  private Record _query;

  /**
   * accessor for the service runtime parameter annotation.
   */
  private ParameterAnnotation _parameters;

  /**
   * helper to format date/time objects as filter values.
   */
  private LiteralFormatHelper _literalFormat;

  /**
   * init request for given pipeline.
   *
   * @param workflowName
   *          pipeline name.
   */
  public QueryBuilder(final String workflowName) {
    this(workflowName, RecordFactory.DEFAULT_INSTANCE.createRecord());
  }

  /**
   * init request for given pipeline, use non-default RecordFactory.
   *
   * @param workflowName
   *          pipeline name
   * @param factory
   *          record factory to use.
   */
  public QueryBuilder(final String workflowName, final RecordFactory factory) {
    this(workflowName, factory.createRecord());
  }

  /**
   * init request for given record.
   *
   * @param workflowName
   *          pipeline name
   * @param query
   *          query record to use.
   */
  public QueryBuilder(final String workflowName, final Record query) {
    _workflowName = workflowName;
    _query = query;
    _parameters = new ParameterAnnotation(query);
  }

  /**
   * set the ID of the query record. If the ID is not set by the client, the builder will create an Id just before the
   * request is executed, using the local IP address as source and a random {@link UUID} as key.
   *
   * @param id
   *          Id
   * @return "this"
   */
  public QueryBuilder setId(final Id id) {
    _query.setId(id);
    return this;
  }

  /**
   * set the textual query string for the query. The syntax of this string depends on the requirements of the retrieval
   * service used in the pipeline, so every string is accepted here and no syntax checking is done.
   *
   * @param queryString
   *          textual query string.
   * @return "this"
   */
  public QueryBuilder setQuery(final String queryString) {
    _parameters.setParameter(SearchParameters.QUERY, queryString);
    return this;
  }

  /**
   * set the maximum search result size for the query.
   *
   * @param size
   *          maximum result size.
   * @return "this"
   */
  public QueryBuilder setResultSize(final int size) {
    _parameters.setIntParameter(SearchParameters.RESULTSIZE, size);
    return this;
  }

  /**
   * set the search result offset for the query, i.e. the number of most relevant result objects to skip in the result
   * list. Use this to implement paging on the result set.
   *
   * @param offset
   *          result offset
   * @return "this"
   */
  public QueryBuilder setResultOffset(final int offset) {
    _parameters.setIntParameter(SearchParameters.RESULTOFFSET, offset);
    return this;
  }

  /**
   * set the threshold for the relevance value of search results, i.e. only results having a relevance of at least the
   * threshold value, should be returned to the client. Relevances are usually number betwenn 0.0 (completely irrelevant
   * to the query) and 1.0 (perfect match), so the threshold value should be from the same range. However, as the search
   * API does not know about details of the used search engine, no checking is done and all double values are accepted.
   *
   * @param threshold
   *          relevance threshold value.
   * @return "this"
   */
  public QueryBuilder setThreshold(final double threshold) {
    _parameters.setFloatParameter(SearchParameters.THRESHOLD, threshold);
    return this;
  }

  /**
   * set the language parameter for the query. This is used by services/pipelets that have a language specific
   * functionality (e.g. stemming, spellchecking, recognition of currency values ...) to configure the language of the
   * query string. Usually the possible values are the standard locale codes like "en", "de".
   *
   * @param language
   *          a language code.
   * @return "this"
   */
  public QueryBuilder setLanguage(final String language) {
    _parameters.setParameter(SearchParameters.LANGUAGE, language);
    return this;
  }

  /**
   * set the index name for the query. Some search engine integrations may be capable of managing multiple seperated
   * indexes and use this parameter to select the index to search in.
   *
   * @param indexName
   *          index name
   * @return "this"
   */
  public QueryBuilder setIndexName(final String indexName) {
    _parameters.setParameter(SearchParameters.INDEXNAME, indexName);
    return this;
  }

  /**
   * add a literal value to an attribute. Valid value types are
   * <ul>
   * <li> {@link String}
   * <li> {@link Number}
   * <li> {@link Boolean}
   * <li> {@link Date}
   * </ul>
   *
   * @param attributeName
   *          name of attribute
   * @param value
   *          value
   * @return "this"
   * @throws InvalidTypeException
   *           object type cannot be used as a literal value.
   */
  public QueryBuilder addLiteral(final String attributeName, final Object value) throws InvalidTypeException {
    final Literal literal = _query.getFactory().createLiteral();
    literal.setValue(value);
    return addLiteral(attributeName, literal);
  }

  /**
   * add a ready built literal to an attribute.
   *
   * @param attributeName
   *          attribute name.
   * @param literal
   *          a literal to add
   * @return "this"
   */
  public QueryBuilder addLiteral(final String attributeName, final Literal literal) {
    final Attribute attribute = getAttribute(attributeName);
    attribute.addLiteral(literal);
    return this;
  }

  /**
   * add an enumeration filter to an attribute. An enumeration filter describes a list of allowed or forbidden values
   * for this attribute. The search result must contain only objects that contain matching values in this attribute. The
   * {@link FilterMode} specifies what "matching" means:
   * <ul>
   * <li>ANY: valid results must have at least one of the listed attribute values and can have additional values.
   * <li>ALL: valid results must have all of the listed attribute values and can have additional values.
   * <li>ONLY: valid results must have only values listed in the filter, but do not need to have all (or even one)
   * value.
   * <li>NONE: valid results must have none of the listed attribute values (no value at all is fine, too).
   * </ul>
   *
   * @param attributeName
   *          name of attribute.
   * @param mode
   *          filter mode
   * @param filterValues
   *          filter values.
   * @return "this"
   * @throws InvalidTypeException
   *           at least one filter values is of an invalid class
   */
  public QueryBuilder addEnumFilter(final String attributeName, final FilterMode mode,
    final Iterable<? extends Object> filterValues) throws InvalidTypeException {
    final Annotation filter = _query.getFactory().createAnnotation();
    filter.setNamedValue(SearchAnnotations.FILTER_TYPE, SearchAnnotations.FilterType.ENUMERATION.toString());
    filter.setNamedValue(SearchAnnotations.FILTER_MODE, mode.toString());
    for (final Object object : filterValues) {
      final String value = getStringValue(object);
      filter.addAnonValue(value);
    }
    addFilterAnnotation(attributeName, filter);
    return this;
  }

  /**
   * add a range filter to an attribute. A range filter describes a range of allowed or forbidden values by specifying a
   * lower and/or an upper bound. This usually makes sense only for numeric or chronologic (date/time) attributes, but
   * this is not enforced by the builder. Either bound can be left unspecified (null) to describe a filter range with
   * only a minimum or maximum value.
   *
   * @param attributeName
   *          name of attribute.
   * @param mode
   *          filter mode
   * @param lowerBound
   *          lower bound of filter range. can be null
   * @param upperBound
   *          upper bound of filter range. can be null.
   * @return "this"
   * @throws InvalidTypeException
   *           at least one filter values is of an invalid class
   */
  public QueryBuilder addRangeFilter(final String attributeName, final FilterMode mode, final Object lowerBound,
    final Object upperBound) throws InvalidTypeException {
    final Annotation filter = _query.getFactory().createAnnotation();
    filter.setNamedValue(SearchAnnotations.FILTER_TYPE, SearchAnnotations.FilterType.RANGE.toString());
    filter.setNamedValue(SearchAnnotations.FILTER_MODE, mode.toString());
    if (lowerBound != null) {
      final String minValue = getStringValue(lowerBound);
      filter.setNamedValue(SearchAnnotations.FILTER_MIN, minValue);
    }
    if (upperBound != null) {
      final String maxValue = getStringValue(upperBound);
      filter.setNamedValue(SearchAnnotations.FILTER_MAX, maxValue);
    }
    addFilterAnnotation(attributeName, filter);
    return this;
  }

  /**
   * copy filter from a facet to the attribute of this facet. If the facet contains an object filter, this one is added
   * to the appropriate attribute. If it does not contain an object filter, but a text filter, this string is appended
   * immediately to the current query string. It is required that the pipeline returns a text filter valid for immediate
   * appending to the input query string.
   *
   * @param facetList
   *          list of facets for a single attribute
   * @param index
   *          position of used facet.
   * @return "this"
   */
  public QueryBuilder addFacetFilter(final Facets facetList, final int index) {
    final Annotation filter = facetList.getObjectFilter(index);
    if (filter != null) {
      final String attributeName = facetList.getAttributeName();
      addFilterAnnotation(attributeName, filter);
    } else {
      final String textFilter = facetList.getStringFilter(index);
      if (textFilter != null) {
        String query = _parameters.getParameter(SearchParameters.QUERY);
        if (query == null) {
          query = textFilter;
        } else {
          query += textFilter;
        }
        setQuery(query);
      }
    }
    return this;
  }

  /**
   * add an orderby specification.
   *
   * @param attribute
   *          an attribute to order by
   * @param mode
   *          the order direction (ascending or descending)
   * @return "this"
   */
  public QueryBuilder addOrderBy(final String attribute, final OrderMode mode) {
    final Annotation paramAnno = _parameters.getAnnotation();
    final Annotation orderBy = _query.getFactory().createAnnotation();
    orderBy.setNamedValue(SearchParameters.ORDERBY_ATTRIBUTE, attribute);
    orderBy.setNamedValue(SearchParameters.ORDERBY_MODE, mode.toString());
    paramAnno.addAnnotation(SearchParameters.ORDERBY, orderBy);
    return this;
  }

  /**
   * ensure and return ranking annotation for current record or given attribute.
   *
   * @param attributeName
   *          name of attribute, or null for top-level ranking annotation.
   * @return ranking annotation
   */
  public Annotation getRankingAnnotation(final String attributeName) {
    Annotatable object = _query.getMetadata();
    if (attributeName != null) {
      object = getAttribute(attributeName);
    }
    Annotation ranking = object.getAnnotation(SearchAnnotations.RANKING);
    if (ranking == null) {
      ranking = _query.getFactory().createAnnotation();
      object.setAnnotation(SearchAnnotations.RANKING, ranking);
    }
    return ranking;
  }

  /**
   * set boost factor for attribute.
   *
   * @param attributeName
   *          name of attribute
   * @param boost
   *          boost factor
   * @return "this"
   */
  public QueryBuilder setBoostFactor(final String attributeName, final double boost) {
    final Annotation ranking = getRankingAnnotation(attributeName);
    ranking.setNamedValue(SearchAnnotations.RANKING_BOOST, Double.toString(boost));
    return this;
  }

  /**
   * set ranking name for record or attribute.
   *
   * @param attributeName
   *          name of attribute, or null for top-level ranking annotation.
   * @param rankingName
   *          name of ranking.
   * @return "this"
   */
  public QueryBuilder setRankingName(final String attributeName, final String rankingName) {
    final Annotation ranking = getRankingAnnotation(attributeName);
    ranking.setNamedValue(SearchAnnotations.RANKING_NAME, rankingName);
    return this;
  }

  /**
   * sets a named value in the service runtime parameter annotation.
   *
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   * @return "this"
   */
  public QueryBuilder setParameter(final String name, final String value) {
    _parameters.setParameter(name, value);
    return this;
  }

  /**
   * adds an anonymous value to a service runtime parameter annotation.
   *
   * @param name
   *          parameter name
   * @param value
   *          parameter value
   * @return "this"
   */
  public QueryBuilder addParameter(final String name, final String value) {
    _parameters.addParameter(name, value);
    return this;
  }

  /**
   * add attachment to query. Keep in mind that attachments are kept in memory in search processig, so you should care
   * about not adding large attachments.
   *
   * @param name
   *          attachment name
   * @param attachment
   *          attachment content
   * @return "this"
   */
  public QueryBuilder setAttachment(final String name, final byte[] attachment) {
    _query.setAttachment(name, attachment);
    return this;
  }

  /**
   * execute query on given search service and wrap result in high level result helper.
   *
   * @param searchService
   *          search service instance.
   * @return search result.
   * @throws ProcessingException
   *           any error while processing the search
   */
  public ResultAccessor executeRequest(final SearchService searchService) throws ProcessingException {
    ensureQueryId();
    final SearchResult result = searchService.search(_workflowName, _query);
    return new ResultAccessor(result);
  }

  /**
   * execute query on given search service and return search result as XML DOM document.
   * 
   * @param searchService
   *          search service instance.
   * @return search result, or XML description of error that occurred in searcuh
   * @throws ParserConfigurationException
   *           error creating the XML result
   */
  public Document executeRequestXml(final SearchService searchService) throws ParserConfigurationException {
    ensureQueryId();
    return searchService.searchAsXml(_workflowName, _query);
  }

  /**
   * execute query on given search service and return search result as XML string.
   * 
   * @param searchService
   *          search service instance.
   * @return search result, or XML description of error that occurred in searcuh
   * @throws ParserConfigurationException
   *           error creating the XML result
   */
  public String executeRequestXmlString(final SearchService searchService) throws ParserConfigurationException {
    ensureQueryId();
    return searchService.searchAsXmlString(_workflowName, _query);
  }

  /**
   * access underlying query record for advanced manipulation.
   *
   * @return query record.
   */
  public Record getQuery() {
    return _query;
  }

  /**
   * the name of the search pipeline to use.
   *
   * @return pipeline name.
   */
  public String getWorkflowName() {
    return _workflowName;
  }

  /**
   * access service runtime parameters of query object.
   *
   * @return parameter annotation accessor.
   */
  public ParameterAnnotation getParameters() {
    return _parameters;
  }

  /**
   * add a named value to a attribute (sub) annotation.
   *
   * @param attributeName
   *          attribute name
   * @param pathElements
   *          path of annotation names
   * @param valueName
   *          name of named value
   * @param value
   *          value
   * @return "this"
   * @throws InvalidTypeException
   *           should not happen
   */
  public QueryBuilder addAnnotationNamedValue(final String attributeName, final String[] pathElements,
    final String valueName, final String value) throws InvalidTypeException {
    Annotatable annotatable = getAttribute(attributeName);
    for (final String path : pathElements) {
      annotatable = ensureAnnotation(annotatable, path);
    }
    ((Annotation) annotatable).setNamedValue(valueName, value);

    return this;
  }

  // helper methods.

  /**
   * ensure that an annotation exists.
   *
   * @param annotatable
   *          the object to annotate
   * @param name
   *          annotation name
   * @return the annotation.
   */
  private Annotation ensureAnnotation(final Annotatable annotatable, final String name) {
    if (annotatable.hasAnnotation(name)) {
      return annotatable.getAnnotation(name);
    } else {
      final Annotation annotation = _query.getFactory().createAnnotation();
      annotatable.addAnnotation(name, annotation);
      return annotation;
    }
  }

  /**
   * get an existing attribute or create a new one.
   *
   * @param attributeName
   *          name of attribute
   * @return attribute.
   */
  private Attribute getAttribute(final String attributeName) {
    Attribute attribute = _query.getMetadata().getAttribute(attributeName);
    if (attribute == null) {
      attribute = _query.getFactory().createAttribute();
      _query.getMetadata().setAttribute(attributeName, attribute);
    }
    return attribute;
  }

  /**
   * add a filter annotation to an attribute.
   *
   * @param attributeName
   *          name of attribute
   * @param filter
   *          filter annotation.
   */
  private void addFilterAnnotation(final String attributeName, final Annotation filter) {
    final Attribute attribute = getAttribute(attributeName);
    attribute.addAnnotation(SearchAnnotations.FACET_FILTER, filter);
  }

  /**
   * convert value object to a string, using the literal format helper for date/time objects and toString for different
   * kinds of objects. Eventually, it should also ensure that the object has a supported class, but currently it does
   * not...
   *
   * @param object
   *          value object.
   * @return string representation
   * @throws InvalidTypeException
   *           object is not of a supported type.
   */
  private String getStringValue(final Object object) throws InvalidTypeException {
    if (object instanceof Date) {
      return _literalFormat.formatDateTime((java.util.Date) object);
    }
    return object.toString();

  }

  /**
   * create a ID for the query object, if the client did not set one. Use the local IP adress as source and create a
   * random UUID for the key. This makes it possible to use the ID for query logging.
   */
  private void ensureQueryId() {
    if (_query.getId() == null) {
      String source = s_localhostIP;
      if (source == null) {
        source = _workflowName;
      }
      final String key = UUID.randomUUID().toString();
      final Id id = IdFactory.DEFAULT_INSTANCE.createId(source, key);
      setId(id);
    }
  }

}
