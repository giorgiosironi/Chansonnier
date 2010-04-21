/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import javax.xml.validation.Schema;

import junit.framework.TestCase;

import org.eclipse.smila.utils.xml.SchemaUtils;
import org.xml.sax.SAXException;

/**
 * The Class TestSchemaUtils.
 */
public class TestSchemaUtils extends TestCase {

  /**
   * The Constant SCHEMA_PATH.
   */
  private static final String SCHEMA_PATH = "/schemas/test.xsd";

  /**
   * Test schema utils.
   * 
   * @throws SAXException
   *           the SAX exception
   */
  public void testSchemaUtils() throws SAXException {
    final Schema schema = SchemaUtils.loadSchema(AllTests.BUNDLE_ID, SCHEMA_PATH);
    assertNotNull(schema);
  }

  /**
   * Test schema utils runtime ex.
   */
  public void testSchemaUtilsRuntimeEx() {
    final Schema schema = SchemaUtils.loadSchemaRuntimeEx(AllTests.BUNDLE_ID, SCHEMA_PATH);
    assertNotNull(schema);
  }

  /**
   * Test schema utils runtime ex2.
   */
  public void testSchemaUtilsRuntimeEx2() {
    Schema schema = null;
    try {
      schema = SchemaUtils.loadSchemaRuntimeEx(AllTests.BUNDLE_ID, "/schemas/testBrokenSchema.xsd");
      throw new AssertionError("Schema should not be loaded!");
    } catch (final Exception e) {
      ;// ok
    }
    assertNull(schema);
  }

}
