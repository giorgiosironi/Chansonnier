/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.management.test;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class BlackboardServiceTest.
 */
public class ConfigurationAgentTest extends DeclarativeServiceTestCase {

  /**
   * Constant for number 123.
   */
  private static final int NUMBER_123 = 123;

  /** MBeans default domain. */
  private static final String DOMAIN = "SMILA";

  /** MBean server. */
  private MBeanServerConnection _mbeanServer;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    final JMXServiceURL jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9004/jmxrmi");
    _mbeanServer = JMXConnectorFactory.connect(jmxUrl).getMBeanServerConnection();
  }

  /**
   * Test jmx controller.
   * 
   * @throws Exception
   *           the exception
   */
  public void testJmxController() throws Exception {
    final ObjectName mBean = new ObjectName(DOMAIN + ":Agent=sampleAgent");
    _mbeanServer.invoke(mBean, "setIntProperty", new Object[] { Integer.valueOf(NUMBER_123) },
      new String[] { "java.lang.Integer" });
    final Integer property =
      (Integer) _mbeanServer.invoke(mBean, "getIntProperty", new Object[] {}, new String[] {});
    assertEquals(Integer.valueOf(NUMBER_123), property);
  }

}
