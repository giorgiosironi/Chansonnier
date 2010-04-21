/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tmenzel (brox IT Solution GmbH )- initial creator
 *******************************************************************************/

package org.eclipse.smila.connectivity.deltaindexing.impl.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author tmenzel
 *
 */
public class AllTestsMemory {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.eclipse.smila.connectivity.deltaindexing.impl.test");
    //$JUnit-BEGIN$
    suite.addTestSuite(TestDimMemory.class);
    suite.addTestSuite(TestDeltaIndexerWorkflowMemory.class);
    //$JUnit-END$
    return suite;
  }

}
