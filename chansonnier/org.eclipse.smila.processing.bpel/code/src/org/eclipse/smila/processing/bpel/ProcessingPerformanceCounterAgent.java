/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.processing.bpel;

import org.eclipse.smila.management.LocatedManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.MeasureUnit;
import org.eclipse.smila.management.error.ErrorsBuffer;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class ProcessingPerformanceCounterAgent.
 */
public class ProcessingPerformanceCounterAgent implements LocatedManagementAgent {

  /**
   * counter for number of invocations of a pipeline.
   */
  private final PerformanceCounter _invocations =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * counter for number of failures while in a pipeline.
   */
  private final PerformanceCounter _failures =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * counter for average execution time of a pipeline.
   */
  private final PerformanceCounter _averageExecutionTime =
    new PerformanceCounter(PerformanceCounterFormula.AVERAGE_FORMULA);

  /**
   * counter for number of incoming records in a pipeline.
   */
  private final PerformanceCounter _incomingRecords =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * counter for number of incoming records in a pipeline.
   */
  private final PerformanceCounter _outgoingRecords =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /**
   * The _location.
   */
  private ManagementAgentLocation _location;

  /**
   * error buffer for this agent.
   */
  private final ErrorsBuffer _errorsBuffer = new ErrorsBuffer();

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.management.LocatedManagementAgent#getLocation()
   */
  public ManagementAgentLocation getLocation() {
    return _location;
  }

  /**
   * Sets the location.
   *
   * @param location
   *          the new location
   */
  public void setLocation(final ManagementAgentLocation location) {
    _location = location;
  }

  /**
   * Gets the invocations.
   *
   * @return the invocations
   */
  public PerformanceCounter getInvocations() {
    return _invocations;
  }

  /**
   * Gets the failures.
   *
   * @return the failures
   */
  public PerformanceCounter getFailures() {
    return _failures;
  }

  /**
   * Gets the average execution time.
   *
   * @return the average execution time
   */
  @MeasureUnit("ms")
  public PerformanceCounter getAverageExecutionTime() {
    return _averageExecutionTime;
  }

  /**
   * Gets the errors buffer.
   *
   * @return the errors buffer
   */
  public ErrorsBuffer getErrorsBuffer() {
    return _errorsBuffer;
  }

  /**
   * Get the number of incoming records.
   * 
   * @return the number of incoming records
   */
  public PerformanceCounter getIncomingRecords() {
    return _incomingRecords;
  }

  /**
   * Get the number of outgoing records.
   * 
   * @return the number of outgoing records
   */
  public PerformanceCounter getOutgoingRecords() {
    return _outgoingRecords;
  }
}
