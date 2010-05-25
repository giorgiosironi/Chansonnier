/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;

/**
 * The Class AbstractBrokerImpl.
 */
public abstract class AbstractBrokerPropertiesConfigImpl implements ConnectivityBroker {

  /**
   * The Constant CONFIG_JMS_NAME.
   */
  protected static final String CONFIG_JMS_NAME = "jms.properties";

  /**
   * The Constant PROPERTY_PROVIDER_URL.
   */
  protected static final String PROPERTY_PROVIDER_URL = "java.naming.provider.url";

  /**
   * The Constant PROPERTY_DEFAULT_DIRECTORY_PREFIX.
   */
  protected static final String PROPERTY_DEFAULT_DIRECTORY_PREFIX = "org.apache.activemq.default.directory.prefix";

  /**
   * The Constant DEFAULT_DLQ.
   */
  protected static final String DLQ = "ActiveMQ.DLQ";

  /**
   * The _session.
   */
  protected Session _session;

  /**
   * The _log.
   */
  protected final Log _log = LogFactory.getLog(getClass());

  /**
   * The _connection.
   */
  protected Connection _connection;

  /**
   * Gets the bundle id.
   * 
   * @return the bundle id
   */
  protected abstract String getBundleId();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.ConnectivityBroker#start()
   */
  public synchronized void start() throws BrokerException {
    Properties properties;
    try {
      properties = loadJMSProperties();
    } catch (final IOException e) {
      throw new BrokerException("Unable to load JMS properies", e);
    }
    Context ctx;
    try {
      ctx = new InitialContext(properties);
      System.setProperty(PROPERTY_DEFAULT_DIRECTORY_PREFIX, properties
        .getProperty(PROPERTY_DEFAULT_DIRECTORY_PREFIX));
      final ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
      _connection = factory.createConnection();
      _connection.start();
      _session = _connection.createSession(true, Session.SESSION_TRANSACTED);
      _session.createQueue(DLQ);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (_session != null) {
        try {
          _session.commit();
        } catch (final JMSException e) {
          ;// nothing
        }
        try {
          _session.close();
        } catch (final JMSException e) {
          ;// nothing
        }
      }
    }
  }

  /**
   * Load jms properties.
   * 
   * @return the properties
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private Properties loadJMSProperties() throws IOException {
    final InputStream inputStream = ConfigUtils.getConfigStream(getBundleId(), CONFIG_JMS_NAME);
    final Properties properties = new Properties();
    try {
      properties.load(inputStream);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
    if (!properties.containsKey(PROPERTY_DEFAULT_DIRECTORY_PREFIX)) {
      final File folder = WorkspaceHelper.createWorkingDir(getBundleId());
      // path should ends with "/" because it's just used as a prefix
      String path = folder.getPath();
      if (!path.endsWith("/")) {
        path = path + "/";
      }
      properties.put(PROPERTY_DEFAULT_DIRECTORY_PREFIX, path);
    }
    return properties;
  }

  /**
   * Stop.
   */
  public synchronized void stop() {
    if (_session != null) {
      try {
        _session.close();
      } catch (final JMSException e) {
        _log.error(e);
      }
      _session = null;
    }
    if (_connection != null) {
      try {
        _connection.stop();
      } catch (final JMSException e) {
        _log.error(e);
      }
      _connection = null;
    }
  }

}
