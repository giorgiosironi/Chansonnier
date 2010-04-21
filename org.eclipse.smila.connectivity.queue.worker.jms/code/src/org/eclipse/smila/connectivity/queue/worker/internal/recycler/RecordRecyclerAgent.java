/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal.recycler;

import org.eclipse.smila.connectivity.queue.worker.RecordRecycler;
import org.eclipse.smila.management.DeclarativeServiceManagementAgent;

/**
 * The Class RecordRecyclerAgent.
 */
public class RecordRecyclerAgent extends DeclarativeServiceManagementAgent<RecordRecycler> {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.DeclarativeServiceManagementAgent#getCategory()
   */
  @Override
  protected String getCategory() {
    return "QueueWorker";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.DeclarativeServiceManagementAgent#getName()
   */
  @Override
  public String getName() {
    return "Recycler";
  }

  /**
   * Recycle start.
   * 
   * @param configurationId
   *          the configuration id
   * @param dataSourceID
   *          the data source id
   */
  public void startRecycle(final String configurationId, final String dataSourceID) {
    try {
      _service.recycleAsync(configurationId, dataSourceID);
    } catch (final Throwable e) {
      e.printStackTrace();
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Stop recycle.
   * 
   * @param dataSourceID
   *          the data source id
   */
  public void stopRecycle(final String dataSourceID) {
    try {
      _service.stopRecycle(dataSourceID);
    } catch (final Throwable e) {
      e.printStackTrace();
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Gets the status.
   * 
   * @param dataSourceID
   *          the data source id
   * 
   * @return the status
   */
  public String getStatus(final String dataSourceID) {
    return _service.getStatus(dataSourceID).toString();
  }

  /**
   * Gets the records recycled.
   * 
   * @param dataSourceID
   *          the data source id
   * 
   * @return the records recycled
   */
  public long getRecordsRecycled(final String dataSourceID) {
    return _service.getRecordsRecycled(dataSourceID);
  }

  /**
   * Gets the configurations.
   * 
   * @return the configurations
   */
  public String[] getConfigurations() {
    try {
      return _service.getConfigurations();
    } catch (final Throwable e) {
      e.printStackTrace();
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

}
