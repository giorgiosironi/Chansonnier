/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.records;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * reads a record from a sesame ontology.
 * 
 * @author jschumacher
 * 
 */
public class SesameRecordReader {
  /**
   * my repository connection.
   */
  private RepositoryConnection _repoConn;

  /**
   * include inferred statements?
   */
  private boolean _includeInferred;
  /**
   * map of namespaces to prefixes for abbreviation of attribute names.
   */
  private Map<String, String> _namespaces;

  /**
   * create instance for connection.
   *
   * @param repoConn
   *          a Sesame repository connection.
   * @param includeInferred
   *          include inferred statements in reading
   */
  public SesameRecordReader(final RepositoryConnection repoConn, final boolean includeInferred) {
    super();
    _repoConn = repoConn;
    _includeInferred = includeInferred;
    initNamespaces();
  }

  /**
   * read namespaces and prefixes to use in abbreviation of attribute names.
   */
  private void initNamespaces() {
    _namespaces = new HashMap<String, String>();
    try {
      final RepositoryResult<Namespace> repoNamespaces = _repoConn.getNamespaces();
      while (repoNamespaces.hasNext()) {
        final Namespace repoNamespace = repoNamespaces.next();
        final String repoPrefix = repoNamespace.getPrefix();
        final String currentPrefix = _namespaces.get(repoNamespace.getName());
        if (currentPrefix == null || repoPrefix.length() < currentPrefix.length()) {
          _namespaces.put(repoNamespace.getName(), repoPrefix);
        }
      }
    } catch (RepositoryException ex) {
      ex = null; // ignore. we will not use namespaces.
    }
  }

  /**
   * read statements of given URI in repository into a new record on blackboard. Its ID will have
   * {@link SesameRecordHelper#SESAME_SOURCE} as source and the URI as key.
   *
   * @param uri
   *          resource URI
   * @param blackboard
   *          target blackboard
   * @return record ID
   * @throws BlackboardAccessException
   *           error writing to blackboard.
   * @throws RepositoryException
   *           error reading repository.
   */
  public Id readBlackboardRecord(final URI uri, final Blackboard blackboard)
    throws BlackboardAccessException, RepositoryException {
    final Id id = createId(uri);
    blackboard.create(id);
    readBlackboardRecord(uri, blackboard, id);
    return id;
  }

  /**
   * read statements of given URI in repository into an existing record on blackboard. The URI will be set as attribute
   * {@link SesameRecordHelper#ATTRIBUTE_URI}
   *
   * @param uri
   *          resource URI
   * @param blackboard
   *          target blackboard
   * @param id
   *          target record ID
   * @throws BlackboardAccessException
   *           error writing to blackboard.
   * @throws RepositoryException
   *           error reading repository.
   */
  public void readBlackboardRecord(final URI uri, final Blackboard blackboard, final Id id)
    throws BlackboardAccessException, RepositoryException {
    final Literal uriLiteral = blackboard.createLiteral(id);
    uriLiteral.setStringValue(uri.stringValue());
    uriLiteral.setSemanticType(SesameRecordHelper.SEMTYPE_RESOURCE);
    blackboard.setLiteral(id, SesameRecordHelper.PATH_URI, uriLiteral);
    final RepositoryResult<Statement> statements = _repoConn.getStatements(uri, null, null, _includeInferred);
    if (statements != null) {
      final Map<String, Path> attributePaths = new HashMap<String, Path>();
      while (statements.hasNext()) {
        final Statement statement = statements.next();
        final String attributeName = uriToString(statement.getPredicate());
        final Literal valueLiteral = blackboard.createLiteral(id);
        setLiteralValueFromObject(valueLiteral, statement);
        Path path = attributePaths.get(attributeName);
        if (path == null) {
          path = new Path();
          path.add(attributeName);
          attributePaths.put(attributeName, path);
          blackboard.removeLiterals(id, path);
        }
        blackboard.addLiteral(id, path, valueLiteral);
      }
    }
  }

  /**
   * read statements of given URI in repository into a new record. Its ID will have
   * {@link SesameRecordHelper#SESAME_SOURCE} as source and the URI as key.
   *
   * @param uri
   *          resource URI
   * @return record ID
   * @throws RepositoryException
   *           error reading repository.
   */
  public Record readRecord(final URI uri) throws RepositoryException {
    final Id id = createId(uri);
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);
    readRecord(uri, record);
    return record;
  }

  /**
   * read statements of given URI in repository into an existing record. The URI will be set as attribute
   * {@link SesameRecordHelper#ATTRIBUTE_URI}
   *
   * @param uri
   *          resource URI
   * @param record
   *          target record
   * @throws RepositoryException
   *           error reading repository.
   */
  public void readRecord(final URI uri, final Record record) throws RepositoryException {
    final Literal uriLiteral = record.getFactory().createLiteral();
    uriLiteral.setStringValue(uri.stringValue());
    uriLiteral.setSemanticType(SesameRecordHelper.SEMTYPE_RESOURCE);
    setAttributeLiteral(record, SesameRecordHelper.ATTRIBUTE_URI, uriLiteral);
    final RepositoryResult<Statement> statements = _repoConn.getStatements(uri, null, null, _includeInferred);
    if (statements != null) {
      final Set<String> attributes = new HashSet<String>();
      while (statements.hasNext()) {
        final Statement statement = statements.next();
        final String attributeName = uriToString(statement.getPredicate());
        final Literal valueLiteral = record.getFactory().createLiteral();
        setLiteralValueFromObject(valueLiteral, statement);
        if (attributes.add(attributeName)) {
          setAttributeLiteral(record, attributeName, valueLiteral);
        } else {
          addAttributeLiteral(record, attributeName, valueLiteral);
        }
      }
    }
  }

  /**
   * @param literal
   *          SMILA literal to fill from statement object.
   * @param statement
   *          statement
   */
  private void setLiteralValueFromObject(final Literal literal, final Statement statement) {
    final Value value = statement.getObject();
    if (value instanceof Resource) {
      literal.setStringValue(((Resource) value).stringValue());
      literal.setSemanticType(SesameRecordHelper.SEMTYPE_RESOURCE);
    } else {
      if (value instanceof org.openrdf.model.Literal) {
        final org.openrdf.model.Literal ontoLiteral = (org.openrdf.model.Literal) value;
        SesameRecordHelper.setLanguage(literal, ontoLiteral.getLanguage());
        final URI datatype = ontoLiteral.getDatatype();
        if (datatype != null) {
          if (XMLDatatypeUtil.isIntegerDatatype(datatype)) {
            literal.setIntValue(ontoLiteral.longValue());
          } else if (XMLDatatypeUtil.isFloatingPointDatatype(datatype)) {
            literal.setFpValue(ontoLiteral.doubleValue());
          } else if (XMLSchema.BOOLEAN.equals(datatype)) {
            literal.setBoolValue(ontoLiteral.booleanValue());
          } else if (XMLDatatypeUtil.isCalendarDatatype(datatype)) {
            final XMLGregorianCalendar ontoCal = ontoLiteral.calendarValue();
            if (XMLSchema.DATETIME.equals(datatype)) {
              final Calendar cal = ontoCal.toGregorianCalendar();
              literal.setDateTimeValue(cal.getTime());
            } else if (XMLSchema.TIME.equals(datatype)) {
              final Calendar cal = ontoCal.toGregorianCalendar();
              literal.setTimeValue(cal.getTime());
            } else {
              // TODO: Support timezone here? I think one should use date/times if this should be handled
              // timezone specific. A date is the same on the whole planet, so always write it as a UTC literal.
              // So remove timezone offset here.
              final Calendar cal = getDateOnly(ontoCal);
              literal.setDateValue(cal.getTime());
            }
          }
        }
      }
      if (literal.getValue() == null) {
        literal.setStringValue(value.stringValue());
      }
    }
  }

  /**
   * get only the date (year, month, day) part of the calendar literal, don't calculate with timezones.
   *
   * @param ontoCal
   *          a ontology calendar literal value.
   * @return a Calendar with only the date part.
   */
  private Calendar getDateOnly(final XMLGregorianCalendar ontoCal) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(0);
    if (ontoCal.getYear() != DatatypeConstants.FIELD_UNDEFINED) {
      cal.set(Calendar.YEAR, ontoCal.getYear());
    }
    if (ontoCal.getMonth() != DatatypeConstants.FIELD_UNDEFINED) {
      cal.set(Calendar.MONTH, ontoCal.getMonth() - 1);
    }
    if (ontoCal.getDay() != DatatypeConstants.FIELD_UNDEFINED) {
      cal.set(Calendar.DAY_OF_MONTH, ontoCal.getDay());
    }
    return cal;
  }

  /**
   * create attribute in record and set literal as its value.
   *
   * @param record
   *          record
   * @param attributeName
   *          attribute name
   * @param literal
   *          literal
   */
  private void setAttributeLiteral(final Record record, final String attributeName, final Literal literal) {
    final Attribute attribute = record.getFactory().createAttribute();
    attribute.setName(attributeName);
    record.getMetadata().setAttribute(attributeName, attribute);
    attribute.addLiteral(literal);
  }

  /**
   * ensure that attribute exists in record and add literal to it.
   *
   * @param record
   *          record
   * @param attributeName
   *          attribute name
   * @param literal
   *          literal
   */
  private void addAttributeLiteral(final Record record, final String attributeName, final Literal literal) {
    if (record.getMetadata().hasAttribute(attributeName)) {
      final Attribute attribute = record.getMetadata().getAttribute(attributeName);
      attribute.addLiteral(literal);
    } else {
      setAttributeLiteral(record, attributeName, literal);
    }
  }

  /**
   * create SMILA ID from URI.
   *
   * @param uri
   *          uri
   * @return SMILA ID
   */
  private Id createId(final URI uri) {
    return IdFactory.DEFAULT_INSTANCE.createId(SesameRecordHelper.SESAME_SOURCE, uri.stringValue());
  }

  /**
   * create string value from URI and replace known namespaces by their prefix.
   *
   * @param uri
   *          Sesame URI
   * @return string value.
   */
  private String uriToString(final URI uri) {
    final String prefix = _namespaces.get(uri.getNamespace());
    if (prefix != null) {
      return prefix + ":" + uri.getLocalName();
    }
    return uri.stringValue();
  }
}
