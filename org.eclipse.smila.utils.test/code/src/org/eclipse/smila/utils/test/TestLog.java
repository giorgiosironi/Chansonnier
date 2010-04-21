/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.utils.log.BundleLogHelper;
import org.eclipse.smila.utils.log.RecordLifecycleLogHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * The Class TestLog.
 */
public class TestLog extends TestCase {

  /**
   * Test bundle log helper.
   * 
   * @throws BundleException
   *           if any error occurs
   */
  public void testBundleLogHelper() throws BundleException {
    BundleLogHelper.logBundlesState();
    final Bundle bundle = Platform.getBundle("org.apache.ode");
    bundle.stop();
    BundleLogHelper.logBundlesState();
    bundle.uninstall();
    BundleLogHelper.logBundlesState();
  }

  /**
   * Test record lifecycle log helper.
   */
  public void testRecordLifecycleLogHelper() {
    if (RecordLifecycleLogHelper.isRecordStateLogEnabled()) {
      RecordLifecycleLogHelper.logRecordState("Dummy record was crawled", "some dummy hash");
    }
  }
}
