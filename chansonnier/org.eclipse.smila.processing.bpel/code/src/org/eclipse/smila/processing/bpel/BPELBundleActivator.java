/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Register SimplePipeletManager instance as listener for SimplePipeletTracker events.
 * 
 * @author jschumacher
 * 
 */
public class BPELBundleActivator implements BundleActivator {

  /**
   * Register SimplePipeletManager instance as listener for SimplePipeletTracker events.
   * 
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    PipeletManager.getInstance().registerAsListener(context);
  }

  /**
   * does nothing. All services registered by the activator should be deregistered automatically at bundle stop.
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    // nothing to do, see method javadoc.
  }

}
