/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.test;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * The Class JmxTestCase.
 */
public abstract class JmxTestCase extends DeclarativeServiceTestCase {

  /**
   * The DOMAIN.
   */
  protected static final String DOMAIN = "SMILA";

  /**
   * MBean server.
   */
  protected MBeanServerConnection _mbeanServer;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    final String port = System.getProperty("com.sun.management.jmxremote.port");
    assertNotNull("com.sun.management.jmxremote.port system property is not set", port);
    forceStartBundle("org.eclipse.smila.management");
    forceStartBundle("org.eclipse.smila.management.jmx");
    final JMXServiceURL jmxUrl =
      new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://localhost:%s/jmxrmi", port));
    _mbeanServer = JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _mbeanServer = null;
    super.tearDown();
  }

  /**
   * Method.
   * 
   * @param domain
   *          the domain
   * @param key
   *          the key
   * @param operation
   *          the operation
   * @param arguments
   *          the arguments
   * @param signature
   *          the signature
   * 
   * @return the object
   */
  protected Object method(final String domain, final String key, final String operation, final Object[] arguments,
    final String[] signature) {
    try {
      final ObjectName objectName = prepareObjectName(domain, key);
      // new ObjectName(domain + ":type=" + agent);
      return _mbeanServer.invoke(objectName, operation, arguments, signature);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method.
   * 
   * @param key
   *          the key
   * @param operation
   *          the operation
   * @param arguments
   *          the arguments
   * @param signature
   *          the signature
   * 
   * @return the object
   */
  protected Object method(final String key, final String operation, final Object[] arguments,
    final String[] signature) {
    return method(DOMAIN, key, operation, arguments, signature);
  }

  /**
   * Method argument string.
   * 
   * @param key
   *          the key
   * @param operation
   *          the operation
   * @param argument
   *          the argument
   * 
   * @return the object
   */
  protected Object methodArgumentString(final String key, final String operation, final String argument) {
    return method(DOMAIN, key, operation, new Object[] { argument }, new String[] { String.class.getName() });
  }

  /**
   * Attribute.
   * 
   * @param domain
   *          the domain
   * @param key
   *          the key
   * @param attribute
   *          the attribute
   * 
   * @return the object
   */
  protected Object attribute(final String domain, final String key, final String attribute) {
    ObjectName objectName;
    try {
      objectName = prepareObjectName(domain, key);
      // new ObjectName(domain + ":type=" + key);
      return _mbeanServer.getAttribute(objectName, attribute);
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Attribute.
   * 
   * @param key
   *          the key
   * @param attribute
   *          the attribute
   * 
   * @return the object
   */
  protected Object attribute(final String key, final String attribute) {
    return attribute(DOMAIN, key, attribute);
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
