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
import org.eclipse.smila.management.jmx.client.config.AttributeType;
import org.eclipse.smila.management.jmx.client.exceptions.JmxInvocationException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;
import org.eclipse.smila.management.jmx.client.helpers.OutWriter;
import org.eclipse.smila.management.jmx.client.helpers.ParameterFormatHelper;

/**
 * The Class AttributeTask.
 */
public class AttributeTask implements Task<AttributeType> {

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
  public Object execute(final AttributeType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException {
    Object newLocalResult;
    if (_log.isDebugEnabled()) {
      _log.debug(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult));
    }
    try {
      newLocalResult = connection.getAttribute(//
        ParameterFormatHelper.format(config.getDomain(), globalResult, parameters, localResult), //
        ParameterFormatHelper.format(config.getKey(), globalResult, parameters, localResult), //
        ParameterFormatHelper.format(config.getName(), globalResult, parameters, localResult));
    } catch (final JmxInvocationException e) {
      throw new OperationException(e);
    }
    OutWriter.write(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult),
      newLocalResult, _log);
    if (_log.isDebugEnabled()) {
      _log.debug("JMX attribute successfully invoked");
    }
    return newLocalResult;
  }
}
