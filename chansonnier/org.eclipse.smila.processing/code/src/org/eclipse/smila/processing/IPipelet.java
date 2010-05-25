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

import org.eclipse.smila.processing.configuration.PipeletConfiguration;

/**
 * common base interface for SimplePipelets and SearchPipelets (and maybe more in future) to make common tracking
 * possible. For now it defines only the configurability of pipelets.
 * 
 * @author jschumacher
 * 
 */
public interface IPipelet {

  /**
   * set configuration of pipelet. called once after instantiation before the pipelet is actually used in a workflow.
   * 
   * @param configuration
   *          configuration of pipelet.
   * @throws ProcessingException
   *           configuration is not applicable for pipelet (missing properties, wrong datatypes)
   */
  void configure(PipeletConfiguration configuration) throws ProcessingException;

}
