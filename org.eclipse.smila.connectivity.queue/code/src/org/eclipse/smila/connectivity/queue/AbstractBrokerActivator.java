/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class Activator.
 */
public abstract class AbstractBrokerActivator implements BundleActivator {

  /**
   * The Constant LOG.
   */
  private final Log _log = LogFactory.getLog(AbstractBrokerActivator.class);

  /**
   * The _manager.
   */
  private ConnectivityBroker _manager;

  /**
   * Creates the instance.
   * 
   * @return the connectivity broker
   */
  protected abstract ConnectivityBroker createInstance();

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(final BundleContext context) throws Exception {
    _manager = createInstance();
    _manager.start();
    final Hashtable<String, String> properties = new Hashtable<String, String>();
    context.registerService(ConnectivityBroker.class.getName(), _manager, properties);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(final BundleContext context) throws Exception {
    try {
      _manager.stop();
    } catch (final Throwable ex) {
      _log.error(ex);
    }
    _manager = null;
  }

}
