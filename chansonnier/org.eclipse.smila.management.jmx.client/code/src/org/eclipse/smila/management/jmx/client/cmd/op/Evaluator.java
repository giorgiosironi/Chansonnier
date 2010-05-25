/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.op;

import org.eclipse.smila.management.jmx.client.config.BooleanOpType;
import org.eclipse.smila.management.jmx.client.exceptions.EvaluationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Interface Evaluator.
 * 
 * @param <ConfigType>
 *          the type of config
 */
public interface Evaluator<ConfigType extends BooleanOpType> {

  /**
   * Eval.
   * 
   * @param config
   *          the config
   * @param connection
   *          the connection
   * @param globalResult
   *          the global result
   * @param parameters
   *          the parameters
   * @param localResult
   *          the local result
   * 
   * @return true, if successful
   * 
   * @throws EvaluationException
   *           the evaluation exception
   */
  boolean eval(ConfigType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws EvaluationException;

}
