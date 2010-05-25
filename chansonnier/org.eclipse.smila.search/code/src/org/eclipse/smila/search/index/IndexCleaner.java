/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class does the garbage collection for index connections.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class IndexCleaner extends Thread {

  /**
   * Sleep time for garbage collection.
   */
  private static final int SLEEP_TIME = 500;

  /**
   * 
   */
  public IndexCleaner() {
    this.setDaemon(true); // garbage collector should no reason to kill process
    this.start(); // start garbage collector
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    final Log log = LogFactory.getLog(getClass());
    while (true) {
      try {
        sleep(SLEEP_TIME);
        IndexManager.doGarbageCollection();
      } catch (final InterruptedException e) {
        log.error("unable to set cleaner into wait state", e.fillInStackTrace());
      }
    }
  }
}
