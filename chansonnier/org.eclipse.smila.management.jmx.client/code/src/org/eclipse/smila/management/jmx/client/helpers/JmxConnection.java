/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator, Sebastian Voigt (Brox IT Solutions GmbH), Ivan
 * Churkin (Brox IT Solutions GmbH)
 **********************************************************************************************************************/

package org.eclipse.smila.management.jmx.client.helpers;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.smila.management.jmx.client.config.ConnectionConfigType;
import org.eclipse.smila.management.jmx.client.exceptions.JmxConnectionException;
import org.eclipse.smila.management.jmx.client.exceptions.JmxInvocationException;

/**
 * The Class JmxConnection.
 */
public class JmxConnection {

  /**
   * The default host.
   */
  private static final String DEFAULT_HOST = "localhost";

  /**
   * The default Port.
   */
  private static final int DEFAULT_PORT = 9004;

  /**
   * The _mbean server.
   */
  private MBeanServerConnection _mbeanServerConnection;

  /**
   * The JMX server host. Defaults to localhost.
   */
  private String _host = DEFAULT_HOST;

  /**
   * The JMX server port. Defaults to 9004.
   */
  private int _port = DEFAULT_PORT;

  /**
   * Instantiates a new m bean resource manager.
   * 
   * @param connection
   *          the connection
   */
  public JmxConnection(final ConnectionConfigType connection) {
    if (connection != null) {
      _host = connection.getHost();
      _port = connection.getPort();
    }
  }

  /**
   * Connect to JMX MBean server.
   * 
   * @throws JmxConnectionException
   *           the connection exception
   */
  public void connect() throws JmxConnectionException {
    try {
      final JMXServiceURL jmxUrl =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + _host + ":" + _port + "/jmxrmi");
      _mbeanServerConnection = JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
    } catch (final Exception exception) {
      throw new JmxConnectionException(exception);
    }
  }

  /**
   * Invoke.
   * 
   * @param domain
   *          the domain
   * @param key
   *          the agent
   * @param operation
   *          the operation
   * @param arguments
   *          the arguments
   * @param signature
   *          the signature
   * 
   * @return the object
   * 
   * @throws JmxInvocationException
   *           the jmx invocation exception
   */
  public Object invoke(final String domain, final String key, final String operation, final Object[] arguments,
    final String[] signature) throws JmxInvocationException {
    try {
      final ObjectName objectName = prepareObjectName(domain, key);
      return _mbeanServerConnection.invoke(objectName, operation, arguments, signature);
    } catch (final Throwable e) {
      throw new JmxInvocationException(e);
    }
  }

  /**
   * Returns MBeans attribute value.
   * 
   * @param domain
   *          the domain
   * @param key
   *          the key
   * @param attribute
   *          the attribute
   * 
   * @return the attribute
   * 
   * @throws JmxInvocationException
   *           the jmx invocation exception
   */
  public Object getAttribute(final String domain, final String key, final String attribute)
    throws JmxInvocationException {
    try {
      final ObjectName objectName = prepareObjectName(domain, key);
      return _mbeanServerConnection.getAttribute(objectName, attribute);
    } catch (final Throwable e) {
      throw new JmxInvocationException(e);
    }
  }

  /**
   * Prepare object name.
   * 
   * @param domain
   *          the domain
   * @param key
   *          the key
   * 
   * @return the object name
   * 
   * @throws MalformedObjectNameException
   *           the malformed object name exception
   */
  private ObjectName prepareObjectName(final String domain, String key) throws MalformedObjectNameException {
    if (!key.contains("=")) {
      // SMILLa local key!
      // split it
      final String[] parts = key.split("/");
      key = "";
      for (int i = 0; i < parts.length - 1; i++) {
        key += String.format("C%d=%s,", i, parts[i]);
      }
      key += "Agent=" + parts[parts.length - 1];
    }
    return new ObjectName(domain + ":" + key);
  }
}
