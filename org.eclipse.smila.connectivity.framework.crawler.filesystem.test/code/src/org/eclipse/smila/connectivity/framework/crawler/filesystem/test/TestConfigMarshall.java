/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.crawler.filesystem.test;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;

/**
 * The Class TestConfigMarshall.
 */
public class TestConfigMarshall extends TestCase {

  /**
   * log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Test load.
   * 
   * @throws Exception
   *           the exception
   */
  public void testLoadMarshall() throws Exception {
    final DataSourceConnectionConfig configuration =
      ConfigurationLoader.unmarshall(TestConfigMarshall.class.getResourceAsStream("ConfigExample.xml"));
    _log.info("Marshalling......");
    final Writer writer = new StringWriter();
    final Marshaller marshaller = ConfigurationLoader.crateMarshaller(configuration);
    assertNotNull(marshaller);
    _log.info("Marshaller created......");
    marshaller.marshal(configuration, writer);
    _log.info("Marshalled");
    _log.info(writer.toString());
  }

}
