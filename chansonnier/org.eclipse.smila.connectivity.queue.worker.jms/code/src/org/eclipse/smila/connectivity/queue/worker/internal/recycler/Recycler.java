/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.recycler;

import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;

/**
 * The Interface RecordRecycler.
 */
public interface Recycler extends Runnable {

  /**
   * Start.
   */
  void start();

  /**
   * Stop.
   */
  void stop();

  /**
   * Recycle.
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  void recycle() throws RecordRecyclerException;

  /**
   * Stop recycle.
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  void stopRecycle() throws RecordRecyclerException;

  /**
   * Gets the status.
   * 
   * @return the status
   */
  RecordRecyclerStatus getStatus();

  /**
   * Gets the records recycled.
   * 
   * @return the records recycled
   */
  long getRecordsRecycled();

}
