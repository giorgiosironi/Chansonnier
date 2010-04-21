/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.schema.test;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaNotFoundException;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;

/**
 * The Class TestErrorsCatching.
 */
public class TestErrorsCatching extends TestCase {

  /**
   * Test not valid xml load.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNotValidXmlLoad() throws Exception {
    try {
      ConfigurationLoader.unmarshall(TestErrorsCatching.class.getResourceAsStream("ConfigExample_NotValid.xml"));
      throw new RuntimeException("Wrong Config was loaded!");
    } catch (final JAXBException e) {
      assertNotNull(e);
    }
  }

  /**
   * Test no stream.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNoStream() throws Exception {
    try {
      ConfigurationLoader.unmarshall(null);
      fail("SchemaRuntimeException must be thrown");
    } catch (final SchemaRuntimeException e) {
      assertNotNull(e);
    }
  }
  
  /**
   * Test no provider.
   * 
   * @throws Exception
   *           the exception
   */
  public void testNoProvider() throws Exception {
    try {
      ConfigurationLoader.unmarshall(TestErrorsCatching.class.getResourceAsStream("ConfigExample_NoProvider.xml"));
      throw new RuntimeException("Wrong Config was loaded!");
    } catch (final SchemaNotFoundException e) {
      assertNotNull(e);
    }

  }

}
