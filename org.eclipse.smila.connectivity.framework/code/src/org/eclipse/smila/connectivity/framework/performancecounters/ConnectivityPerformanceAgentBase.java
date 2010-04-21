/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator, Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import org.eclipse.smila.management.Coefficient;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.MeasureUnit;
import org.eclipse.smila.management.error.ErrorsBuffer;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class ConnectivityPerformanceAgentBase.
 */
public abstract class ConnectivityPerformanceAgentBase implements ManagementAgent,
  CrawlerControllerPerformanceAgent {

  /** The Constant MILLISECS_IN_SEC. */
  protected static final double MILLISECS_IN_SEC = 1000;

  /** KBtyes. */
  protected static final double KBYTES = 1024;

  /** The _attachment bytes transfered. */
  private final PerformanceCounter _attachmentBytesTransfered =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /** The _attachment transfer rate. */
  private final PerformanceCounter _attachmentTransferRate =
    new PerformanceCounter(PerformanceCounterFormula.RATE_FORMULA);

  /** The _average records processing time counter. */
  private final PerformanceCounter _averageRecordsProcessingTime =
    new PerformanceCounter(PerformanceCounterFormula.AVERAGE_TIMER_FORMULA);

  /** The _average delta indices processing time. */
  private final PerformanceCounter _averageDeltaIndicesProcessingTime =
    new PerformanceCounter(PerformanceCounterFormula.AVERAGE_TIMER_FORMULA);

  /** The _records. */
  private final PerformanceCounter _records =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /** The _exceptions. */
  private final PerformanceCounter _exceptions =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /** The _exceptions critical. */
  private final PerformanceCounter _exceptionsCritical =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /** The _delta indices. */
  private final PerformanceCounter _deltaIndices =
    new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);

  /** The _error buffer. */
  private final ErrorsBuffer _errorBuffer = new ErrorsBuffer();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters.
   *      CrawlerControllerPerformanceAgent#getAttachmentBytesTransfered()
   */
  @Coefficient(1 / KBYTES)
  @MeasureUnit("KBytes")
  public PerformanceCounter getAttachmentBytesTransfered() {
    return _attachmentBytesTransfered;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters.CrawlerControllerPerformanceAgent#getRecords()
   */
  public PerformanceCounter getRecords() {
    return _records;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters.CrawlerControllerPerformanceAgent#getExceptions()
   */
  public PerformanceCounter getExceptions() {
    return _exceptions;
  }

  /**
   * {@inheritDoc}
   */
  @MeasureUnit("ms")
  public PerformanceCounter getAverageRecordsProcessingTime() {
    return _averageRecordsProcessingTime;
  }

  /**
   * {@inheritDoc}
   */
  @MeasureUnit("ms")
  public PerformanceCounter getAverageDeltaIndicesProcessingTime() {
    return _averageDeltaIndicesProcessingTime;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getExceptionsCritical()
   */
  public PerformanceCounter getExceptionsCritical() {
    return _exceptionsCritical;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getDeltaIndices()
   */
  public PerformanceCounter getDeltaIndices() {
    return _deltaIndices;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getAttachmentTransferRate()
   */
  @Coefficient(MILLISECS_IN_SEC / KBYTES)
  @MeasureUnit("KBytes/Seconds")
  public PerformanceCounter getAttachmentTransferRate() {
    return _attachmentTransferRate;
  }

  /**
   * Gets the error buffer.
   * 
   * @return the error buffer
   */
  public ErrorsBuffer getErrorBuffer() {
    return _errorBuffer;
  }

}
