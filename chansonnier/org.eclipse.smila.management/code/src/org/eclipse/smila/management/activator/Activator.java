/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.activator;

import org.eclipse.smila.management.ManagementRegistration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class Activator.
 */
public class Activator implements BundleActivator {

  /**
   * Reference to a BundleContext.
   */
  private static BundleContext s_bundleContext;

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(final BundleContext context) throws Exception {
    s_bundleContext = context;
    // required to initialize
    final ManagementRegistration managementRegistration = ManagementRegistration.INSTANCE;
    // to avoid warning
    managementRegistration.hashCode();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(final BundleContext context) throws Exception {
  }

  /**
   * Returns the BundleContext.
   * 
   * @return BundleContext
   */
  public static BundleContext getBundleContext() {
    return s_bundleContext;
  }

}
