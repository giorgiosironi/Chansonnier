/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.jpa.impl;

import java.io.IOException;
import java.io.Serializable;

/**
 * A JPA Entity to store DataSource locked state.
 */
//@Entity
//@Table(name = "DATA_SOURCES")
//@NamedQueries( {
//  @NamedQuery(name = "DataSourceDao.killAllSessions", query = "UPDATE DataSourceDao d SET d._sessionId = NULL"),
//  @NamedQuery(name = "DataSourceDao.killSession", query = "UPDATE DataSourceDao d SET d._sessionId = NULL WHERE d._dataSourceId = :source"),
//  @NamedQuery(name = "DataSourceDao.selectAll", query = "SELECT d FROM DataSourceDao d"),
//  @NamedQuery(name = "DataSourceDao.deleteSources", query = "DELETE FROM DataSourceDao"),
//  @NamedQuery(name = "DataSourceDao.findBySessionId", query = "SELECT DISTINCT d FROM DataSourceDao d WHERE d._sessionId = :sessionId") })
public class DataSourceDao implements Serializable {

  /**
   * Constant for the named query DataSourceDao.killAllSessions.
   */
  public static final String NAMED_QUERY_KILL_ALL_SESSIONS = "DataSourceDao.killAllSessions";

  /**
   * Constant for the named query DataSourceDao.killSession.
   */
  public static final String NAMED_QUERY_KILL_SESSION = "DataSourceDao.killSession";

  /**
   * Constant for the named query DataSourceDao.selectAll.
   */
  public static final String NAMED_QUERY_SELECT_ALL = "DataSourceDao.selectAll";

  /**
   * Constant for the named query DataSourceDao.deleteSources.
   */
  public static final String NAMED_QUERY_DELETE_SOURCES = "DataSourceDao.deleteSources";

  /**
   * Constant for the named query DataSourceDao.deleteBySource.
   */
  public static final String NAMED_QUERY_FIND_BY_SESSION_ID = "DataSourceDao.findBySessionId";

  /**
   * Constant for the entity attribute _sessionId.
   */
  public static final String NAMED_QUERY_PARAM_SESSION_ID = "sessionId";

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = -7744287668952440717L;

  /**
   * The data source id.
   */
//  @Id
//  @Column(name = "SOURCE_ID", length = 1024)
  private String _dataSourceId;

  /**
   * The session ID the data source was locked by.
   */
//  @Column(name = "SESSION_ID", length = 1024, unique = true)
  private String _sessionId;

  /**
   * Default Constructor, used by JPA.
   */
  protected DataSourceDao() {
  }

  /**
   * Conversion Constructor. Converts a dataSourceId and a sessionId into a DataSourceDao object.
   * 
   * @param dataSourceId
   *          the id of the data source
   * @param sessionId
   *          the id of the session that locked this data source
   * @throws IOException
   *           if any exception occurs
   */
  public DataSourceDao(final String dataSourceId, final String sessionId) throws IOException {
    if (dataSourceId == null) {
      throw new IllegalArgumentException("parameter dataSourceId is null");
    }

    _dataSourceId = dataSourceId;
    _sessionId = sessionId;
  }

  /**
   * Returns the dataSourceId.
   * 
   * @return the dataSourceId
   */
  public String getDataSourceId() {
    return _dataSourceId;
  }

  /**
   * Returns sessionId if any exists.
   * 
   * @return the sessionId
   */
  public String getSessionId() {
    return _sessionId;
  }

}
