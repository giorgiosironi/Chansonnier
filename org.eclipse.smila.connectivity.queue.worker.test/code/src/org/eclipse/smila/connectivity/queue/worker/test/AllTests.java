/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The Class AllTests.
 */
public final class AllTests {

  /**
   * Private constructor.
   */
  private AllTests() {

  }

  /**
   * Creates test suite.
   * 
   * @return Test suite.
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.queue.worker bundle");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestRouter.class);
    suite.addTestSuite(TestListener.class);
    suite.addTestSuite(TestDlq.class);
    suite.addTestSuite(TestRecycler.class);
    suite.addTestSuite(TestRecyclerAgent.class);
    suite.addTestSuite(TestConfig.class);
    suite.addTestSuite(TestConnection.class);
    // $JUnit-END$
    return suite;
  }

}
