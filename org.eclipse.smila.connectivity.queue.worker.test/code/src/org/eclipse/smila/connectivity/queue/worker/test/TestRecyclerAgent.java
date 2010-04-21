/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import org.eclipse.smila.connectivity.queue.worker.RecordRecyclerStatus;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Class TestRecycler.
 */
public class TestRecyclerAgent extends AbstractTestWorker {

  /**
   * The Constant AGENT.
   */
  private static final String AGENT = "QueueWorker/Recycler";

  /**
   * The Constant FAILED_SOURCE.
   */
  private static final String FAILED_SOURCE = "TestRecyclerAgent_no_source";

  /**
   * The Constant MAIN_SOURCE.
   */
  private static final String MAIN_SOURCE = "TestRecyclerAgent_source";

  /**
   * The Constant ASYNC_SOURCE.
   */
  private static final String ASYNC_SOURCE = "TestRecyclerAgent_async_source";

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
    final String status = (String) methodArgumentString(AGENT, "getStatus", MAIN_SOURCE);
    assertTrue("Expected FINISHED or STOPPED Recycler status but it was was " + status,
      RecordRecyclerStatus.STOPPED.toString().equals(status)
        || RecordRecyclerStatus.FINISHED.toString().equals(status));

    final Record record = createTestRecord(MAIN_SOURCE, "key22");
    _blackboard.setRecord(record);
    _blackboard.commit(record.getId());
    _recycler.recycle("Recycler1", MAIN_SOURCE);
    assertEquals(_recycler.getRecordsRecycled(MAIN_SOURCE), 1);
    final RecordRecyclerStatus newStatus = _recycler.getStatus(MAIN_SOURCE);
    assertEquals("Expected FINISHED Recycler status but it was was " + newStatus, RecordRecyclerStatus.FINISHED,
      newStatus);
  }

  /**
   * Test recycler status.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerStatus() throws Exception {
    final String status = (String) methodArgumentString(AGENT, "getStatus", FAILED_SOURCE);
    assertTrue("Expected FINISHED or STOPPED Recycler status but it was was " + status,
      RecordRecyclerStatus.STOPPED.toString().equals(status)
        || RecordRecyclerStatus.FINISHED.toString().equals(status));
  }

  /**
   * Test recycler records recycled.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerRecordsRecycled() throws Exception {
    final long recycled = (Long) methodArgumentString(AGENT, "getRecordsRecycled", FAILED_SOURCE);
    assertEquals(0, recycled);
  }

  /**
   * Test recycler configurations.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerConfigurations() throws Exception {
    final String[] configs = (String[]) attribute(AGENT, "Configurations");
    assertEquals(1, configs.length);
    assertEquals("Recycler1", configs[0]);
  }

  /**
   * Test recycler async stop.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRecyclerAsyncStop() throws Exception {
    String status = (String) methodArgumentString(AGENT, "getStatus", ASYNC_SOURCE);
    assertTrue(RecordRecyclerStatus.STOPPED.toString().equals(status)
      || RecordRecyclerStatus.FINISHED.toString().equals(status));
    // push records
    for (int i = 0; i < RECORDS_TO_PUSH; i++) {
      final Record record = this.createTestRecord(ASYNC_SOURCE, "id" + i);
      _blackboard.setRecord(record);
      _blackboard.commit(record.getId());
    }
    // recycle
    method(AGENT, "startRecycle", new Object[] { "Recycler1", ASYNC_SOURCE }, new String[] {
      String.class.getName(), String.class.getName() });
    // second try should throw exception
    try {
      method(AGENT, "startRecycle", new Object[] { "Recycler1", ASYNC_SOURCE }, new String[] {
        String.class.getName(), String.class.getName() });
      throw new AssertionError();
    } catch (final RuntimeException e) {
      assertNotNull(e);
    }
    // push stopping
    methodArgumentString(AGENT, "stopRecycle", ASYNC_SOURCE);
    final long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < MAX_PAUSE) {
      status = (String) methodArgumentString(AGENT, "getStatus", ASYNC_SOURCE);
      if (RecordRecyclerStatus.STARTED.toString().equals(status) || //
        RecordRecyclerStatus.IN_PROGRESS.toString().equals(status) || //
        RecordRecyclerStatus.STOPPING.toString().equals(status)) {
        Thread.sleep(SMALL_PAUSE);
      } else {
        break;
      }
    }
    status = (String) methodArgumentString(AGENT, "getStatus", ASYNC_SOURCE);
    assertTrue("Expected FINISHED or STOPPED Recycler status but it was was " + status,
      RecordRecyclerStatus.STOPPED.toString().equals(status)
        || RecordRecyclerStatus.FINISHED.toString().equals(status));
  }
}
