/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.lucene;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DAnyFinderDataDictionary;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DField;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DFieldConfig;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.index.IndexManager;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter.DOperator;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter.DTolerance;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.search.DQuery;
import org.eclipse.smila.search.utils.searchresult.DHit;
import org.eclipse.smila.search.utils.searchresult.LuceneSearchResult;
import org.osgi.service.component.ComponentContext;

/**
 * Lucene Search Service.
 */
public class LuceneSearchService extends LuceneServie implements SearchProcessingService {

  /**
   * Constant for the SearchAnnotation QueryAttribut. The attribute to use for a default query.
   */
  public static final String SEARCH_ANNOTATION_QUERY_ATTRIBUTE = "QueryAttribute";

  /**
   * Constant for the SearchAnnotation TemplateSelectorName.
   */
  public static final String SEARCH_ANNOTATION_TEMPLATE_SELECTOR_NAME = "TemplateSelectorName";

  /**
   * Constant for the SearchAnnotation named value Operator of Annotation ranking.
   */
  public static final String RANKING_OPERATOR = "Operator";

  /**
   * Constant for the SearchAnnotation named value Tolerance of Annotation ranking.
   */
  public static final String RANKING_TOLERANCE = "Tolerance";

  /**
   * Name of the attributes mapping key.
   */
  public static final String MAPPINGS_ATTRIBUTES = "attributes";

  /**
   * Name of the attachments mapping key.
   */
  public static final String MAPPINGS_ATTACHMENTS = "attachments";

  /**
   * The default date format to use.
   */
  public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

  /**
   * The default date time format to use.
   */
  public static final String DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(LuceneSearchService.class);

  /**
   * Reverse mappings of fieldNo to attribute names.
   */
  private HashMap<String, HashMap<String, HashMap<Integer, String>>> _reverseMappings =
    new HashMap<String, HashMap<String, HashMap<Integer, String>>>();

  /**
   * Reference to the DataDictioanry.
   */
  private DAnyFinderDataDictionary _dataDictionary;

  /**
   * DS activate method.
   * 
   * @param context
   *          ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void activate(final ComponentContext context) throws Exception {

    try {
      loadMappings();
      loadReverseMappings();
      _dataDictionary = DataDictionaryController.getDataDictionary();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error initializing LuceneSearchService", e);
      }
      throw e;
    }

  }

  /**
   * DS deactivate method.
   * 
   * @param context
   *          the ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    try {
      unloadReverseMappings();
      unloadMappings();
      _dataDictionary = null;
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("error deactivating LuceneSearchService", e);
      }
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SearchProcessingService#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.processing.SearchMessage)
   */
  public SearchMessage process(Blackboard blackboard, SearchMessage message) throws ProcessingException {
    if (message.hasQuery()) {
      try {
        search(blackboard, message);
      } catch (final Exception ex) {
        final String msg = "Error processing message " + message.getQuery();
        if (_log.isErrorEnabled()) {
          _log.error(msg, ex);
        }
        throw new ProcessingException(msg, ex);
      }
    }
    return message;
  }

  /**
   * Creates a query and executes the query on the lucene index.
   * 
   * @param blackboard
   *          the BlackBoardService
   * @param message
   *          the SearchMessege
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws IndexException
   *           if any error occurs
   * @throws DataDictionaryException
   *           if any error occurs
   * @throws NodeTransformerException
   *           if any error occurs
   * @throws ProcessingException
   *           if parameter SEARCH_ANNOTATION_QUERY_ATTRIBUTE is not set for simple query or any other error occurs
   */
  private void search(final Blackboard blackboard, SearchMessage message) throws BlackboardAccessException,
    IndexException, DataDictionaryException, NodeTransformerException, ProcessingException {
    IndexConnection indexConnection = null;
    try {
      final ParameterAccessor parameters = new ParameterAccessor(blackboard, message.getQuery());
      final String indexName = getIndexname(blackboard, message.getQuery(), parameters);
      indexConnection = IndexManager.getInstance(indexName);
      if (indexConnection != null) {
        DQuery dQuery = null;
        if (hasQueryString(parameters)) {
          dQuery = createSimpleQuery(parameters, indexName);
        } else if (hasAttributeQuery(blackboard, message.getQuery(), parameters, indexName)) {
          dQuery = createFieldedQuery(blackboard, message.getQuery(), parameters, indexName);
        }

        // append any filters to the query
        dQuery = appendFilter(dQuery, blackboard, message.getQuery(), parameters, indexName);

        // check if a DQueryExpression exists and execute it
        if (dQuery != null) {
          // append the field numbers for the result attributes
          dQuery = appendResultFields(dQuery, parameters, indexName);

          // append the field numbers for all attrributes with a highlight annotation
          dQuery = appendHiglightingFields(dQuery, blackboard, message.getQuery(), indexName);

          // execute expression query
          final LuceneSearchResult result = indexConnection.doQuery(dQuery);

          // process lucene result
          processResult(blackboard, message, result);
        } else {
          // no query exists, create empty result
          message.setRecords(new Id[0]);
          try {
            setTotalHits(blackboard, message.getQuery(), 0);
          } catch (BlackboardAccessException ex) {
            _log.error(ex);
          }
        }
      } else {
        throw new IndexException("Could not open connection to index " + indexName);
      }
    } finally {
      if (indexConnection != null) {
        IndexManager.releaseInstance(indexConnection, false);
      }
    }
  }

  /**
   * Checks if the ParameterAccessor contain a query.
   * 
   * @param parameters
   *          the ParameterAccessor
   * @return true if the ParameterAccessor contains a query, false otherwise
   */
  private boolean hasQueryString(ParameterAccessor parameters) {
    final String queryString = parameters.getQuery();
    if (queryString != null && queryString.trim().length() != 0) {
      return true;
    }
    return false;
  }

  /**
   * Checks the query record if it contains attributes for the configured IndexFields with Literal values.
   * 
   * @param blackboard
   *          the BlackBoardService
   * @param id
   *          the Id of the query record
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name of the index
   * @return true if the record contains such attributes, false otherwise
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private boolean hasAttributeQuery(final Blackboard blackboard, final Id id, final ParameterAccessor parameters,
    final String indexName) throws BlackboardAccessException {
    final DIndex dIndex = _dataDictionary.getIndex(indexName);
    final Iterator fieldIt = dIndex.getConfiguration().getDefaultConfig().getFields();
    while (fieldIt.hasNext()) {
      final DField field = (DField) fieldIt.next();
      final String attributeName = getAttributeName(indexName, field.getFieldNo());

      if (blackboard.getRecord(id).getMetadata().hasAttribute(attributeName)
        && blackboard.getRecord(id).getMetadata().getAttribute(attributeName).hasLiterals()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a simple query based on a single TextFiled.
   * 
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name of the index
   * @return a DQuery object
   * @throws ProcessingException
   *           if parameter SEARCH_ANNOTATION_QUERY_ATTRIBUTE is not set or any other error occurs
   */
  private DQuery createSimpleQuery(final ParameterAccessor parameters, final String indexName)
    throws ProcessingException {
    final String queryAttribute = parameters.getRequiredParameter(SEARCH_ANNOTATION_QUERY_ATTRIBUTE);
    final int fieldNo = getFieldNo(indexName, queryAttribute);
    final DField field =
      _dataDictionary.getIndex(indexName).getConfiguration().getDefaultConfig().getField(fieldNo);
    final DFieldConfig fieldConfig = field.getFieldConfig();

    final org.eclipse.smila.search.utils.search.DTextField textField =
      new org.eclipse.smila.search.utils.search.DTextField();
    textField.setFieldNo(field.getFieldNo());
    textField.setType(fieldConfig.getType());
    textField.setText(parameters.getQuery());

    final DQuery dQuery = createDQuery(parameters, indexName);
    dQuery.addField(textField);
    return dQuery;
  }

  /**
   * Creates a fielded query.
   * 
   * @param blackboard
   *          the BlackBoardService
   * @param id
   *          the id of the query record
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name of the index
   * @return a DQuery object
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws ProcessingException
   *           if any error occurs
   */
  private DQuery createFieldedQuery(final Blackboard blackboard, final Id id, final ParameterAccessor parameters,
    final String indexName) throws BlackboardAccessException, ProcessingException {
    final DQuery dQuery = createDQuery(parameters, indexName);
    final DIndex dIndex = _dataDictionary.getIndex(indexName);
    final Iterator fieldIt = dIndex.getConfiguration().getDefaultConfig().getFields();
    while (fieldIt.hasNext()) {
      final DField field = (DField) fieldIt.next();
      final String attributeName = getAttributeName(indexName, field.getFieldNo());
      if (blackboard.getRecord(id).getMetadata().hasAttribute(attributeName)
        && blackboard.getRecord(id).getMetadata().getAttribute(attributeName).hasLiterals()) {
        final Attribute attribute = blackboard.getRecord(id).getMetadata().getAttribute(attributeName);
        final org.eclipse.smila.search.utils.search.DField searchField = createSearchField(field, attribute);
        dQuery.addField(searchField);
      }
    }
    return dQuery;
  }

  /**
   * Appends a filter to the given dQuery. If no dQuery exists but a filter is provided a dQuery will be created.
   * 
   * @param dQuery
   *          the dQuery to append the filter to
   * @param blackboard
   *          teh BalckBoardService
   * @param id
   *          the query record id
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name fo the index
   * @return a dQuery or null
   * @throws BlackboardAccessException
   *           id any eerror occurs
   * @throws ProcessingException
   *           id any eerror occurs
   */
  private DQuery appendFilter(DQuery dQuery, final Blackboard blackboard, final Id id,
    final ParameterAccessor parameters, final String indexName) throws BlackboardAccessException,
    ProcessingException {
    final DIndex dIndex = _dataDictionary.getIndex(indexName);
    final Iterator fieldIt = dIndex.getConfiguration().getDefaultConfig().getFields();
    while (fieldIt.hasNext()) {
      final DField field = (DField) fieldIt.next();
      final String attributeName = getAttributeName(indexName, field.getFieldNo());
      if (blackboard.getRecord(id).getMetadata().hasAttribute(attributeName)
        && blackboard.getRecord(id).getMetadata().getAttribute(attributeName).hasAnnotation(
          SearchAnnotations.FILTER)) {
        // ensure a dquery exists
        if (dQuery == null) {
          dQuery = createDQuery(parameters, indexName);
        }
        final Annotation filter =
          blackboard.getRecord(id).getMetadata().getAttribute(attributeName)
            .getAnnotation(SearchAnnotations.FILTER);
        final org.eclipse.smila.search.utils.search.DField searchField = createSearchFilter(field, filter);
        dQuery.addField(searchField);
      } // if
    } // while
    return dQuery;
  }

  /**
   * Appends the resultFields (if any) to the given dQuery and returns it.
   * 
   * @param dQuery
   *          the DQuery to append the highlighting fields to
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name of the index
   * @return the modified DQuery
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private DQuery appendResultFields(DQuery dQuery, final ParameterAccessor parameters, final String indexName)
    throws BlackboardAccessException {
    final List<String> resultAttributes = parameters.getResultAttributes();
    if (resultAttributes != null) {
      final ArrayList<Integer> resultFields = new ArrayList<Integer>(resultAttributes.size());
      for (String attributeName : resultAttributes) {
        try {
          resultFields.add(getFieldNo(indexName, attributeName));
        } catch (Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("error appending result fieldNo for attribute " + attributeName, e);
          }
        }
      } // for

      if (!resultFields.isEmpty()) {
        dQuery.setResultFields(resultFields);
      }

    } // if
    return dQuery;
  }

  /**
   * Appends the highlightingFields (if any) to the given dQuery and returns it.
   * 
   * @param dQuery
   *          the DQuery to append the highlighting fields to
   * @param blackboard
   *          the BlackBoardService
   * @param id
   *          the query record id
   * @param indexName
   *          the name of the index
   * @return the modified DQuery
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private DQuery appendHiglightingFields(DQuery dQuery, final Blackboard blackboard, final Id id,
    final String indexName) throws BlackboardAccessException {
    final ArrayList<Integer> highlightFields = new ArrayList<Integer>();
    final Iterator<String> attributeNames = blackboard.getAttributeNames(id);
    while (attributeNames.hasNext()) {
      final String attributeName = attributeNames.next();
      final Path path = new Path().add(attributeName, PathStep.ATTRIBUTE_ANNOTATION);
      if (blackboard.hasAnnotation(id, path, SearchAnnotations.HIGHLIGHT)) {
        try {
          highlightFields.add(getFieldNo(indexName, attributeName));
        } catch (Exception e) {
          if (_log.isWarnEnabled()) {
            _log.warn("error appending highlight fieldNo for attribute " + attributeName, e);
          }
        }
      }
    }

    if (!highlightFields.isEmpty()) {
      dQuery.setHighlightFields(highlightFields);
    }
    return dQuery;
  }

  /**
   * Creates a basic DQuery object setting global parameters.
   * 
   * @param parameters
   *          the ParameterAccessor
   * @param indexName
   *          the name of the Index
   * @return a DQuery object
   */
  private DQuery createDQuery(final ParameterAccessor parameters, final String indexName) {
    final DQuery dQuery = new DQuery();
    dQuery.setIndexName(indexName);
    dQuery.setMaxHits(parameters.getResultSize());
    dQuery.setStartHits(parameters.getResultOffset());
    dQuery.setMinSimilarity((int) (parameters.getThreshold() * 100));
    dQuery.setShowHitDistribution(true);

    // these parameters are set by special annotations
    dQuery.setTemplateSelectorName(parameters.getParameter(SEARCH_ANNOTATION_TEMPLATE_SELECTOR_NAME, ""));

    return dQuery;
  }

  /**
   * Creates a search.DField from the given ddconfig.DField and the associated Attribute.
   * 
   * @param field
   *          the DField
   * @param attribute
   *          the record Attribute
   * @return a org.eclipse.smila.search.utils.search.DField object
   * @throws ProcessingException
   *           if any error occurs
   */
  private org.eclipse.smila.search.utils.search.DField createSearchField(final DField field,
    final Attribute attribute) throws ProcessingException {
    final DFieldConfig fieldConfig = field.getFieldConfig();
    final String type = fieldConfig.getType();
    org.eclipse.smila.search.utils.search.DField searchField;
    // create Field depending on type
    if ("FTText".equals(type)) {
      searchField = createTextField(attribute);
    } else if ("FTDate".equals(type)) {
      searchField = createDateField(attribute);
    } else if ("FTNumber".equals(type)) {
      searchField = createNumberField(attribute);
    } else {
      throw new ProcessingException("unknown FieldConfig type " + type);
    }
    // set fieldNo and type
    searchField.setFieldNo(field.getFieldNo());
    searchField.setType(fieldConfig.getType());
    return searchField;
  }

  /**
   * Creates a DTextField from the given attribute.
   * 
   * @param attribute
   *          the Attribute
   * @return a org.eclipse.smila.search.utils.search.DTextField object
   */
  private org.eclipse.smila.search.utils.search.DTextField createTextField(Attribute attribute) {
    final org.eclipse.smila.search.utils.search.DTextField textField =
      new org.eclipse.smila.search.utils.search.DTextField();
    textField.setText(attribute.getLiteral().getStringValue());

    // check for annotations to convert to parameters
    if (attribute.hasAnnotation(SearchAnnotations.RANKING)) {
      final Annotation fieldParam = attribute.getAnnotation(SearchAnnotations.RANKING);
      if (fieldParam.hasNamedValues()) {
        final DTextFieldParameter parameter = new DTextFieldParameter();

        final String operator = fieldParam.getNamedValue(RANKING_OPERATOR);
        if (operator != null) {
          parameter.setOperator(DOperator.getInstance(operator));
        }
        final String tolerance = fieldParam.getNamedValue(RANKING_TOLERANCE);
        if (operator != null) {
          parameter.setTolerance(DTolerance.getInstance(tolerance));
        }
        textField.setParameter(parameter);
      } // if
    } // if
    return textField;
  }

  /**
   * Creates a DDateField from the given attribute.
   * 
   * @param attribute
   *          the Attribute
   * @return a org.eclipse.smila.search.utils.search.DDateField object
   * @throws ProcessingException
   *           if any error occurs
   */
  private org.eclipse.smila.search.utils.search.DDateField createDateField(Attribute attribute)
    throws ProcessingException {
    final org.eclipse.smila.search.utils.search.DDateField dateField =
      new org.eclipse.smila.search.utils.search.DDateField();
    try {
      if (attribute.literalSize() != 2) {
        final Date date = parseDate(attribute.getLiteral().getStringValue());
        final Calendar value = Calendar.getInstance();
        value.setTime(date);
        dateField.setDateMin(value);
        dateField.setDateMax(value);
      }
    } catch (ParseException e) {
      throw new ProcessingException("error parsing date object of attribute " + attribute.getName(), e);
    }
    // no parameters to set
    // dateField.setParameter(parameter);
    return dateField;
  }

  /**
   * Creates a DNumberField from the given attribute.
   * 
   * @param attribute
   *          the Attribute
   * @return a org.eclipse.smila.search.utils.search.DNumberField object
   */
  private org.eclipse.smila.search.utils.search.DNumberField createNumberField(Attribute attribute) {
    final org.eclipse.smila.search.utils.search.DNumberField numberField =
      new org.eclipse.smila.search.utils.search.DNumberField();
    final Long value = Long.parseLong(attribute.getLiteral().getStringValue());
    numberField.setMin(value);
    numberField.setMax(value);
    // no parameters to set
    // numberField.setParameter(parameter);
    return numberField;
  }

  /**
   * Creates a search.DField from the given ddconfig.DField and the associated filter annotation.
   * 
   * @param field
   *          the field
   * @param annotation
   *          the filter Annotation
   * @return a org.eclipse.smila.search.utils.search.DField object
   * @throws ProcessingException
   *           if any error occurs
   */
  private org.eclipse.smila.search.utils.search.DField createSearchFilter(final DField field,
    final Annotation annotation) throws ProcessingException {
    final DFieldConfig fieldConfig = field.getFieldConfig();
    final String type = fieldConfig.getType();
    org.eclipse.smila.search.utils.search.DField searchField;
    // create Field depending on type
    if ("FTText".equals(type)) {
      throw new ProcessingException("FTText does not support filter annotations");
    } else if ("FTDate".equals(type)) {
      searchField = createDateFilter(annotation);
    } else if ("FTNumber".equals(type)) {
      searchField = createNumberFilter(annotation);
    } else {
      throw new ProcessingException("unknown FieldConfig type " + type);
    }
    // set fieldNo and type
    searchField.setFieldNo(field.getFieldNo());
    searchField.setType(fieldConfig.getType());
    return searchField;
  }

  /**
   * Creates a DDateField for the given filter annotation.
   * 
   * @param annotation
   *          the filter Annotation
   * @return a DDateField
   * @throws ProcessingException
   *           if any error occurs
   */
  private org.eclipse.smila.search.utils.search.DDateField createDateFilter(final Annotation annotation)
    throws ProcessingException {
    final org.eclipse.smila.search.utils.search.DDateField dateField =
      new org.eclipse.smila.search.utils.search.DDateField();
    if (SearchAnnotations.FilterType.RANGE.name().equals(annotation.getNamedValue(SearchAnnotations.FILTER_TYPE))) {
      try {
        final Calendar minValue = Calendar.getInstance();
        final String minStringValue = annotation.getNamedValue(SearchAnnotations.FILTER_MIN);
        if (minStringValue != null) {
          final Date minDate = parseDate(minStringValue);
          minValue.setTime(minDate);
        } else {
          minValue.setTimeInMillis(0);
        }

        final Calendar maxValue = Calendar.getInstance();
        final String maxStringValue = annotation.getNamedValue(SearchAnnotations.FILTER_MAX);
        if (maxStringValue != null) {
          final Date maxDate = parseDate(maxStringValue);
          maxValue.setTime(maxDate);
        } else {
          maxValue.setTimeInMillis(Long.MAX_VALUE);

        }

        dateField.setDateMin(minValue);
        dateField.setDateMax(maxValue);
      } catch (ParseException e) {
        throw new ProcessingException("error parsing date object of annotation " + annotation, e);
      }
    } else {
      throw new ProcessingException("");
    }
    return dateField;
  }

  /**
   * Creates a DNumberField for the given filter annotation.
   * 
   * @param annotation
   *          the filter Annotation
   * @return a DNumberField
   * @throws ProcessingException
   *           if any error occurs
   */
  private org.eclipse.smila.search.utils.search.DNumberField createNumberFilter(final Annotation annotation)
    throws ProcessingException {
    final org.eclipse.smila.search.utils.search.DNumberField numberField =
      new org.eclipse.smila.search.utils.search.DNumberField();

    if (SearchAnnotations.FilterType.RANGE.name().equals(annotation.getNamedValue(SearchAnnotations.FILTER_TYPE))) {
      long minValue = Long.MIN_VALUE;
      final String minStringValue = annotation.getNamedValue(SearchAnnotations.FILTER_MIN);
      if (minStringValue != null) {
        minValue = Long.parseLong(minStringValue);
      }
      long maxValue = Long.MAX_VALUE;
      final String maxStringValue = annotation.getNamedValue(SearchAnnotations.FILTER_MAX);
      if (maxStringValue != null) {
        maxValue = Long.parseLong(maxStringValue);
      }
      numberField.setMin(minValue);
      numberField.setMax(maxValue);
    } else {
      throw new ProcessingException("");
    }
    return numberField;
  }

  /**
   * Processes the lucene result and create result records.
   * 
   * @param blackboard
   *          the BlackboardService
   * @param message
   *          the SearchMessage
   * @param luceneResult
   *          the Result
   * @throws BlackboardAccessException
   *           if any error occurs
   * @throws IndexException
   *           if any error occurs
   */
  private void processResult(final Blackboard blackboard, final SearchMessage message,
    final LuceneSearchResult luceneResult) throws BlackboardAccessException, IndexException {

    /*************************************/

    if (luceneResult != null && !luceneResult.getResultList().isEmpty()) {
      // global infos
      final int[] hitStatistic = getHitStatistics(luceneResult);
      setTotalHits(blackboard, message.getQuery(), hitStatistic[0]);
      setIndexSize(blackboard, message.getQuery(), hitStatistic[1]);

      // set the result records in the blackboard and set record IDs in message
      final ArrayList<Id> resultIds = new ArrayList<Id>(luceneResult.getResultList().size());
      for (Record record : luceneResult.getResultList()) {
        blackboard.setRecord(record);
        resultIds.add(record.getId());
      }
      message.setRecords(resultIds);
    } else {
      setTotalHits(blackboard, message.getQuery(), 0);
    }
  }

  /**
   * Returns the total number of hits and the size of the index.
   * 
   * @param luceneResult
   *          the lucene result
   * @return an array containing the total number of hits (pos 0) and the indes size (pos 1)
   */
  private int[] getHitStatistics(final LuceneSearchResult luceneResult) {
    int resultCount = 0;
    int docsIndexed = 0;
    final Enumeration hitEnum = luceneResult.getHitDistribution().getHits();
    while (hitEnum.hasMoreElements()) {
      final DHit hit = ((DHit) hitEnum.nextElement());
      if (hit.getScore() > 0) {
        resultCount += hit.getHits();
      }
      docsIndexed += hit.getHits();
    }
    return new int[] { resultCount, docsIndexed };
  }

  /**
   * Returns the name of the index to search in. First checks if the query contains a indexname annotation. If not if
   * checks if the pipeline contains a pipelet annotation
   * 
   * @param blackboard
   *          the Blackboard
   * @param id
   *          the query id
   * @param parameters
   *          the query parameters
   * @return the name of the index to search in
   * @throws BlackboardAccessException
   *           if any error occurs
   */
  private String getIndexname(Blackboard blackboard, Id id, ParameterAccessor parameters)
    throws BlackboardAccessException {
    String indexName = parameters.getIndexName();
    if (indexName == null) {
      final Annotation pipeletAnnotation = blackboard.getAnnotation(id, null, getClass().getName());
      if (pipeletAnnotation != null) {
        indexName = pipeletAnnotation.getNamedValue(INDEX_NAME);
      }
    }
    return indexName;
  }

  /**
   * Returns the record attribute name for a lucene field number.
   * 
   * @param indexName
   *          the name of the index
   * @param fieldNo
   *          the field number
   * @return the name of the record attribute
   */
  private String getAttributeName(String indexName, int fieldNo) {
    String attributeName = null;
    final HashMap<String, HashMap<Integer, String>> indexMap = _reverseMappings.get(indexName);
    if (indexMap != null) {
      final HashMap<Integer, String> attMap = indexMap.get(MAPPINGS_ATTRIBUTES);
      if (attMap != null) {
        attributeName = attMap.get(Integer.valueOf(fieldNo));
        if (attributeName == null) {
          final HashMap<Integer, String> attachMap = indexMap.get(MAPPINGS_ATTACHMENTS);
          if (attachMap != null) {
            attributeName = attachMap.get(Integer.valueOf(fieldNo));
          }
        }
      }
    }
    return attributeName;
  }

  /**
   * Gets the fieldNo for a given attribute/attachment name in an index.
   * 
   * @param indexName
   *          name of the index
   * @param name
   *          name of the attribute/attachment
   * @return the fieldNo
   * @throws ProcessingException
   *           if no fieldNo can be found
   */
  private int getFieldNo(final String indexName, final String name) throws ProcessingException {
    if (getMappings().get(indexName).get(MappingsLoader.ATTRIBUTES).containsKey(name)) {
      return getMappings().get(indexName).get(MappingsLoader.ATTRIBUTES).get(name);
    } else if (getMappings().get(indexName).get(MappingsLoader.ATTACHMENTS).containsKey(name)) {
      return getMappings().get(indexName).get(MappingsLoader.ATTACHMENTS).get(name);
    } else {
      throw new ProcessingException("Could not find fieldNo for attribute/attachment named " + name);
    }
  }

  /**
   * Loads the reverse mappings of fieldNo to attribute/attachment names.
   */
  private void loadReverseMappings() {
    if (getMappings() != null) {
      final Iterator<String> indexNames = getMappings().keySet().iterator();
      while (indexNames.hasNext()) {
        final HashMap<String, HashMap<Integer, String>> reverseMap =
          new HashMap<String, HashMap<Integer, String>>();
        final String indexName = indexNames.next();
        final HashMap<String, HashMap<String, Integer>> indexMap = getMappings().get(indexName);
        final Iterator<String> mapNames = indexMap.keySet().iterator();
        while (mapNames.hasNext()) {
          final HashMap<Integer, String> reverseAttMap = new HashMap<Integer, String>();
          final String mapName = mapNames.next();
          final HashMap<String, Integer> attMap = indexMap.get(mapName);
          final Iterator<String> attNames = attMap.keySet().iterator();
          while (attNames.hasNext()) {
            final String attName = attNames.next();
            final Integer fieldNo = attMap.get(attName);
            reverseAttMap.put(fieldNo, attName);
          }
          reverseMap.put(mapName, reverseAttMap);
        }
        _reverseMappings.put(indexName, reverseMap);
      }
    }
  }

  /**
   * Unloads the reverse mappings.
   */
  private void unloadReverseMappings() {
    if (_reverseMappings != null) {
      final Collection<HashMap<String, HashMap<Integer, String>>> collection = _reverseMappings.values();
      for (HashMap<String, HashMap<Integer, String>> map : collection) {
        if (map != null) {
          final Collection<HashMap<Integer, String>> values = map.values();
          for (HashMap<Integer, String> submap : values) {
            if (submap != null) {
              submap.clear();
            }
          }
          map.clear();
        }
      }
      _reverseMappings.clear();
      _reverseMappings = null;
    }
  }

  /**
   * Parse a date String.
   * 
   * @param dateString
   *          the date as a string
   * @return a Date object
   * @throws ParseException
   *           if the date string cannot be parsed
   */
  private Date parseDate(final String dateString) throws ParseException {
    try {
      return (new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).parse(dateString));
    } catch (ParseException e) {
      return (new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(dateString));
    }
  }

  // *********************************** helper code copied from MockSearchPipelet ***********************************

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
  private void setTotalHits(Blackboard blackboard, Id record, int noOfHits) throws BlackboardAccessException {
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
  private void setIndexSize(Blackboard blackboard, Id record, int indexSize) throws BlackboardAccessException {
    final Annotation resultAnno = ensureRecordAnnotation(blackboard, record, SearchAnnotations.RESULT);
    resultAnno.setNamedValue(SearchAnnotations.INDEX_SIZE, Integer.toString(indexSize));
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
  private Annotation ensureRecordAnnotation(Blackboard blackboard, Id record, String name)
    throws BlackboardAccessException {
    Annotation annotation = blackboard.getAnnotation(record, null, name);
    if (annotation == null) {
      annotation = blackboard.createAnnotation(record);
      blackboard.setAnnotation(record, null, name, annotation);
    }
    return annotation;
  }

}
