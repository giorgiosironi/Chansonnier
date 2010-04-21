/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.io.Serializable;

/**
 * Utility class that contains the state of a crawl run.
 */
public abstract class State implements Serializable, Cloneable {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 7499001878583883888L;

  /**
   * The dataSourceId of this CrawlState.
   */
  private String _dataSourceId;

  /**
   * Start time in milliseconds.
   */
  private Long _startTime;

  /**
   * End time in milliseconds.
   */
  private Long _endTime;

  /**
   * The last error.
   */
  private Throwable _lastError;

  /**
   * A unique Id for the actual "run" of a crawler or an agent.
   */
  private String _jobId;

  /**
   * Returns the dataSourceId.
   * 
   * @return the dataSourceId
   */
  public String getDataSourceId() {
    return _dataSourceId;
  }

  /**
   * Set the dataSourceId.
   * 
   * @param dataSourceId
   *          the dataSourceId
   */
  public void setDataSourceId(final String dataSourceId) {
    _dataSourceId = dataSourceId;
  }

  /**
   * Get the start time.
   * 
   * @return the start time in milliseconds
   */
  public Long getStartTime() {
    return _startTime;
  }

  /**
   * Set the start time.
   * 
   * @param startTime
   *          the start time in milliseconds
   */
  public void setStartTime(final Long startTime) {
    _startTime = startTime;
  }

  /**
   * Get the end time.
   * 
   * @return the end time in milliseconds or null if the crawl has not finished yet.
   */
  public Long getEndTime() {
    return _endTime;
  }

  /**
   * Set the end time.
   * 
   * @param endTime
   *          the end time in milliseconds
   */
  public void setEndTime(final Long endTime) {
    _endTime = endTime;
  }

  /**
   * Get the last error.
   * 
   * @return the last error or null if no error occurred
   */
  public Throwable getLastError() {
    return _lastError;
  }

  /**
   * Set the last error.
   * 
   * @param t
   *          the last error
   */
  public void setLastError(final Throwable t) {
    _lastError = t;
  }

  /**
   * Returns the jobId.
   * 
   * @return the jobId
   */
  public String getJobId() {
    return _jobId;
  }

  /**
   * Set the jobId.
   * 
   * @param jobId
   *          the jobId
   */
  public void setJobId(final String jobId) {
    _jobId = jobId;
  }
}
