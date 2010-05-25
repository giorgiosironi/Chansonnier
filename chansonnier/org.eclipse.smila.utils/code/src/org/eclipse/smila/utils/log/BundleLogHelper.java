/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The Class BundleLogHelper.
 */
public final class BundleLogHelper {

  /**
   * The Constant _descriptions.
   */
  private static final Map<Integer, String> DESCRIPTIONS = new HashMap<Integer, String>();

  static {
    DESCRIPTIONS.put(Bundle.ACTIVE, "ACTIVE     ");
    DESCRIPTIONS.put(Bundle.INSTALLED, "INSTALLED  ");
    DESCRIPTIONS.put(Bundle.RESOLVED, "RESOLVED   ");
    DESCRIPTIONS.put(Bundle.START_ACTIVATION_POLICY, "START ACTIVATION POLICY");
    DESCRIPTIONS.put(Bundle.START_TRANSIENT, "START TRANSIENT");
    DESCRIPTIONS.put(Bundle.STARTING, "STARTING...");
    DESCRIPTIONS.put(Bundle.STOP_TRANSIENT, "STOP TRANSIENT");
    DESCRIPTIONS.put(Bundle.STOPPING, "STOPPING...");
    DESCRIPTIONS.put(Bundle.UNINSTALLED, "UNINSTALLED");
  }

  /**
   * Instantiates a new bundle log helper.
   */
  private BundleLogHelper() {
  }

  /**
   * Log services.
   */
  public static void logBundlesState() {
    final Log log = LogFactory.getLog(BundleLogHelper.class);
    String result = "";
    for (final Bundle bundle : Platform.getBundle("org.eclipse.core.runtime").getBundleContext().getBundles()) {
      final String state = DESCRIPTIONS.get(bundle.getState());
      result += String.format("\r\n%3d %s %s", bundle.getBundleId(), state, bundle.getSymbolicName());
    }
    if (log.isInfoEnabled()) {
      log.info(result);
    }
  }
}
