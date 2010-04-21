/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.binarystorage.persistence.io.test;

import junit.framework.TestCase;

import org.eclipse.smila.binarystorage.BinaryStorageException;

/**
 * The Class TestBinaryStorageException.
 */
public class TestBinaryStorageException extends TestCase {

  /**
   * Test binary storage exception root cause.
   */
  public void testBinaryStorageExceptionRootCause() {
    final Throwable testThrowable = new Throwable() {
      /**
       * serialVersionUID.
       */
      private static final long serialVersionUID = 1L;

      @Override
      public String toString() {
        return "Test string";
      }
    };
        
    final BinaryStorageException cause = new BinaryStorageException(testThrowable);
    final BinaryStorageException exception = new BinaryStorageException(cause);
    
    assertTrue(exception.getRootCause() == testThrowable);
    assertEquals("Test string", exception.toString());
  }
  
  /**
   * Test binary storage exception constructors.
   */
  public void testBinaryStorageExceptionConstructors() {
    BinaryStorageException exception;
    try {
      exception = new BinaryStorageException();
    } catch (final Exception e) {
      fail(e.getMessage());
    }
    
    exception = new BinaryStorageException("Test message");
    assertEquals("Test message", exception.getMessage());
    
    final RuntimeException argumentException = new IllegalArgumentException("Test exception"); 
    exception = new BinaryStorageException(argumentException, "Test message");
    assertEquals("Test message", exception.getMessage());
    assertTrue(exception.getException() == argumentException);
    
    final Object[] params = new Object[] { new Object(), new Object() }; 
    exception = new BinaryStorageException(params); 
    assertTrue(params == exception.getParam());
  }
  
}
