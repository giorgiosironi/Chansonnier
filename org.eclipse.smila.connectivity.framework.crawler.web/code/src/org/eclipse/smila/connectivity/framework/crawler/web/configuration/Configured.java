/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.configuration;

/**
 * Base class for things that may be configured with a {@link Configuration}.
 */
public class Configured implements Configurable {

  /** The configuration. */
  protected Configuration _configuration;

  /**
   * Default empty constructor.
   */
  public Configured() {
    // empty stub
  }

  /**
   * Construct a Configured.
   * 
   * @param configuration
   *          {@link Configuration}
   */
  public Configured(Configuration configuration) {
    setConf(configuration);
  }

  /**
   * {@inheritDoc}
   */
  public void setConf(Configuration configuration) {
    _configuration = configuration;
  }

  /**
   * {@inheritDoc}
   */
  public Configuration getConf() {
    return _configuration;
  }
}
