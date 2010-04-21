/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.cmd.task.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.jmx.client.cmd.task.Task;
import org.eclipse.smila.management.jmx.client.config.RegexpType;
import org.eclipse.smila.management.jmx.client.exceptions.OperationException;
import org.eclipse.smila.management.jmx.client.helpers.JmxConnection;
import org.eclipse.smila.management.jmx.client.helpers.OutWriter;
import org.eclipse.smila.management.jmx.client.helpers.ParameterFormatHelper;

/**
 * The Class RegexpTask.
 */
public class RegexpTask implements Task<RegexpType> {

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
  public Object execute(final RegexpType config, final JmxConnection connection, final Object globalResult,
    final String[] parameters, final Object localResult) throws OperationException {
    if (_log.isDebugEnabled()) {
      _log.debug(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult));
    }
    if (localResult == null) {
      throw new IllegalArgumentException("Regexp cannot be applied because previous operation result was NULL!");
    }
    final Pattern pattern = Pattern.compile(config.getPattern());
    final Matcher matcher = pattern.matcher(localResult.toString());
    if (!matcher.matches()) {
      throw new OperationException(String.format(
        "Result [%s] is not matched by regular exception group extractor [%s]", localResult, config.getEcho()));
    }
    if (matcher.groupCount() < config.getGroup()) {
      throw new OperationException(String.format(
        "Result [%s] group [%d] regular exception group extractor [%s] is not found", localResult, config
          .getGroup(), config.getEcho()));
    }
    final Object newLocalResult = matcher.group(config.getGroup());
    OutWriter.write(ParameterFormatHelper.format(config.getEcho(), globalResult, parameters, localResult),
      newLocalResult, _log);
    if (_log.isDebugEnabled()) {
      _log.debug("Regexp task successfully finished");
    }
    return newLocalResult;
  }

}
