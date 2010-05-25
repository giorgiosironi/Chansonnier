/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.jms;

import javax.jms.ConnectionFactory;

/**
 * The Interface MQAccess.
 */
public interface MQAccess {

  /**
   * New connection factory.
   * 
   * @param brokerUrl
   *          the broker url
   * @param userName
   *          the user name
   * @param password
   *          the password
   * 
   * @return the connection factory
   */
  ConnectionFactory newConnectionFactory(final String brokerUrl, final String userName, final String password);

}
