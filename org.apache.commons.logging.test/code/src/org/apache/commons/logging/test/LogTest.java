/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.apache.commons.logging.test;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test case for commons-logging.
 */
public class LogTest extends TestCase {

  /**
   * Test Commons-Logging.
   */
  public final void testCommonsLogging() {
    final Log log = LogFactory.getLog(LogTest.class);
    assertNotNull(log);
  }

}
