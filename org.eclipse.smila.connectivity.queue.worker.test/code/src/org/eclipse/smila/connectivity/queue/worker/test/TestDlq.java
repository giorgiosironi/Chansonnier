/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.connectivity.queue.worker.RouterException;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.test.RecordCreator;
import org.eclipse.smila.datamodel.tools.DatamodelDeserializationException;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.jms.ConnectionFactoryRegistry;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class used for testing that DLQ works with router/listener environment.
 * 
 * Listener causes error by wrong pipeline execution; Finally record automatically put into DLQ.
 * 
 * We extract it and compare.
 */
public class TestDlq extends DeclarativeServiceTestCase {

  /**
   * The Constant TEST_LIMIT.
   */
  private static final int TEST_LIMIT = 2;

  /**
   * The Constant MQ_FACTORY_NAME.
   */
  private static final String MQ_FACTORY_NAME = "org.apache.activemq.ActiveMQConnectionFactory";

  /**
   * The Constant MQ_BROKER_URL.
   */
  // private static final String MQ_BROKER_URL = "tcp://localhost:61616";
  private static final String MQ_BROKER_URL = "vm://localhost";

  /**
   * The Constant MQ_USER.
   */
  private static final String MQ_USER = "any";

  /**
   * The Constant MQ_PASSWORD.
   */
  private static final String MQ_PASSWORD = "any";

  /**
   * The Constant DLQ_NAME.
   */
  private static final String DLQ_NAME = "ActiveMQ.DLQ";

  // private static final int PROCESSING_PAUSE = 5000;

  /**
   * The Constant CONSUMER_WAITING_TIME.
   */
  private static final int CONSUMER_WAITING_TIME = 30000;

  /**
   * The Constant CONSUMER_WAITING_TIME_EMPTY.
   */
  private static final int CONSUMER_WAITING_TIME_EMPTY = 5000;

  /**
   * The Constant DATASOURCE_THAT_CAUSE_ERROR_BY_NO_BROKER.
   */
  private static final String DATASOURCE_THAT_CAUSE_ERROR_BY_NO_BROKER = "error_no_broker";

  /**
   * The Constant DATASOURCE_THAT_CAUSE_ERROR_BY_NO_PIPELINE.
   */
  private static final String DATASOURCE_THAT_CAUSE_ERROR_BY_NO_PIPELINE = "error_ErrorNOPipeline";

  /**
   * The Constant DATASOURCE_THAT_CAUSE_ERROR_BY_ERROR_PIPELET_PIPELINE.
   */
  private static final String DATASOURCE_THAT_CAUSE_ERROR_BY_ERROR_PIPELET_PIPELINE = "error_ErrorPipeletPipeline";

  /**
   * The _router.
   */
  protected Router _router;

  /**
   * The _connection factory.
   */
  protected ConnectionFactory _connectionFactory;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _log.info("getting Router...");
    _router = getService(Router.class);
    _log.info("Creating JMS Conneciton factory...");
    _connectionFactory =
      ConnectionFactoryRegistry.getConnectionFactory(MQ_FACTORY_NAME, MQ_BROKER_URL, MQ_USER, MQ_PASSWORD);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _connectionFactory = null;
    _router = null;
  }

  /**
   * Test - no broker cause.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNoBrokerCause() throws Exception {
    executeDLQCauseCycle(DATASOURCE_THAT_CAUSE_ERROR_BY_NO_BROKER);
  }

  /**
   * Test no pipeline cause.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNoPipelineCause() throws Exception {
    executeDLQCauseCycle(DATASOURCE_THAT_CAUSE_ERROR_BY_NO_PIPELINE);
  }

  /**
   * Test error pipelet pipeline cause.
   * 
   * @throws Exception
   *           the exception
   */
  public void testErrorPipeletPipelineCause() throws Exception {
    executeDLQCauseCycle(DATASOURCE_THAT_CAUSE_ERROR_BY_ERROR_PIPELET_PIPELINE);
  }

  /* Private Methods Section */

  /**
   * Execute dlq cause cycle.
   * 
   * @param dataSourceID
   *          the data source id
   * 
   * @throws RouterException
   *           the router exception
   * @throws InterruptedException
   *           the interrupted exception
   * @throws JMSException
   *           the JMS exception
   * @throws DatamodelDeserializationException
   *           the datamodel deserialization exception
   */
  private void executeDLQCauseCycle(final String dataSourceID) throws RouterException, InterruptedException,
    JMSException, DatamodelDeserializationException {
    _log.info(String.format("test DLQ with dataSource %s", dataSourceID));
    assertNotNull(_router);
    assertNotNull(_connectionFactory);
    for (int i = 0; i < TEST_LIMIT; i++) {
      executeDLQMessageCycle(String.format("key_%s_%d", dataSourceID, i), dataSourceID);
      assertThatDLQIsEmpty();
    }
  }

  /**
   * Execute dlq message cycle.
   * 
   * @param key
   *          the key source
   * @param dlqDataSourceID
   *          the dlq data source id
   * 
   * @throws RouterException
   *           the router exception
   * @throws InterruptedException
   *           the interrupted exception
   * @throws JMSException
   *           the JMS exception
   * @throws DatamodelDeserializationException
   *           the datamodel deserialization exception
   */
  private void executeDLQMessageCycle(final String key, final String dlqDataSourceID) throws RouterException,
    InterruptedException, JMSException, DatamodelDeserializationException {
    _log.info("executing DLQ message cycle with keySource =  " + key);
    // "toDLQ" source ID will cause that message will redirect, finally, into DLQ
    // because it will try to invoke absent pipeline makeERRORPipeline
    _log.info("Creating new record with  = key " + key);
    final Record record = RecordCreator.createTestRecord(dlqDataSourceID, key);
    assertNotNull(record);
    _log.info("New record created, routing...");
    _router.route(new Record[]{record}, Operation.ADD);
    // _log.info(String.format("Record router, waiting %d ...", PROCESSING_PAUSE));
    // Thread.sleep(PROCESSING_PAUSE);
    _log.info("Continue...");
    // should be in DLQ already
    // check DLQ size directly via JMS to avoid
    final BytesMessage message = extractJMSMessageFromDLQ(CONSUMER_WAITING_TIME);
    assertNotNull("Its expected but where is no massage in DLQ!", message);
    final String dataSourceID = message.getStringProperty("DataSourceID");
    _log.info(String.format("Extracted record with JMS property DataSourceID = \"%s\"", dataSourceID));
    assertEquals(dataSourceID, dlqDataSourceID);
    // read bytes
    final byte[] byteArray = new byte[(int) message.getBodyLength()];
    message.readBytes(byteArray);
    // deserialize record from byte array
    final Record recordAccepted = DatamodelSerializationUtils.deserialize(byteArray);
    assertEquals("Record id read from DLQ is not expected one!", record.getId(), recordAccepted.getId());
    _log.info(String.format("Extracted record with datasourceID = \"%s\" ; id.toString()= \"%s\"", recordAccepted
      .getId().getSource(), recordAccepted.getId().toString()));
  }

  /**
   * Assert that dlq is empty.
   * 
   * @throws JMSException
   *           the JMS exception
   */
  private void assertThatDLQIsEmpty() throws JMSException {
    _log.info("asserting that DLQ is empty...");
    final BytesMessage message = extractJMSMessageFromDLQ(CONSUMER_WAITING_TIME_EMPTY);
    assertNull("DLQ is not empty!", message);
  }

  /**
   * Extract jms message from dlq.
   * 
   * @param extractingTime
   *          extracting message timeout
   * @return the bytes message
   * 
   * @throws JMSException
   *           the JMS exception
   */
  private BytesMessage extractJMSMessageFromDLQ(final int extractingTime) throws JMSException {
    _log.info("extracting JMS Message from DLQ");
    final Connection connection = _connectionFactory.createConnection();
    Session session = null;
    try {
      connection.start();
      session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
      final Queue queue = session.createQueue(DLQ_NAME);
      final MessageConsumer consumer = session.createConsumer(queue);
      final BytesMessage message = (BytesMessage) consumer.receive(extractingTime);
      if (message != null) {
        _log.info("JMS Message is not null");
        message.acknowledge();
      } else {
        _log.info("JMS Message is null");
      }
      session.commit();
      consumer.close();
      return message;
    } finally {
      if (session != null) {
        session.close();
      }
      connection.close();
    }
  }
}
