/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import java.util.concurrent.locks.Lock;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskListExecutionService;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionService;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.recordstorage.RecordStorage;

/**
 * The Interface ServicesAccessPoint.
 */
public interface ServicesAccessPoint {

  /**
   * Create a new blackboard instance.
   *
   * @param persisting
   *          true to get a blackboard that writes to configured storages (at least binary, optionally record storage).
   *          false to get a transient blackboard only.
   * @return the blackboard instance
   * @throws BlackboardAccessException
   *           persisting blackboard could not be created (usually because binary storage is not active).
   */
  Blackboard getBlackboardService(boolean persisting) throws BlackboardAccessException;

  /**
   * Gets the record storage.
   *
   * @return the record storage
   */
  RecordStorage getRecordStorage();

  /**
   * Gets the workflow processor.
   *
   * @return the workflow processor
   */
  WorkflowProcessor getWorkflowProcessor();

  /**
   * Gets the broker connections.
   *
   * @return the broker connections
   */
  BrokerConnectionService getBrokerConnections();

  /**
   * Get task list execution service.
   *
   * @return the task list execution service
   */
  TaskListExecutionService getTaskListExecutionService();

  /**
   * Get the lock to notify that a client is still working and the the service should not yet been deactivated.
   * 
   * @return processing lock.
   */
  Lock getProcessingLock();
}
