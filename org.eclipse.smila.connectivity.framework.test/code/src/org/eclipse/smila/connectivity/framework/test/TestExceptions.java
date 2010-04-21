/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator, Daniel Stucky (empolis GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.AgentException;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.compound.CompoundException;

/**
 * Tests all the exception classes of bundle org.eclipse.smila.connectivity.framework.
 */
public class TestExceptions extends TestCase {

  /**
   * Test agent exception.
   */
  public void testAgentException() {
    final Throwable throwable = new Throwable();

    AgentException exception = new AgentException();
    assertNull(exception.getCause());
    assertNull(exception.getMessage());

    exception = new AgentException("test");
    assertEquals("test", exception.getMessage());

    exception = new AgentException(throwable);
    assertTrue(throwable == exception.getCause());

    exception = new AgentException("test", throwable);
    assertTrue(throwable == exception.getCause());
    assertEquals("test", exception.getMessage());
  }

  /**
   * Test compound exception.
   */
  public void testCompoundException() {
    final Throwable throwable = new Throwable();

    CompoundException exception = new CompoundException();
    assertNull(exception.getCause());
    assertNull(exception.getMessage());

    exception = new CompoundException("test");
    assertEquals("test", exception.getMessage());

    exception = new CompoundException(throwable);
    assertTrue(throwable == exception.getCause());

    exception = new CompoundException("test", throwable);
    assertTrue(throwable == exception.getCause());
    assertEquals("test", exception.getMessage());
  }

  /**
   * Test crawler critical exception.
   */
  public void testCrawlerCriticalException() {
    final Throwable throwable = new Throwable();

    CrawlerCriticalException exception = new CrawlerCriticalException("test");
    assertEquals("test", exception.getMessage());

    exception = new CrawlerCriticalException(throwable);
    assertTrue(throwable == exception.getCause());

    exception = new CrawlerCriticalException("test", throwable);
    assertTrue(throwable == exception.getCause());
    assertEquals("test", exception.getMessage());
  }

  /**
   * Test crawler exception.
   */
  public void testCrawlerException() {
    final Throwable throwable = new Throwable();

    CrawlerException exception = new CrawlerException();
    assertNull(exception.getCause());
    assertNull(exception.getMessage());

    exception = new CrawlerException("test");
    assertEquals("test", exception.getMessage());

    exception = new CrawlerException(throwable);
    assertTrue(throwable == exception.getCause());

    exception = new CrawlerException("test", throwable);
    assertTrue(throwable == exception.getCause());
    assertEquals("test", exception.getMessage());
  }

}
