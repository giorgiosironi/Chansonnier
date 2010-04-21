/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.bpel;

/**
 * combined performance counters for Simple Pipelet measurement. Counters will appear in JMX under "SMILA processing" ->
 * "Processing Service" -> pipeline name -> service name@location. The location could be line number in the BPEL file.
 * 
 * @author jschumacher
 * 
 */
public class ServicePerformanceCounter extends PipelinePartPerformanceCounter {

  /**
   * @param pipelineName
   *          pipeline name
   * @param serviceName
   *          name of service
   * @param location
   *          location descriptor, e.g. line in BPEL file.
   */
  public ServicePerformanceCounter(final String pipelineName, final String serviceName, final String location) {
    super(pipelineName, serviceName + "@" + location);
    registerAgent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ProcessingPerformanceCounter#getElementType()
   */
  @Override
  protected String getElementType() {
    return "Processing Service";
  }
}
