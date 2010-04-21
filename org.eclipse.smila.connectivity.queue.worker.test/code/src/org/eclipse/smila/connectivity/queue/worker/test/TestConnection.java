/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import javax.jms.Connection;

import org.eclipse.smila.connectivity.queue.worker.config.ObjectFactory;
import org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionService;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestConnection.
 */
public class TestConnection extends DeclarativeServiceTestCase {

  /**
   * The _service.
   */
  private BrokerConnectionService _service;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _log.info("SETUP getting Broker connections...");
    _service = getService(BrokerConnectionService.class);
    assertNotNull(_service);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _service = null;
    super.tearDown();
  }

  /**
   * Test service.
   * 
   * @throws Exception
   *           the exception
   */
  public void testService() throws Exception {
    final ObjectFactory factory = new ObjectFactory();
    final QueueConnectionType connectionType = factory.createQueueConnectionType();
    connectionType.setBrokerId("broker1");
    connectionType.setQueue("queue");
    final Connection connection = _service.getConnection(connectionType, false);
    connection.setClientID("my-client-id");
    assertEquals("my-client-id", connection.getClientID());
    connection.setExceptionListener(null);
    assertNull(connection.getExceptionListener());
    assertNotNull(connection.getMetaData());
    // assertNotNull(object)
    try {
      connection.createConnectionConsumer(null, null, null, 0);
      fail("should fails because wrong arguments");
    } catch (final Throwable e) {
      assertNotNull(e);
    }
    try {
      connection.createDurableConnectionConsumer(null, null, null, null, 0);
      fail("should fails because wrong arguments");
    } catch (final Throwable e) {
      assertNotNull(e);
    }
  }
}
