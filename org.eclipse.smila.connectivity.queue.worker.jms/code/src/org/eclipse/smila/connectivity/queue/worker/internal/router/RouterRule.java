/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.router;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.jms.Message;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.RouterException;
import org.eclipse.smila.connectivity.queue.worker.config.RouterRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractRule;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.jms.MessageSelectorEvalException;
import org.eclipse.smila.jms.MessageSelectorEvalHelper;
import org.eclipse.smila.utils.log.RecordLifecycleLogHelper;

/**
 * The Class RouterRule.
 */
public class RouterRule extends AbstractRule<RouterRuleType> {

  /**
   * The _eval helper.
   */
  private final MessageSelectorEvalHelper _evalHelper;

  /**
   * Instantiates a new router rule.
   * 
   * @param accessPoint
   *          the access point
   * @param ruleConfig
   *          the rule config
   * 
   * @throws RouterException
   *           the router exception
   */
  public RouterRule(final ServicesAccessPoint accessPoint, final RouterRuleType ruleConfig) throws RouterException {
    super(accessPoint, ruleConfig);
    try {
      _evalHelper = new MessageSelectorEvalHelper(ruleConfig.getCondition());
    } catch (final MessageSelectorEvalException e) {
      throw new RouterException(e);
    }
  }

  /**
   * Checks if is applied.
   * 
   * @param message
   *          the message
   * 
   * @return true, if checks if is applied
   * 
   * @throws RouterException
   *           the router exception
   */
  public boolean isApplied(final Message message) throws RouterException {
    try {
      return _evalHelper.evaluate(message);
    } catch (final Throwable e) {
      throw new RouterException(e);
    }
  }

  /**
   * Route.
   * 
   * @param recordPropertyMap
   *          a map of records and jms message properties
   * 
   * @throws RouterException
   *           the router exception
   */
  public void route(final Map<Record, Properties> recordPropertyMap) throws RouterException {
    try {
      _accessPoint.getTaskListExecutionService().execute(_accessPoint, _ruleConfig.getTask(), recordPropertyMap);
      if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {

        final Iterator<Record> it = recordPropertyMap.keySet().iterator();
        while (it.hasNext()) {
          final Record record = it.next();
          final Properties props = recordPropertyMap.get(record);
          RecordLifecycleLogHelper.logRecordState("Record is routed with rule [" + _ruleConfig.getName()
            + "] and operation [" + props.getProperty(Operation.ADD.name()) + "]", record.getId().getIdHash());
        }
      }
    } catch (final Throwable e) {
      throw new RouterException(e);
    }
  }

}
