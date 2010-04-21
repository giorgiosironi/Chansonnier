/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.framework.AgentController;
import org.eclipse.smila.connectivity.framework.CrawlerController;
import org.eclipse.smila.connectivity.framework.impl.AbstractController;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.util.ControllerCallback;
import org.eclipse.smila.test.DeclarativeServiceTestCase;

/**
 * The Class TestCrawlerController.
 */
public class TestControllerCallback extends DeclarativeServiceTestCase {

  /**
   * Constant for thread sleep time.
   */
  private static final int SLEEP_TIME = 3000;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    forceStartBundle("org.eclipse.osgi.services");
    forceStartBundle("org.eclipse.update.configurator");
    forceStartBundle("org.eclipse.equinox.ds");
    forceStartBundle("org.eclipse.smila.blackboard");
    forceStartBundle("org.eclipse.smila.connectivity.queue.broker.main");
    forceStartBundle("org.eclipse.smila.jms");
    forceStartBundle("org.eclipse.smila.jms.activemq");
    forceStartBundle("org.eclipse.smila.connectivity.queue.worker.jms");
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
   * Test the CrawlerControllerCallback interface.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testCrawlerControllerCallback() throws Exception {
    final CrawlerController crawlerController = getService(CrawlerController.class);
    assertNotNull(crawlerController);
    testDoDeltaIndexing((AbstractController) crawlerController);
    testDoDeltaDelete((AbstractController) crawlerController);
    testDoCheckForUpdate((AbstractController) crawlerController);
  }

  /**
   * Test the AgentControllerCallback interface.
   * 
   * @throws Exception
   *           if any error occurs
   */
  public void testAgentControllerCallback() throws Exception {
    final AgentController agentController = getService(AgentController.class);
    assertNotNull(agentController);
    testDoDeltaIndexing((AbstractController) agentController);
    testDoDeltaDelete((AbstractController) agentController);
    testDoCheckForUpdate((AbstractController) agentController);
  }

  /**
   * Test DoDeltaIndexing.
   * 
   * @param contoller
   *          a AbstractController
   * @throws Exception
   *           if any error occurs
   */
  private void testDoDeltaIndexing(AbstractController contoller) throws Exception {
    final ControllerCallback callback = (ControllerCallback) contoller;

    assertTrue(callback.doDeltaIndexing(DeltaIndexingType.FULL));
    assertTrue(callback.doDeltaIndexing(DeltaIndexingType.ADDITIVE));
    assertTrue(callback.doDeltaIndexing(DeltaIndexingType.INITIAL));
    assertFalse(callback.doDeltaIndexing(DeltaIndexingType.DISABLED));

    // unset delta indexing manager
    final DeltaIndexingManager diManager = contoller.getDeltaIndexingManager();
    contoller.unsetDeltaIndexingManager(diManager);
    try {
      try {
        callback.doDeltaIndexing(DeltaIndexingType.FULL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doDeltaIndexing(DeltaIndexingType.ADDITIVE);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doDeltaIndexing(DeltaIndexingType.INITIAL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      assertFalse(callback.doDeltaIndexing(DeltaIndexingType.DISABLED));
    } finally {
      // reset delta indexing manager
      contoller.setDeltaIndexingManager(diManager);
    }
  }

  /**
   * Test doDeltaDelete.
   * 
   * @param contoller
   *          a AbstractController
   * @throws Exception
   *           if any error occurs
   */
  private void testDoDeltaDelete(AbstractController contoller) throws Exception {
    final ControllerCallback callback = (ControllerCallback) contoller;

    assertTrue(callback.doDeltaDelete(DeltaIndexingType.FULL));
    assertFalse(callback.doDeltaDelete(DeltaIndexingType.ADDITIVE));
    assertFalse(callback.doDeltaDelete(DeltaIndexingType.INITIAL));
    assertFalse(callback.doDeltaDelete(DeltaIndexingType.DISABLED));

    // unset delta indexing manager
    final DeltaIndexingManager diManager = contoller.getDeltaIndexingManager();
    contoller.unsetDeltaIndexingManager(diManager);
    try {
      try {
        callback.doDeltaDelete(DeltaIndexingType.FULL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doDeltaDelete(DeltaIndexingType.ADDITIVE);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doDeltaDelete(DeltaIndexingType.INITIAL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      assertFalse(callback.doDeltaDelete(DeltaIndexingType.DISABLED));
    } finally {
      // reset delta indexing manager
      contoller.setDeltaIndexingManager(diManager);
    }
  }

  /**
   * Test doCheckForUpdate.
   * 
   * @param contoller
   *          a AbstractController
   * @throws Exception
   *           if any error occurs
   */
  private void testDoCheckForUpdate(AbstractController contoller) throws Exception {
    final ControllerCallback callback = (ControllerCallback) contoller;

    assertTrue(callback.doCheckForUpdate(DeltaIndexingType.FULL));
    assertTrue(callback.doCheckForUpdate(DeltaIndexingType.ADDITIVE));
    assertFalse(callback.doCheckForUpdate(DeltaIndexingType.INITIAL));
    assertFalse(callback.doCheckForUpdate(DeltaIndexingType.DISABLED));

    // unset delta indexing manager
    final DeltaIndexingManager diManager = contoller.getDeltaIndexingManager();
    contoller.unsetDeltaIndexingManager(diManager);
    try {
      try {
        callback.doCheckForUpdate(DeltaIndexingType.FULL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doCheckForUpdate(DeltaIndexingType.ADDITIVE);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      try {
        callback.doCheckForUpdate(DeltaIndexingType.INITIAL);
        fail("expected RuntimeException");
      } catch (RuntimeException e) {
        assertEquals("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
          + DeltaIndexingType.DISABLED, e.getMessage());
      }
      assertFalse(callback.doCheckForUpdate(DeltaIndexingType.DISABLED));
    } finally {
      // reset delta indexing manager
      contoller.setDeltaIndexingManager(diManager);
    }
  }
}
