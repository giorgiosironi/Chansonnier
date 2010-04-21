/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import java.util.Map;

import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.test.RecordCreator;

/**
 * The Class TestWorker.
 */
public class TestRouter extends AbstractTestWorker {

  /**
   * Test router.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRouter() throws Exception {
    final Record record = RecordCreator.createTestRecord1();
    _router.route(new Record[] { record }, Operation.ADD);
    _log.info("ROUTED!!!");
    Thread.sleep(PAUSE);
  }

  /**
   * Test router no rule ex.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRouterNoRuleEx() throws Exception {
    final Record record = createTestRecord("qq", "key");
    final Map<Id, Exception> exceptionMap = _router.route(new Record[] { record }, Operation.DELETE);
    assertNotNull(exceptionMap);
    final Exception e = exceptionMap.get(record.getId());
    assertNotNull(e);
    assertEquals("Unable to find rule for record " + record.getId(), e.getMessage());
  }
}
