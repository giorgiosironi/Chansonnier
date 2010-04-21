/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.activemq.test;

import java.io.File;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The Class ActiveMQJNDITest.
 */
public final class ActiveMQJNDITest extends TestCase {

  /**
   * Tests using of the ActiveMQInitialContextFactory.
   */
  public void testActiveMQJNDI() {
    Connection connection = null;
    Session session = null;
    try {
      final Hashtable<Object, Object> properties = new Hashtable<Object, Object>();
      properties.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
      properties.put("java.naming.provider.url", "vm://localhost?brokerConfig=broker:(tcp://localhost:61616)");
      final File folder = WorkspaceHelper.createWorkingDir("org.apache.activemq.test");
      // path should ends with "/" because it's just used as a prefix
      String path = folder.getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
      System.setProperty("org.apache.activemq.default.directory.prefix", path);
      final Context ctx = new InitialContext(properties);

      final Queue queueA = (Queue) ctx.lookup("dynamicQueues/example.A");
      final ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");

      connection = factory.createConnection();
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      TextMessage textMessage = session.createTextMessage();
      textMessage.setText("hello world!");

      // Producer
      MessageProducer producer = session.createProducer(queueA);
      producer.send(textMessage);
      producer.close();
      producer = null;

      // Consumer
      MessageConsumer consumer = session.createConsumer(queueA);
      final Message message = consumer.receive();
      consumer.close();
      consumer = null;

      assertTrue(message instanceof TextMessage);
      textMessage = (TextMessage) message;
      assertEquals(textMessage.getText(), "hello world!");

    } catch (final Exception e) {
      fail(e.getMessage());
    } finally {
      try {
        session.close();
        session = null;
      } catch (final Throwable ignore) {
        ;
      }

      try {
        connection.stop();
        connection = null;
      } catch (final Throwable ignore) {
        ;
      }
    }
  }
}
