/***************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc.test;

import junit.framework.Test;
import junit.framework.TestSuite;

// CHECKSTYLE:OFF
// dont need checkstyle for the testsuite

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.framework.crawler.jdbc.test");
    // $JUnit-BEGIN$
    suite.addTestSuite(TestConfigMarshall.class);
    suite.addTestSuite(TestConfigLoad.class);
    suite.addTestSuite(TestDeltaIndexingDerby.class);
    suite.addTestSuite(TestRecordRetrievalDerbyWithGrouping.class);
    suite.addTestSuite(TestRecordRetrievalDerbyWithoutGrouping.class);

    // 27.11.2008, Michael Breidenband:
    // the following TestCases are outcommented on purpose: they would not run on Bamboo. They depend on the
    // presence of a specific ODBC datasource (UsedCars) which is very likely not installed on Bamboo. Please
    // dont delete the outcommented lines though, as its convenient for testing other Jdbc-Drivers than Derby on
    // my workstation

    // suite.addTestSuite(TestRecordRetrievalOdbcWithGrouping.class);
    // suite.addTestSuite(TestRecordRetrievalOdbcWithoutGrouping.class);
    // suite.addTestSuite(TestRecordRetrievalOdbcWithWhitespaceInSql.class);
    // $JUnit-END$
    return suite;
  }

}
// CHECKSTYLE:ON
