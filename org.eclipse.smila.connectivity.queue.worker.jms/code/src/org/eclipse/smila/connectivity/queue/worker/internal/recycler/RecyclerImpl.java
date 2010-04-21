/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.recycler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jms.Message;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskListExecutionService;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionService;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.jms.MessageSelectorEvalHelper;
import org.eclipse.smila.processing.JMSMessageAnnotations;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.recordstorage.RecordStorage;
import org.eclipse.smila.recordstorage.RecordStorageException;

/**
 * The Class RecordRecyclerImpl.
 */
public class RecyclerImpl extends AbstractQueueService<RecordRecyclerConfigType> implements Recycler {

  /**
   * The _config name.
   */
  private final String _configName;

  /**
   * The _rules.
   */
  private final Set<RecyclerRule> _rules = new LinkedHashSet<RecyclerRule>();

  /**
   * The _access point.
   */
  private final ServicesAccessPoint _accessPoint;

  /**
   * The _iterator.
   */
  private Iterator<Record> _iterator;

  /**
   * The _data source id.
   */
  private final String _dataSourceId;

  /**
   * The _record number.
   */
  private long _recordNumber;

  /**
   * The _status.
   */
  private RecordRecyclerStatus _status;

  /**
   * The _status monitor.
   */
  private final Object _statusMonitor = new Object();

  /**
   * Instantiates a new recycler impl.
   * 
   * @param configName
   *          the config name
   * @param accessPoint
   *          the access point
   * @param dataSourceId
   *          the data source id
   */
  public RecyclerImpl(final String configName, final ServicesAccessPoint accessPoint, final String dataSourceId) {
    super("RecordRecycler");
    _configName = configName;
    _accessPoint = accessPoint;
    _dataSourceId = dataSourceId;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#getConfigName()
   */
  @Override
  public String getConfigName() {
    return String.format("recyclers/%s.xml", _configName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#start()
   */
  @Override
  public synchronized void start() {
    _log.info(msg("Starting..."));
    final Set<String> names = new HashSet<String>();
    _rules.clear();
    try {
      super.start();
      for (final RecordRecyclerRuleType ruleConfig : _config.getRule()) {
        if (names.contains(ruleConfig.getName())) {
          throw new RecordRecyclerException(String.format("Wrong configuration: rule name %s is not unique",
            ruleConfig.getName()));
        }
        names.add(ruleConfig.getName());
        _rules.add(new RecyclerRule(this, ruleConfig));
      }
      synchronized (_statusMonitor) {
        _status = RecordRecyclerStatus.STARTED;
      }
      _log.info(msg(String.format("Started successfully, found %d rules", _rules.size())));
    } catch (final Throwable e) {
      _log.error(msg("Error starting"), e);
      throw new RuntimeException(e);
    }
    try {
      _iterator = getRecordsIterator(_dataSourceId);
    } catch (final RecordRecyclerException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#stop()
   */
  @Override
  public synchronized void stop() {
    _rules.clear();
    super.stop();
    _status = RecordRecyclerStatus.FINISHED;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.recycler.Recycler#getRecordsRecycled()
   */
  public long getRecordsRecycled() {
    return _recordNumber;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.recycler.Recycler#getStatus()
   */
  public RecordRecyclerStatus getStatus() {
    return _status;
  }

  /**
   * (non-Javadoc).
   * 
   * @return the record storage
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueServicesAccessPoint#getRecordStorage()
   */
  @Override
  public RecordStorage getRecordStorage() {
    return _accessPoint.getRecordStorage();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueServicesAccessPoint#getWorkflowProcessor()
   */
  @Override
  public WorkflowProcessor getWorkflowProcessor() {
    return _accessPoint.getWorkflowProcessor();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueServicesAccessPoint#getBrokerConnections()
   */
  @Override
  public BrokerConnectionService getBrokerConnections() {
    return _accessPoint.getBrokerConnections();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.
   *      AbstractQueueServicesAccessPoint#getTaskListExecutionService()
   */
  @Override
  public TaskListExecutionService getTaskListExecutionService() {
    return _accessPoint.getTaskListExecutionService();
  }

  /**
   * Gets the records iterator.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @return the records iterator
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  private Iterator<Record> getRecordsIterator(final String dataSourceId) throws RecordRecyclerException {
    final Iterator<Record> iterator;
    try {
      iterator = getRecordStorage().loadRecords(dataSourceId);
    } catch (final RecordStorageException e) {
      throw new RecordRecyclerException(e);
    }
    if (!iterator.hasNext()) {
      throw new RecordRecyclerException(String.format("No one record found under %s data source!", dataSourceId));
    }
    return iterator;
  }

  /**
   * Prepate dummy message.
   * 
   * @param record
   *          the record
   * @param operation
   *          the operation
   * 
   * @return the message
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  private Message prepateDummyMessage(final Record record, final Operation operation)
    throws RecordRecyclerException {
    final Message message = MessageSelectorEvalHelper.createDummyMessage();
    try {
      message.setStringProperty(JMSMessageAnnotations.PROPERTY_OPERATION, operation.toString());
      if (record != null && record.getId() != null && record.getId().getSource() != null) {
        message.setStringProperty(JMSMessageAnnotations.PROPERTY_SOURCE, record.getId().getSource());
      }
    } catch (final Throwable e) {
      throw new RecordRecyclerException(e);
    }
    return message;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.recycler.Recycler#recycle()
   */
  public void recycle() throws RecordRecyclerException {
    synchronized (_statusMonitor) {
      _status = RecordRecyclerStatus.IN_PROGRESS;
    }
    final Operation operation = Operation.ADD;
    // iterate partition
    while (_iterator.hasNext()) {
      synchronized (_statusMonitor) {
        if (_status == RecordRecyclerStatus.STOPPING) {
          _log.info(String.format("Recycling for data source [%s] is stopped", _dataSourceId));
          break;
        } else {
          _log.info("CONtinue " + _status);
        }
      }
      final Record record = _iterator.next();
      _recordNumber++;
      if (_log.isDebugEnabled()) {
        _log.debug("repeating " + record.getId().getIdHash());
      }
      try {
        final Message message = prepateDummyMessage(record, operation);
        // small list of rules
        for (final RecyclerRule rule : _rules) {
          if (rule.isApplied(message)) {
            rule.process(record);
            break;
          }
        }
      } catch (final Throwable e) {
        _log.error(e);
      }
    }
    synchronized (_statusMonitor) {
      if (_status == RecordRecyclerStatus.STOPPING) {
        _status = RecordRecyclerStatus.STOPPED;
      } else {
        _status = RecordRecyclerStatus.FINISHED;
      }
    }
    _log.info("Recycling was finished");
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    try {
      recycle();
    } catch (final Throwable e) {
      synchronized (_statusMonitor) {
        _status = RecordRecyclerStatus.EXCEPTION;
      }
      _log.error("Recycling was finished unsuccessfully", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.recycler.Recycler#stopRecycle()
   */
  public void stopRecycle() throws RecordRecyclerException {
    synchronized (_statusMonitor) {
      if (_status == RecordRecyclerStatus.STARTED || _status == RecordRecyclerStatus.IN_PROGRESS) {
        _status = RecordRecyclerStatus.STOPPING;
      }
    }
  }
}
