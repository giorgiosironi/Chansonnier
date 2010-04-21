/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue;

/**
 * The Interface ConnectivityBroker.
 */
public interface ConnectivityBroker {
  /**
   * Start.
   * 
   * @throws BrokerException
   *           the broker exception
   */
  void start() throws BrokerException;

  /**
   * Stop.
   * 
   * @throws BrokerException
   *           the broker exception
   */
  void stop() throws BrokerException;

}
