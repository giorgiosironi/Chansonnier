/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.jpa.impl.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.deltaindexing.jpa.impl.DataSourceDao;

/**
 * Test class for DataSourceDao.
 */
public class TestDataSourceDao extends TestCase {

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {

  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {

  }

  /**
   * Test DataSourceDao.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testDataSourceDao() throws Exception {
    final String dataSourceId = "testSource";
    final String sessionId = "12345";

    try {
      new DataSourceDao(null, null);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertNotNull(e);
      assertEquals("parameter dataSourceId is null", e.getMessage());
    } catch (Exception e) {
      fail("expected IllegalArgumentException");
    }

    DataSourceDao dao = new DataSourceDao(dataSourceId, null);
    assertNotNull(dao);
    assertEquals(dataSourceId, dao.getDataSourceId());
    assertNull(dao.getSessionId());

    dao = new DataSourceDao(dataSourceId, sessionId);
    assertNotNull(dao);
    assertEquals(dataSourceId, dao.getDataSourceId());
    assertEquals(sessionId, dao.getSessionId());
  }
}
