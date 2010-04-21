/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.processing.pipelets.xmlprocessing.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestDOMErrorHandler;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestNullPrintWriter;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestXMLUtils;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestXmlException;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestXmlUtilsException;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestXslException;
import org.eclipse.smila.processing.pipelets.xmlprocessing.util.test.TestXslTransformer;

/**
 * Test suite for org.eclipse.smila.processing.pipelets.xmlprocessing bundle.
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

    final TestSuite suite = new TestSuite("Tests for org.eclipse.smila.processing.pipelets.xmlprocessing");
    // $JUnit-BEGIN$

    // test utility classes
    suite.addTestSuite(TestXmlException.class);
    suite.addTestSuite(TestXmlUtilsException.class);
    suite.addTestSuite(TestXslException.class);
    suite.addTestSuite(TestNullPrintWriter.class);    
    suite.addTestSuite(TestXMLUtils.class);
    suite.addTestSuite(TestXslTransformer.class);
    suite.addTestSuite(TestDOMErrorHandler.class);
    
    // test pipelets
    suite.addTestSuite(TestXslTransformationPipelet.class);
    suite.addTestSuite(TestTidyPipelet.class);
    suite.addTestSuite(TestXPathExtractorPipelet.class);
    suite.addTestSuite(TestRemoveElementFromXMLPipelet.class);
    suite.addTestSuite(TestXPathFilterPipelet.class);

    // $JUnit-END$
    return suite;
  }

}
