/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker;

import org.eclipse.smila.management.performance.PerformanceCounter;

/**
 * The Interface ListenerRule.
 */
public interface ListenerRule {

  /**
   * Increment threads.
   */
  void increaseNoOfThreads();

  /**
   * Decrement threads.
   */
  void decreaseNoOfThreads();

  /**
   * Gets the threads quantity.
   * 
   * @return the threads quantity
   */
  int getNoOfThreads();

  /**
   * Sets the threads quantity.
   * 
   * @param size
   *          the new threads quantity
   */
  void setNoOfThreads(final int size);

  /**
   * Gets the records processed.
   * 
   * @return the records processed
   */
  PerformanceCounter getNoOfProcessedRecords();

  /**
   * Gets the maximum number of messages to be received from the Queue in one session quantity.
   * 
   * @return the maximum message block size
   */
  int getMaxMessageBlockSize();

  /**
   * Sets the maximum number of messages to receive in one session from the Queue..
   * 
   * @param size
   *          the new maximum message block size
   */
  void setMaxMessageBlockSize(final int size);
}
