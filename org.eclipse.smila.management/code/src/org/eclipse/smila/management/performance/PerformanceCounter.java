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
 * PerformanceCounter base class.
 */
public class PerformanceCounter {

  /**
   * Last two samples.
   */
  private PerformanceSample[] _samples;

  /**
   * Counter value calculation formula.
   */
  private final PerformanceCounterFormula _formula;

  /**
   * Value change since last sample.
   */
  private long _valueChange;

  /**
   * Value changes counter.
   */
  private long _changesCount;

  /**
   * Constructs PerformanceCounter object.
   * 
   * @param formula
   *          counter value calculation formula
   */
  public PerformanceCounter(final PerformanceCounterFormula formula) {
    if (formula == null) {
      throw new IllegalArgumentException("Formula can't be null");
    }
    this._formula = formula;
    reset();
  }

  /**
   * Increment by.
   * 
   * @param value
   *          value to increment counter by
   */
  public void incrementBy(final long value) {
    _changesCount++;
    _valueChange = _valueChange + value;
  }

  /**
   * Decrement by.
   * 
   * @param value
   *          value to decrement counter by
   */
  public void decrementBy(final long value) {
    _changesCount++;
    _valueChange = _valueChange - value;
  }

  /**
   * Gets the next value.
   * 
   * @return next calculated value
   */
  public double getNextValue() {
    return _formula.calculateValue(_samples[0], _samples[1]);
  }

  /**
   * Sample and gets the next value.
   * 
   * @return next calculated value
   */
  public double getNextSampleValue() {
    getNextPerformanceSample();
    return getNextValue();
  }

  /**
   * Gets the next performance sample.
   * 
   * @return next sample
   */
  public PerformanceSample getNextPerformanceSample() {
    synchronized (_samples) {
      sample();
      return _samples[1];
    }
  }

  /**
   * Sample the counter.
   */
  private void sample() {
    _samples[0].setBaseValue(_samples[1].getBaseValue());
    _samples[0].setRawValue(_samples[1].getRawValue());
    _samples[0].setTimestamp(_samples[1].getTimestamp());

    _samples[1].setTimestamp(System.currentTimeMillis());
    _samples[1].setRawValue(_samples[1].getRawValue() + _valueChange);
    _samples[1].setBaseValue(_samples[1].getBaseValue() + _changesCount);

    _valueChange = 0L;
    _changesCount = 0L;
  }

  /**
   * Reset the counter.
   */
  public void reset() {
    _samples = new PerformanceSample[2];
    synchronized (_samples) {
      _samples[0] = new PerformanceSample();
      _samples[1] = new PerformanceSample();
    }
    _valueChange = 0L;
    _changesCount = 0L;
  }

  /**
   * Sets the raw value.
   * 
   * @param rawValue
   *          set the new raw value for the counter
   */
  public void setRawValue(final long rawValue) {
    synchronized (_samples) {
      sample();
      _samples[1].setRawValue(rawValue);
    }
  }

  /**
   * Increment the counter by one.
   */
  public void increment() {
    incrementBy(1);
  }

  /**
   * Decrement the counter by one.
   */
  public void decrement() {
    decrementBy(1);
  }

}
