/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.wrongconfig.test;

import org.eclipse.smila.connectivity.queue.worker.Listener;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.test.JmxTestCase;

/**
 * The Class TestWorkerBase.
 */
public class TestWrongConfigLoad extends JmxTestCase {

  /**
   * The Constant PAUSE.
   */
  protected static final int PAUSE = 1000;

  /**
   * Test router.
   * 
   * @throws Exception
   *           the exception
   */
  public void testRouter() throws Exception {
    doTestByClass(Router.class);
  }

  /**
   * Test listener.
   * 
   * @throws Exception
   *           the exception
   */
  public void testListener() throws Exception {
    doTestByClass(Listener.class);
  }

  /**
   * Do test by class.
   * 
   * @param clazz
   *          the clazz
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings( { "deprecation", "unchecked" })
  public void doTestByClass(final Class clazz) throws Exception {
    try {
      getService(clazz, PAUSE);
      throw new AssertionError(String.format("[%s] service should not be found because wrong config loaded!", clazz
        .getName()));
    } catch (final Exception e) {
      assertNotNull(e);
    }
  }

}
