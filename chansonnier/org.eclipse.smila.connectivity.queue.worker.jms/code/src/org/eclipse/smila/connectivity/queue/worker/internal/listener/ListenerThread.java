/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.listener;

/**
 * The Class ListenerThread.
 */
public class ListenerThread extends Thread {

  /**
   * The _worker.
   */
  private final ListenerWorker _worker;

  /**
   * Instantiates a new listener thread.
   *
   * @param worker
   *          the worker
   */
  public ListenerThread(final ListenerWorker worker) {
    super(worker, "QueueWorker-Listener@" + worker.hashCode());
    _worker = worker;
  }

  /**
   * Stop worker.
   */
  public void stopWorker() {
    _worker.suggestToStop();
  }

}
