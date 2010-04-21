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
package org.eclipse.smila.ontology.pipelets;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.eclipse.smila.ontology.records.SesameRecordWriter;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.openrdf.repository.RepositoryConnection;

/**
 * write records to ontology.
 *
 * @author jschumacher
 *
 */
public class SesameRecordWriterPipelet extends ASesameRecordPipelet {

  /**
   * configuration property name for default types of resources to create if not set in attribute "rdf:type".
   */
  public static final String PARAM_TYPEURI = "typeUri";

  /**
   * local logger.
   */
  // private final Log _log = LogFactory.getLog(getClass());

  /**
   * write the records from the blackboard to a Sesame ontology.
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
          final String recordFilterName = parameters.getParameter(ASesameRecordPipelet.PARAM_RECORDFILTER, null);
          final String typeUri = parameters.getParameter(PARAM_TYPEURI, null);
          final SesameRecordWriter writer = new SesameRecordWriter(repoConn);
          if (recordFilterName == null) {
            writer.writeBlackboardRecord(blackboard, id, typeUri);
          } else {
            final Record record = blackboard.getRecord(id, recordFilterName);
            final Annotation annotation = blackboard.getAnnotation(id, null, SesameRecordHelper.ANNOTATION_MODE);
            if (annotation != null) {
              record.getMetadata().setAnnotation(SesameRecordHelper.ANNOTATION_MODE, annotation);
            }
            writer.writeRecord(record, typeUri);
          }
          repoConn.close();
        } catch (final Exception ex) {
          throw new ProcessingException("error writing record to ontology", ex);
        }
      }
    }
    return recordIds;
  }

}
