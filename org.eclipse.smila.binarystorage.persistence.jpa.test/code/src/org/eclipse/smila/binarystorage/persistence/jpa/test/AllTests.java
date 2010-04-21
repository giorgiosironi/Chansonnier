/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.jpa.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.binarystorage.persistence.jpa bundle.
 * 
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
    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.binarystorage.persistence.jpa");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestBinaryStorageDao.class);
    suite.addTestSuite(TestBinaryStorageServiceJPA.class);
    suite.addTestSuite(TestBinaryPersistenceJPA.class);
    suite.addTestSuite(TestConcurrentBSSAccessJPA.class);
    // $JUnit-END$
    return suite;
  }
}
