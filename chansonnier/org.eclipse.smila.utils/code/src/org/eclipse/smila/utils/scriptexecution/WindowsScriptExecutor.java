/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.scriptexecution;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes script on windows platform.
 */
public class WindowsScriptExecutor implements ScriptExecutor {

  /**
   * Log.
   */
  private final Log _log = LogFactory.getLog(WindowsScriptExecutor.class);

  /**
   * {@inheritDoc}
   */
  public int execute(final File file) throws IOException, InterruptedException {

    _log.debug("Executing " + file.getAbsolutePath());

    final String fileName = file.getName();

    final IOCase ioCase = IOCase.SYSTEM;

    if (!ioCase.checkEndsWith(fileName, ".cmd") && !ioCase.checkEndsWith(fileName, ".bat")) {

      _log.debug("It doesn't end with .cmd or .bat");

      final File tempFile = getTempCmdFile(file.getParentFile());

      try {

        _log.debug("Copy " + file.getAbsolutePath() + " to " + tempFile);
        FileUtils.copyFile(file, tempFile);

        return doExecute(tempFile);

      } finally {

        tempFile.delete();
      }
    } else {

      return doExecute(file);
    }
  }

  /**
   * Do execution of given command file.
   * 
   * @param file
   *          file
   * 
   * @return result code
   * 
   * @throws IOException
   *           IOException
   * @throws InterruptedException
   *           InterruptedException
   */
  private int doExecute(final File file) throws IOException, InterruptedException {

    _log.debug("Do Execute " + file.getAbsolutePath());

    final ProcessBuilder processBuilder = new ProcessBuilder(file.getAbsolutePath());
    processBuilder.directory(file.getParentFile());
    processBuilder.redirectErrorStream(true);

    final Process process = processBuilder.start();

    final int resultCode = process.waitFor();

    LogHelper.debug(_log, process.getInputStream());

    return resultCode;
  }

  /**
   * Returns temp file with .cmd extension in specified folder.
   * 
   * @param folder
   *          folder
   * 
   * @return temp file
   */
  private File getTempCmdFile(final File folder) {

    File tempFile = null;

    do {
      final String fileName = ".temp-cmd-file-" + hashCode() + "-" + System.currentTimeMillis() + ".cmd";

      tempFile = new File(folder, fileName);
    } while (tempFile.exists());

    return tempFile;
  }
}
