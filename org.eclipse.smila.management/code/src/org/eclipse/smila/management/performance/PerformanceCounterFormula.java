/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.performance;

import org.eclipse.smila.management.performance.formula.AverageFormula;
import org.eclipse.smila.management.performance.formula.AverageTimerFormula;
import org.eclipse.smila.management.performance.formula.RateFormula;
import org.eclipse.smila.management.performance.formula.SimpleCountFormula;

/**
 * Counter value calculation formula interface.
 */
public interface PerformanceCounterFormula {

  /**
   * Simple items count formula returning the counter raw value.
   */
  PerformanceCounterFormula SIMPLE_COUNT_FORMULA = new SimpleCountFormula();

  /**
   * Average count formula calculates how many items are processed, on average, during an operation. The formula is
   * 
   * (Xn - X0)/(Bn - B0) where Xi is a counter sample and the Bi is the corresponding base value (number of counter
   * changes).
   */
  PerformanceCounterFormula AVERAGE_FORMULA = new AverageFormula();

  /**
   * Rate formula calculates (Xn - X0)/(Tn - T0), where Xi is a counter sample and Ti is the time that the corresponding
   * sample was taken. The result is the average usage per second.
   */
  PerformanceCounterFormula RATE_FORMULA = new RateFormula();

  /**
   * Average timer formulla calculates the time (in seconds) it takes, on average, to complete a process or operation.
   * The formula is (Tn - T0)/(Bn - B0) where Bi is base value and the Ti is corresponding timestamp.
   */
  PerformanceCounterFormula AVERAGE_TIMER_FORMULA = new AverageTimerFormula();

  /**
   * Calculates next counter value by two samples.
   * 
   * @param sample1
   *          sample1
   * @param sample2
   *          sample2
   * 
   * @return next counter value
   */
  double calculateValue(PerformanceSample sample1, PerformanceSample sample2);

}
