/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.util.CrawlThreadState;

/**
 * The Class TestCrawlThreadState.
 */
public class TestCrawlThreadState extends TestCase {

  /**
   * Test crawl thread state valueOf().
   */
  public void testValueOf() {
    assertEquals(CrawlThreadState.Aborted, CrawlThreadState.valueOf("Aborted")); 
    assertEquals(CrawlThreadState.Finished, CrawlThreadState.valueOf("Finished"));
    assertEquals(CrawlThreadState.Running, CrawlThreadState.valueOf("Running"));
    assertEquals(CrawlThreadState.Stopped, CrawlThreadState.valueOf("Stopped"));
  }
  
  /**
   * Test values.
   */
  public void testValues() {
    final List<CrawlThreadState> values = Arrays.asList(CrawlThreadState.values());
    assertNotNull(values);
    assertTrue(!values.isEmpty());
    assertTrue(values.containsAll(EnumSet.allOf(CrawlThreadState.class)));
  }
  
}
