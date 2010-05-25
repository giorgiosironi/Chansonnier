/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.task;

import org.eclipse.smila.management.jmx.client.config.ItemType;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;

/**
 * The Interface Task.
 * 
 * @param <ConfigType>
 *          the type of config
 */
public interface Task<ConfigType extends ItemType> {

  /**
   * Execute.
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
   * @return the object
   * 
   * @throws OperationException
   *           the operation exception
   */
  Object execute(final ConfigType config, final JmxConnection connection, Object globalResult,
    final String[] parameters, Object localResult) throws OperationException;

}
