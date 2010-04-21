/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.records;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * writes a record to a sesame ontology. Implementation is not thread safe!
 *
 * @author jschumacher
 *
 */
public class SesameRecordWriter {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * my repository connection.
   */
  private RepositoryConnection _repoConn;

  /**
   * statements to add.
   */
  private Collection<Statement> _addStmts;

  /**
   * statements to delete.
   */
  private Collection<Statement> _clearStmts;

  /**
   * value creation helper.
   */
  private SesameValueHelper _valueHelper = SesameValueHelper.INSTANCE;

  /**
   * create instance for given connection.
   *
   * @param repoConn
   *          Sesame repository connection to write to.
   */
  public SesameRecordWriter(final RepositoryConnection repoConn) {
    super();
    this._repoConn = repoConn;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.ontology.records.SesameRecordWriter
   *      #writeBlackboardRecord(org.eclipse.smila.blackboard.Blackboard, org.eclipse.smila.datamodel.id.Id,
   *      org.openrdf.repository.RepositoryConnection)
   */
  public URI writeBlackboardRecord(final Blackboard blackboard, final Id id, final String defaultTypeUri)
    throws BlackboardAccessException, RepositoryException {
    final Record record = blackboard.getRecord(id);
    return writeRecord(record, defaultTypeUri);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.ontology.records.SesameRecordWriter #writeRecord(org.eclipse.smila.datamodel.record.Record,
   *      org.openrdf.repository.RepositoryConnection)
   */
  public URI writeRecord(final Record record, final String defaultTypeUri) throws RepositoryException {
    boolean commit = false;
    try {
      _repoConn.setAutoCommit(false);
      final URI uri = findUri(record);
      final MObject metadata = record.getMetadata();
      if (metadata.hasAnnotation(SesameRecordHelper.ANNOTATION_MODE)) {
        final Annotation annotation = SesameRecordHelper.getModeAnnotation(metadata);
        if (annotation.getNamedValue(SesameRecordHelper.ANNOVALUENAME_CLEAR) != null) {
          clearResource(uri);
          commit = true;
          return uri;
        }
      }
      _addStmts = new ArrayList<Statement>();
      _clearStmts = new ArrayList<Statement>();
      final Iterator<String> attributeNames = metadata.getAttributeNames();
      while (attributeNames.hasNext()) {
        final String attributeName = attributeNames.next();
        if (!SesameRecordHelper.ATTRIBUTE_URI.equals(attributeName)) {
          createStatements(uri, metadata, attributeName, defaultTypeUri);
        }
      }
      if (!_addStmts.isEmpty()) {
        for (final Statement stmt : _clearStmts) {
          _repoConn.remove(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
        }
        for (final Statement stmt : _addStmts) {
          try {
            _log.debug("adding stmt: " + stmt);
            _repoConn.add(stmt);
          } catch (final Exception ex) {
            _log.error("failed to add a stmt", ex);
          }
        }
        commit = true;
      }
      return uri;
    } finally {
      _addStmts = null;
      _clearStmts = null;
      if (commit) {
        _repoConn.commit();
      } else {
        _repoConn.rollback();
      }
    }
  }

  /**
   * remove an object completely.
   *
   * @param uri
   *          object uri
   * @throws RepositoryException
   *           error removing statements.
   */
  private void clearResource(final URI uri) throws RepositoryException {
    _repoConn.remove((URI) null, (URI) null, uri);
    _repoConn.remove(uri, (URI) null, null);
  }

  /**
   * @param uri
   *          object URI
   * @param metadata
   *          metadata object
   * @param attributeName
   *          attribute name
   * @param defaultTypeUri
   *          optional default type to use if rdf:type attribute is not set.
   */
  private void createStatements(final URI uri, final MObject metadata, final String attributeName,
    final String defaultTypeUri) {
    final Attribute attribute = metadata.getAttribute(attributeName);
    final URI predicate = _valueHelper.createUri(_repoConn, attributeName);
    Annotation annotation = null;
    boolean isReverse = false;
    boolean haveTypeStatement = false;
    if (attribute.hasAnnotation(SesameRecordHelper.ANNOTATION_MODE)) {
      annotation = SesameRecordHelper.getModeAnnotation(attribute);
      if (annotation.hasAnonValues()) {
        isReverse = annotation.getAnonValues().contains(SesameRecordHelper.ANNOVALUE_REVERSE);
      }
    }
    // check if existing values should be removed.
    addClearStatement(uri, predicate, annotation, isReverse);
    if (attribute.hasLiterals()) {
      final Collection<Literal> literals = attribute.getLiterals();
      int position = 0;
      for (final Literal literal : literals) {
        Value value = null;
        if (RDF.TYPE.equals(predicate)) {
          value = _valueHelper.createUri(_repoConn, literal);
          haveTypeStatement = true;
        } else {
          value = _valueHelper.createValue(_repoConn, literal);
        }
        if (isReverse && value instanceof URI) {
          _addStmts.add(_repoConn.getValueFactory().createStatement((URI) value, predicate, uri));
        } else {
          _addStmts.add(_repoConn.getValueFactory().createStatement(uri, predicate, value));
        }
        position++;
      }
    }
    if (!haveTypeStatement && defaultTypeUri != null) {
      final URI defaultType = _valueHelper.createUri(_repoConn, defaultTypeUri);
      _addStmts.add(_repoConn.getValueFactory().createStatement(uri, RDF.TYPE, defaultType));
    }
  }

  /**
   * add a clear-statement for the given uri, predicate and drection.
   *
   * @param uri
   *          object uri
   * @param predicate
   *          predicate to clear
   * @param annotation
   *          annotation possibly containing a clear command.
   * @param isReverse
   *          if true, delete statements with uri as <em>object</em>.
   */
  private void addClearStatement(final Resource uri, final URI predicate, final Annotation annotation,
    final boolean isReverse) {
    if (annotation != null) {
      final String clearLocale = annotation.getNamedValue(SesameRecordHelper.ANNOVALUENAME_CLEAR);
      if (clearLocale != null) {
        // TODO: language specific clear - possible with sesame?
        if (isReverse) {
          _clearStmts.add(_repoConn.getValueFactory().createStatement((URI) null, predicate, uri));
        } else {
          _clearStmts.add(_repoConn.getValueFactory().createStatement(uri, predicate, null));
        }
      }
    }
  }

  /**
   * find resource URI in record ID or attributes.
   *
   * @param record
   *          record
   * @return URI for record
   * @throws RepositoryException
   *           no URI found in record.
   */
  private URI findUri(final Record record) throws RepositoryException {
    URI uri = null;
    if (record.getMetadata().hasAttribute(SesameRecordHelper.ATTRIBUTE_URI)) {
      final Literal uriLiteral = record.getMetadata().getAttribute(SesameRecordHelper.ATTRIBUTE_URI).getLiteral();
      if (uriLiteral != null) {
        uri = _valueHelper.createUri(_repoConn, uriLiteral);
      }
    } else {
      final Id id = record.getId();
      uri = _valueHelper.createUri(_repoConn, id.getKey().getKey());
    }
    if (uri == null) {
      throw new RepositoryException("did not find a resource URI for record.");
    }
    return uri;
  }
}
