/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.recycler;

import java.util.HashMap;
import java.util.Properties;

import javax.jms.Message;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractRule;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.jms.MessageSelectorEvalException;
import org.eclipse.smila.jms.MessageSelectorEvalHelper;
import org.eclipse.smila.processing.JMSMessageAnnotations;
import org.eclipse.smila.utils.log.RecordLifecycleLogHelper;

/**
 * The Class RecordRecyclerRule.
 */
public class RecyclerRule extends AbstractRule<RecordRecyclerRuleType> {

  /**
   * The _eval helper.
   */
  private final MessageSelectorEvalHelper _evalHelper;

  /**
   * Instantiates a new recycler rule.
   * 
   * @param accessPoint
   *          the access point
   * @param ruleConfig
   *          the rule config
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  public RecyclerRule(final ServicesAccessPoint accessPoint, final RecordRecyclerRuleType ruleConfig)
    throws RecordRecyclerException {
    super(accessPoint, ruleConfig);
    try {
      _evalHelper = new MessageSelectorEvalHelper(ruleConfig.getCondition());
    } catch (final MessageSelectorEvalException e) {
      throw new RecordRecyclerException(e);
    }
  }

  /**
   * Checks if is applied.
   * 
   * @param message
   *          the message
   * 
   * @return true, if is applied
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  public boolean isApplied(final Message message) throws RecordRecyclerException {
    try {
      return _evalHelper.evaluate(message);
    } catch (final MessageSelectorEvalException e) {
      throw new RecordRecyclerException(e);
    }
  }

  /**
   * Process.
   * 
   * @param record
   *          the record
   * 
   * @throws RecordRecyclerException
   *           the recycler exception
   */
  public void process(final Record record) throws RecordRecyclerException {
    final HashMap<Record, Properties> recordPropertyMap = new HashMap<Record, Properties>();
    final Properties messageProperties = new Properties();
    messageProperties.setProperty(JMSMessageAnnotations.PROPERTY_OPERATION, Operation.ADD.name());
    recordPropertyMap.put(record, messageProperties);

    try {
      _accessPoint.getTaskListExecutionService().execute(_accessPoint, _ruleConfig.getTask(), recordPropertyMap);
      if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {
        RecordLifecycleLogHelper.logRecordState("Record processing is repeated by rule [" + _ruleConfig.getName()
          + "] and operation [" + Operation.ADD.name() + "]", record.getId().getIdHash());
      }
    } catch (final Throwable e) {
      throw new RecordRecyclerException(e);
    }
  }

}
