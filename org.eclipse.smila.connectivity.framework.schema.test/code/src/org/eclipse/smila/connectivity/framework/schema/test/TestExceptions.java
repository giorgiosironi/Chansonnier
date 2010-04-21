/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.test;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;

/**
 * The Class ExceptionTest.
 * @author Alexander Eliseyev 
 */
public class TestExceptions extends TestCase {
  
  /**
   * Test schema runtime exception.
   */
  public void testSchemaRuntimeException() {
    final Throwable throwable = new Throwable();
    
    SchemaRuntimeException exception = new SchemaRuntimeException("test");
    assertEquals("test", exception.getMessage());
    
    exception = new SchemaRuntimeException(throwable);
    assertTrue(throwable == exception.getCause());
    
    exception = new SchemaRuntimeException("test", throwable);
    assertTrue(throwable == exception.getCause());
    assertEquals("test", exception.getMessage());
  }
}
