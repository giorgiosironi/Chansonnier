/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.util;

/**
 * A Enum defining the states a CrawlThread can be in.
 */
public enum CrawlThreadState {

  /**
   * The different states. Running - the CrawlThread is actively running Stopped - the CrawlThread was stopped by user
   * interaction Finished - the CrawlThread completed it's crawl and finished without any fatal errors Aborted - the
   * CrawlThread aborted because of some fatal error
   */
  Running, Stopped, Finished, Aborted
}
