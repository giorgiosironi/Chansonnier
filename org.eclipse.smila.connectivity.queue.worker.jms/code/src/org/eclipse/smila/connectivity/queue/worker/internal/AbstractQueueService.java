/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import org.osgi.service.component.ComponentContext;

/**
 * The Class AbstractQueueService.
 *
 * @param <ConfigType>
 *          configuration type
 */
public abstract class AbstractQueueService<ConfigType> extends AbstractQueueServicesAccessPoint {

  /**
   * The _config.
   */
  protected ConfigType _config;

  /**
   * Instantiates a new abstract queue service.
   *
   * @param id
   *          the id
   */
  public AbstractQueueService(final String id) {
    super(id);
  }

  /**
   * Gets the config name.
   *
   * @return the config name
   */
  public abstract String getConfigName();

  /**
   * Start.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void start() {
    super.start();
    _config = (ConfigType) LocalUtils.loadConfig(getConfigName());
  }

  /**
   * Stop.
   */
  @Override
  public void stop() {
    super.stop();
    _config = null;
  }

  /**
   * Activate.
   *
   * @param context
   *          the context
   */
  protected synchronized void activate(final ComponentContext context) {
    if (_log.isDebugEnabled()) {
      _log.debug("Activating " + getClass());
    }
    // TODO: remove it when Declarative Services will set it correctly
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    try {
      this.start();
      if (_log.isDebugEnabled()) {
        _log.debug("Activation of " + getClass() + " was successfull");
      }
    } catch (final RuntimeException e) {
      _log.error("Activation of " + getClass() + " was fail", e);
      throw e;
    } finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }

  /**
   * OSGi Declarative Services service deactivation method. Shuts down BPEL engine.
   *
   * @param context
   *          OSGi service component context.
   */
  protected synchronized void deactivate(final ComponentContext context) {
    // TODO: remove it when DS will work ok
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    getManagementLock().lock();
    try {
      this.stop();
    } finally {
      getManagementLock().unlock();
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }

}
