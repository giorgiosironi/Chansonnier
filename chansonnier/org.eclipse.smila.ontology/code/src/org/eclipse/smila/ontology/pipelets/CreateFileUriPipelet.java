/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.pipelets;

import java.io.File;
import java.net.URI;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.ontology.records.SesameRecordHelper;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * write filename in ID key as file:/ URI to rdf:about attribute. Works useful only if the ID key is an absolute path
 * for the same platform as the pipelet is running on.
 *
 * @author jschumacher
 *
 */
public class CreateFileUriPipelet implements SimplePipelet {
  /**
   * local logger.
   */
  // private final Log _log = LogFactory.getLog(getClass());
  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final Blackboard blackboard, final Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (final Id id : recordIds) {
        final String filename = id.getKey().getKey().replace('\\', '/');
        try {
          final File file = new File(filename);
          final URI fileUri = file.toURI();
          final Literal uriLiteral = blackboard.createLiteral(id);
          uriLiteral.setStringValue(fileUri.toString());
          uriLiteral.setSemanticType(SesameRecordHelper.SEMTYPE_RESOURCE);
          blackboard.setLiteral(id, SesameRecordHelper.PATH_URI, uriLiteral);
        } catch (final Exception ex) {
          throw new ProcessingException("error creating file URI from key " + filename, ex);
        }

      }
    }
    return recordIds;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.IPipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    ; // not needed.
  }

}
