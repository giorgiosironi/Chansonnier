/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.connectivity bundle.
 */
public final class AllTests {

  /**
   * Constant for number 10.
   */
  public static final int NUMBER_4 = 4;

  /**
   * Constant for number 10.
   */
  public static final int NUMBER_8 = 8;

  /**
   * Constant for number 10.
   */
  public static final int NUMBER_10 = 10;

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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity");
    // $JUnit-BEGIN$        
    suite.addTestSuite(TestConnectivityException.class);
    suite.addTestSuite(TestConnectivity.class);
    // $JUnit-END$
    return suite;
  }
}
