/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker;

// TODO: Auto-generated Javadoc
/**
 * The Interface RecordRecycler.
 */
public interface RecordRecycler {

  /**
   * Gets the configurations.
   * 
   * @return the configurations
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  String[] getConfigurations() throws RecordRecyclerException;

  /**
   * Recycle.
   * 
   * @param configurationId
   *          the configuration id
   * @param dataSourceId
   *          the data source id
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  void recycle(String configurationId, final String dataSourceId) throws RecordRecyclerException;

  /**
   * Recycle async.
   * 
   * @param configurationId
   *          the configuration id
   * @param dataSourceId
   *          the data source id
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  void recycleAsync(final String configurationId, final String dataSourceId) throws RecordRecyclerException;

  /**
   * Stop.
   * 
   * @param dataSourceId
   *          the data source id
   * 
   * @throws RecordRecyclerException
   *           the record recycler exception
   */
  void stopRecycle(final String dataSourceId) throws RecordRecyclerException;

  /**
   * Gets the status.
   * 
   * @param dataSourceID
   *          the data source id
   * 
   * @return the status
   */
  RecordRecyclerStatus getStatus(final String dataSourceID);

  /**
   * Gets the records recycled.
   * 
   * @param dataSourceID
   *          the data source id
   * 
   * @return the records recycled
   */
  long getRecordsRecycled(final String dataSourceID);

  // /**
  // * Recycle.
  // *
  // * @param partitionId
  // * the partition id
  // * @param configurationId
  // * the configuration id
  // * @param xquery
  // * the xquery
  // *
  // * @throws RecordRecyclerException
  // * the record recycler exception
  // */
  // void recycle(String partitionId, String configurationId, final String xquery) throws RecordRecyclerException;
  //
  // /**
  // * Recycle async.
  // *
  // * @param partitionId
  // * the partition id
  // * @param configurationId
  // * the configuration id
  // * @param xquery
  // * the xquery
  // *
  // * @throws RecordRecyclerException
  // * the record recycler exception
  // */
  // void recycleAsync(final String partitionId, final String configurationId, final String xquery)
  // throws RecordRecyclerException;
}
