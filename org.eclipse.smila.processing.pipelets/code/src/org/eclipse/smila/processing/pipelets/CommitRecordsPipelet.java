/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.pipelets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * Pipelet that commits all records on the blackboard. No configuration is needed. Commit errors are logged, but no
 * exception is thrown.
 * 
 * @author jschumacher
 * 
 */
public class CommitRecordsPipelet implements SimplePipelet {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(CommitRecordsPipelet.class);

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(PipeletConfiguration configuration) throws ProcessingException {
    // nothing to do (for now)
  }

  /**
   * set configured annotation on each record on blackboard as specified by the recordIds. {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.SimplePipelet#process(org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException {
    if (recordIds != null) {
      for (Id id : recordIds) {
        if (id != null) {
          try {
            blackboard.commit(id);
          } catch (BlackboardAccessException ex) {
            _log.error("Error committing record " + id, ex);
          }
        }
      }
    }
    return recordIds;
  }

}
