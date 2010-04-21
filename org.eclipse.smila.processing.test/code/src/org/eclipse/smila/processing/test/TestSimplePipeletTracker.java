/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.test;

import java.util.Map;

import org.eclipse.smila.processing.IPipelet;
import org.eclipse.smila.processing.PipeletTracker;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * Base class for WorkflowProcessor tests.
 *
 * @author jschumacher
 *
 */
public class TestSimplePipeletTracker extends DeclarativeServiceTestCase {

  /**
   * SimplePipeletTracker instance to test.
   */
  private PipeletTracker _tracker;

  /**
   * Check if WorkflowProcessor service is active. Wait up to 30 seconds for start. Fail, if no service is starting.
   * {@inheritDoc}
   *
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // forceStartBundle("org.eclipse.smila.processing");
    _tracker = getService(PipeletTracker.class);
    assertNotNull("no PipeletTracker service found.", _tracker);
  }

  /**
   * test if pipelet tracker knows about Test1Pipelet and Test2Pipelet.
   *
   * @throws Exception
   *           unexpected error.
   *
   */
  public void testRegisteredPipelets() throws Exception {
    final Map<String, Class<? extends IPipelet>> pipelets = _tracker.getRegisteredPipelets();

    assertTrue(pipelets.containsKey(Test1Pipelet.class.getName()));
    final Class<? extends IPipelet> test1class = pipelets.get(Test1Pipelet.class.getName());
    try {
      final IPipelet test1pipelet = test1class.newInstance();
      test1pipelet.configure(null);
      assertTrue(test1pipelet instanceof Test1Pipelet);
      assertTrue(test1pipelet instanceof SimplePipelet);
      ((SimplePipelet) test1pipelet).process(null, null);
      assertFalse(test1pipelet instanceof SearchPipelet);

    } catch (final Exception ex) {
      fail("Instantiation of Test1Pipelet failed: " + ex.toString());
    }

    assertTrue(pipelets.containsKey(Test2Pipelet.class.getName()));
    final Class<? extends IPipelet> test2class = pipelets.get(Test2Pipelet.class.getName());
    try {
      final IPipelet test2pipelet = test2class.newInstance();
      test2pipelet.configure(null);
      assertTrue(test2pipelet instanceof Test2Pipelet);
      assertTrue(test2pipelet instanceof SimplePipelet);
      ((SimplePipelet) test2pipelet).process(null, null);
      assertTrue(test2pipelet instanceof SearchPipelet);
      ((SearchPipelet) test2pipelet).process(null, null);
    } catch (final Exception ex) {
      fail("Instantiation of Test2Pipelet failed: " + ex.toString());
    }

    assertTrue(pipelets.containsKey(Test3Pipelet.class.getName()));
    final Class<? extends IPipelet> test3class = pipelets.get(Test3Pipelet.class.getName());
    try {
      final IPipelet test3pipelet = test3class.newInstance();
      test3pipelet.configure(null);
      assertTrue(test3pipelet instanceof Test3Pipelet);
      assertFalse(test3pipelet instanceof SimplePipelet);
      assertTrue(test3pipelet instanceof SearchPipelet);
      ((SearchPipelet) test3pipelet).process(null, null);
    } catch (final Exception ex) {
      fail("Instantiation of Test3Pipelet failed: " + ex.toString());
    }

  }
}
