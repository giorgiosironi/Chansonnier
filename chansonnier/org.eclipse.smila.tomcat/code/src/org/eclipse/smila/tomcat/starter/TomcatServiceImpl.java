/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.tomcat.starter;

import java.io.File;

import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.Embedded;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.osgi.service.component.ComponentContext;

/**
 * 
 * @author Georg Schmidt (brox IT Solutions GmbH)
 * 
 */
public class TomcatServiceImpl implements TomcatService {

  /**
   * The bundle name.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.tomcat";

  /**
   * The catalina home directory.
   */
  private String _catalinaHome;

  /**
   * The Catalina server.
   */
  private Catalina _catalina;

  /**
   * The embedded service.
   */
  private Embedded _embedded;

  /**
   * The host.
   */
  private Host _host;

  /**
   * The Logger.
   */
  private final Log _log = LogFactory.getLog(TomcatServiceImpl.class);

  /**
   * Default Constructor.
   */
  public TomcatServiceImpl() {
  }

  /**
   * DS activate method.
   * 
   * @param context
   *          ComponentContext
   */
  protected void activate(final ComponentContext context) {
    start();
  }

  /**
   * DS deactivate method.
   * 
   * @param context
   *          the ComponentContext
   */
  protected void deactivate(final ComponentContext context) {
    stop();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.tomcat.starter.TomcatService#start()
   */
  public void start() {

    // TODO: workdir ???
    setCatalinaHome();

    final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(Server.class.getClassLoader());

      _catalina = new Catalina2();

      // set the home directory
      _catalina.setCatalinaBase(_catalinaHome);
      _catalina.setCatalinaHome(_catalinaHome);
      _catalina.setConfigFile(_catalinaHome + "/conf/server.xml");

      // start catalina
      _catalina.start();
    } catch (final Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("Error starting Tomcat server", e);
      }
    } finally {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.tomcat.starter.TomcatService#stop()
   */
  public void stop() {
    if (_catalina != null) {
      try {
        _catalina.stop();
        _catalina = null;
      } catch (final Throwable e) {
        if (_log.isErrorEnabled()) {
          _log.error("Error stopping Tomcat server", e);
        }
      }
      _catalina = null;
    }
  }

  /**
   * Set CATALINA_HOME directory to [configuration-folder]/[bundle-name].
   */
  private synchronized void setCatalinaHome() {
    if (_catalinaHome == null) {
      final File configFolder = ConfigUtils.getConfigurationFolder();
      _catalinaHome = configFolder.getAbsolutePath() + "/" + BUNDLE_NAME + "/";
    }
  }
}
