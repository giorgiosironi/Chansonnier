/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.op;

import org.eclipse.smila.management.jmx.client.cmd.CmdConsole;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsoleImpl;
import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConstantType;
import org.eclipse.smila.management.jmx.client.exceptions.LauncherInitializationException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.ConversionHelper;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Class EqualsEvaluator.
 */
public class BaseEvaluator {

  /**
   * Check equals.
   * 
   * @param result1
   *          the result1
   * @param result2
   *          the result2
   * 
   * @return true, if successful
   */
  protected boolean checkEquals(final Object result1, final Object result2) {
    if (result1 == null && result2 == null) {
      return true;
    }
    if (result1 != null) {
      return result1.equals(result2);
    }
    if (result2 != null) {
      return result2.equals(result1);
    }
    return false;
  }

  /**
   * Eval.
   * 
   * @param term
   *          the term
   * @param connection
   *          the connection
   * @param parameters
   *          the parameters
   * @param globalResult
   *          the global result
   * @param localResult
   *          the local result
   * 
   * @return the object
   * 
   * @throws OperationException
   *           the operation exception
   * @throws LauncherInitializationException
   *           the launcher initialization exception
   */
  protected Object eval(final Object term, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException, LauncherInitializationException {
    if (term instanceof ConstantType) {
      final ConstantType constT = (ConstantType) term;
      return ConversionHelper.convert(constT.getValue(), constT.getClazz());
    }
    if (term instanceof CmdConfigType) {
      final CmdConfigType cmdConfig = (CmdConfigType) term;
      final CmdConsole console = new CmdConsoleImpl();
      return console.execute(cmdConfig, connection, globalResult, parameters);
    }
    throw new RuntimeException(String.format("Evaluation for class [%s] is not implemented!", term.getClass()));
  }

}
