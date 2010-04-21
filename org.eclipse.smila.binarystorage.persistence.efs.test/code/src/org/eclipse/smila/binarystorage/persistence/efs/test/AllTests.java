/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.efs.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.binarystorage.persistence.impl bundle.
 *
 * @author Alexander Eliseyev
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
    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.binarystorage.persistence.efs");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestBinaryStorageServiceEFS.class);
    suite.addTestSuite(TestBinaryPersistenceEFS.class);
    suite.addTestSuite(TestConcurrentBSSAccessEFS.class);
    // $JUnit-END$
    return suite;
  }
}
