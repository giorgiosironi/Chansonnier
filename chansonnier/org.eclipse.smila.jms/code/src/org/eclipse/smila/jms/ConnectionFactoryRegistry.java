/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.jms;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * The Class ConnectionFactoryRegistry.
 */
public class ConnectionFactoryRegistry {

  /**
   * The Constant EXTENTION_POINT_NAME.
   */
  private static final String EXTENTION_POINT_NAME = "org.eclipse.smila.jms";

  /**
   * The Constant ATTRIBUTE_CLASS.
   */
  private static final String ATTRIBUTE_CLASS = "class";

  /**
   * The Constant ATTRIBUTE_FACTORY_CLASS.
   */
  private static final String ATTRIBUTE_FACTORY_CLASS = "connectionFactoryClass";

  /**
   * The Constant LOG.
   */
  private static final Log LOG = LogFactory.getLog(ConnectionFactoryRegistry.class);

  /**
   * Constructor.
   */
  private ConnectionFactoryRegistry() {

  }

  /**
   * Gets the connection factory.
   * 
   * @param className
   *          the class name
   * @param brokerUrl
   *          the broker url
   * @param userName
   *          the user name
   * @param password
   *          the password
   * 
   * @return the connection factory
   */
  public static ConnectionFactory getConnectionFactory(final String className, final String brokerUrl,
    final String userName, final String password) {
    final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENTION_POINT_NAME);
    for (final IExtension extension : extensionPoint.getExtensions()) {
      final IConfigurationElement configurationElement = extension.getConfigurationElements()[0];
      if (LOG.isDebugEnabled()) {
        LOG.debug("FOUND CLASS " + configurationElement.getAttribute(ATTRIBUTE_FACTORY_CLASS));
      }
      if (configurationElement.getAttribute(ATTRIBUTE_FACTORY_CLASS).equals(className)) {
        try {
          final MQAccess access = (MQAccess) configurationElement.createExecutableExtension(ATTRIBUTE_CLASS);
          return access.newConnectionFactory(brokerUrl, userName, password);
        } catch (final CoreException e) {
          throw new RuntimeException(e);
        }
      }
    }
    throw new RuntimeException(String.format("Connection factory %s is not found", className));
  }
}
