/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * Andreas Weber, Juergen Schumacher (empolis GmbH) - fix for 269967
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.task;

import java.util.Map;
import java.util.Properties;

import org.eclipse.smila.connectivity.queue.worker.internal.AbstractLoggedComponent;
import org.eclipse.smila.datamodel.id.Id;

/**
 * The Class AbstractTask.
 * 
 * @param <ConfigType>
 *          configuration type
 */
public abstract class AbstractTask<ConfigType> extends AbstractLoggedComponent implements
  TaskExecutionService<ConfigType> {

  /**
   * Instantiates a new abstract task.
   * 
   * @param id
   *          the id
   */
  public AbstractTask(final String id) {
    super(id);
  }

  /**
   * {@inheritDoc}
   * 
   */
  public Id[] execute(final TaskExecutionEnv env, final ConfigType config, Map<Id, Properties> idPropertyMap)
    throws TaskExecutionException {

    try {
      if (_log.isDebugEnabled()) {
        _log.debug(msg("Executing.."));
      }
      final Id[] ids = executeInternal(env, config, idPropertyMap);
      if (_log.isDebugEnabled()) {
        _log.debug(msg("Executed"));
      }
      return ids;
    } catch (final TaskExecutionException e) {
      _log.error(msg("Error"), e);
      throw e;
    } catch (final Throwable e) {
      _log.error(msg("Error"), e);
      throw new TaskExecutionException(e);
    }
  }

  /**
   * Execute internal task.
   * 
   * @param config
   *          the config
   * @param idPropertyMap
   *          a map of the record IDs and message propertie
   * @param env
   *          the env
   * 
   * @return the resulting record IDs
   * 
   * @throws Exception
   *           the exception
   */
  protected abstract Id[] executeInternal(final TaskExecutionEnv env, final ConfigType config,
    Map<Id, Properties> idPropertyMap) throws Exception;
}
