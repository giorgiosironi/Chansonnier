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
 * Base class for performance counters used to measure processing performance. Manages three performance counters for
 * number of invocations, average execution time and errors.
 * 
 * @author jschumacher
 */
public abstract class ProcessingPerformanceCounter {

  /**
   * nanos in milliseconds constant.
   */
  private static final int NANOS_IN_MILLIS = 1000000;

  /**
   * name of measured pipeline.
   */
  private final String _elementName;

  /**
   * The _agent.
   */
  private final ProcessingPerformanceCounterAgent _agent;

  /**
   * create new performance counters for given element.
   * 
   * @param elementName
   *          name of a element to measure
   */
  public ProcessingPerformanceCounter(final String elementName) {
    _elementName = elementName;
    _agent = new ProcessingPerformanceCounterAgent();
  }

  /**
   * Register.
   */
  protected void registerAgent() {
    _agent.setLocation(getLocation());
    ManagementRegistration.INSTANCE.registerAgent(_agent);
  }

  /**
   * Gets the element type.
   * 
   * @return name of top level JMX category under "SMILA Processing"
   */
  protected abstract String getElementType();

  /**
   * Gets the element name.
   * 
   * @return name of measured elements.
   */
  protected String getElementName() {
    return _elementName;
  }

  /**
   * count a successful invocation.
   * 
   * @param executionTime
   *          time for execution in milliseconds
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countSuccessMillis(final long executionTime, final int incomingRecords, final int outgoingRecords) {
    _agent.getInvocations().increment();
    _agent.getAverageExecutionTime().incrementBy(executionTime);
    _agent.getIncomingRecords().incrementBy(incomingRecords);
    _agent.getOutgoingRecords().incrementBy(outgoingRecords);
  }

  /**
   * count a successful invocation in nanoseconds.
   * 
   * @param executionTime
   *          time for execution in nano seconds
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countSuccessNanos(final long executionTime, final int incomingRecords, final int outgoingRecords) {
    countSuccessMillis(executionTime / NANOS_IN_MILLIS, incomingRecords, outgoingRecords);
  }

  /**
   * Count a failed invocation.
   * 
   * @param executionTime
   *          time for execution in millis seconds
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countFailureMillis(final long executionTime, final int incomingRecords, final int outgoingRecords) {
    _agent.getInvocations().increment();
    _agent.getFailures().increment();
    _agent.getAverageExecutionTime().incrementBy(executionTime);
    _agent.getIncomingRecords().incrementBy(incomingRecords);
    _agent.getOutgoingRecords().incrementBy(outgoingRecords);
  }

  /**
   * Count a failed invocation.
   * 
   * @param executionTime
   *          time for execution in nano seconds
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countFailureNanos(final long executionTime, final int incomingRecords, final int outgoingRecords) {
    countFailureMillis(executionTime / NANOS_IN_MILLIS, incomingRecords, outgoingRecords);
  }

  /**
   * count an invocation.
   * 
   * @param executionTime
   *          time for execution in milli seconds
   * @param success
   *          true to count as success, false to count as failure
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countInvocationMillis(final long executionTime, final boolean success, final int incomingRecords,
    final int outgoingRecords) {
    if (success) {
      countSuccessMillis(executionTime, incomingRecords, outgoingRecords);
    } else {
      countFailureMillis(executionTime, incomingRecords, outgoingRecords);
    }
  }

  /**
   * count an invocation in nano seconds.
   * 
   * @param executionTime
   *          time for execution in nano seconds
   * @param success
   *          true to count as success, false to count as failure
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countInvocationNanos(final long executionTime, final boolean success, final int incomingRecords,
    final int outgoingRecords) {
    countInvocationMillis(executionTime / NANOS_IN_MILLIS, success, incomingRecords, outgoingRecords);
  }

  /**
   * Count incoming and outgoing records separately.
   * 
   * @param incomingRecords
   *          the number of incoming records
   * @param outgoingRecords
   *          the number of outgoing records
   */
  public void countIds(final int incomingRecords, final int outgoingRecords) {
    _agent.getIncomingRecords().incrementBy(incomingRecords);
    _agent.getOutgoingRecords().incrementBy(outgoingRecords);
  }

  /**
   * Adds the error.
   * 
   * @param ex
   *          the ex
   * @param isCritical
   *          the is critical
   */
  public void addError(final Throwable ex, final boolean isCritical) {
    _agent.getErrorsBuffer().addError(ex, isCritical);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("Performance of ").append(_elementName).append(": ");
    builder.append("total = ").append(_agent.getInvocations().getNextValue());
    builder.append(", average time = ").append(_agent.getAverageExecutionTime().getNextValue());
    builder.append("ms, failures = ").append(_agent.getFailures().getNextValue());
    builder.append(", incomingRecords = ").append(_agent.getIncomingRecords().getNextValue());
    builder.append(", outgoingRecords = ").append(_agent.getOutgoingRecords().getNextValue());
    return builder.toString();
  }

  /**
   * Gets the location.
   * 
   * @return the location
   */
  public abstract ManagementAgentLocation getLocation();
}
