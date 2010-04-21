/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.crawler.filesystem.test;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.schema.config.interfaces.IAttribute;

/**
 * The Class TestConfigLoad.
 */
public class TestConfigLoad extends TestCase {

  /**
   * The _log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Test load.
   * 
   * @throws Exception
   *           the exception
   */
  public void testLoad() throws Exception {
    final DataSourceConnectionConfig configuration =
      ConfigurationLoader.unmarshall(TestConfigLoad.class.getResourceAsStream("ConfigExample.xml"));
    _log.info("DataSourceID=" + configuration.getDataSourceID());
    final DataConnectionID dataConnectionID = configuration.getDataConnectionID();
    assertEquals(true, (dataConnectionID.getType() == DataConnectionType.CRAWLER));
    _log.info("DataConnectionID TYPE=" + dataConnectionID.getType());
    assertEquals("org.eclipse.smila.connectivity.framework.crawler.filesystem", configuration.getSchemaID());
    _log.info("DataConnectionID ID=" + dataConnectionID.getId());
    // attributes check
    final List<IAttribute> attrs = configuration.getAttributes().getAttribute();
    assertEquals(2 + 2 + 1, attrs.size());
    // process check
    final org.eclipse.smila.connectivity.framework.schema.config.interfaces.IProcess iProcess =
      configuration.getProcess();
    assertNotNull(iProcess);
    // end of process check
  }

}
