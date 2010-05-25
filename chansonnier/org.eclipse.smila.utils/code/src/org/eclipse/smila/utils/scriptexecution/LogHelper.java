/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.scriptexecution;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;

/**
 * Log helper.
 */
public final class LogHelper {

  /**
   * Default constructor.
   */
  private LogHelper() {
  }

  /**
   * Log data from input stream with level DEBUG.
   * 
   * @param log
   *          log
   * @param inputStream
   *          input stream
   * 
   * @throws IOException
   *           IOException
   */
  public static void debug(final Log log, final InputStream inputStream) throws IOException {
    log(log, inputStream, LogLevel.DEBUG);
  }

  /**
   * Log data from input stream with level INFO.
   * 
   * @param log
   *          log
   * @param inputStream
   *          input stream
   * 
   * @throws IOException
   *           IOException
   */
  public static void info(final Log log, final InputStream inputStream) throws IOException {
    log(log, inputStream, LogLevel.INFO);
  }

  /**
   * Log data from input stream with level WARN.
   * 
   * @param log
   *          log
   * @param inputStream
   *          input stream
   * 
   * @throws IOException
   *           IOException
   */
  public static void warn(final Log log, final InputStream inputStream) throws IOException {
    log(log, inputStream, LogLevel.WARN);
  }

  /**
   * Log data from input stream with level ERROR.
   * 
   * @param log
   *          log
   * @param inputStream
   *          input stream
   * 
   * @throws IOException
   *           IOException
   */
  public static void error(final Log log, final InputStream inputStream) throws IOException {
    log(log, inputStream, LogLevel.ERROR);
  }

  /**
   * Log info from input stream with specified level.
   * 
   * @param log
   *          log
   * @param inputStream
   *          input stream
   * @param logLevel
   *          log level
   * 
   * @throws IOException
   *           IOException
   */
  private static void log(final Log log, final InputStream inputStream, final LogLevel logLevel) throws IOException {

    final LineIterator lineIterator = IOUtils.lineIterator(inputStream, null);

    while (lineIterator.hasNext()) {
      final String string = lineIterator.nextLine();

      log(log, string, logLevel);
    }
  }

  /**
   * Logs message with specified level.
   * 
   * @param log
   *          log
   * @param message
   *          message
   * @param logLevel
   *          log level
   */
  private static void log(final Log log, final String message, final LogLevel logLevel) {
    if (LogLevel.DEBUG.equals(logLevel)) {
      log.debug(message);
    } else if (LogLevel.INFO.equals(logLevel)) {
      log.info(message);
    } else if (LogLevel.WARN.equals(logLevel)) {
      log.warn(message);
    } else if (LogLevel.ERROR.equals(logLevel)) {
      log.error(message);
    } else {
      throw new IllegalArgumentException("Unknown log level [" + logLevel + "]");
    }
  }

  /**
   * Log level.
   */
  private enum LogLevel {

    /**
     * Debug.
     */
    DEBUG,

    /**
     * Info.
     */
    INFO,

    /**
     * Warn.
     */
    WARN,

    /**
     * Error.
     */
    ERROR;
  }
}
