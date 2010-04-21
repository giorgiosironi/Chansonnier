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

package org.eclipse.smila.processing;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;

/**
 * Interface of SMILA ProcessingServices (aka "Big Pipelets"). In contrast to "SimplePipelets" instances of
 * ProcessingServices are not instantiated and configured by the Workflow engine, but are started as OSGi services
 * (preferably by Declarative Services) independently from the workflow engine and can read their configuration from
 * wherever they want. Their service registration must have a property named "smila.processing.service.name" that
 * specifies the name with which the service is references by the workflow engine.
 * 
 * @author jschumacher
 * 
 */
public interface ProcessingService {

  /**
   * Property of OSGi services specifying the name of the service. The workflow engine needs this name to determine
   * which service to invoke from the workflow.
   */
  String PROPERTY_NAME = "smila.processing.service.name";

  /**
   * process records on Blackboard service.
   * 
   * @param blackboard
   *          Blackboard service managing the records.
   * @param recordIds
   *          Ids of records to process.
   * @return Ids of result records.
   * @throws ProcessingException
   *           error during processing.
   */
  Id[] process(Blackboard blackboard, Id[] recordIds) throws ProcessingException;

}
