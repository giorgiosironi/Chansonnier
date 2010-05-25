/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.connection;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * The Class ConnectionWrapper.
 */
public class ConnectionWrapper implements Connection {

  /**
   * The _connection.
   */
  private final Connection _connection;

  /**
   * Instantiates a new connection wrapper.
   * 
   * @param connection
   *          the connection
   */
  public ConnectionWrapper(final Connection connection) {
    // it cannot be null because used only internally
    // if (connection == null) {
    // throw new IllegalArgumentException("connection cannot be null!");
    // }
    _connection = connection;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#close()
   */
  public void close() throws JMSException {
    ;// nothing
  }

  /**
   * Close internal.
   * 
   * @throws JMSException
   *           the JMS exception
   */
  void closeInternal() throws JMSException {
    _connection.close();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#createConnectionConsumer(javax.jms.Destination, java.lang.String,
   *      javax.jms.ServerSessionPool, int)
   */
  public ConnectionConsumer createConnectionConsumer(final Destination destination, final String s,
    final ServerSessionPool serversessionpool, final int i) throws JMSException {
    return _connection.createConnectionConsumer(destination, s, serversessionpool, i);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#createDurableConnectionConsumer(javax.jms.Topic, java.lang.String, java.lang.String,
   *      javax.jms.ServerSessionPool, int)
   */
  public ConnectionConsumer createDurableConnectionConsumer(final Topic topic, final String s, final String s1,
    final ServerSessionPool serversessionpool, final int i) throws JMSException {
    return _connection.createDurableConnectionConsumer(topic, s, s1, serversessionpool, i);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#createSession(boolean, int)
   */
  public Session createSession(final boolean flag, final int i) throws JMSException {
    return _connection.createSession(flag, i);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#getClientID()
   */
  public String getClientID() throws JMSException {
    return _connection.getClientID();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#getExceptionListener()
   */
  public ExceptionListener getExceptionListener() throws JMSException {
    return _connection.getExceptionListener();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#getMetaData()
   */
  public ConnectionMetaData getMetaData() throws JMSException {
    return _connection.getMetaData();
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#setClientID(java.lang.String)
   */
  public void setClientID(final String s) throws JMSException {
    _connection.setClientID(s);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#setExceptionListener(javax.jms.ExceptionListener)
   */
  public void setExceptionListener(final ExceptionListener exceptionlistener) throws JMSException {
    _connection.setExceptionListener(exceptionlistener);
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#start()
   */
  public void start() throws JMSException {
    ;// nothing
  }

  // /**
  // * Start internal.
  // *
  // * @throws JMSException
  // * the JMS exception
  // */
  // void startInternal() throws JMSException {
  // _connection.start();
  // }

  /**
   * {@inheritDoc}
   * 
   * @see javax.jms.Connection#stop()
   */
  public void stop() throws JMSException {
    ;// nothing
  }

  /**
   * Stop internal.
   * 
   * @throws JMSException
   *           the JMS exception
   */
  void stopInternal() throws JMSException {
    _connection.stop();
  }
}
