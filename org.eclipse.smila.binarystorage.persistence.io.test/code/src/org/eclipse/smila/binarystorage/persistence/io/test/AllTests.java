/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marius Cimpean (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.binarystorage bundle.
 *
 * @author mcimpean
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.binarystorage.persistence.io");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestBinaryPersistence.class);
    suite.addTestSuite(TestBinaryStorageException.class);
    suite.addTestSuite(TestBinaryStorageConfiguration.class);
    suite.addTestSuite(TestBinaryStorageService.class);
    // suite.addTestSuite(TestOOMVfs.class);
    suite.addTestSuite(TestConcurrentBSSAccess.class);
    // $JUnit-END$
    return suite;
  }
}
