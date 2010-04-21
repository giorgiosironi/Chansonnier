/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.test;

import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestConnectivity.
 */
public class TestConnectivity extends DeclarativeServiceTestCase {

  /**
   * the ConnectivityManager.
   */
  private ConnectivityManager _connectivityManager;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    _connectivityManager = getService(ConnectivityManager.class);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    if (_connectivityManager != null) {
      _connectivityManager = null;
    }
  }

  /**
   * Test {@link ConnectivityManager#add(Record[])}.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testAdd() throws Exception {
    String dataSourceId = "dummyDataSource";
    Record[] records = TestUtil.createDiRecord(dataSourceId);
    int addCount = _connectivityManager.add(records);
    assertEquals(0, addCount);

    dataSourceId = "testDataSource";
    records = TestUtil.createDiRecord(dataSourceId);
    addCount = _connectivityManager.add(records);
    assertEquals(records.length, addCount);
  }

  /**
   * Test {@link ConnectivityManager#delete(Id[])}.
   * 
   * @throws Exception
   *           the Exception
   */
  public void testDelete() throws Exception {
    final RecordFactory recordFactory = RecordFactory.DEFAULT_INSTANCE;
    String dataSourceId = "dummyDataSource";
    Id[] ids = TestUtil.createIds(dataSourceId);
    Record[] records = new Record[ids.length];
    for (int i = 0; i < ids.length; i++) {
      records[i] = recordFactory.createRecord();
      records[i].setId(ids[i]);
    } // for

    int deleteCount = _connectivityManager.delete(records);
    assertEquals(0, deleteCount);

    dataSourceId = "testDataSource";
    ids = TestUtil.createIds(dataSourceId);
    records = new Record[ids.length];
    for (int i = 0; i < ids.length; i++) {
      records[i] = recordFactory.createRecord();
      records[i].setId(ids[i]);
    } // for
    deleteCount = _connectivityManager.delete(records);
    assertEquals(ids.length, deleteCount);
  }
}
