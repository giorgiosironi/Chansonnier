/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.jmx.client.cmd.task.Task;
import org.eclipse.smila.management.jmx.client.cmd.task.impl.AttributeTask;
import org.eclipse.smila.management.jmx.client.cmd.task.impl.CustomTask;
import org.eclipse.smila.management.jmx.client.cmd.task.impl.OperationTask;
import org.eclipse.smila.management.jmx.client.cmd.task.impl.RegexpTask;
import org.eclipse.smila.management.jmx.client.cmd.task.impl.WaitTask;
import org.eclipse.smila.management.jmx.client.config.AttributeType;
import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.config.CustomType;
import org.eclipse.smila.management.jmx.client.config.ItemType;
import org.eclipse.smila.management.jmx.client.config.OperationType;
import org.eclipse.smila.management.jmx.client.config.RegexpType;
import org.eclipse.smila.management.jmx.client.config.WaitType;
import org.eclipse.smila.management.jmx.client.exceptions.JmxConnectionException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Class CmdConsoleImpl.
 */
public class CmdConsoleImpl implements CmdConsole {

  /**
   * The _log.
   */
  protected Log _log = LogFactory.getLog(getClass());

  /**
   * The _tasks.
   */
  @SuppressWarnings("unchecked")
  private final Map<Class, Task> _tasks = new HashMap<Class, Task>();

  /**
   * Instantiates a new cmd console impl.
   */
  public CmdConsoleImpl() {
    _tasks.put(AttributeType.class, new AttributeTask());
    _tasks.put(OperationType.class, new OperationTask());
    _tasks.put(RegexpType.class, new RegexpTask());
    _tasks.put(WaitType.class, new WaitTask());
    _tasks.put(CustomType.class, new CustomTask());
  }

  /**
   * {@inheritDoc}
   * 
   * @throws OperationException
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.CmdConsole#execute(org.eclipse.smila.management.jmx.client.config.CmdConfigType,
   *      org.eclipse.smila.management.jmx.client.config.ConnectionConfigType, java.lang.String[])
   */
  public Object execute(final CmdConfigType cmdConfig, final ConnectionConfigType connectionConfig,
    final String[] parameters) throws JmxConnectionException, OperationException {
    final JmxConnection connection = new JmxConnection(connectionConfig);
    connection.connect();
    return execute(cmdConfig, connection, null, parameters);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.CmdConsole#execute(org.eclipse.smila.management.jmx.client.config.CmdConfigType,
   *      org.eclipse.smila.management.jmx.client.helpers.JmxConnection, java.lang.String[])
   */
  @SuppressWarnings("unchecked")
  public Object execute(final CmdConfigType cmdConfig, final JmxConnection connection, final Object globalResult,
    final String[] parameters) throws OperationException {
    Object localResult = null;
    for (final ItemType item : cmdConfig.getAttributeOrOperationOrRegexp()) {
      final Task task = _tasks.get(item.getClass());
      if (task == null) {
        throw new RuntimeException(String.format("Processing of [%s] is not implemented!", item.getClass()));
      }
      localResult = task.execute(item, connection, globalResult, parameters, localResult);
    }
    return localResult;
  }

}
