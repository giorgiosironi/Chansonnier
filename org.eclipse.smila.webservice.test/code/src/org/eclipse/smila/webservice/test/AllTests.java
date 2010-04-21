/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.webservice.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.utils bundle.
 */
public final class AllTests {

  /**
   * The Constant BUNDLE_ID.
   */
  public static final String BUNDLE_ID = "org.eclipse.smila.utils.test";

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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.utils");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestHelloWorldService.class);
    // $JUnit-END$
    return suite;
  }
}
