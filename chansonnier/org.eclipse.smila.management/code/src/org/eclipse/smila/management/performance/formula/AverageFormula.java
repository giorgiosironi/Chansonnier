/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.performance.formula;

import org.eclipse.smila.management.performance.PerformanceCounterFormula;
import org.eclipse.smila.management.performance.PerformanceSample;

/**
 * The Class AverageFormula.
 */
public class AverageFormula implements PerformanceCounterFormula {
  /**
   * {@inheritDoc}
   */
  public double calculateValue(final PerformanceSample sample1, final PerformanceSample sample2) {
    final double numerator = sample2.getRawValue() - sample1.getRawValue();
    final double denomenator = sample2.getBaseValue() - sample1.getBaseValue();
    if (numerator == 0 || denomenator == 0) {
      return 0;
    }
    return numerator / denomenator;
  }
}
