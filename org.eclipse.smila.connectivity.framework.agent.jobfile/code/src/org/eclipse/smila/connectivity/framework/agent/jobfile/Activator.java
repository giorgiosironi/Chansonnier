/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.agent.jobfile;

import javax.xml.stream.XMLInputFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle Activator for bundle org.eclipse.smila.connectivity.framework.agent.jobfile.
 */
public class Activator implements BundleActivator {

  /**
   * The XMLInputFactory.
   */
  private static XMLInputFactory s_inputFactory;

  /**
   * Get the XMLInputFactory.
   * 
   * @return the XMLInputFactory
   */
  protected static XMLInputFactory getXMLInputFactory() {
    return s_inputFactory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    s_inputFactory = XMLInputFactory.newInstance();
  }

  /**
   * {@inheritDoc} Closes all opened IndexWriters.
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    s_inputFactory = null;
  }

}
