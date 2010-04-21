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

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;

/**
 * Interface of SimplePipelets. SimplePipelets are not standalone services, but their lifecycle and configuration is
 * managed by the workflow engine. They are not shared by multiple workflows, each occurrence of a pipelet in a workflow
 * uses a different pipelet instance.
 * 
 * SimplePipelets must have a public no-argument constructor.
 * 
 * The pipelet class name must be registered in META-INF/MANIFEST.MF of the providing bundle using the header name
 * "SMILA-Pipelets". Then they can be detected by the {@link PipeletTracker} service.
 * 
 * <pre>
 * SMILA-Pipelets: org.eclipse.smila.processing.bpel.pipelets.SimpleTestPipelet
 * </pre>
 * 
 * Multiple classes can be registered separeted by comma:
 * 
 * <pre>
 * SMILA-Pipelets: org.eclipse.smila.processing.test.Test1Pipelet,org.eclipse.smila.processing.test.Test2Pipelet
 * </pre>
 * 
 * @author jschumacher
 * 
 */
public interface SimplePipelet extends IPipelet {

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
