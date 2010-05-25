/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.op;

import org.eclipse.smila.management.jmx.client.config.InOpType;
import org.eclipse.smila.management.jmx.client.exceptions.EvaluationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Class EqualsEvaluator.
 */
public class InEvaluator extends BaseEvaluator implements Evaluator<InOpType> {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.op.Evaluator#eval(org.eclipse.smila.management.jmx.client.config.BooleanOpType,
   *      org.eclipse.smila.management.jmx.client.helpers.JmxConnection, java.lang.Object, java.lang.String[],
   *      java.lang.Object)
   */
  public boolean eval(final InOpType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws EvaluationException {
    final Object term1 = config.getCmdOrConst().get(0);
    final Object result1;
    try {
      result1 = eval(term1, connection, globalResult, parameters, localResult);
    } catch (final Throwable e) {
      throw new EvaluationException(e);
    }
    for (int i = 1; i < config.getCmdOrConst().size(); i++) {
      final Object term2 = config.getCmdOrConst().get(i);
      final Object result2;
      try {
        result2 = eval(term2, connection, globalResult, parameters, localResult);
      } catch (final Throwable e) {
        throw new EvaluationException(e);
      }
      if (checkEquals(result1, result2)) {
        return true;
      }
    }
    return false;
  }
}
