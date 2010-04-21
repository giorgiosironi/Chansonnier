/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.jms.activemq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.smila.jms.MQAccess;

/**
 * The Class ActiveMQAccess.
 */
public class ActiveMQAccess implements MQAccess {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.jms.MQAccess#newConnectionFactory(java.lang.String, java.lang.String, java.lang.String)
   */
  public ConnectionFactory newConnectionFactory(final String brokerUrl, final String userName, final String password) {
    if (userName == null || "".equals(userName)) {
      return new ActiveMQConnectionFactory(brokerUrl);
    } else {
      return new ActiveMQConnectionFactory(userName, password, brokerUrl);
    }
  }
}
