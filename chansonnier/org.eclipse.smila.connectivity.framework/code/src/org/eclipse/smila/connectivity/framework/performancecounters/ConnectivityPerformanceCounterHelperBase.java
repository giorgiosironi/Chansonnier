/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Alexander Eliseyev (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.performancecounters;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.CrawlerController;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;
import org.eclipse.smila.management.performance.PerformanceCounter;

/**
 * The Class ConnectivityPerformanceCounterHelperBase.
 * 
 * @param <AgentType>
 *          real agent class
 */
public abstract class ConnectivityPerformanceCounterHelperBase<AgentType extends CrawlerPerformanceAgent> {

  /**
   * The _log.
   */
  protected Log _log = LogFactory.getLog(getClass());

  /**
   * The _controller agent.
   */
  protected CrawlerControllerPerformanceAgent _controllerAgent;

  /**
   * The _type agent.
   */
  protected final AgentType _typeAgent;

  /**
   * The _instance agent.
   */
  protected final AgentType _instanceAgent;

  /**
   * The _agent class.
   */
  protected final Class<AgentType> _agentClass;

  /**
   * Instantiates a new crawler performance counter helper.
   * 
   * @param configuration
   *          the configuration
   * @param hashCode
   *          the hash code
   * @param agentClass
   *          the agent class
   */
  public ConnectivityPerformanceCounterHelperBase(final DataSourceConnectionConfig configuration,
    final int hashCode, final Class<AgentType> agentClass) {
    _agentClass = agentClass;
    try {
      _controllerAgent =
        (CrawlerControllerPerformanceAgent) ManagementRegistration.INSTANCE
          .getAgent(CrawlerController.PERFORMANCE_AGENT_LOCATION);
    } catch (final Throwable maf) {
      // should not happens but tests sometimes run crawlers without connectivity
      // TODO: fix tests
      _log.warn("Controller Agent was not found!");
    }
    final String crawlerName = configuration.getDataConnectionID().getId().replaceFirst("Crawler$", "");
    // register global and local agents
    final ManagementAgentLocation typeAgentLocation = ManagementRegistration.INSTANCE//
      .getCategory("Crawlers")//
      .getCategory(crawlerName)//
      .getLocation("Total");
    _typeAgent = initAgent(typeAgentLocation);
    final ManagementAgentLocation instanceAgentLocation = ManagementRegistration.INSTANCE//
      .getCategory("Crawlers")//
      .getCategory(crawlerName)//
      .getLocation(configuration.getDataSourceID() + " - " + String.valueOf(hashCode));
    _instanceAgent = initAgent(instanceAgentLocation);
  }

  /**
   * Sets the crawler start date.
   * 
   * @param date
   *          the new start date
   */
  public void setCrawlerStartDate(final Date date) {
    if (_typeAgent != null) {
      _typeAgent.setStartDate(date);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.setStartDate(date);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Sets the crawler end date.
   * 
   * @param endDate
   *          the new end date
   */
  public void setCrawlerEndDate(final Date endDate) {
    if (_typeAgent != null) {
      _typeAgent.setEndDate(endDate);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.setEndDate(endDate);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Inits the agent.
   * 
   * @param location
   *          the location
   * 
   * @return the agent type
   */
  protected abstract AgentType initAgent(final ManagementAgentLocation location);

  /**
   * Increment delta indices.
   */
  public void incrementDeltaIndices() {
    incrementDeltaIndicesBy(1);
  }

  /**
   * Increment delta indices by.
   * 
   * @param value
   *          the value
   */
  public void incrementDeltaIndicesBy(final long value) {
    if (_controllerAgent != null) {
      _controllerAgent.getDeltaIndices().incrementBy(value);
      _controllerAgent.getAverageDeltaIndicesProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Controller agent is null");
    }
    if (_typeAgent != null) {
      _typeAgent.getDeltaIndices().incrementBy(value);
      _typeAgent.getAverageDeltaIndicesProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.getDeltaIndices().incrementBy(value);
      _instanceAgent.getAverageDeltaIndicesProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Increment attachment bytes.
   * 
   * @param value
   *          the value
   */
  public void incrementAttachmentBytes(final long value) {
    if (_controllerAgent != null) {
      _controllerAgent.getAttachmentTransferRate().incrementBy(value);
      _controllerAgent.getAttachmentBytesTransfered().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Controller agent is null");
    }
    if (_typeAgent != null) {
      _typeAgent.getAttachmentTransferRate().incrementBy(value);
      _typeAgent.getAttachmentBytesTransfered().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.getAttachmentTransferRate().incrementBy(value);
      _instanceAgent.getAttachmentBytesTransfered().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Increment records.
   */
  public void incrementRecords() {
    incrementRecordsBy(1);
  }

  /**
   * Increment records by.
   * 
   * @param value
   *          the value
   */
  public void incrementRecordsBy(final long value) {
    if (_controllerAgent != null) {
      _controllerAgent.getRecords().incrementBy(value);
      _controllerAgent.getAverageRecordsProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Controller agent is null");
    }
    if (_typeAgent != null) {
      _typeAgent.getRecords().incrementBy(value);
      _typeAgent.getAverageRecordsProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.getRecords().incrementBy(value);
      _instanceAgent.getAverageRecordsProcessingTime().incrementBy(value);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Increment.
   * 
   * @param name
   *          the name
   */
  public void increment(final String name) {
    incrementBy(name, 1);
  }

  /**
   * Increment by.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public void incrementBy(final String name, final long value) {
    try {
      if (_typeAgent != null) {
        final PerformanceCounter counter = (PerformanceCounter) PropertyUtils.getProperty(_typeAgent, name);
        counter.incrementBy(value);
      } else {
        // TODO: sometimes tests does not initialize controller correctly
        _log.warn("Type agent agent is null");
      }
      if (_instanceAgent != null) {
        final PerformanceCounter counter = (PerformanceCounter) PropertyUtils.getProperty(_instanceAgent, name);
        counter.incrementBy(value);
      } else {
        // TODO: sometimes tests does not initialize controller correctly
        _log.warn("Instance agent agent is null");
      }
    } catch (final Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Adds the exception.
   * 
   * @param ex
   *          the ex
   */
  public void addException(final Throwable ex) {
    addException(ex, false);
  }

  /**
   * Adds the critical exception.
   * 
   * @param ex
   *          the ex
   */
  public void addCriticalException(final Throwable ex) {
    addException(ex, true);
  }

  /**
   * Adds the exception.
   * 
   * @param exception
   *          the exception
   * @param isCritical
   *          the is critical
   */
  public void addException(final Throwable exception, final boolean isCritical) {
    if (_controllerAgent != null) {
      _controllerAgent.getErrorBuffer().addError(exception, isCritical);
      if (isCritical) {
        _controllerAgent.getExceptionsCritical().increment();
      } else {
        _controllerAgent.getExceptions().increment();
      }
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Controller agent is null");
    }
    if (_typeAgent != null) {
      _typeAgent.getErrorBuffer().addError(exception, isCritical);
      if (isCritical) {
        _typeAgent.getExceptionsCritical().increment();
      } else {
        _typeAgent.getExceptions().increment();
      }
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.getErrorBuffer().addError(exception, isCritical);
      if (isCritical) {
        _instanceAgent.getExceptionsCritical().increment();
      } else {
        _instanceAgent.getExceptions().increment();
      }
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

  /**
   * Set the jobId.
   * 
   * @param jobid
   *          the jobId
   */
  public void setJobId(final String jobid) {
    if (_typeAgent != null) {
      _typeAgent.setJobId(jobid);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Type agent agent is null");
    }
    if (_instanceAgent != null) {
      _instanceAgent.setJobId(jobid);
    } else {
      // TODO: sometimes tests does not initialize controller correctly
      _log.warn("Instance agent agent is null");
    }
  }

}
