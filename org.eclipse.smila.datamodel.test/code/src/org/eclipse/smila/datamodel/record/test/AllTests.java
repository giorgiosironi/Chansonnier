/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite combining all record tests.
 *
 * @author jschumacher
 *
 */
public final class AllTests {

  /**
   * utility class, do not create instances.
   */
  private AllTests() {
  }

  /**
   * @return suite for all record tests
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite("Test for org.eclipse.smila.datamodel.record");
    //$JUnit-BEGIN$
    suite.addTestSuite(TestXmlDomUtilities.class);
    suite.addTestSuite(TestLiterals.class);
    suite.addTestSuite(TestAnnotations.class);
    suite.addTestSuite(TestAttributes.class);
    suite.addTestSuite(TestXmlStaxUtilities.class);
    //$JUnit-END$
    return suite;
  }

}
