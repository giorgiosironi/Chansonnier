/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.smila.connectivity.queue.worker.Listener;
import org.eclipse.smila.connectivity.queue.worker.ListenerException;
import org.eclipse.smila.connectivity.queue.worker.ListenerRule;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService;

/**
 * The Class ListenerImpl.
 */
public class ListenerImpl extends AbstractQueueService<ListenerConfigType> implements Listener {

  /**
   * The _rules.
   */
  private ListenerRuleImpl[] _rules;

  /**
   * Instantiates a new listener impl.
   */
  public ListenerImpl() {
    super("Listener");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#getConfigName()
   */
  @Override
  public String getConfigName() {
    return "QueueWorkerListenerConfig.xml";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#start()
   */
  @Override
  public synchronized void start() {
    _log.info(msg("Starting..."));
    final List<ListenerRuleImpl> rules = new ArrayList<ListenerRuleImpl>();
    try {
      super.start();
      final Set<String> names = new HashSet<String>();
      for (final ListenerRuleType ruleConfig : _config.getRule()) {
        if (names.contains(ruleConfig.getName())) {
          throw new ListenerException(String.format("Wrong configuration: rule name %s is not unique", ruleConfig
            .getName()));
        }
        names.add(ruleConfig.getName());
        rules.add(new ListenerRuleImpl(this, ruleConfig));
      }
      _rules = rules.toArray(new ListenerRuleImpl[rules.size()]);
      _log.info(msg(String.format("Started successfully, found %d rules", rules.size())));
    } catch (final Throwable e) {
      _log.error(msg("Error starting"), e);
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
    for (final ListenerRuleImpl rule : _rules) {
      rule.stop();
    }
    super.stop();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.Listener#getRules()
   */
  public ListenerRule[] getRules() {
    return _rules;
  }
}
