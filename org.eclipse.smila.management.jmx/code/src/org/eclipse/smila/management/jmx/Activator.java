/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (Brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx;

import java.util.Dictionary;

import org.eclipse.smila.management.controller.ManagementController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class Activator.
 */
public class Activator implements BundleActivator {

  /**
   * The _controller.
   */
  private JmxManagementController _controller;
  
  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @SuppressWarnings("unchecked")
  public void start(final BundleContext context) throws Exception {
    final Dictionary properties = new java.util.Hashtable();
    _controller = new JmxManagementController();
    context.registerService(ManagementController.class.getName(), _controller, properties);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(final BundleContext context) throws Exception {
    try {
      _controller.unregisterAll();
    } finally {
      _controller = null;
    }
  }

}
