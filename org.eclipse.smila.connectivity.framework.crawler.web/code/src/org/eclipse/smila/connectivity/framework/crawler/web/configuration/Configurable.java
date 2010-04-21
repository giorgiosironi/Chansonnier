/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.configuration;

/** Something that may be configured with a {@link Configuration}. */
public interface Configurable {

  /**
   * Set the configuration to be used by this object.
   * 
   * @param configuration
   *          Configuration
   */
  void setConf(Configuration configuration);

  /**
   * Return the configuration used by this object.
   * 
   * @return Configuration
   */
  Configuration getConf();
}
