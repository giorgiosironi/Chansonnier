/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.router;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jms.Message;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.connectivity.queue.worker.RouterException;
import org.eclipse.smila.connectivity.queue.worker.config.RouterConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.RouterRuleType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.jms.MessageSelectorEvalHelper;
import org.eclipse.smila.processing.JMSMessageAnnotations;

/**
 * The Class RouterImpl.
 */
public class RouterImpl extends AbstractQueueService<RouterConfigType> implements Router {

  /**
   * The _rules.
   */
  private final Set<RouterRule> _rules = new LinkedHashSet<RouterRule>();

  /**
   * Instantiates a new router impl.
   */
  public RouterImpl() {
    super("Router");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#getConfigName()
   */
  @Override
  public String getConfigName() {
    return "QueueWorkerRouterConfig.xml";
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
    try {
      super.start();
      for (final RouterRuleType ruleConfig : _config.getRule()) {
        if (names.contains(ruleConfig.getName())) {
          throw new RouterException(String.format("Wrong configuration: rule name %s is not unique", ruleConfig
            .getName()));
        }
        names.add(ruleConfig.getName());
        _rules.add(new RouterRule(this, ruleConfig));
      }
      _log.info(msg(String.format("Started successfully, found %d rules", _rules.size())));
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
    _rules.clear();
    super.stop();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.Router#route(org.eclipse.smila.datamodel.record.Record,
   *      org.eclipse.smila.connectivity.queue.worker.Operation)
   */
  public Map<Id, Exception> route(final Record[] records, final Operation operation) throws RouterException {
    getProcessingLock().lock();
    final HashMap<Id, Exception> errorMap = new HashMap<Id, Exception>();
    try {
      if (getTaskListExecutionService() == null) {
        throw new RouterException("Router is not active anymore.");
      }

      final HashMap<RouterRule, Map<Record, Properties>> rulemap =
        new HashMap<RouterRule, Map<Record, Properties>>();
      // iterate over records, check if a rule applies and associate the record with the rule
      for (Record record : records) {
        final Properties messageProperties = createMessageProperties(record, operation);
        final Message message = prepareDummyMessage(record, messageProperties);

        boolean foundRule = false;
        for (final RouterRule rule : _rules) {
          if (rule.isApplied(message)) {
            Map<Record, Properties> recordPropertiesMap = rulemap.get(rule);
            if (recordPropertiesMap == null) {
              recordPropertiesMap = new HashMap<Record, Properties>();
              rulemap.put(rule, recordPropertiesMap);
            }
            recordPropertiesMap.put(record, messageProperties);
            foundRule = true;
            break;
          }
        } // for
        if (!foundRule) {
          final String msg = "Unable to find rule for record " + record.getId();
          if (_log.isErrorEnabled()) {
            _log.error(msg);
          }
          errorMap.put(record.getId(), new RouterException(msg));
        } // if
      } // for

      // route the records according to the rules
      for (RouterRule rule : rulemap.keySet()) {
        final Map<Record, Properties> recordPropertiesMap = rulemap.get(rule);
        if (recordPropertiesMap != null && !recordPropertiesMap.isEmpty()) {
          try {
            rule.route(recordPropertiesMap);
          } catch (RouterException e) {
            final String msg = "Error while routing record ";
            for (Record record : recordPropertiesMap.keySet()) {
              if (_log.isErrorEnabled()) {
                _log.error(msg + record.getId(), e);
              }
              errorMap.put(record.getId(), e);
            } // for
          } // catch
        } // if
      } // for
    } finally {
      getProcessingLock().unlock();
    }
    return errorMap;
  }

  /**
   * Prepare dummy message.
   * 
   * @param record
   *          the record
   * @param messageProperties
   *          the jms message properties
   * 
   * @return the message
   * 
   * @throws RouterException
   *           the router exception
   */
  private Message prepareDummyMessage(final Record record, final Properties messageProperties)
    throws RouterException {
    final Message message = MessageSelectorEvalHelper.createDummyMessage();
    try {
      if (messageProperties != null) {
        // set message properties
        final Enumeration<?> propertyNames = messageProperties.propertyNames();
        while (propertyNames.hasMoreElements()) {
          final String name = (String) propertyNames.nextElement();
          message.setStringProperty(name, messageProperties.getProperty(name));
        }
      }
    } catch (final Throwable e) {
      throw new RouterException(e);
    }
    return message;
  }

  /**
   * Creates jms message properties from the given record and operation.
   * 
   * @param record
   *          the Record
   * @param operation
   *          the Operation
   * @return the Properties
   */
  private Properties createMessageProperties(final Record record, final Operation operation) {
    final Properties properties = new Properties();

    // set named message properties
    properties.setProperty(JMSMessageAnnotations.PROPERTY_OPERATION, operation.toString());
    if (record != null) {
      if (record.getId() != null && record.getId().getSource() != null) {
        properties.setProperty(JMSMessageAnnotations.PROPERTY_SOURCE, record.getId().getSource());
      }

      // check for message properties annotation
      final MObject mObject = record.getMetadata();
      if (mObject != null && mObject.hasAnnotation(JMSMessageAnnotations.ANNOTATION_MESSAGE_PROPERTIES)) {
        final Annotation messageProperties = mObject.getAnnotation(JMSMessageAnnotations.ANNOTATION_MESSAGE_PROPERTIES);
        final Iterator<String> propertyNames = messageProperties.getValueNames();
        while (propertyNames.hasNext()) {
          final String propertyName = propertyNames.next();
          if (propertyName != null) {
            final String propertyValue = messageProperties.getNamedValue(propertyName);
            if (propertyValue != null) {
              properties.setProperty(propertyName, propertyValue);
            } // if
          } // if
        } // while
      } // if
    } // if
    return properties;
  }

}
