/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.scriptexecution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Platform;

/**
 * Factory for script executors.
 */
public final class ScriptExecutorFactory {

  /**
   * Default constructor.
   */
  private ScriptExecutorFactory() {
  }

  /**
   * Returns script executor according to current platform.
   * 
   * @return script executor
   */
  public static ScriptExecutor getScriptExecutor() {
    final Log log = LogFactory.getLog(ScriptExecutorFactory.class);
    final String platform = Platform.getOS();

    if (log.isDebugEnabled()) {
      log.debug("Getting script executor for " + platform);
    }

    if (Platform.OS_WIN32.equals(platform)) {
      return new WindowsScriptExecutor();
    } else if (Platform.OS_LINUX.equals(platform) || Platform.OS_SOLARIS.equals(platform)) {
      return new UnixScriptExecutor();
    } else {
      throw new IllegalStateException("Valid script executor is missing for [" + platform + "] platform.");
    }
  }
}
