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

package org.eclipse.smila.search.highlighting.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.smila.search.highlighting.transformer.test.TestHighlightingTransformerException;

/**
 * Suite combining all search.highlighting tests.
 * 
 */
public final class AllTests {
  /**
   * utility class, do not create instances.
   */
  private AllTests() {
  }

  /**
   * @return suite for all search.highlighting tests
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite("Test for org.eclipse.smila.search.highlighting");
    // $JUnit-BEGIN$    
    suite.addTestSuite(TestHighlightingTransformerException.class);
    suite.addTestSuite(TestHighlightingService.class);
    // $JUnit-END$
    return suite;
  }
}
