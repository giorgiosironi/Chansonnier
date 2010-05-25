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
import org.eclipse.smila.management.jmx.client.cmd.task.Task;
import org.eclipse.smila.management.jmx.client.config.OperationType;
import org.eclipse.smila.management.jmx.client.config.ParameterType;
import org.eclipse.smila.management.jmx.client.exceptions.JmxInvocationException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.ConversionHelper;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;
import org.eclipse.smila.management.jmx.client.helpers.OutWriter;
import org.eclipse.smila.management.jmx.client.helpers.ParameterFormatHelper;

/**
 * The Class OperationTask.
 */
public class OperationTask implements Task<OperationType> {

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
  public Object execute(final OperationType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException {
    if (_log.isDebugEnabled()) {
      _log.debug(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult));
    }
    final Object newLocalResult;
    final int operationParamatersLength = config.getParameter().size();
    if (operationParamatersLength > parameters.length) {
      throw new IllegalArgumentException(String.format(
        "Operation parameters are not matched, it's required %d parameters, but is was passed %d",
        operationParamatersLength, parameters.length));
    }
    final String[] signature = new String[operationParamatersLength];
    final Object[] arguments = new Object[operationParamatersLength];
    for (int i = 0; i < operationParamatersLength; i++) {
      final ParameterType parameterConfig = config.getParameter().get(i);
      signature[i] = parameterConfig.getClazz();
      if (parameterConfig.getValue() != null) {
        arguments[i] = ConversionHelper.convert(parameterConfig.getValue(), parameterConfig.getClazz());
      } else if (parameterConfig.getArgument() != null) {
        final int source = parameterConfig.getArgument();
        if (source == 0) {
          arguments[i] = globalResult;
        } else {
          if (parameters.length < source) {
            throw new IllegalArgumentException();
          }
          arguments[i] = ConversionHelper.convert(parameters[source - 1], parameterConfig.getClazz());
        }
      } else {
        arguments[i] = ConversionHelper.convert(parameters[i], parameterConfig.getClazz());
      }
    }
    try {
      newLocalResult = connection.invoke(//
        ParameterFormatHelper.format(config.getDomain(), globalResult, parameters, localResult), // 
        ParameterFormatHelper.format(config.getKey(), globalResult, parameters, localResult),//
        ParameterFormatHelper.format(config.getName(), globalResult, parameters, localResult),//
        arguments, signature);
    } catch (final JmxInvocationException e) {
      throw new OperationException(e);
    }
    if (config.isVoidType()) {
      OutWriter.write(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult), _log);
    } else {
      OutWriter.write(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult),
        newLocalResult, _log);
    }
    if (_log.isDebugEnabled()) {
      _log.debug("JMX operation successfully invoked");
    }
    return newLocalResult;
  }
}
