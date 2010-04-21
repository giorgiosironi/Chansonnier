/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;

/**
 * Utility class that contains the state of a crawl run.
 */
public class CrawlState extends State {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 7499001878583883888L;

 
  /**
   * State of the CrawlThread.
   */
  private CrawlThreadState _state;

  
  /**
   * Returns the state of the CrawlThread.
   * 
   * @return the state of the CrawlThread.
   */
  public CrawlThreadState getState() {
    return _state;
  }

  /**
   * Set the state of the CrawlThread.
   * 
   * @param state
   *          the state of the CrawlThread.
   */
  public void setState(final CrawlThreadState state) {
    _state = state;
  }


  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public CrawlState clone() throws CloneNotSupportedException {
    super.clone();
    final CrawlState crawlState = new CrawlState();
    crawlState.setDataSourceId(getDataSourceId());
    crawlState.setEndTime(getEndTime());
    crawlState.setLastError(getLastError());
    crawlState.setStartTime(getStartTime());
    crawlState.setState(getState());
    crawlState.setJobId(getJobId());

    return crawlState;
  }
}
