/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.processing.test;

import java.io.InputStream;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.eclipse.smila.test.DeclarativeServiceTestCase;
import org.eclipse.smila.utils.config.ConfigUtils;

/**
 * The Class TestPipeletConfiguration.
 */
public class TestPipeletConfiguration extends DeclarativeServiceTestCase {

  /**
   * Test load.
   *
   * @throws Exception
   *           the exception
   */
  public void testLoad() throws Exception {
    final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller();
    final InputStream inputStream =
      ConfigUtils.getConfigStream("org.eclipse.smila.processing.test", "TestPipeletConfiguration.xml");
    try {
      final PipeletConfiguration configuration = (PipeletConfiguration) unmarshaller.unmarshal(inputStream);

      assertNotNull(configuration);
      assertEquals(1 + 1 + 1, configuration.getProperties().size());

      // first
      assertTrue(configuration.hasPropertyValue("FirstIsString"));
      assertFalse(configuration.hasPropertyValue("WrongProperty"));
      Object value = configuration.getPropertyFirstValue("FirstIsString");      
      assertNotNull(value);
      System.out.println(value);
      assertEquals(value, "FirstValue");

      value = configuration.getPropertyFirstValueNotNull("FirstIsString");
      assertNotNull(value);
      System.out.println(value);
      assertEquals(value, "FirstValue");
      // second
      value = configuration.getPropertyFirstValue("SecondIsDate");
      assertNotNull(value);

      System.out.println(value.toString());

      // third
      final Object[] array = configuration.getPropertyValues("ThirdIsArray");
      assertEquals(array.length, 2 + 1);
      // 0
      assertEquals(java.util.Date.class, array[0].getClass());
      assertEquals(array[0], value);
      System.out.println(array[0]);

      // 1
      assertEquals(java.util.Date.class, array[1].getClass());
      System.out.println(array[1]);
      // 2
      assertEquals(java.util.Date.class, array[2].getClass());
      System.out.println(array[2]);

      // missing value
      value = configuration.getPropertyFirstValue("Missing");
      assertNull(value);

      try {
        value = configuration.getPropertyFirstValueNotNull("Missing");
        fail("expected an exception");
      } catch (final Exception e) {
        assertTrue(e instanceof ProcessingException);
      }
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }
}
