/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd;

import org.eclipse.smila.management.jmx.client.config.CmdConfigType;
import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.exceptions.JmxConnectionException;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Interface Launcher.
 */
public interface CmdConsole {

  /**
   * Execute.
   * 
   * @param cmdConfig
   *          the cmd config
   * @param connectionConfig
   *          the connection config
   * @param parameters
   *          the parameters
   * 
   * @return the object
   * 
   * @throws JmxConnectionException
   *           the jmx connection exception
   * @throws OperationException
   *           the operation exception
   */
  Object execute(CmdConfigType cmdConfig, ConnectionConfigType connectionConfig, String[] parameters)
    throws JmxConnectionException, OperationException;

  /**
   * Execute.
   * 
   * @param cmdConfig
   *          the cmd config
   * @param connection
   *          the connection
   * @param parameters
   *          the parameters
   * @param globalResult
   *          the result
   * 
   * @return the object
   * 
   * @throws OperationException
   *           the operation exception
   */
  Object execute(CmdConfigType cmdConfig, JmxConnection connection, Object globalResult, String[] parameters)
    throws OperationException;
}
