/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.bpel;

import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;

/**
 * combined performance counters for pipeline measurement.
 * 
 * @author jschumacher
 * 
 */
public class PipelinePerformanceCounter extends ProcessingPerformanceCounter {

  /**
   * create new performance counters for given pipeline.
   * 
   * @param pipelineName
   *          name of a pipeline.
   */
  public PipelinePerformanceCounter(final String pipelineName) {
    super(pipelineName);
    registerAgent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ProcessingPerformanceCounter#getElementType()
   */
  @Override
  protected String getElementType() {
    return "Pipeline";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ManagementAgentLocation getLocation() {
    return ManagementRegistration.INSTANCE//
      .getCategory("Processing")//
      .getCategory(getElementType())//
      .getLocation(getElementName());
  }

}
