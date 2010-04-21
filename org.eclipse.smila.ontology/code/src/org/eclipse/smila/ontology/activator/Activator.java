/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.activator;

import org.eclipse.smila.ontology.SesameOntologyManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * create a service tracker for SesameOntologyManager services.
 * 
 * @author jschumacher
 * 
 */
public class Activator implements BundleActivator {

  /**
   * my service tracker.
   */
  private static ServiceTracker s_tracker;

  /**
   * 
   * @return the current SesameOntologyManager service instance.
   */
  public static SesameOntologyManager getService() {
    if (s_tracker == null) {
      return null;
    }
    return (SesameOntologyManager) s_tracker.getService();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    s_tracker = new ServiceTracker(context, SesameOntologyManager.class.getName(), null);
    s_tracker.open();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    s_tracker.close();
    s_tracker = null;
  }

}
