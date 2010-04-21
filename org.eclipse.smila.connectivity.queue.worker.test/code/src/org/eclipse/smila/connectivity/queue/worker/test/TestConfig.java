/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.queue.BrokerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.connectivity.queue.worker.RouterException;
import org.eclipse.smila.connectivity.queue.worker.config.BrokerConnectionType;
import org.eclipse.smila.connectivity.queue.worker.config.ConnectionsConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerRuleType;
import org.eclipse.smila.connectivity.queue.worker.config.ListenerTaskListType;
import org.eclipse.smila.connectivity.queue.worker.config.ObjectFactory;
import org.eclipse.smila.connectivity.queue.worker.config.ProcessType;
import org.eclipse.smila.connectivity.queue.worker.config.PropertyType;
import org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerRuleType;
import org.eclipse.smila.connectivity.queue.worker.config.RecordRecyclerTaskListType;
import org.eclipse.smila.connectivity.queue.worker.config.RouterConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.RouterRuleType;
import org.eclipse.smila.connectivity.queue.worker.config.RouterTaskListType;
import org.eclipse.smila.connectivity.queue.worker.config.SendType;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionException;
import org.eclipse.smila.utils.jaxb.JaxbUtils;
import org.xml.sax.SAXException;

/**
 * The Class TestForCoverage.
 */
public class TestConfig extends TestCase {

  /**
   * The Constant SCHEMA_BUNDLE_ID.
   */
  private static final String SCHEMA_BUNDLE_ID = "org.eclipse.smila.connectivity.queue.worker";

  /**
   * Test status.
   */
  public void testStatus() {
    assertEquals(2 + 2 + 2, RecordRecyclerStatus.values().length);
    assertEquals(RecordRecyclerStatus.EXCEPTION, RecordRecyclerStatus.valueOf("EXCEPTION"));
  }

  /**
   * Test exceptions constructor.
   */
  public void testExceptionsConstructor() {
    final Throwable e = new RuntimeException();
    assertNotNull(new BrokerConnectionException(e).getCause());
    assertNotNull(new RecordRecyclerException(e).getCause());
    assertNotNull(new RouterException(e).getCause());
    assertNotNull(new RouterException("msg").getMessage());
    final RouterException re = new RouterException("msg", e);
    assertNotNull(re.getMessage());
    assertNotNull(re.getCause());
    assertNotNull(new BrokerException("Test exception", e).getCause());
  }

  /**
   * Test config.
   * 
   * @throws Exception
   *           the exception
   */
  public void testConnectionsConfig() throws Exception {
    final ObjectFactory factory = new ObjectFactory();
    final ConnectionsConfigType brokerConnections = factory.createConnectionsConfigType();
    final BrokerConnectionType brokerConnection = factory.createBrokerConnectionType();
    brokerConnections.getConnectionConfig().add(brokerConnection);
    final QueueConnectionType connection = factory.createQueueConnectionType();
    assertNotNull(connection);
    brokerConnection.setConnectionFactory("factory");
    brokerConnection.setId("id");
    brokerConnection.setPassword("password");
    brokerConnection.setURL("url");
    brokerConnection.setUser("user");
    final JAXBElement<ConnectionsConfigType> element =
      factory.createQueueWorkerConnectionsConfig(brokerConnections);
    marshall(element);
  }

  /**
   * Test router config.
   * 
   * @throws SAXException
   *           the SAX exception
   * @throws JAXBException
   *           the JAXB exception
   */
  public void testRouterConfig() throws JAXBException, SAXException {
    final ObjectFactory factory = new ObjectFactory();
    final RouterConfigType rootConfig = factory.createRouterConfigType();
    final RouterRuleType routerRuleType = factory.createRouterRuleType();
    rootConfig.getRule().add(routerRuleType);
    routerRuleType.setCondition("Condition");
    routerRuleType.setName("Name");
    final RouterTaskListType taskListType = factory.createRouterTaskListType();
    routerRuleType.setTask(taskListType);
    taskListType.setBlackboardSync(true);
    final ProcessType processType = factory.createProcessType();
    processType.setWorkflow("Workflow");
    taskListType.getProcessOrSend().add(processType);
    final SendType sendType = factory.createSendType();
    sendType.setBrokerId("BrokerId");
    sendType.setPersistentDelivery(true);
    assertTrue(sendType.isPersistentDelivery());
    sendType.setQueue("Queue");
    sendType.setRecordFilter("Filter");
    sendType.setWithAttachments(true);
    final PropertyType property = factory.createPropertyType();
    property.setName("Name");
    property.setValue("Value");
    sendType.getSetProperty().add(property);
    assertTrue(sendType.isWithAttachments());
    marshall(factory.createQueueWorkerRouterConfig(rootConfig));
  }

  /**
   * Test listener config.
   * 
   * @throws SAXException
   *           the SAX exception
   * @throws JAXBException
   *           the JAXB exception
   */
  public void testListenerConfig() throws JAXBException, SAXException {
    final ObjectFactory factory = new ObjectFactory();
    final ListenerConfigType rootConfig = factory.createListenerConfigType();
    final ListenerRuleType ruleType = factory.createListenerRuleType();
    rootConfig.getRule().add(ruleType);
    final QueueConnectionType source = factory.createQueueConnectionType();
    source.setBrokerId("broker");
    source.setQueue("queue");
    ruleType.setSource(source);
    ruleType.setCondition("Condition");
    ruleType.setName("Name");
    final ListenerTaskListType taskListType = factory.createListenerTaskListType();
    ruleType.setTask(taskListType);
    taskListType.setBlackboardSync(true);
    taskListType.setInitiallySet(true);
    final ProcessType processType = factory.createProcessType();
    processType.setWorkflow("Workflow");
    taskListType.getProcessOrSend().add(processType);
    final SendType sendType = factory.createSendType();
    sendType.setBrokerId("BrokerId");
    sendType.setPersistentDelivery(true);
    sendType.setQueue("Queue");
    sendType.setRecordFilter("Filter");
    sendType.setWithAttachments(true);
    marshall(factory.createQueueWorkerListenerConfig(rootConfig));
  }

  /**
   * Test recycler config.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerConfig() throws Exception {
    final ObjectFactory factory = new ObjectFactory();
    final RecordRecyclerConfigType rootConfig = factory.createRecordRecyclerConfigType();
    final RecordRecyclerRuleType ruleType = factory.createRecordRecyclerRuleType();
    rootConfig.getRule().add(ruleType);
    ruleType.setCondition("Condition");
    ruleType.setName("Name");
    final RecordRecyclerTaskListType taskListType = factory.createRecordRecyclerTaskListType();
    ruleType.setTask(taskListType);
    taskListType.setBlackboardSync(true);
    final ProcessType processType = factory.createProcessType();
    processType.setWorkflow("Workflow");
    taskListType.getProcessOrSend().add(processType);
    final SendType sendType = factory.createSendType();
    sendType.setBrokerId("BrokerId");
    sendType.setPersistentDelivery(true);
    sendType.setQueue("Queue");
    sendType.setRecordFilter("Filter");
    sendType.setWithAttachments(true);
    marshall(factory.createQueueWorkerRecordRecyclerJob(rootConfig));
  }

  /**
   * Marshall.
   * 
   * @param element
   *          the element
   * 
   * @throws JAXBException
   *           the JAXB exception
   * @throws SAXException
   *           the SAX exception
   */
  private void marshall(final JAXBElement<?> element) throws JAXBException, SAXException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    JaxbUtils.marshall(element, SCHEMA_BUNDLE_ID, SCHEMA_BUNDLE_ID + ".config", ObjectFactory.class
      .getClassLoader(), "schemas/QueueWorkerConfig.xsd", bos);
    assertTrue(bos.toByteArray().length > 0);
  }
}
