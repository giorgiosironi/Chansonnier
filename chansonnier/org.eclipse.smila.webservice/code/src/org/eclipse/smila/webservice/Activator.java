/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.webservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the bundle life cycle.
 */
public class Activator implements BundleActivator {

  /**
   * The bundle ID.
   */
  public static final String BUNDLE_ID = "org.eclipse.smila.webservice";

  /**
   * The shared instance.
   */
  private static Activator s_plugin;

  /**
   * The Webservice Publisher.
   */
  private WebservicePublisher _webservicePublisher;

  /**
   * The constructor.
   */
  public Activator() {
  }

  /**
   * {@inheritDoc} Create and activate Webservice Publisher.
   */
  public void start(final BundleContext context) throws Exception {
    s_plugin = this;

    _webservicePublisher = new WebservicePublisher(context);
    _webservicePublisher.open();

  }

  /**
   * {@inheritDoc} Stop and release the webservice publisher.
   */
  public void stop(final BundleContext context) throws Exception {
    if (_webservicePublisher != null) {
      _webservicePublisher.close();
    }
    _webservicePublisher = null;
    s_plugin = null;
  }

  /**
   * Returns the shared instance.
   * 
   * @return the shared instance
   */
  public static Activator getDefault() {
    return s_plugin;
  }

}
