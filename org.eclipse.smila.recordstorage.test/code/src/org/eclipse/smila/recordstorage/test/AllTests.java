/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Stucky (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.recordstorage.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite combining all recordstorage tests.
 * 
 */
public final class AllTests {
  /**
   * utility class, do not create instances.
   */
  private AllTests() {
  }

  /**
   * @return suite for all recordstorage tests
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite("Test for org.eclipse.smila.recordstorage.");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestUtilities.class);
    suite.addTestSuite(TestRecordStorage.class);
    // $JUnit-END$
    return suite;
  }
}
