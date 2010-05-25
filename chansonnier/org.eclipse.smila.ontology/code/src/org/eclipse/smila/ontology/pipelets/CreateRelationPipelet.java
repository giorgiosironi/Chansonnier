/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology.pipelets;

import java.util.List;

import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * pipelet for creating relations between attribute values in ontology.
 *
 * @author jschumacher
 *
 */
public class CreateRelationPipelet extends ASesameRecordPipelet {

  /**
   * configuration property/parameter name for specifying the attribute name of the relation subjects:
   * "subjectAttribute".
   */
  public static final String PARAM_SUBJECTATTRIBUTE = "subjectAttribute";

  /**
   * configuration property/parameter name for specifying the attribute name of the relation objects: "objectAttribute".
   */
  public static final String PARAM_OBJECTATTRIBUTE = "objectAttribute";

  /**
   * configuration property/parameter name for specifying the URI of the relation predicate: "predicateUri".
   */
  public static final String PARAM_PREDICATEURI = "predicateUri";

  /**
   * local logger.
   */
  // private final Log _log = LogFactory.getLog(getClass());
  /**
   * {@inheritDoc}
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    if (recordIds != null && recordIds.length > 0) {
      try {
        final ParameterAccessor parameters = getParameters(blackboard);
        for (final Id id : recordIds) {
          parameters.setCurrentRecord(id);
          final RepositoryConnection connection = getRepositoryConnection(parameters);
          try {
            final String subjectAttribute = parameters.getRequiredParameter(PARAM_SUBJECTATTRIBUTE);
            final Path subjectPath = new Path().add(subjectAttribute);
            final String objectAttribute = parameters.getRequiredParameter(PARAM_OBJECTATTRIBUTE);
            final Path objectPath = new Path().add(objectAttribute);
            final String predicateUriString = parameters.getRequiredParameter(PARAM_PREDICATEURI);
            final URI predicate = createUri(connection, predicateUriString);
            if (blackboard.getLiteralsSize(id, subjectPath) > 0) {
              final List<Literal> subjects = blackboard.getLiterals(id, subjectPath);
              final List<Literal> objects = blackboard.getLiterals(id, objectPath);
              for (final Literal subjectLit : subjects) {
                final URI subject = createUri(connection, subjectLit.getStringValue());
                for (final Literal objectLit : objects) {
                  final Value object = _valueHelper.createValue(connection, objectLit);
                  connection.add(subject, predicate, object);
                }
              }
            }
            connection.commit();
          } catch (final BlackboardAccessException ex) {
            throw new ProcessingException("error accessing blackboard", ex);
          } finally {
            connection.close();
          }
        }
      } catch (final RepositoryException ex) {
        throw new ProcessingException("could not get connection to sesame repository", ex);
      }
    }
    return recordIds;
  }

}
