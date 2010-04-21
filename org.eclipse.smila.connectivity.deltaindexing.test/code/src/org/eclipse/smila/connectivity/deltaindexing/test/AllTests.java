/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.smila.connectivity.deltaindexing.impl.test.AllTestsMemory;
import org.eclipse.smila.connectivity.deltaindexing.jpa.impl.test.AllTestsJPA;

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
    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.deltaindexing bundle");
    // $JUnit-BEGIN$

    // test utilities
    suite.addTestSuite(TestDeltaIndexingException.class);
    suite.addTestSuite(TestDeltaIndexingSessionException.class);

    // test memory impl
    suite.addTest(AllTestsMemory.suite());

    // test jpa impl
    suite.addTest(AllTestsJPA.suite());

    // test JMX agent
    suite.addTestSuite(TestDimAgent.class);
    // $JUnit-END$
    return suite;
  }
}
