/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * Andreas Weber, Juergen Schumacher (empolis GmbH) - fix for 269967
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.task.impl;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.connectivity.queue.worker.config.PropertyType;
import org.eclipse.smila.connectivity.queue.worker.config.SendType;
import org.eclipse.smila.connectivity.queue.worker.internal.task.AbstractTask;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskExecutionEnv;
import org.eclipse.smila.connectivity.queue.worker.internal.task.TaskExecutionException;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterNotFoundException;

/**
 * The Class Send.
 */
public class Send extends AbstractTask<SendType> {

  /**
   * Instantiates a new send.
   */
  public Send() {
    super("TASK/Send");
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public Id[] executeInternal(final TaskExecutionEnv env, final SendType config,
    final Map<Id, Properties> idPropertyMap) throws TaskExecutionException, BlackboardAccessException,
    RecordFilterNotFoundException {
    Connection connection = null;
    Session session = null;
    try {
      // get cached connection, if do not cache connections -> socket error when many records pushed
      connection = env.getServices().getBrokerConnections().getConnection(config, true);
      connection.start();
      session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
      final Destination destination = session.createQueue(config.getQueue());
      final MessageProducer producer = session.createProducer(destination);
      if (config.isPersistentDelivery()) {
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
      } else {
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      }

      final Iterator<Entry<Id, Properties>> entries = idPropertyMap.entrySet().iterator();
      while (entries.hasNext()) {
        final Entry<Id, Properties> entry = entries.next();
        // get message record, optionally a filtered copy.
        final Record record = createMessageRecord(entry.getKey(), config, env);
        // prepare queue message. messages are actually sent on session.commit() below.
        producer.send(createMessage(config, record, entry.getValue(), session));
      }

      // we must commit here so that the message consumer find the correct record version in storages.
      env.getBlackboard().commit();
      env.setCommitRequired(false);
      // finally send the messages.
      session.commit();

      return idPropertyMap.keySet().toArray(new Id[idPropertyMap.size()]);
    } catch (final Throwable e) {
      _log.error(msg("Error"), e);
      rollbackQuietly(session);
      throw new TaskExecutionException(e);
    } finally {
      closeQuietly(session);
      closeQuietly(connection);
    }
  }

  /**
   * get record instance for ID, optionally filtered and with attachments.
   * 
   * @param id
   *          record ID
   * @param config
   *          configuration of Send task
   * @param env
   *          access to blackboard.
   * @return record
   * @throws BlackboardAccessException
   *           error reading record from Blackboard or filtering it.
   * @throws RecordFilterNotFoundException
   *           configured record filter does not exist.
   */
  private Record createMessageRecord(final Id id, final SendType config, final TaskExecutionEnv env)
    throws BlackboardAccessException, RecordFilterNotFoundException {
    Record record = null;
    if (StringUtils.isNotEmpty(config.getRecordFilter())) {
      record = env.getBlackboard().getRecord(id, config.getRecordFilter());
    } else {
      record = env.getBlackboard().getRecord(id);
    }
    // glue back attachments
    if (config.isWithAttachments()) {
      glueAttachmentsBack(env, record);
    }
    return record;
  }

  /**
   * Glue attachments back.
   * 
   * @param env
   *          the env
   * @param record
   *          the record
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  private void glueAttachmentsBack(final TaskExecutionEnv env, final Record record)
    throws BlackboardAccessException {
    final Blackboard blackboard = env.getBlackboard();
    final Iterator<String> attachmentNames = record.getAttachmentNames();
    while (attachmentNames.hasNext()) {
      final String attachmentName = attachmentNames.next();
      if (record.getAttachment(attachmentName) == null) {
        record.setAttachment(attachmentName, blackboard.getAttachment(record.getId(), attachmentName));
      }
    }
  }

  /**
   * Creates message.
   * 
   * @param config
   *          the config
   * @param record
   *          the record
   * @param messageProperties
   *          the jms message properties
   * @param session
   *          the session
   * 
   * @return the message
   * 
   * @throws JMSException
   *           the JMS exception
   */
  private Message createMessage(final SendType config, final Record record, final Properties messageProperties,
    final Session session) throws JMSException {
    final BytesMessage message = session.createBytesMessage();

    // set dynamic message properties
    if (messageProperties != null) {
      final Enumeration<?> propertyNames = messageProperties.propertyNames();
      while (propertyNames.hasMoreElements()) {
        final String name = (String) propertyNames.nextElement();
        message.setStringProperty(name, messageProperties.getProperty(name));
      }
    }

    // get static properties of config file
    for (final PropertyType property : config.getSetProperty()) {
      message.setStringProperty(property.getName(), property.getValue());
    }

    final byte[] serialized = DatamodelSerializationUtils.serialize2byteArray(record);
    message.writeBytes(serialized);
    return message;
  }

  /**
   * close connection if not null, catching and logging possible exceptions.
   * 
   * @param connection
   *          a JMS conection
   */
  private void closeQuietly(final Connection connection) {
    if (connection != null) {
      try {
        connection.stop();
      } catch (final Throwable e) {
        _log.error("Unable to stop connection", e);
      }
      try {
        connection.close();
      } catch (final Throwable e) {
        _log.error("Unable to close connection", e);
      }
    }
  }

  /**
   * rollback session if not null, catching and logging possible exceptions.
   * 
   * @param session
   *          a JMS session
   */
  private void rollbackQuietly(final Session session) {
    if (session != null) {
      try {
        session.rollback();
      } catch (final Throwable e1) {
        _log.error("Unable to rollback session", e1);
      }
    }
  }

  /**
   * close session if not null, catching and logging possible exceptions.
   * 
   * @param session
   *          a JMS session
   */
  private void closeQuietly(final Session session) {
    if (session != null) {
      try {
        session.close();
      } catch (final Throwable e1) {
        _log.error("Unable to close session", e1);
      }
    }
  }
}
