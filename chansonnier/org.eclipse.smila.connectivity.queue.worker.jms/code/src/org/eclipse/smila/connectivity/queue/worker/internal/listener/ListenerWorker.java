/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator, Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.listener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.eclipse.smila.connectivity.queue.worker.ListenerException;
import org.eclipse.smila.connectivity.queue.worker.ListenerRule;
import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractLoggedComponent;
import org.eclipse.smila.connectivity.queue.worker.internal.ServicesAccessPoint;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.processing.JMSMessageAnnotations;
import org.eclipse.smila.utils.log.RecordLifecycleLogHelper;

/**
 * The Class ListenerWorker.
 */
public class ListenerWorker extends AbstractLoggedComponent implements Runnable {

  /**
   * The Constant MS.
   */
  private static final int MS = 1000;

  /**
   * The _access point.
   */
  private final ServicesAccessPoint _accessPoint;

  /**
   * The _rule config.
   */
  private final ListenerRuleType _ruleConfig;

  /**
   * The _connection.
   */
  private Connection _connection;

  /**
   * The _session.
   */
  private Session _session;

  /**
   * The _queue.
   */
  private Queue _queue;

  /**
   * The _consumer.
   */
  private MessageConsumer _consumer;

  /**
   * The _stop.
   */
  private boolean _stop;

  /**
   * The _rule.
   */
  private final ListenerRule _rule;

  /**
   * Instantiates a new listener worker.
   * 
   * @param accessPoint
   *          the access point
   * @param ruleConfig
   *          the rule config
   * @param number
   *          the number
   * @param rule
   *          the rule
   */
  public ListenerWorker(final ListenerRule rule, final ServicesAccessPoint accessPoint,
    final ListenerRuleType ruleConfig, final int number) {
    super(String.format("%s, thread:%d", ruleConfig.getName(), number));
    _rule = rule;
    _accessPoint = accessPoint;
    _ruleConfig = ruleConfig;
    if (_log.isDebugEnabled()) {
      _log.debug(msg("Started"));
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    initialize();
    final HashMap<Record, Properties> recordPropertiesMap = new HashMap<Record, Properties>();
    Message message = null;
    while (!_stop) {
      message = recieveMessage(true);
      _accessPoint.getProcessingLock().lock();
      try {
        while (message != null) {
          try {
            final Properties messageProperties = new Properties();
            final Record record = parseMessage(message, messageProperties);
            recordPropertiesMap.put(record, messageProperties);
            if (recordPropertiesMap.size() >= _rule.getMaxMessageBlockSize()) {
              message = null;
            } else {
              // try to get more messages
              message = recieveMessage(false);
            }
          } catch (final ListenerException e) {
            _log.error(e);
            rollbackSessionQuietly();
          }
        } // while

        if (!recordPropertiesMap.isEmpty()) {
          try {
            _accessPoint.getTaskListExecutionService().execute(_accessPoint, _ruleConfig.getTask(),
              recordPropertiesMap);
            // message.acknowledge();
            _session.commit();
            _rule.getNoOfProcessedRecords().increment();
            if (_log.isDebugEnabled()) {
              _log.debug(msg("JMS message processed."));
            }
          } catch (final Exception e) {
            _log.error(e);
            rollbackSessionQuietly();
          } finally {
            recordPropertiesMap.clear();
          }
        } // if
      } finally {
        _accessPoint.getProcessingLock().unlock();
      }
    } // while
    try {
      _session.close();
      if (_log.isDebugEnabled()) {
        _log.debug(msg("Session closed"));
      }
    } catch (final JMSException e) {
      _log.error(msg("Closing session"), e);
    }
    try {
      _connection.close();
      if (_log.isDebugEnabled()) {
        _log.debug(msg("Connection closed"));
      }
    } catch (final JMSException e) {
      _log.error(msg("Closing connection"), e);
    }
    if (_log.isDebugEnabled()) {
      _log.debug(msg("Stopped"));
    }
  }

  /**
   * Suggest to stop.
   */
  public void suggestToStop() {
    _stop = true;
  }

  /**
   * Initialize.
   */
  private void initialize() {
    try {
      _stop = false;
      // get not cached connection
      _connection = _accessPoint.getBrokerConnections().getConnection(_ruleConfig.getSource(), false);
      _connection.start();
      _session = _connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
      _queue = _session.createQueue(_ruleConfig.getSource().getQueue());
      _consumer = _session.createConsumer(_queue, _ruleConfig.getCondition());
    } catch (final Throwable e) {
      _log.error(e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Receive message.
   * 
   * @param wait
   *          boolean flag if to wait for messages or not
   * 
   * @return the message
   */
  private Message recieveMessage(boolean wait) {
    Message message = null;
    try {
      if (wait) {
        message = _consumer.receive(_ruleConfig.getWaitMessageTimeout() * MS);
      } else {
        message = _consumer.receiveNoWait();
      }
    } catch (final JMSException e) {
      _log.error(msg("Error during recieving message"), e);
      rollbackSessionQuietly();
    }
    return message;
  }

  /**
   * Parses the message, creates a Record and fills the messageProperties.
   * 
   * @param message
   *          the message
   * @param messageProperties
   *          a Properties object that is filled with the message properties
   * @return a Record
   * @throws ListenerException
   *           the listener exception
   */
  private Record parseMessage(final Message message, final Properties messageProperties) throws ListenerException {
    if (_log.isDebugEnabled()) {
      _log.debug(msg("Processing JMS message..."));
    }
    if (!(message instanceof BytesMessage)) {
      throw new ListenerException(msg("Message is not a ByteMessage"));
    }
    try {
      final BytesMessage byteMessage = (BytesMessage) message;

      final byte[] byteArray = new byte[(int) byteMessage.getBodyLength()];
      byteMessage.readBytes(byteArray);
      if (_log.isDebugEnabled()) {
        try {
          _log.debug(msg("Accepted bytes as STRING: " + new String(byteArray)));
        } catch (final Exception e) {
          _log.error(e);
        }
      }
      final Record record = DatamodelSerializationUtils.deserialize(byteArray);

      // get messageProperties
      final Enumeration names = byteMessage.getPropertyNames();
      while (names.hasMoreElements()) {
        final String name = (String) names.nextElement();
        final String value = byteMessage.getStringProperty(name);
        messageProperties.setProperty(name, value);

        // TODO: set message properties as annotation (ignoring data source and opertaion) ???
      }

      final String operationStr = messageProperties.getProperty(JMSMessageAnnotations.PROPERTY_OPERATION);
      // calculate operation
      Operation operation;
      try {
        operation = Operation.valueOf(operationStr);
      } catch (final Throwable e) {
        operation = Operation.NONE;
      }

      // logging
      if (_log.isDebugEnabled()) {
        _log.debug(msg(String.format("processMessage: Operation=%s; %s", operation.toString(),
          DatamodelSerializationUtils.serialize2string(record))));
      }
      if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {
        RecordLifecycleLogHelper.logRecordState("Record is processed by Listener with rule: ["
          + _ruleConfig.getName() + "] and operation [" + operation.name() + "]", record.getId().getIdHash());
      }
      return record;
    } catch (final Exception e) {
      throw new ListenerException(msg("processMessage"), e);
    }
  }

  /**
   * Roll back session quietly.
   */
  private void rollbackSessionQuietly() {
    try {
      _session.rollback();
    } catch (final JMSException e1) {
      _log.error(msg("JMSException"), e1);
    }
  }
}
