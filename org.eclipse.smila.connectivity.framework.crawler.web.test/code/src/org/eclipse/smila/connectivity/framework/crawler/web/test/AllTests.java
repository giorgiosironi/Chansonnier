/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for org.eclipse.smila.connectivity.framework.crawler.web bundle.
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.framework.crawler.web");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestIndexDocument.class);
    suite.addTestSuite(TestConfiguration.class);
    suite.addTestSuite(TestHTMLMetaProcessor.class);
    suite.addTestSuite(TestParseHTML.class);
    suite.addTestSuite(TestParseData.class);
    suite.addTestSuite(TestJavascriptParser.class);
    suite.addTestSuite(TestConfigLoad.class);
    suite.addTestSuite(TestConfigMarshall.class);
    suite.addTestSuite(TestCrawler.class);
    // $JUnit-END$
    return suite;
  }
}
