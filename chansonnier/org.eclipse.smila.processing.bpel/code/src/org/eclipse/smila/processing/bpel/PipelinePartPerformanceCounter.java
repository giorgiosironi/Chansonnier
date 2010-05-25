/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.processing.bpel;

import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;

/**
 * The Class PipelinePartPerformanceCounter.
 */
public abstract class PipelinePartPerformanceCounter extends ProcessingPerformanceCounter {

  /**
   * name of containing pipeline.
   */
  private final String _pipelineName;

  /**
   * Instantiates a new pipeline part performance counter.
   * 
   * @param pipelineName
   *          the pipeline name
   * @param elementName
   *          the element name
   */
  public PipelinePartPerformanceCounter(final String pipelineName, final String elementName) {
    super(elementName);
    _pipelineName = pipelineName;
  }

  /**
   * Gets the pipeline name.
   * 
   * @return the pipeline name
   */
  protected String getPipelineName() {
    return _pipelineName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ProcessingPerformanceCounter#getLocation()
   */
  @Override
  public ManagementAgentLocation getLocation() {
    return ManagementRegistration.INSTANCE//
      .getCategory("Processing")//
      .getCategory(getElementType())//
      .getCategory(getPipelineName())//
      .getLocation(getElementName());
  }

}
