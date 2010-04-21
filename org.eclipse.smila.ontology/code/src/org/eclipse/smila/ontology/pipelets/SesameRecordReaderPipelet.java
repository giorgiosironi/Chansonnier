/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.pipelets;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.eclipse.smila.ontology.records.SesameRecordReader;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

/**
 * fill records from ontology.
 *
 * @author jschumacher
 *
 */
public class SesameRecordReaderPipelet extends ASesameRecordPipelet {
  /**
   * configuration property name for including inferred statements when reading the ontology.
   */
  public static final String PARAM_INCLUDEINFERRED = "includeInferred";

  /**
   * default value for "includeInferred": false.
   */
  public static final boolean DEFAULT_INCLUDEINFERRED = false;

  /**
   * local logger.
   */
  // private final Log _log = LogFactory.getLog(getClass());
  /**
   * read statements from the ontology into the given records.
   *
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      final ParameterAccessor parameters = getParameters(blackboard);
      for (final Id id : recordIds) {
        try {
          parameters.setCurrentRecord(id);
          final RepositoryConnection repoConn = getRepositoryConnection(parameters);
          final boolean includeInferred =
            parameters.getBooleanParameter(PARAM_INCLUDEINFERRED, DEFAULT_INCLUDEINFERRED);
          String resourceUri = id.getKey().getKey();
          final Literal uriLiteral = blackboard.getLiteral(id, SesameRecordHelper.PATH_URI);
          if (uriLiteral != null) {
            resourceUri = uriLiteral.getStringValue();
          }
          final URI uri = repoConn.getValueFactory().createURI(resourceUri);
          final SesameRecordReader reader = new SesameRecordReader(repoConn, includeInferred);
          final String recordFilterName = parameters.getParameter(PARAM_RECORDFILTER, null);
          if (recordFilterName == null) {
            reader.readBlackboardRecord(uri, blackboard, id);
          } else {
            Record ontoRecord = reader.readRecord(uri);
            ontoRecord.setId(id);
            ontoRecord = blackboard.filterRecord(ontoRecord, recordFilterName);
            blackboard.synchronize(ontoRecord);
          }
          repoConn.close();
        } catch (final Exception ex) {
          throw new ProcessingException("error reading record from ontology", ex);
        }
      }
    }
    return recordIds;
  }

}
