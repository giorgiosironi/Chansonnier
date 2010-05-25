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
 * Interface of search pipelets that can process SearchMessages instead of ProcessorMessages. A pipelet that can process
 * both kinds of messages should also implement {@link SimplePipelet}.
 * 
 * @author jschumacher
 * 
 */
public interface SearchPipelet extends IPipelet {
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
