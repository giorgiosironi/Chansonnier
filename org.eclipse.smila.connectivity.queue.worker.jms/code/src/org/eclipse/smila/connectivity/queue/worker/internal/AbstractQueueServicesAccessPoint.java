/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskListExecutionService;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionService;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.recordstorage.RecordStorage;

/**
 * The Class AbstractQueueServicesAccessPoint.
 */
public abstract class AbstractQueueServicesAccessPoint extends AbstractLoggedComponent implements
  ServicesAccessPoint {

  /**
   * The broker connections.
   */
  protected BrokerConnectionService _brokerConnections;

  /**
   * The workflow processor.
   */
  private WorkflowProcessor _workflowProcessor;

  /**
   * The blackboard factory service.
   */
  private BlackboardFactory _blackboardFactory;

  /**
   * The _record storage.
   */
  private RecordStorage _recordStorage;

  /**
   * The task list execution service.
   */
  private TaskListExecutionService _taskListExecutionService;

  /**
   * process methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * Instantiates a new abstract queue worker service.
   *
   * @param id
   *          the id
   */
  public AbstractQueueServicesAccessPoint(final String id) {
    super(id);
  }

  /**
   * Sets the broker connections.
   *
   * @param brokerConnections
   *          the new broker connections
   */
  public void setBrokerConnections(final BrokerConnectionService brokerConnections) {
    _brokerConnections = brokerConnections;
  }

  /**
   * Unset broker connections.
   *
   * @param brokerConnections
   *          the broker connections
   */
  public void unsetBrokerConnections(final BrokerConnectionService brokerConnections) {
    if (brokerConnections == _brokerConnections) {
      getManagementLock().lock();
      _brokerConnections = null;
      getManagementLock().unlock();

    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker
   *      #setBlackboardService(org.eclipse.smila.blackboard.Blackboard)
   */
  public void setBlackboardFactory(final BlackboardFactory blackboardFactory) {
    _blackboardFactory = blackboardFactory;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker
   *      #unsetBlackboardService(org.eclipse.smila.blackboard.Blackboard)
   */
  public void unsetBlackboardFactory(final BlackboardFactory blackboardFactory) {
    if (_blackboardFactory == blackboardFactory) {
      getManagementLock().lock();
      _blackboardFactory = null;
      getManagementLock().unlock();
    }
  }

  /**
   * Sets the record storage.
   *
   * @param recordStorage
   *          the new record storage
   */
  public void setRecordStorage(final RecordStorage recordStorage) {
    _recordStorage = recordStorage;
  }

  /**
   * Unset record storage.
   *
   * @param recordStorage
   *          the record storage
   */
  public void unsetRecordStorage(final RecordStorage recordStorage) {
    if (recordStorage == _recordStorage) {
      getManagementLock().lock();
      _recordStorage = null;
      getManagementLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker
   *      #setWorkflowProcessor(org.eclipse.smila.processing.WorkflowProcessor)
   */
  public void setWorkflowProcessor(final WorkflowProcessor workflowProcessor) {
    _workflowProcessor = workflowProcessor;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker
   *      #unsetWorkflowProcessor(org.eclipse.smila.processing.WorkflowProcessor)
   */
  public void unsetWorkflowProcessor(final WorkflowProcessor workflowProcessor) {
    if (workflowProcessor == _workflowProcessor) {
      getManagementLock().lock();
      _workflowProcessor = null;
      getManagementLock().unlock();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint#getBrokerConnections()
   */
  public BrokerConnectionService getBrokerConnections() {
    if (_brokerConnections == null) {
      throw new RuntimeException("BrokerConnectionService is not set!");
    }
    return _brokerConnections;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker#getBlackboardService()
   */
  public Blackboard getBlackboardService(final boolean persisting) throws BlackboardAccessException {
    if (_blackboardFactory == null) {
      throw new RuntimeException("BlackboardService is not set!");
    }
    if (persisting) {
      return _blackboardFactory.createPersistingBlackboard();
    } else {
      return _blackboardFactory.createTransientBlackboard();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint#getRecordStorage()
   */
  public RecordStorage getRecordStorage() {
    if (_recordStorage == null) {
      throw new RuntimeException("RecordStorage is not set!");
    }
    return _recordStorage;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.connectivity.queue.QueueWorker#getWorkflowProcessor()
   */
  public WorkflowProcessor getWorkflowProcessor() {
    if (_workflowProcessor == null) {
      throw new RuntimeException("WorkflowProcessor is not set!");
    }
    return _workflowProcessor;
  }

  /**
   * Gets the task list execution service.
   *
   * @return the task list execution service
   */
  public TaskListExecutionService getTaskListExecutionService() {
    if (_taskListExecutionService == null) {
      throw new RuntimeException("TaskListExecutionService is not set!");
    }
    return _taskListExecutionService;
  }

  /**
   * {@inheritDoc}
   */
  public Lock getProcessingLock() {
    return _lock.readLock();
  }

  /**
   * @return lock to use before unbinding services and deactivating completely to prevent deactivation while messages
   *         are processed.
   */
  protected Lock getManagementLock() {
    return _lock.writeLock();
  }

  /**
   * Start.
   */
  public void start() {
    _taskListExecutionService = new TaskListExecutionService();
  }

  /**
   * Stop.
   */
  public void stop() {
    _taskListExecutionService = null;
  }

}
