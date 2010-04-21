/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerException;
import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Class TestRecycler.
 */
public class TestRecycler extends AbstractTestWorker {

  /**
   * The Constant FAILED_SOURCE.
   */
  private static final String FAILED_SOURCE = "TestRecycler_no_source";

  /**
   * The Constant MAIN_SOURCE.
   */
  private static final String MAIN_SOURCE = "TestRecycler_source";

  /**
   * The Constant ASYNC_SOURCE.
   */
  private static final String ASYNC_SOURCE = "TestRecycler_async_source";

  /**
   * The Constant RECORDS_TO_PUSH.
   */
  private static final int RECORDS_TO_PUSH = 1000;

  /**
   * Test recycler sync.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerSync() throws Exception {
    final RecordRecyclerStatus status = _recycler.getStatus(MAIN_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.equals(status) || RecordRecyclerStatus.FINISHED.equals(status));
    final Record record = createTestRecord(MAIN_SOURCE, "key11");
    _blackboard.setRecord(record);
    _blackboard.commit(record.getId());
    _recycler.recycle("Recycler1", MAIN_SOURCE);
    assertEquals(_recycler.getRecordsRecycled(MAIN_SOURCE), 1);
    assertEquals(RecordRecyclerStatus.FINISHED, _recycler.getStatus(MAIN_SOURCE));
  }

  /**
   * Test recycler async.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerAsync() throws Exception {
    final RecordRecyclerStatus status = _recycler.getStatus(MAIN_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.equals(status) || RecordRecyclerStatus.FINISHED.equals(status));
    final Record record = createTestRecord(MAIN_SOURCE, "key12");
    _blackboard.setRecord(record);
    _blackboard.commit(record.getId());
    final long startTime = System.currentTimeMillis();
    _recycler.recycleAsync("Recycler1", MAIN_SOURCE);
    while (RecordRecyclerStatus.FINISHED != _recycler.getStatus(MAIN_SOURCE)
      && System.currentTimeMillis() - startTime < MAX_PAUSE) {
      Thread.sleep(SMALL_PAUSE);
    }
    assertEquals(RecordRecyclerStatus.FINISHED, _recycler.getStatus(MAIN_SOURCE));
  }

  /**
   * Test recycler async stop.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerAsyncStop() throws Exception {
    RecordRecyclerStatus status = _recycler.getStatus(ASYNC_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.equals(status) || RecordRecyclerStatus.FINISHED.equals(status));
    // push records
    for (int i = 0; i < RECORDS_TO_PUSH; i++) {
      final Record record = this.createTestRecord(ASYNC_SOURCE, "id" + i);
      _blackboard.setRecord(record);
      _blackboard.commit(record.getId());
    }
    _recycler.recycleAsync("Recycler1", ASYNC_SOURCE);
    // second try should throw exception
    try {
      _recycler.recycleAsync("Recycler1", ASYNC_SOURCE);
      throw new AssertionError();
    } catch (final RecordRecyclerException e) {
      assertNotNull(e);
    }
    // push stopping
    _recycler.stopRecycle(ASYNC_SOURCE);
    final long startTime = System.currentTimeMillis();
    while ((//
      RecordRecyclerStatus.STARTED == _recycler.getStatus(ASYNC_SOURCE) //
        || RecordRecyclerStatus.IN_PROGRESS == _recycler.getStatus(ASYNC_SOURCE) // 
      || RecordRecyclerStatus.STOPPING == _recycler.getStatus(ASYNC_SOURCE)) //
      && System.currentTimeMillis() - startTime < MAX_PAUSE) {
      Thread.sleep(SMALL_PAUSE);
    }
    status = _recycler.getStatus(ASYNC_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.equals(status) || RecordRecyclerStatus.FINISHED.equals(status));
  }

  /**
   * Test recycler async stop ex.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerAsyncStopEx() throws Exception {
    try {
      _recycler.stopRecycle(FAILED_SOURCE);
      throw new AssertionError();
    } catch (final RecordRecyclerException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test recycler no source r count.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerNoSourceRCount() throws Exception {
    assertEquals(0, _recycler.getRecordsRecycled(FAILED_SOURCE));
  }

  /**
   * Test recycler configurations.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerConfigurations() throws Exception {
    final String[] configs = _recycler.getConfigurations();
    assertEquals(1, configs.length);
    assertEquals("Recycler1", configs[0]);
  }

  /**
   * Test recycler no records.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerNoRecords() throws Exception {
    final RecordRecyclerStatus status = _recycler.getStatus(FAILED_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.equals(status) || RecordRecyclerStatus.FINISHED.equals(status));
    boolean isFails;
    try {
      _recycler.recycle("Recycler1", FAILED_SOURCE);
      isFails = true;
    } catch (final Throwable e) {
      isFails = false;
      assertNotNull(e);
    }
    if (isFails) {
      throw new AssertionError();
    }
  }

}
