/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.schema.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.connectivity.framework.schema bundle.
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.framework.schema");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestCrawlThread.class);
    suite.addTestSuite(TestCrawlThreadState.class);
    suite.addTestSuite(TestConfigurationLoader.class);
    suite.addTestSuite(TestMessages.class);
    suite.addTestSuite(TestConfigLoad.class);
    suite.addTestSuite(TestErrorsCatching.class);
    suite.addTestSuite(TestExceptions.class);
    suite.addTestSuite(TestSimpleDateFormatter.class);
    // $JUnit-END$
    return suite;
  }
}
