

/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.compound.zip.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.connectivity.framework.compound.zip bundle.
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.framework.compound.zip");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestZipCompoundCrawler.class);
    suite.addTestSuite(TestZipCompoundHandler.class);    
    // $JUnit-END$
    return suite;
  }
}
