/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import java.util.Date;

import org.eclipse.smila.management.MeasureUnit;

/**
 * The Class CrawlerPerformanceAgent.
 */
public class CrawlerPerformanceAgent extends ConnectivityPerformanceAgentBase {

  /** The _start date. */
  private Date _startDate;

  /** The _end date. */
  private Date _endDate;
  
  /**
   * The jobId.
   */
  private String _jobId;

  /**
   * Instantiates a new crawler performance agent.
   */
  public CrawlerPerformanceAgent() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getAverageAttachmentTransferRate()
   */
  @MeasureUnit("KBytes/Second")
  public double getAverageAttachmentTransferRate() {
    return (getAttachmentBytesTransfered().getNextSampleValue() / getLastRunTime()) * MILLISECS_IN_SEC / 1024;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getOverallAverageDeltaIndicesProcessingTime()
   */
  @MeasureUnit("ms")
  public double getOverallAverageDeltaIndicesProcessingTime() {
    return (getLastRunTime() / getDeltaIndices().getNextSampleValue());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.performancecounters
   *      .CrawlerControllerPerformanceAgent#getOverallAverageRecordsProcessingTime()
   */
  @MeasureUnit("ms")
  public double getOverallAverageRecordsProcessingTime() {
    return (getLastRunTime() / getRecords().getNextSampleValue());
  }

  /**
   * Gets the last run time.
   * 
   * @return the last run time
   */
  private long getLastRunTime() {
    if (_startDate == null) {
      return 0;
    }
    if (_startDate != null && _endDate == null) {
      return (System.currentTimeMillis() - _startDate.getTime());
    }
    return _endDate.getTime() - _startDate.getTime();
  }

  /**
   * Gets the start date.
   * 
   * @return the start date
   */
  public Date getStartDate() {
    return _startDate;
  }

  /**
   * Sets the start date.
   * 
   * @param date
   *          the new start date
   */
  // TODO: make @Hidden
  public void setStartDate(Date date) {
    _startDate = date;
  }

  /**
   * Gets the end date.
   * 
   * @return the end date
   */
  public Date getEndDate() {
    return _endDate;
  }

  /**
   * Sets the end date.
   * 
   * @param endDate
   *          the new end date
   */
  // TODO: make @Hidden
  public void setEndDate(Date endDate) {
    _endDate = endDate;
  }

  /**
   * Gets the jobId.
   * 
   * @return the jobId
   */
  public String getJobId() {
    return _jobId;
  }

  /**
   * Sets the jobId.
   * 
   * @param jobId
   *          the jobId
   */
  // TODO: make @Hidden
  public void setJobId(String jobId) {
    _jobId = jobId;
  }
}
