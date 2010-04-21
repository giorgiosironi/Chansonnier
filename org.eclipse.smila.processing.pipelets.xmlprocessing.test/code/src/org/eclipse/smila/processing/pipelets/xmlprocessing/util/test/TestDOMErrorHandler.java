/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.processing.pipelets.xmlprocessing.util.test;

import junit.framework.TestCase;

import org.eclipse.smila.processing.pipelets.xmlprocessing.util.DOMErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Test class for DOMErrorHandler.
 */
public class TestDOMErrorHandler extends TestCase {

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {

  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {

  }

  /**
   * Test DOMErrorHandler.
   */
  public void testDOMErrorHandler() {
    final String msg = "a test error message";
    final Locator locator = new LocatorImpl();
    final SAXParseException exception = new SAXParseException(msg, locator);

    final DOMErrorHandler handler = new DOMErrorHandler();
    assertNotNull(handler);

    try {
      handler.error(exception);
      fail("expected SAXParseException");
    } catch (SAXParseException e) {
      assertNotNull(e);
      assertNotNull(e.getMessage());
    } catch (Exception e) {
      fail("expected SAXParseException");
    }
    
    try {
      handler.fatalError(exception);
      fail("expected SAXParseException");
    } catch (SAXParseException e) {
      assertNotNull(e);
      assertNotNull(e.getMessage());
    } catch (Exception e) {
      fail("expected SAXParseException");
    }
    
    handler.warning(exception);
  }
}
