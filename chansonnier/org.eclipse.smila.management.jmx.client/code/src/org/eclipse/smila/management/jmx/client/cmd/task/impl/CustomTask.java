/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.task.impl;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.jmx.client.cmd.task.Task;
import org.eclipse.smila.management.jmx.client.config.CustomType;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;
import org.eclipse.smila.management.jmx.client.helpers.ParameterFormatHelper;

/**
 * The Class CustomTask.
 */
public class CustomTask implements Task<CustomType> {

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.jmx.client.cmd.task.Task#execute(org.eclipse.smila.management.jmx.client.config.ItemType,
   *      org.eclipse.smila.management.jmx.client.helpers.JmxConnection, java.lang.Object, java.lang.String[],
   *      java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  public Object execute(final CustomType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException {
    if (_log.isDebugEnabled()) {
      _log.debug(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult));
    }
    final Object result;
    try {
      final Constructor constructor = Class.forName(config.getClazz()).getConstructor(new Class[0]);
      final Task<CustomType> task = (Task<CustomType>) constructor.newInstance(new Object[0]);
      result = task.execute(config, connection, globalResult, parameters, localResult);
    } catch (final Throwable e) {
      throw new OperationException(e);
    }
    if (_log.isDebugEnabled()) {
      _log.debug("Custom task successfully executed");
    }
    return result;
  }

}
