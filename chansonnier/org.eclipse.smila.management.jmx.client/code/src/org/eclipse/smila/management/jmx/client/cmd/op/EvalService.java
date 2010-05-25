/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.op;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smila.management.jmx.client.config.BooleanOpType;
import org.eclipse.smila.management.jmx.client.config.EqualsOpType;
import org.eclipse.smila.management.jmx.client.config.InOpType;
import org.eclipse.smila.management.jmx.client.exceptions.EvaluationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Class EvalService.
 * 
 * @author Ivanhoe
 */
public final class EvalService {

  /**
   * The _evaluators.
   */
  @SuppressWarnings("unchecked")
  private static Map<Class, Evaluator> s_evaluators = new HashMap<Class, Evaluator>();

  static {
    s_evaluators.put(EqualsOpType.class, new EqualsEvaluator());
    s_evaluators.put(InOpType.class, new InEvaluator());
  }

  /**
   * Private Constructor to avoid instatioation.
   */
  private EvalService() {
  }

  /**
   * Eval.
   * 
   * @param config
   *          the config
   * @param connection
   *          the connection
   * @param parameters
   *          the parameters
   * @param globalResult
   *          the global result
   * @param localResult
   *          the local result
   * 
   * @return true, if successful
   * 
   * @throws EvaluationException
   *           the evaluation exception
   */
  @SuppressWarnings("unchecked")
  public static boolean eval(final BooleanOpType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws EvaluationException {
    // TODO
    final Evaluator evaluator = s_evaluators.get(config.getClass());
    if (evaluator == null) {
      throw new RuntimeException(String.format("Evaluator [%s] is not found!", config.getClass()));
    }
    return evaluator.eval(config, connection, globalResult, parameters, localResult);
  }
}
