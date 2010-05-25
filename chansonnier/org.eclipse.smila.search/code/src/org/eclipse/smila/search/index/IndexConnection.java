/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

// standard utility classes
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.search.datadictionary.DataDictionaryController;
import org.eclipse.smila.search.datadictionary.DataDictionaryException;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DIndex;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DConfiguration;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DFieldConfig;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DFieldConstraints;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DNamedConfig;
import org.eclipse.smila.search.datadictionary.messages.ddconfig.DQueryConstraints;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.templates.TemplateException;
import org.eclipse.smila.search.templates.TemplateRegistryController;
import org.eclipse.smila.search.templates.messages.searchtemplates.DTemplate;
import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.indexstructure.DIndexField;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DQuery;
import org.eclipse.smila.search.utils.search.DTextField;
import org.eclipse.smila.search.utils.search.IDFParameter;
import org.eclipse.smila.search.utils.search.INFParameter;
import org.eclipse.smila.search.utils.search.ITFParameter;
import org.eclipse.smila.search.utils.searchresult.LuceneSearchResult;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public abstract class IndexConnection {

  /**
   * Index configuration from data dictionary.
   */
  protected DIndex _index;

  /**
   * Index name.
   */
  protected String _indexName;

  /**
   * @param indexName
   *          name of index to be created
   * @throws IndexException
   *           Unable to create index connection.
   */
  protected IndexConnection(final String indexName) throws IndexException {
    try {
      this._index = DataDictionaryController.getIndex(indexName);
    } catch (final DataDictionaryException e) {
      throw new IndexException(e.getMessage(), e);
    }
    if (this._index == null) {
      throw new IndexException("index not in data dictionary [" + indexName + "]");
    }
    this._indexName = indexName;
  }

  /**
   * Get index name.
   * 
   * @return Index name.
   */
  public String getName() {
    return this._indexName;
  }

  /**
   * Get index configuration definition from data dictionary.
   * 
   * @return Index configuration definition from data dictionary.
   */
  public DIndex getIndex() {
    return this._index;
  }

  /**
   * Close index connection.
   */
  protected abstract void close();

  /**
   * Checks whether a document exists in index.
   * 
   * @param key
   *          Key.
   * @return Whether a document exists.
   * @throws IndexException
   *           Unable to check the document exists in index.
   */
  public abstract boolean docExists(String key) throws IndexException;

  /**
   * Checks whether a document exists in index.
   * 
   * @param id
   *          the Id
   * @return Whether a document exists.
   * @throws IndexException
   *           Unable to check the document exists in index.
   */
  public abstract boolean docExists(Id id) throws IndexException;

  /**
   * Delete document from index.
   * 
   * @param key
   *          Key.
   * @throws IndexException
   *           Unable to delete document from index.
   */
  public abstract void deleteDocument(String key) throws IndexException;

  /**
   * Delete document from index.
   * 
   * @param id
   *          the Id
   * @throws IndexException
   *           Unable to delete document from index.
   */
  public abstract void deleteDocument(Id id) throws IndexException;

  /**
   * Add a document to index.
   * 
   * @param blackboard
   *          the BlackboardService
   * @param id
   *          the Id of the record to add.
   * @param attributeMapping
   *          Map containing the attribute to FieldNo mapping
   * @param attachmentMapping
   *          Map containing the attachment to FieldNo mapping
   * @throws IndexException
   *           Unable to add document to index.
   * @throws BlackboardAccessException
   *           if errors occur accessing the Blackboard
   */
  public abstract void learnDocument(Blackboard blackboard, Id id, Map<String, Integer> attributeMapping,
    Map<String, Integer> attachmentMapping) throws IndexException, BlackboardAccessException;

  /**
   * Start burst mode.
   * 
   * @throws IndexException
   *           Unable to start burst mode.
   */
  protected abstract void startBurstmode() throws IndexException;

  /**
   * Stop burst mode.
   * 
   * @throws IndexException
   *           Unable to stop burst mode.
   */
  protected abstract void stopBurstmode() throws IndexException;

  /**
   * Transform a simple search to the corresponding advanced search.
   * 
   * @param dQuery
   *          Simple search.
   * @return Advanced search.
   * @throws IndexException
   *           Unable to get advanced search.
   * @throws NodeTransformerException
   *           Unable to perform node transformation.
   * @throws TemplateException
   *           Unable to apply template.
   */
  public abstract IQueryExpression getSimpleSearchQuery(DQuery dQuery) throws IndexException,
    NodeTransformerException, TemplateException;

  /**
   * Execute search query.
   * 
   * @param dQE
   *          Advanced search query.
   * @param startPos
   *          Start position.
   * @return Result.
   * @throws IndexException
   *           Unable to perform search query.
   */
  protected abstract LuceneSearchResult doQuery(IQueryExpression dQE, int startPos) throws IndexException;

  /**
   * @param text
   *          -
   * @return IQueryExpression
   * @throws IndexException
   *           -
   */
  protected abstract IQueryExpression getTestQueryExpression(String text) throws IndexException;

  /**
   * @param field
   *          -
   * @param configParameter
   *          -
   * @param config
   *          -
   * @param sb
   *          -
   */
  protected abstract void insertTextParameter(org.eclipse.smila.search.utils.search.DTextField field,
    ITFParameter configParameter, String config, StringBuffer sb);

  /**
   * @param field
   *          -
   * @param configParameter
   *          -
   * @param config
   *          -
   * @param sb
   *          -
   */
  protected abstract void insertNumberParameter(org.eclipse.smila.search.utils.search.DNumberField field,
    INFParameter configParameter, String config, StringBuffer sb);

  /**
   * @param field
   *          -
   * @param configParameter
   *          -
   * @param config
   *          -
   * @param sb
   *          -
   */
  protected abstract void insertDateParameter(org.eclipse.smila.search.utils.search.DDateField field,
    IDFParameter configParameter, String config, StringBuffer sb);

  /**
   * @param keys
   *          -
   * @param fields
   *          -
   * @param values
   *          -
   * @throws IndexException
   *           -
   */
  protected abstract void getResultValues(String[] keys, int[] fields, String[][] values) throws IndexException;

  protected abstract String[] getResultValues(final Id id, final int fieldNo) throws IndexException;

  /**
   * @param tf
   *          -
   * @throws IndexException
   *           -
   */
  protected abstract void encodeTextField(DTextField tf) throws IndexException;

  /**
   * @param fieldNo
   *          -
   * @param value
   *          -
   * @throws IndexException
   *           -
   */
  protected abstract void isSupportedValue(int fieldNo, Object value) throws IndexException;

  /**
   * This method extends a given search query with all possible configurable attributes. Additionally checks this method
   * wether there are any conflicts with the index structure or the query constraints.
   * 
   * @param dQuery
   *          -
   * @throws IndexException
   *           -
   */
  public void validateQuery(final DQuery dQuery) throws IndexException {
    final Log log = LogFactory.getLog(getClass());
    final Hashtable<Integer, DField> queryFields = new Hashtable<Integer, DField>();
    final DConfiguration dConfig = _index.getConfiguration();

    // 1. check fields of query against index.
    // 2. apply configuration settings
    Enumeration fields = dQuery.getFields();
    while (fields.hasMoreElements()) {
      final DField field = (DField) fields.nextElement();
      if (!_index.getIndexStructure().hasField(field.getFieldNo())) {
        throw new IndexException("field in search query does not exist in index [" + field.getFieldNo() + "]");
      }
      // checks whether types of search field and index field are compatible.
      // for checking, the type taken from the index field's default configuration will be used,
      // because there we already have simple search types, whereas in the index structure,
      // different types may be used (e. g. Date may be represented by Number)
      if (!dConfig.getDefaultConfig().getField(field.getFieldNo()).getFieldConfig().getType().equals(
        field.getType())) {
        throw new IndexException("field type in search query does not match field type in index ["
          + field.getFieldNo() + "]");
      }

      // get default whole parameterization
      DFieldConfig fc = null;

      // check for named config
      if (field.getParameterDescriptor() != null) {
        DNamedConfig nc = null;
        if (!dConfig.hasNamedConfig(field.getParameterDescriptor())) {
          log.error("unable to locate named config for index [" + dQuery.getIndexName() + ";"
            + field.getParameterDescriptor() + "]");
        } else {
          nc = dConfig.getNamedConfig(field.getParameterDescriptor());

          if (nc != null) {
            // check for field type specifig named config
            fc = nc.getFieldConfig(field);

            if (fc == null) {
              log.error("unable to locate named config for index [" + dQuery.getIndexName() + ";"
                + field.getParameterDescriptor() + ";" + field.getType() + "]");
            }

          }
        }
      }

      final DFieldConfig defaultfc = dConfig.getDefaultConfig().getField(field.getFieldNo()).getFieldConfig();
      // set default config
      applyFieldConfig(field, fc, defaultfc);

      queryFields.put(new Integer(field.getFieldNo()), field);
    }

    // 3. check query constraints for simple search.
    final DQueryConstraints queryConstraints = dConfig.getQueryConstraints();
    if (queryConstraints != null) {
      for (int i = 0; i < queryConstraints.getFieldConstraintsLength(); i++) {
        final DFieldConstraints dFieldConstraints = queryConstraints.getFieldConstraints(i);

        final String occurrence = dFieldConstraints.getOccurrence();
        if (occurrence.equals("required")) {
          if (!queryFields.containsKey(new Integer(dFieldConstraints.getFieldNo()))) {
            throw new IndexException("query constraint requires field [" + dFieldConstraints.getFieldNo() + "]");
          }
        } else if (occurrence.equals("prohibited")) {
          if (queryFields.containsKey(new Integer(dFieldConstraints.getFieldNo()))) {
            throw new IndexException("query constraint prohibits field [" + dFieldConstraints.getFieldNo() + "]");
          }
        }

        // check further query constraints
        final DField field = queryFields.get(new Integer(dFieldConstraints.getFieldNo()));
        if (field == null) {
          continue;
        }
        if (dFieldConstraints.getFieldTemplateCount() > 0) {
          boolean matchConstraint = false;
          final String[] fieldTemplates = dFieldConstraints.getFieldTemplates();
          final String fieldTemplate = (field.getFieldTemplate() != null ? field.getFieldTemplate().trim() : "");
          for (int j = 0; j < fieldTemplates.length; j++) {
            if (fieldTemplates[j].equals(fieldTemplate)) {
              matchConstraint = true;
              break;
            }
          }

          if (!matchConstraint) {
            throw new IndexException("query field does not match field template constraint [" + field.getFieldNo()
              + "]");
          }
        }

        if (dFieldConstraints.getNodeTransformerCount() > 0) {
          boolean matchConstraint = false;
          final String[] nodeTransformers = dFieldConstraints.getNodeTransformers();
          final String nodeTransformerName =
            (field.getNodeTransformer() != null ? field.getNodeTransformer().getName() : "");
          for (int j = 0; j < nodeTransformers.length; j++) {
            if (nodeTransformers[j].equals(nodeTransformerName)) {
              matchConstraint = true;
              break;
            }
          }

          if (!matchConstraint) {
            throw new IndexException("query field does not match node transformer constraint ["
              + field.getFieldNo() + "]");
          }
        }

        if (dFieldConstraints.getConstraintCount() > 0) {
          boolean matchConstraint = false;
          final String[] constraints = dFieldConstraints.getConstraints();
          final String constraint = (field.getConstraint() != null ? field.getConstraint() : "");
          for (int j = 0; j < constraints.length; j++) {
            if (constraints[j].equals(constraint)) {
              matchConstraint = true;
              break;
            }
          }

          if (!matchConstraint) {
            throw new IndexException("query field does not match constraint constraint [" + field.getFieldNo()
              + "]");
          }
        }
      }
    }

    // apply field valiadtion and transformation
    fields = dQuery.getFields();
    while (fields.hasMoreElements()) {
      final DField field = (DField) fields.nextElement();

      if (field instanceof DTextField) {
        encodeTextField((DTextField) field);
      }
    }
  }

  public LuceneSearchResult doQuery(final IQueryExpression dQE) throws IndexException {
    final int startPos = (dQE.getStartHits() == null ? 0 : dQE.getStartHits().intValue());
    final LuceneSearchResult result = doQuery(dQE, startPos);
    return result;
  }

  /**
   * Performs a search for a fully complex query expression.
   * 
   * @param dQuery
   *          -
   * @return LuceneSearchResult
   * @throws IndexException
   *           -
   */
  public LuceneSearchResult doQuery(final DQuery dQuery) throws IndexException {
    final Log log = LogFactory.getLog(getClass());

    validateQuery(dQuery);

    try {
      final DTemplate dTemplate = TemplateRegistryController.getTemplate(dQuery);
      IQueryExpression dQE = null;
      if (dTemplate != null) {
        if (log.isInfoEnabled()) {
          log.info("using template [" + dQuery.getIndexName() + ";" + dTemplate.getName() + "]");
        }
        dQE = TemplateRegistryController.applyTemplate(dQuery, dTemplate, this);
      } else {
        // transform
        dQE = getSimpleSearchQuery(dQuery);
      }

      final LuceneSearchResult searchResult =
        doQuery(dQE, (dQuery.getStartHits() != null ? dQuery.getStartHits().intValue() : 0));

      // add result attributes
      if (dQuery.getResultFields() != null) {
        addResultAttributes(dQuery.getResultFields(), searchResult);
      }

      // add highlight attributes
      if (dQuery.getHighlightFields() != null) {
        addHighlightResultAttributes(dQuery.getHighlightFields(), searchResult, dQE);
      }

      return searchResult;
    } catch (final TemplateException e) {
      log.error("error while NQE transformation", e);
      throw new IndexException("unable to apply templates", e);
    } catch (final NodeTransformerException e) {
      log.error("unable to perform node transformation", e);
      throw new IndexException("unable to perform node transformation", e);
    }
  }

  /**
   * Adds the value of index field as Attributes to the LuceneSearchResult.
   * 
   * @param resultDefinition
   *          the result definition
   * @param searchResult
   *          the LuceneSearchResult
   * @throws IndexException
   *           if any error occurs
   */
  private void addResultAttributes(final Collection<Integer> resultFields, final LuceneSearchResult searchResult)
    throws IndexException {
    for (final Record record : searchResult.getResultList()) {
      for (final int fieldNo : resultFields) {
        final DIndexField field = _index.getIndexStructure().getField(fieldNo);
        // TODO: map from fieldNo to Attribute name
        final String name = field.getName();
        final Attribute attribute = record.getFactory().createAttribute();
        attribute.setName(name);
        record.getMetadata().setAttribute(name, attribute);
        final String[] values = getResultValues(record.getId(), fieldNo);
        if (values != null) {
          for (final String value : values) {
            final Literal literal = record.getFactory().createLiteral();
            setLiteralValue(literal, field.getFieldNo(), value);
            attribute.addLiteral(literal);
          } // for
        } // if
      } // for
    } // for
  }

  /**
   * Adds the highlight annotations for the configured highlighting results.
   * 
   * @param highlightFields
   *          the highlightFields
   * @param searchResult
   *          the LuceneSearchResult
   * @param dQE
   *          the IQueryExpression
   * @throws IndexException
   *           if any errror occurs
   */
  private void addHighlightResultAttributes(final Collection<Integer> highlightFields,
    final LuceneSearchResult searchResult, final IQueryExpression dQE) throws IndexException {
    for (final Record record : searchResult.getResultList()) {
      for (final int fieldNo : highlightFields) {
        final DIndexField field = _index.getIndexStructure().getField(fieldNo);
        // TODO: map from fieldNo to Attribute name
        final String name = field.getName();
        final Attribute attribute = record.getFactory().createAttribute();
        attribute.setName(name);
        record.getMetadata().setAttribute(name, attribute);
        addHighlightAnnotation(dQE, record, fieldNo, name, searchResult.getIndexName());
      } // for
    } // for
  }

  /**
   * Set the value of the given literal using the appropriate type method.
   * 
   * @param literal
   *          the literal to set the value
   * @param fieldNo
   *          the fieldNo
   * @param value
   *          the string value
   * @throws IndexException
   *           if any error occurs
   */
  protected abstract void setLiteralValue(final Literal literal, final int fieldNo, final String value)
    throws IndexException;

  /**
   * Adds a highlighting annotation on the given record and attribute
   * 
   * @param dQE
   *          the IQueryExpression
   * @param record
   *          the Record
   * @param fieldNo
   *          the fieldNo
   * @param attributeName
   *          the name of the attribute
   * @param indexName
   *          the name of the index
   * @throws IndexException
   *           if any errorr occurs
   */
  protected abstract void addHighlightAnnotation(final IQueryExpression dQE, final Record record, int fieldNo,
    final String attributeName, final String indexName) throws IndexException;

  /**
   * Execute test query.
   * 
   * @param text
   *          Query text.
   * @return Result.
   * @throws IndexException
   *           Unable to perform test query.
   */
  public LuceneSearchResult doTestQuery(final String text) throws IndexException {

    return doQuery(getTestQueryExpression(text), 0);
  }

  /**
   * This method applies a given FieldConfig to a field.
   * 
   * @param field
   *          Search field.
   * @param nc
   *          Named config.
   * @param defaultfc
   *          Default config.
   */
  private void applyFieldConfig(final DField field, final DFieldConfig nc, final DFieldConfig defaultfc) {
    final Log log = LogFactory.getLog(getClass());

    StringBuffer sb = new StringBuffer("Processing parameters for search field [" + field.getFieldNo() + "]: ");
    if (nc == null) {
      sb = sb.append("[no named config] ");
    }
    if (field.getConstraint() == null) {
      if (nc == null || nc.getConstraint() == null) {
        field.setConstraint(defaultfc.getConstraint());
        sb = sb.append("DC:");
      } else {
        field.setConstraint(nc.getConstraint());
        sb = sb.append("NC:");
      }
    }
    sb = sb.append("Constraint=" + field.getConstraint() + " ");

    if (field.getFieldTemplate() == null) {
      if (nc == null || nc.getFieldTemplate() == null) {
        field.setFieldTemplate(defaultfc.getFieldTemplate());
        sb = sb.append("DC:");
      } else {
        field.setFieldTemplate(nc.getFieldTemplate());
        sb = sb.append("NC:");
      }
    }
    sb = sb.append("FieldTemplate=" + field.getFieldTemplate() + " ");

    if (field.getNodeTransformer() == null) {
      if (nc == null || nc.getNodeTransformer() == null) {
        field.setNodeTransformer(defaultfc.getNodeTransformer());
        sb = sb.append("DC:");
      } else {
        field.setNodeTransformer(nc.getNodeTransformer());
        sb = sb.append("NC:");
      }
    }
    sb =
      sb.append("NodeTransformer="
        + (field.getNodeTransformer() == null ? "" : field.getNodeTransformer().getName()) + " ");

    if (field.getWeight() == null) {
      if (nc == null || nc.getWeight() == null) {
        field.setWeight(defaultfc.getWeight());
        sb = sb.append("DC:");
      } else {
        field.setWeight(nc.getWeight());
        sb = sb.append("NC:");
      }
    }
    sb = sb.append("Weight=" + field.getWeight() + " ");

    // include technologie dependant parameter assignation
    if (field instanceof org.eclipse.smila.search.utils.search.DTextField) {
      final org.eclipse.smila.search.utils.search.DTextField tf =
        (org.eclipse.smila.search.utils.search.DTextField) field;

      // assign named config parameters
      if (nc != null) {
        final ITFParameter tfp =
          ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DTextField) nc).getParameter();
        insertTextParameter(tf, tfp, "NC", sb);
      }

      // assign default config parameters
      final ITFParameter tfp =
        ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DTextField) defaultfc).getParameter();
      insertTextParameter(tf, tfp, "DC", sb);

      if (tf.getParameter() != null) {
        tf.setParameter((ITFParameter) tf.getParameter().clone());
      }
    } else if (field instanceof org.eclipse.smila.search.utils.search.DNumberField) {
      final org.eclipse.smila.search.utils.search.DNumberField nf =
        (org.eclipse.smila.search.utils.search.DNumberField) field;

      // assign named config parameters
      if (nc != null) {
        final INFParameter nfp =
          ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DNumberField) nc).getParameter();
        insertNumberParameter(nf, nfp, "NC", sb);
      }

      // assign default config parameters
      final INFParameter nfp =
        ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DNumberField) defaultfc).getParameter();
      insertNumberParameter(nf, nfp, "DC", sb);

      if (nf.getParameter() != null) {
        nf.setParameter((INFParameter) nf.getParameter().clone());
      }
    } else if (field instanceof org.eclipse.smila.search.utils.search.DDateField) {
      final org.eclipse.smila.search.utils.search.DDateField df =
        (org.eclipse.smila.search.utils.search.DDateField) field;

      // assign named config parameters
      if (nc != null) {
        final IDFParameter dfp =
          ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DDateField) nc).getParameter();
        insertDateParameter(df, dfp, "NC", sb);
      }

      // assign default config parameters
      final IDFParameter dfp =
        ((org.eclipse.smila.search.datadictionary.messages.ddconfig.DDateField) defaultfc).getParameter();
      insertDateParameter(df, dfp, "DC", sb);

      if (df.getParameter() != null) {
        df.setParameter((IDFParameter) df.getParameter().clone());
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(sb.toString());
    }

  }

  /**
   * Unlocks a locked index.
   * 
   * @throws IndexException
   *           if any error occurs
   */
  public abstract void unlock() throws IndexException;

}
