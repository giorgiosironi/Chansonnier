/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.test;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaNotFoundException;

/**
 * The Class TestConfigurationLoader.
 * 
 * @author Alexander Eliseyev
 */
public class TestConfigurationLoader extends TestCase {

  /**
   * The test new context exception.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNewContextException() throws Exception {
    try {
      ConfigurationLoader.newContext(null);
      fail("SchemaNotFoundException must be thrown");
    } catch (final SchemaNotFoundException e) {
      assertEquals(String.format("JAXB Schema is not found in the bundle plug-in \"%s\"", new Object[] { null }), e
        .getMessage());
    }
  }

  /**
   * Test marshall.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMarshall() throws Exception {
    final DataSourceConnectionConfig configuration =
      ConfigurationLoader.unmarshall(TestConfigurationLoader.class.getResourceAsStream("ConfigExample.xml"));
    final Writer writer = new StringWriter();
    ConfigurationLoader.marshall(configuration, writer);
    System.out.println(writer.toString());
    assertTrue(writer.toString().contains("<DataSourceID>FileSystem_C_TEST</DataSourceID>"));
  }

}
