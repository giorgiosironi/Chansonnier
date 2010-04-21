/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smila.connectivity.queue.worker.ListenerRule;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractRule;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionException;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;
import org.eclipse.smila.management.performance.PerformanceCounter;
import org.eclipse.smila.management.performance.PerformanceCounterFormula;

/**
 * The Class ListenerRule.
 */
public class ListenerRuleImpl extends AbstractRule<ListenerRuleType> implements ListenerRule, ManagementAgent {

  /**
   * The _threads.
   */
  private final List<ListenerThread> _threads = new ArrayList<ListenerThread>();

  /**
   * The _agent name.
   */
  private final String _agentName;

  /**
   * The _records processed.
   */
  private final PerformanceCounter _recordsProcessed;

  /**
   * The _location.
   */
  private final ManagementAgentLocation _location;

  /**
   * The maxMessageBlockSize.
   */
  private int _maxMessageBlockSize;

  /**
   * Instantiates a new listener rule.
   * 
   * @param accessPoint
   *          the access point
   * @param ruleConfig
   *          the rule config
   * 
   * @throws BrokerConnectionException
   *           the broker connection exception
   */
  public ListenerRuleImpl(final ServicesAccessPoint accessPoint, final ListenerRuleType ruleConfig)
    throws BrokerConnectionException {
    super(accessPoint, ruleConfig);
    _agentName = ruleConfig.getName();
    _recordsProcessed = new PerformanceCounter(PerformanceCounterFormula.SIMPLE_COUNT_FORMULA);
    _location = ManagementRegistration.INSTANCE//
      .getCategory("QueueWorker")//
      .getCategory("Listener")//
      .getLocation(_agentName);
    _maxMessageBlockSize = ruleConfig.getMaxMessageBlockSize();
    ManagementRegistration.INSTANCE.registerAgent(_location, this);
    for (int i = 0; i < ruleConfig.getThreads(); i++) {
      increaseNoOfThreads();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#increaseNoOfThreads()
   */
  public void increaseNoOfThreads() {
    final ListenerThread thread;
    synchronized (_threads) {
      final ListenerWorker worker = new ListenerWorker(this, _accessPoint, _ruleConfig, _threads.size());
      thread = new ListenerThread(worker);
      _threads.add(thread);
    }
    thread.start();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#decreaseNoOfThreads()
   */
  public void decreaseNoOfThreads() {
    synchronized (_threads) {
      if (_threads.size() > 0) {
        final ListenerThread thread = _threads.remove(_threads.size() - 1);
        if (thread.isAlive()) {
          thread.stopWorker();
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#getNoOfThreads()
   */
  public int getNoOfThreads() {
    synchronized (_threads) {
      return _threads.size();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#setNoOfThreads(int)
   */
  public void setNoOfThreads(final int size) {
    if (size < 0) {
      throw new IllegalArgumentException("Threads quantity cannot be negative!");
    }
    synchronized (_threads) {
      while (_threads.size() < size) {
        increaseNoOfThreads();
      }
      while (_threads.size() > size) {
        decreaseNoOfThreads();
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#getNoOfProcessedRecords()
   */
  public PerformanceCounter getNoOfProcessedRecords() {
    return _recordsProcessed;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#getMaxMessageBlockSize()
   */
  public int getMaxMessageBlockSize() {
    return _maxMessageBlockSize;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.ListenerRule#setMaxMessageBlockSize(int)
   */
  public void setMaxMessageBlockSize(final int size) {
    _maxMessageBlockSize = size;
  }

  /**
   * Stop.
   */
  void stop() {
    synchronized (_threads) {
      while (_threads.size() > 0) {
        decreaseNoOfThreads();
      }
    }
    ManagementRegistration.INSTANCE.unregisterAgent(_location);
  }
}
