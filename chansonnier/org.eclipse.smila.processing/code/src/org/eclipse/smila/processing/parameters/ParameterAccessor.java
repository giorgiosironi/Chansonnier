/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.processing.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.parameters.SearchParameters.OrderMode;

/**
 * read service runtime parameters from current processing record, query record and pipelet configurations.
 * 
 * @author jschumacher
 * 
 */
public class ParameterAccessor {
  /**
   * blackboard service in processing.
   */
  private Blackboard _blackboard;

  /**
   * ID of query record (optional).
   */
  private Id _query;

  /**
   * ID of currently processed record. (optional).
   */
  private Id _record;

  /**
   * pipelet configuration (optional).
   */
  private PipeletConfiguration _config;

  /**
   * currently active orderby configuration. is cleared on changes in the above fields.
   */
  private LinkedHashMap<String, OrderMode> _orderByCache;

  /**
   * create accessor for given blackboard.
   * 
   * @param blackboard
   *          blackboard instance.
   */
  public ParameterAccessor(final Blackboard blackboard) {
    _blackboard = blackboard;
  }

  /**
   * for search pipelets/services: create accessor for given blackboard and ID effective query record for fallback.
   * 
   * @param blackboard
   *          blackboard instance.
   * @param query
   *          current query record.
   */
  public ParameterAccessor(final Blackboard blackboard, final Id query) {
    this(blackboard);
    _query = query;
  }

  /**
   * set the ID of the record to read parameters from.
   * 
   * @param currentRecord
   *          current record to proces.
   * @return "this", make it easier to use this method immediately after constructor.
   */
  public ParameterAccessor setCurrentRecord(final Id currentRecord) {
    _record = currentRecord;
    _orderByCache = null;
    return this;
  }

  /**
   * set the configuration of the current pipelet, to read fallback values for missing parameters.
   * 
   * @param config
   *          pipelet configuration.
   * @return "this", make it easier to use this method immediately after constructor
   */
  public ParameterAccessor setPipeletConfiguration(final PipeletConfiguration config) {
    _config = config;
    _orderByCache = null;
    return this;
  }

  /**
   * access to predefined parameters as supported in the search API.
   * 
   * @return textual query string. null, if none is set.
   */
  public String getQuery() {
    return getParameter(SearchParameters.QUERY, null);
  }

  /**
   * get the value of result size parameter. Default value is 10.
   * 
   * 
   * @return value of result size parameter.
   */
  public int getResultSize() {
    return getIntParameter(SearchParameters.RESULTSIZE, SearchParameters.DEFAULT_RESULTSIZE);
  }

  /**
   * get the value of result offset parameter. Default value is 0.
   * 
   * @return value of result offset parameter
   */
  public int getResultOffset() {
    return getIntParameter(SearchParameters.RESULTOFFSET, SearchParameters.DEFAULT_RESULTOFFSET);
  }

  /**
   * get the value of threshold parameter. Default value is 0.0.
   * 
   * @return value of threshold parameter
   */
  public double getThreshold() {
    return getFloatParameter(SearchParameters.THRESHOLD, SearchParameters.DEFAULT_THRESHOLD);
  }

  /**
   * get the value of language parameter. Default value is null
   * 
   * @return value of language parameter
   */
  public String getLanguage() {
    return getParameter(SearchParameters.LANGUAGE, null);
  }

  /**
   * get the value of indexName parameter. Default value is null
   * 
   * @return value of indexName parameter
   */
  public String getIndexName() {
    return getParameter(SearchParameters.INDEXNAME, null);
  }

  /**
   * Get the values (names of attributes) of resultAttributes parameter. Default value is an empty list.
   * 
   * @return values of resultAttributes parameter
   */
  public List<String> getResultAttributes() {
    List<String> values = getParameters(SearchParameters.RESULTATTRIBUTES);
    if (values == null || values.isEmpty()) {
      values = new ArrayList<String>();
      final String value = getParameter(SearchParameters.RESULTATTRIBUTES, null);
      if (value != null) {       
        values.add(value);
      } // if
    } // if
    return values;
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
   * get named value of parameter annotation, according to precedence rules.
   * 
   * @param name
   *          parameter name
   * @param defaultValue
   *          default value.
   * @return return default value if no matching parameter value can be found
   */
  public String getParameter(final String name, final String defaultValue) {
    String value = null;
    if (_record != null) {
      value = getRecordParameter(name, _record);
    }
    if (value == null && _query != null) {
      value = getRecordParameter(name, _query);
    }
    if (value == null && _config != null) {
      final Object property = _config.getPropertyFirstValue(name);
      if (property != null) {
        value = property.toString();
      }
    }
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * get named value of parameter annotation, according to precedence rules.
   * 
   * @param name
   *          parameter name.
   * @return parameter value.
   * @throws MissingParameterException
   *           if no matching parameter value can be found.
   */
  public String getRequiredParameter(final String name) throws MissingParameterException {
    final String value = getParameter(name, null);
    if (value == null) {
      throw new MissingParameterException("no single value for required parameter " + name);
    }
    return value;
  }

  /**
   * get anonymous values of first (according to precendence rules) subannotation.
   * 
   * @param name
   *          parameter name
   * @return anonymous values of named subannotation, or empty list if no matching parameter value can be found
   */
  @SuppressWarnings("unchecked")
  public List<String> getParameters(final String name) {
    List<String> values = null;
    if (_record != null) {
      values = getRecordParameters(name, _record);
    }
    if (values == null && _query != null) {
      values = getRecordParameters(name, _query);
    }
    if (values == null && _config != null) {
      final String[] propValues = _config.getPropertyStringValues(name);
      if (propValues != null && propValues.length > 0) {
        values = Arrays.asList(propValues);
      }
    }
    if (values == null) {
      return Collections.EMPTY_LIST;
    }
    return values;
  }

  /**
   * get anonymous values of first (according to precendence rules) subannotation.
   * 
   * @param name
   *          parameter name
   * @return anonymous values of named subannotation,
   * @throws MissingParameterException
   *           if no matching parameter value can be found.
   */
  public List<String> getRequiredParameters(final String name) throws MissingParameterException {
    final List<String> values = getParameters(name);
    if (values == null || values.isEmpty()) {
      throw new MissingParameterException("no list value for required parameter " + name);
    }
    return values;
  }

  /**
   * type-aware convenience method: convert result of getParameter() to Integer. Throws NumberFormatException, if
   * parameter value is not in valid integer format.
   * 
   * @param name
   *          parameter name
   * @param defaultValue
   *          default value
   * @return integer value
   */
  public Integer getIntParameter(final String name, final Integer defaultValue) {
    final String value = getParameter(name, null);
    if (value == null) {
      return defaultValue;
    }
    return Integer.valueOf(value);
  }

  /**
   * type-aware convenience method: convert result of getParameter() to Double. Throws NumberFormatException if
   * parameter value is not in valid double format.
   * 
   * @param name
   *          parameter name
   * @param defaultValue
   *          default value
   * @return double value
   */
  public Double getFloatParameter(final String name, final Double defaultValue) {
    final String value = getParameter(name, null);
    if (value == null) {
      return defaultValue;
    }
    return Double.valueOf(value);
  }

  /**
   * type-aware convenience method: convert result of getParameter() to Boolean.
   * 
   * @param name
   *          parameter name
   * @param defaultValue
   *          default value
   * @return double value
   */
  public Boolean getBooleanParameter(final String name, final Boolean defaultValue) {
    final String value = getParameter(name, null);
    if (value == null) {
      return defaultValue;
    }
    return Boolean.valueOf(value);
  }

  /**
   * type-aware convenience method: convert result of getRequiredParameter() to Integer. Throws NumberFormatException,
   * if parameter value is not in valid integer format.
   * 
   * @param name
   *          parameter name
   * @return integer value *
   * @throws MissingParameterException
   *           if no matching parameter value can be found.
   */
  public Integer getRequiredIntParameter(final String name) throws MissingParameterException {
    final String value = getRequiredParameter(name);
    return Integer.valueOf(value);
  }

  /**
   * type-aware convenience method: convert result of getRequiredParameter() to Double. Throws NumberFormatException, if
   * parameter value is not in valid double format.
   * 
   * @param name
   *          parameter name
   * @return floating point value *
   * @throws MissingParameterException
   *           if no matching parameter value can be found.
   */
  public Double getRequiredFloatParameter(final String name) throws MissingParameterException {
    final String value = getRequiredParameter(name);
    return Double.valueOf(value);
  }

  /**
   * type-aware convenience method: convert result of getRequiredParameter() to Boolean.
   * 
   * @param name
   *          parameter name
   * @return boolean value *
   * @throws MissingParameterException
   *           if no matching parameter value can be found.
   */
  public Boolean getRequiredBooleanParameter(final String name) throws MissingParameterException {
    final String value = getRequiredParameter(name);
    return Boolean.valueOf(value);
  }

  /**
   * access to first subannotation, according to precedence rules (no fallback to PipeletConfig possible, of course).
   * 
   * @param name
   *          annotation name.
   * @return annotation object.
   */
  public Annotation getAnnotation(final String name) {
    final List<Annotation> annotations = getAnnotations(name);
    if (annotations == null || annotations.isEmpty()) {
      return null;
    } else {
      return annotations.get(0);
    }
  }

  /**
   * access to a multiple sub annotations, according to precedence rules (no fallback to PipeletConfig possible, of
   * course). The annotations are not merged from multiple sources
   * 
   * @param name
   *          annotation name.
   * @return list of annotations.
   */
  @SuppressWarnings("unchecked")
  public List<Annotation> getAnnotations(final String name) {
    List<Annotation> annotations = null;
    if (_record != null) {
      annotations = getRecordSubAnnotations(name, _record);
    }
    if (annotations == null && _query != null) {
      annotations = getRecordSubAnnotations(name, _query);
    }

    if (annotations == null) {
      return Collections.EMPTY_LIST;
    }
    return annotations;
  }

  /**
   * read orderby annotations and setup the cache for simpler access later.
   */
  private void createOrderByCache() {
    _orderByCache = new LinkedHashMap<String, OrderMode>();
    final List<Annotation> annotations = getAnnotations(SearchParameters.ORDERBY);
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

  /**
   * get a parameter value from the record with the given Id.
   * 
   * @param name
   *          parameter name.
   * @param record
   *          record Id.
   * @return parameter value.
   */
  private String getRecordParameter(final String name, final Id record) {
    try {
      final Annotation annotation = _blackboard.getAnnotation(record, null, SearchParameters.PARAMETERS);
      if (annotation != null) {
        final String value = annotation.getNamedValue(name);
        if (value != null) {
          return value;
        }
      }
    } catch (Exception ex) {
      // ignore.
      ex = null;
    }
    return null;
  }

  /**
   * get list parameter value from the record with the given Id.
   * 
   * @param name
   *          parameter name.
   * @param record
   *          record Id.
   * @return parameter values.
   */
  private List<String> getRecordParameters(final String name, final Id record) {
    try {
      final Annotation annotation = _blackboard.getAnnotation(record, null, SearchParameters.PARAMETERS);
      if (annotation != null && annotation.hasAnnotation(name)) {
        Collection<String> values = annotation.getAnnotation(name).getAnonValues();
        if (!values.isEmpty()) {
          if (!(values instanceof List)) {
            values = new ArrayList<String>(values);
          }
          return (List<String>) values;
        }
      }
    } catch (Exception ex) {
      ex = null;
      // ignore.
    }
    return null;
  }

  /**
   * get a list of parameter subannotations from the record with the given Id.
   * 
   * @param name
   *          parameter name.
   * @param record
   *          record Id.
   * @return parameter values.
   */
  private List<Annotation> getRecordSubAnnotations(final String name, final Id record) {
    try {
      final Annotation annotation = _blackboard.getAnnotation(record, null, SearchParameters.PARAMETERS);
      if (annotation != null && annotation.hasAnnotation(name)) {
        Collection<Annotation> subAnnotations = annotation.getAnnotations(name);
        if (!subAnnotations.isEmpty()) {
          if (!(subAnnotations instanceof List)) {
            subAnnotations = new ArrayList<Annotation>(subAnnotations);
          }
          return (List<Annotation>) subAnnotations;
        }
      }
    } catch (Exception ex) {
      ex = null;
      // ignore.
    }
    return null;
  }
}
