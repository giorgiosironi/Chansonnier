/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.task.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsole;
import org.eclipse.smila.management.jmx.client.cmd.CmdConsoleImpl;
import org.eclipse.smila.management.jmx.client.cmd.op.EvalService;
import org.eclipse.smila.management.jmx.client.cmd.task.Task;
import org.eclipse.smila.management.jmx.client.config.BooleanOpType;
import org.eclipse.smila.management.jmx.client.config.WaitType;
import org.eclipse.smila.management.jmx.client.exceptions.EvaluationException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;
import org.eclipse.smila.management.jmx.client.helpers.ParameterFormatHelper;

/**
 * The Class WaitTask.
 */
public class WaitTask implements Task<WaitType> {

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.task.Task#execute(org.eclipse.smila.management.jmx.client.config.ItemType,
   *      org.eclipse.smila.management.jmx.client.helpers.JmxConnection, java.lang.Object, java.lang.String[],
   *      java.lang.Object)
   */
  public Object execute(final WaitType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException {
    if (_log.isDebugEnabled()) {
      _log.debug(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult));
    }
    boolean doStop = false;
    while (!doStop) {
      if (config.getCmd() != null) {
        CmdConsole console;
        try {
          console = new CmdConsoleImpl();
          console.execute(config.getCmd(), connection, localResult, parameters);
        } catch (final Throwable e) {
          throw new OperationException(e);
        }
      }
      try {
        BooleanOpType configOp = config.getEquals();
        if (configOp == null) {
          configOp = config.getIn();
        }
        doStop = EvalService.eval(configOp, connection, globalResult, parameters, localResult);
      } catch (final EvaluationException e) {
        throw new OperationException(e);
      }
      if (!doStop) {
        try {
          Thread.sleep(config.getPause());
        } catch (final InterruptedException e) {
          ;// nothing
        }
      }
    }
    if (_log.isDebugEnabled()) {
      _log.debug("Wait task successfully finished");
    }
    return localResult;
  }
}
