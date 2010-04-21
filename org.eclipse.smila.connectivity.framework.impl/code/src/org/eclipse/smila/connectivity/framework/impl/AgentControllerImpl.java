/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingException;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingSessionException;
import org.eclipse.smila.connectivity.framework.Agent;
import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.connectivity.framework.AgentState;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.util.AgentControllerCallback;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.osgi.service.component.ComponentContext;

/**
 * Basic Implementation of a AgentController.
 */
public class AgentControllerImpl extends AbstractController implements AgentController, AgentControllerCallback {

  /** The Constant BUNDLE_ID. */
  private static final String BUNDLE_ID = "org.eclipse.smila.connectivity.framework";

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(AgentControllerImpl.class);

  /**
   * The record factory.
   */
  private final RecordFactory _recordFactory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * A Map of active Agents.
   */
  private final java.util.Map<String, Agent> _activeAgents;

  /**
   * A Map of AgentStates.
   */
  private final java.util.Map<String, AgentState> _agentStates;

  /**
   * Default Constructor.
   */
  public AgentControllerImpl() {
    if (_log.isTraceEnabled()) {
      _log.trace("Creating AgentControllerImpl");
    }
    _activeAgents = new HashMap<String, Agent>();
    _agentStates = new HashMap<String, AgentState>();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#startAgent(String)
   */
  public int startAgent(final String dataSourceId) throws ConnectivityException {
    // check parameters
    if (dataSourceId == null) {
      final String msg = "Parameter dataSourceId is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }

    // check if data source is already used by another agent
    if (_activeAgents.containsKey(dataSourceId)) {
      throw new ConnectivityException("Can't start a new agent for DataSourceId '" + dataSourceId
        + "'. An agent is already started for it.");
    }

    try {
      final DataSourceConnectionConfig configuration = getConfiguration(BUNDLE_ID, dataSourceId);
      final Agent agent = createInstance(Agent.class, configuration.getDataConnectionID().getId());
      final int jobId = agent.hashCode();

      // initialize the AgentState
      final AgentState agentState = new AgentState();
      agentState.setJobId(Integer.toString(jobId));
      _agentStates.put(dataSourceId, agentState);

      String sessionId = null;
      if (doDeltaIndexing(configuration.getDeltaIndexing())) {
        sessionId = getDeltaIndexingManager().init(dataSourceId);
      }

      // start agent
      agent.start(this, agentState, configuration, sessionId);
      _activeAgents.put(dataSourceId, agent);
      return jobId;
    } catch (final ConnectivityException e) {
      throw e;
    } catch (final Exception e) {
      final String msg = "Error during start of agent using DataSourceId '" + dataSourceId + "'";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new ConnectivityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#stopAgent(String)
   */
  public void stopAgent(final String dataSourceId) throws ConnectivityException {
    // check parameters
    if (dataSourceId == null) {
      final String msg = "Parameter dataSourceId is null";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new NullPointerException(msg);
    }

    final Agent agent = _activeAgents.get(dataSourceId);
    if (agent == null) {
      final String msg =
        "Could not stop Agent for DataSourceId '" + dataSourceId + "'. No agent has been started for it.";
      if (_log.isErrorEnabled()) {
        _log.error(msg);
      }
      throw new ConnectivityException(msg);
    }
    // stop agent
    try {
      agent.stop();
    } catch (Exception e) {
      final String msg = "Error while stopping agent for DataSourceId '" + dataSourceId + "'";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new ConnectivityException(msg, e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#hasActiveAgents()
   */
  public boolean hasActiveAgents() throws ConnectivityException {
    return !_activeAgents.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#getAgentTasksState()
   */
  public Map<String, AgentState> getAgentTasksState() {
    final HashMap<String, AgentState> states = new HashMap<String, AgentState>();
    states.putAll(_agentStates);
    return states;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#getAvailableAgents()
   */
  public Collection<String> getAvailableAgents() {
    return getAvailableFactories();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.AgentController#getAvailableConfigurations()
   */
  public Collection<String> getAvailableConfigurations() {
    return getConfigurations(BUNDLE_ID, DataConnectionType.AGENT);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.AgentControllerCallback#add(String, DeltaIndexingType, Record,
   *      String)
   */
  public void add(final String sessionId, final DeltaIndexingType deltaIndexingType, final Record record,
    final String hash) {
    if (record != null) {
      // set jobId as annotation on record
      JobIdHelper.setJobIdAnnotation(record, _agentStates.get(record.getId().getSource()));

      try {
        boolean isUpdate = true;
        if (doCheckForUpdate(deltaIndexingType)) {
          isUpdate = getDeltaIndexingManager().checkForUpdate(sessionId, record.getId(), hash);
        }
        if (isUpdate) {
          // TODO: add compound management
          final boolean isCompound = false;

          // add record to connectivity manager
          getConnectivityManager().add(new Record[] { record });

          // set delta indexing visited flag
          if (doDeltaIndexing(deltaIndexingType)) {
            getDeltaIndexingManager().visit(sessionId, record.getId(), hash, isCompound);
          }

          // execute delta delete for compounds only
          if (isCompound) {
            deleteDelta(sessionId, deltaIndexingType, record.getId());
          }

          // getPerformanceCounterHelper().incrementRecords();
        } // if
      } catch (final RuntimeException e) {
        // getPerformanceCounterHelper().addCriticalException(e);
        throw e;
      } catch (final Exception e) {
        // getPerformanceCounterHelper().addCriticalException(e);
        if (_log.isErrorEnabled()) {
          final String msg = "Error while adding record '" + record.getId() + "'";
          _log.error(msg, e);
        }
      }

    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.AgentControllerCallback#delete(String, DeltaIndexingType, Id)
   */
  public void delete(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id id) {
    if (id != null) {
      final String dataSourceId = id.getSource();
      try {
        // TODO: add compound management
        final Record record = _recordFactory.createRecord();
        JobIdHelper.setJobIdAnnotation(record, _agentStates.get(record.getId().getSource()));
        getConnectivityManager().delete(new Record[] { record });

        // remove entry from delta indexing
        if (doDeltaDelete(deltaIndexingType)) {
          getDeltaIndexingManager().delete(sessionId, id);
        }

        // TODO: make sure delete also deletes elements of compounds, additional method in DIManager ?

      } catch (final RuntimeException e) {
        // getPerformanceCounterHelper().addCriticalException(e);
        throw e;
      } catch (final ConnectivityException e) {
        // getPerformanceCounterHelper().addException(e);
        if (_log.isErrorEnabled()) {
          final String msg = "Error while deleting records for DataSourceId '" + dataSourceId + "'";
          _log.error(msg, e);
        }
      } catch (final DeltaIndexingSessionException e) {
        // getPerformanceCounterHelper().addCriticalException(e);
        if (_log.isErrorEnabled()) {
          final String msg = "Error while deleting records for DataSourceId '" + dataSourceId + "'";
          _log.error(msg, e);
        }
      } catch (final DeltaIndexingException e) {
        // getPerformanceCounterHelper().addException(e);
        if (_log.isErrorEnabled()) {
          final String msg = "Error while deleting records for DataSourceId '" + dataSourceId + "'";
          _log.error(msg, e);
        }
      }
    } // if
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.AgentControllerCallback#unregister(String, DeltaIndexingType,
   *      String)
   */
  public void unregister(final String sessionId, final DeltaIndexingType deltaIndexingType,
    final String dataSourceId) {
    _activeAgents.remove(dataSourceId);

    if (doDeltaIndexing(deltaIndexingType)) {
      try {
        getDeltaIndexingManager().finish(sessionId);
      } catch (Exception e) {
        if (_log.isErrorEnabled()) {
          final String msg = "Error finishing delta indexing for DataSourceId '" + dataSourceId + "'";
          _log.error(msg, e);
        }
      }
    }
  }

  /**
   * Deletes all elements of a compound id of a delta indexing run that were not visited.
   * 
   * @param sessionId
   *          the delta indexing session id
   * @param deltaIndexingType
   *          the DeltaIndexingType
   * @param compoundId
   *          the id of the compound record
   * @return the number of deleted Ids
   */
  private int deleteDelta(final String sessionId, final DeltaIndexingType deltaIndexingType, final Id compoundId) {
    int count = 0;
    if (doDeltaDelete(deltaIndexingType)) {
      try {
        final Iterator<Id> it = getDeltaIndexingManager().obsoleteIdIterator(sessionId, compoundId);
        if (it != null) {
          while (it.hasNext()) {
            final Id id = it.next();
            if (id != null) {
              getDeltaIndexingManager().delete(sessionId, it.next());
              count++;
            }
          } // while
        } // if
      } catch (final Exception e) {
        final String msg = "Error during execution of deleteDelta for compoundId " + compoundId;
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
      }
    } // if
    return count;
  }

  /**
   * DS deactivate method.
   * 
   * @param context
   *          the ComponentContext
   * 
   * @throws Exception
   *           if any error occurs
   */
  protected void deactivate(final ComponentContext context) throws Exception {
    if (_log.isInfoEnabled()) {
      _log.info("Deactivating AgentController");
    }
    _lock.writeLock().lock();
    try {
      final Iterator<Map.Entry<String, Agent>> it = _activeAgents.entrySet().iterator();
      while (it.hasNext()) {
        final Map.Entry<String, Agent> entry = it.next();
        try {
          if (entry.getValue() != null) {
            entry.getValue().stop();
          }
        } catch (Exception e) {
          if (_log.isErrorEnabled()) {
            _log.error("Error stopping Agent for data source " + entry.getKey(), e);
          }
        }
      }
    } finally {
      _lock.writeLock().unlock();
    }
  }
}
