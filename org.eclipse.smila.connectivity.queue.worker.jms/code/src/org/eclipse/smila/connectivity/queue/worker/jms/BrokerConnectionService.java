/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.jms;

import javax.jms.Connection;

import org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType;

/**
 * The Interface BrokerConnectionService.
 */
public interface BrokerConnectionService {

  /**
   * Gets the connection.
   * 
   * @param connectionType
   *          the connection type
   * @param cached
   *          the cached
   * 
   * @return the connection
   * 
   * @throws BrokerConnectionException
   *           the broker connection exception
   */
  Connection getConnection(QueueConnectionType connectionType, boolean cached) throws BrokerConnectionException;
}
