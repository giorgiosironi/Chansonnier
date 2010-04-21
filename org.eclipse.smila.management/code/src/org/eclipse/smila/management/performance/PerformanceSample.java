/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator Dmitry Hazin (brox IT Solutions GmbH) -
 * initial creator Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.management.performance;

/**
 * Performance counter 'sample'.
 * 
 */
public final class PerformanceSample {

  /**
   * Counter sampling timestamp.
   */
  private long _timestamp;

  /**
   * Base value - number of operations.
   */
  private long _baseValue;

  /**
   * Raw value - the actual counter value.
   */
  private long _rawValue;

  /**
   * Constructs a PerformanceSample object.
   */
  public PerformanceSample() {
    reset();
  }

  /**
   * @return the timestamp
   */
  public long getTimestamp() {
    return _timestamp;
  }

  /**
   * @param timestamp
   *          the timestamp to set
   */
  public void setTimestamp(final long timestamp) {
    this._timestamp = timestamp;
  }

  /**
   * @return the baseValue
   */
  public long getBaseValue() {
    return _baseValue;
  }

  /**
   * @param baseValue
   *          the baseValue to set
   */
  public void setBaseValue(final long baseValue) {
    this._baseValue = baseValue;
  }

  /**
   * @return the rawValue
   */
  public long getRawValue() {
    return _rawValue;
  }

  /**
   * @param rawValue
   *          the rawValue to set
   */
  public void setRawValue(final long rawValue) {
    this._rawValue = rawValue;
  }

  /**
   * Resets a sample.
   */
  public void reset() {
    _timestamp = System.currentTimeMillis();
    _baseValue = 0L;
    _rawValue = 0L;
  }

}
