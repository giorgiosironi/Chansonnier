/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.connection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.eclipse.smila.connectivity.queue.worker.config.BrokerConnectionType;
import org.eclipse.smila.connectivity.queue.worker.config.ConnectionsConfigType;
import org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType;
import org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionException;
import org.eclipse.smila.connectivity.queue.worker.jms.BrokerConnectionService;

/**
 * The Class BrokerConnectionServiceImpl.
 */
public class BrokerConnectionServiceImpl extends AbstractQueueService<ConnectionsConfigType> implements
  BrokerConnectionService {

  /**
   * The _connection access map.
   */
  private final Map<String, BrokerConnectionAccess> _connectionAccessMap =
    new HashMap<String, BrokerConnectionAccess>();

  /**
   * The _connection cache.
   */
  private final Map<String, ConnectionWrapper> _connectionCache =
    Collections.synchronizedMap(new HashMap<String, ConnectionWrapper>());

  /**
   * Instantiates a new broker connection service impl.
   */
  public BrokerConnectionServiceImpl() {
    super("BrokerConnectionService");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService #getConfigName()
   */
  @Override
  public String getConfigName() {
    return "QueueWorkerConnectionConfig.xml";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#start()
   */
  @Override
  public synchronized void start() {
    _log.info(msg("Starting..."));
    try {
      super.start();
      for (final BrokerConnectionType connectionType : _config.getConnectionConfig()) {
        final String id = connectionType.getId();
        if (_connectionAccessMap.containsKey(id)) {
          throw new RuntimeException(msg(String.format("Broker with ID %s already registered", id)));
        }
        _connectionAccessMap.put(connectionType.getId(), new BrokerConnectionAccess(connectionType));
      }
      _log.info(msg("Started successfully"));
    } catch (final RuntimeException e) {
      _log.error(msg("Error starting"), e);
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.internal.AbstractQueueService#stop()
   */
  @Override
  public synchronized void stop() {
    _connectionAccessMap.clear();
    synchronized (_connectionCache) {
      final Iterator<ConnectionWrapper> iterator = _connectionCache.values().iterator();
      while (iterator.hasNext()) {
        final ConnectionWrapper connection = iterator.next();
        try {
          connection.stopInternal();
        } catch (final Throwable e) {
          _log.error(msg("While stopping JMS connection"), e);
        }
        try {
          connection.closeInternal();
        } catch (final Throwable e) {
          _log.error(msg("While closing JMS connection"), e);
        }
        iterator.remove();
      }
    }
    super.stop();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.queue.worker.BrokerConnectionService
   *      #getConnection(org.eclipse.smila.connectivity.queue.worker.config.QueueConnectionType)
   */
  public Connection getConnection(final QueueConnectionType connectionType, final boolean cached)
    throws BrokerConnectionException {
    // TODO: connections pull?
    final BrokerConnectionAccess access = _connectionAccessMap.get(connectionType.getBrokerId());
    if (access == null) {
      throw new BrokerConnectionException("Unable to find broker with ID=" + connectionType.getBrokerId());
    }
    if (!cached) {
      try {
        // normal connection
        return access.getConnection(connectionType);
      } catch (final JMSException e) {
        throw new BrokerConnectionException(e);
      }
    } else {
      // TODO: normal connections pool
      Connection connection = _connectionCache.get(connectionType.getBrokerId());
      if (connection != null) {
        return connection;
      }
      synchronized (_connectionCache) {
        connection = _connectionCache.get(connectionType.getBrokerId());
        if (connection != null) {
          return connection;
        }
        try {
          connection = access.getConnection(connectionType);
          connection.start();
        } catch (final JMSException e) {
          throw new BrokerConnectionException(e);
        }
        // cached connection with disabled start/stop/close
        final ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection);
        _connectionCache.put(connectionType.getBrokerId(), connectionWrapper);
        connection = connectionWrapper;
      }
      return connection;
    }
  }
}
