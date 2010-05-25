/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing;

import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;

/**
 * Interface of WorkflowProcessors. A workflow orchestrates a set of SMILA pipelets (or other components) to process
 * SMILA records. It may use deliberate workflow definition languages, e.g. BPEL.
 * 
 * @author jschumacher
 * 
 */
public interface WorkflowProcessor {

  /**
   * namespace of SMILA BPEL process names.
   */
  String NAMESPACE_PROCESSOR = "http://www.eclipse.org/smila/processor";

  /**
   * process records on Blackboard service.
   * 
   * @param workflowName
   *          name of workflow to execute on Ids
   * @param blackboard
   *          blackboard to use for processing
   * @param recordIds
   *          Ids of records to process.
   * @return Ids of result records.
   * @throws ProcessingException
   *           error during processing.
   */
  Id[] process(String workflowName, Blackboard blackboard, Id[] recordIds) throws ProcessingException;

  /**
   * process a search request on Blackboard service.
   * 
   * @param workflowName
   *          name of workflow to execute on message.
   * @param blackboard
   *          blackboard to use for processing
   * @param query
   *          Id of query record. The record Id list in this will be null, usually.
   * @return Ids of effective query and result records.
   * @throws ProcessingException
   *           error during processing.
   */
  SearchMessage process(String workflowName, Blackboard blackboard, SearchMessage query)
    throws ProcessingException;

  /**
   * get the names of the currently active workflows.
   * 
   * @return names of workflows.
   */
  List<String> getWorkflowNames();

}
