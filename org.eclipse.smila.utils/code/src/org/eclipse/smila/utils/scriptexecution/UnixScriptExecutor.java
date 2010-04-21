/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.scriptexecution;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes script on windows platform.
 */
public class UnixScriptExecutor implements ScriptExecutor {

  /**
   * Name of SHELL env variable.
   */
  private static final String SHELL_ENV_NAME = "SHELL";

  /**
   * Log.
   */
  private final Log _log = LogFactory.getLog(UnixScriptExecutor.class);

  /**
   * {@inheritDoc}
   */
  public int execute(final File file) throws IOException, InterruptedException {

    final String shell = System.getenv(SHELL_ENV_NAME);

    if (shell == null || shell.trim().length() == 0) {
      throw new RuntimeException("Environment variable '" + SHELL_ENV_NAME + "' is not set");
    }

    final ProcessBuilder processBuilder = new ProcessBuilder(shell);
    processBuilder.directory(file.getParentFile());
    processBuilder.redirectErrorStream(true);

    final Process shellProcess = processBuilder.start();

    InputStream inputStream = null;

    try {

      inputStream = new FileInputStream(file);

      final OutputStream shellProcessOutputStream = shellProcess.getOutputStream();

      IOUtils.copy(inputStream, shellProcessOutputStream);

      shellProcessOutputStream.close();

      final int retVal = shellProcess.waitFor();

      LogHelper.debug(_log, shellProcess.getInputStream());

      return retVal;

    } finally {

      IOUtils.closeQuietly(inputStream);
    }
  }
}
