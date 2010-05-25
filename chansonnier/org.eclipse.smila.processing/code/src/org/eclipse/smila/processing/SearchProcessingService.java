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

package org.eclipse.smila.processing;

import org.eclipse.smila.blackboard.Blackboard;

/**
 * Interface of search services that can process SearchMessages instead of ProcessorMessages. A service that can process
 * both kinds of messages should implement also {@link ProcessingService}.
 * 
 * @author jschumacher
 * 
 */
public interface SearchProcessingService {

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
   * @param message
   *          Ids of query record and records to process.
   * @return Ids of query record and result records.
   * @throws ProcessingException
   *           error during processing.
   */
  SearchMessage process(Blackboard blackboard, SearchMessage message) throws ProcessingException;

}
