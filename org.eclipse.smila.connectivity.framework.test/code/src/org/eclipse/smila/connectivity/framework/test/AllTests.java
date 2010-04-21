/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.smila.connectivity.framework.compound.test.TestCompoundManager;
import org.eclipse.smila.connectivity.framework.util.test.TestConnectivityHashFactory;
import org.eclipse.smila.connectivity.framework.util.test.TestConnectivityIdFactory;

/**
 * Test suite for org.eclipse.smila.connectivity.framework bundle.
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.connectivity.framework");
    // $JUnit-BEGIN$        
        
    // test compounds       
    suite.addTestSuite(TestCompoundManager.class);
    
    // test utils       
    suite.addTestSuite(TestConnectivityHashFactory.class);
    suite.addTestSuite(TestConnectivityIdFactory.class);
    suite.addTestSuite(TestExceptions.class);
    suite.addTestSuite(TestControllerCallback.class);
    
    // test AgentController
    suite.addTestSuite(TestAgentController.class);
    suite.addTestSuite(TestAgentControllerExceptions.class);
    suite.addTestSuite(TestAgentControllerAgent.class);
    
    // test CrawlerController
    suite.addTestSuite(TestCrawlerControllerPerformanceCounterHelper.class);
    suite.addTestSuite(TestCrawlerController.class);
    suite.addTestSuite(TestCrawlerControllerExceptions.class);
    suite.addTestSuite(TestCrawlerControllerAgent.class);
    
    // $JUnit-END$
    return suite;
  }
}
