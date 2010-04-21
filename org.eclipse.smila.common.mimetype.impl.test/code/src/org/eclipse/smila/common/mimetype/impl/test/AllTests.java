/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Georg Schmidt (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.common.mimetype.impl.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite combining all mimetype tests.
 */
public final class AllTests {

  /**
   * utility class, do not create instances.
   */
  private AllTests() {
  }

  /**
   * @return suite for all mimetype tests
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite("Test for org.eclipse.smila.common.mimetype.impl");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestMimeTypeParseException.class);
    suite.addTestSuite(TestMimeTypeMapper.class);
    suite.addTestSuite(TestMimeTypeIdentifier.class);
    // $JUnit-END$
    return suite;
  }
}
