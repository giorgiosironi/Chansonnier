/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.connection;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.eclipse.smila.connectivity.queue.worker.config.BrokerConnectionType;
import org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType;
import org.eclipse.smila.jms.ConnectionFactoryRegistry;

/**
 * The Class BrokerConnectionAccess.
 */
public class BrokerConnectionAccess {

  // /**
  // * The _config.
  // */
  // private final BrokerConnectionType _config;

  /**
   * The _connection factory.
   */
  private final ConnectionFactory _connectionFactory;

  /**
   * Instantiates a new broker connection access.
   * 
   * @param config
   *          the config
   */
  public BrokerConnectionAccess(final BrokerConnectionType config) {
    // _config = config;
    _connectionFactory =
      ConnectionFactoryRegistry.getConnectionFactory(config.getConnectionFactory(), config.getURL(), config
        .getUser(), config.getPassword());
  }

  /**
   * Gets the connection.
   * 
   * @param connectionType
   *          the connection type
   * 
   * @return the connection
   * 
   * @throws JMSException
   *           the JMS exception
   */
  public Connection getConnection(final QueueConnectionType connectionType) throws JMSException {
    return _connectionFactory.createConnection();
  }

}
