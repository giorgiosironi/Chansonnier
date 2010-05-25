/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.config.ConfigurationLoadException;
import org.eclipse.smila.utils.jaxb.JaxbUtils;

/**
 * The Class LocalUtils.
 */
public final class LocalUtils {

  /**
   * The Constant BUNDLE_ID.
   */
  public static final String BUNDLE_ID = "org.eclipse.smila.connectivity.queue.worker.jms";

  /**
   * Constant for the bundle were the config schema is located.
   */
  public static final String SCHEMA_BUNDLE_ID = "org.eclipse.smila.connectivity.queue.worker";

  /**
   * Prevents instantiating of class.
   */
  private LocalUtils() {

  }

  /**
   * Load config.
   * 
   * @param configName
   *          the config name
   * 
   * @return the object
   * 
   */
  public static Object loadConfig(final String configName) {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(LocalUtils.class.getClassLoader());
    try {
      return JaxbUtils.unmarshall(SCHEMA_BUNDLE_ID, SCHEMA_BUNDLE_ID + ".config",
        LocalUtils.class.getClassLoader(), "schemas/QueueWorkerConfig.xsd", ConfigUtils.getConfigStream(BUNDLE_ID,
          configName));
    } catch (final Throwable e) {
      throw new ConfigurationLoadException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

}
