/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Logs information about records state to the pre-defined logger.
 * 
 */
public final class RecordLifecycleLogHelper {

  /**
   * Logger name.
   */
  private static final String RECORDS_LOGGER = "Records";

  /**
   * 
   */
  private RecordLifecycleLogHelper() {

  }

  /**
   * Checks if logging of record state is enabled.
   * 
   * @return boolean
   */
  public static boolean isRecordStateLogEnabled() {
    final Log log = LogFactory.getLog(RECORDS_LOGGER);
    return log.isInfoEnabled();
  }

  /**
   * Logs record state.
   * 
   * @param message
   *          message
   * @param idHash
   *          String value of record hash.
   */
  public static void logRecordState(String message, String idHash) {
    final Log log = LogFactory.getLog(RECORDS_LOGGER);
    if (log.isInfoEnabled()) {
      log.info(message + ", record id=" + idHash);
    }
  }
}
