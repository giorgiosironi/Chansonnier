/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import org.eclipse.smila.connectivity.queue.worker.ListenerRule;
import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.datamodel.record.Record;

/**
 * The Class TestWorker.
 */
public class TestListener extends AbstractTestWorker {

  /**
   * The Constant ANNOTATION_NAME_2.
   */
  private static final String ANNOTATION_NAME_2 = "test-annotation-2";

  /**
   * The Constant ANNOTATION_NAME_1.
   */
  private static final String ANNOTATION_NAME_1 = "test-annotation-1";

  /**
   * Test router.
   * 
   * @throws Exception
   *           the exception
   */
  public void testListener() throws Exception {
    final ListenerRule[] rules = _listener.getRules();
    assertEquals(2 + 2 + 2 + 2, rules.length);
    for (final ListenerRule rule : _listener.getRules()) {
      final int workersNum = rule.getNoOfThreads();
      rule.increaseNoOfThreads();
      assertEquals(workersNum + 1, rule.getNoOfThreads());
      rule.decreaseNoOfThreads();
      assertEquals(workersNum, rule.getNoOfThreads());
      try {
        rule.setNoOfThreads(-1);
        throw new AssertionError();
      } catch (final IllegalArgumentException e) {
        assertNotNull(e);
      }
      rule.setNoOfThreads(2 + 2 + 2 + 2 + 2);
      rule.setNoOfThreads(1);
      assertEquals(1, rule.getNoOfThreads());
    }
    Record record = this.createTestRecord("source2", "mykey1");
    assertNotNull(record.getMetadata());
    assertFalse(record.getMetadata().hasAnnotation(ANNOTATION_NAME_1));
    assertFalse(record.getMetadata().hasAnnotation(ANNOTATION_NAME_2));
    _router.route(new Record[]{record}, Operation.ADD);
    _log.info(String.format("Waiting %d ms", PAUSE));
    Thread.sleep(PAUSE);
    // guess listened
    record = _blackboard.getRecord(record.getId());
    assertNotNull(record.getMetadata());
    // listener should process it by SimplePipeline and add annotations
    assertTrue(record.getMetadata().hasAnnotation(ANNOTATION_NAME_1));
    assertTrue(record.getMetadata().hasAnnotation(ANNOTATION_NAME_2));
  }

  /**
   * Test listener none operation.
   * 
   * @throws Exception
   *           the exception
   */
  public void testListenerResendToQueueAndCustomOperation() throws Exception {
    Record record = this.createTestRecord("source3", "mykey2");
    assertNotNull(record.getMetadata());
    assertFalse(record.getMetadata().hasAnnotation(ANNOTATION_NAME_1));
    assertFalse(record.getMetadata().hasAnnotation(ANNOTATION_NAME_2));
    _router.route(new Record[]{record}, Operation.ADD);
    _log.info(String.format("Waiting %d ms", PAUSE));
    Thread.sleep(2 * PAUSE);
    // guess listened
    record = _blackboard.getRecord(record.getId());
    assertNotNull(record.getMetadata());
    // listener should process it by SimplePipeline and add annotations
    assertTrue(record.getMetadata().hasAnnotation(ANNOTATION_NAME_1));
    assertTrue(record.getMetadata().hasAnnotation(ANNOTATION_NAME_2));
  }

}
