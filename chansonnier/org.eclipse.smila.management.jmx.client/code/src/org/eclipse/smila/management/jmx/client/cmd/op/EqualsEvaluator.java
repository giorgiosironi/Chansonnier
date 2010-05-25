/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.op;

import org.eclipse.smila.management.jmx.client.config.EqualsOpType;
import org.eclipse.smila.management.jmx.client.exceptions.EvaluationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Class EqualsEvaluator.
 */
public class EqualsEvaluator extends BaseEvaluator implements Evaluator<EqualsOpType> {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.op.Evaluator#eval(org.eclipse.smila.management.jmx.client.config.BooleanOpType,
   *      org.eclipse.smila.management.jmx.client.helpers.JmxConnection, java.lang.Object, java.lang.String[],
   *      java.lang.Object)
   */
  public boolean eval(final EqualsOpType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws EvaluationException {
    final Object term1 = config.getCmdOrConst().get(0);
    final Object term2 = config.getCmdOrConst().get(1);
    final Object result1;
    final Object result2;
    try {
      result1 = eval(term1, connection, globalResult, parameters, localResult);
      // OutWriter.write("Result1=" + result1);
      result2 = eval(term2, connection, globalResult, parameters, localResult);
      // OutWriter.write("Result2=" + result2);
    } catch (final Throwable e) {
      throw new EvaluationException(e);
    }
    return checkEquals(result1, result2);
  }

}
