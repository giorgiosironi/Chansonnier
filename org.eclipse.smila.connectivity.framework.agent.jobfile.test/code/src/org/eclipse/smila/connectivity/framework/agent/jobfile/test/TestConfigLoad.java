/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.agent.jobfile.test;

import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.agent.jobfile.messages.Process;
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
    assertEquals(true, (dataConnectionID.getType() == DataConnectionType.AGENT));
    _log.info("DataConnectionID TYPE=" + dataConnectionID.getType());
    assertEquals("org.eclipse.smila.connectivity.framework.agent.jobfile", configuration.getSchemaID());
    _log.info("DataConnectionID ID=" + dataConnectionID.getId());
    // attributes check
    final List<IAttribute> attrs = configuration.getAttributes().getAttribute();
    assertEquals(4, attrs.size());
    // process check
    final Process process = (Process) configuration.getProcess();
    assertNotNull(process);
    assertNotNull(process.getUpdateInterval());
    assertEquals(300, process.getUpdateInterval().longValue());
    assertNotNull(process.getAttachmentSeparator());
    assertEquals("####", process.getAttachmentSeparator());
    assertNotNull(process.getJobFileUrl());
    assertEquals(1, process.getJobFileUrl().size());
    // end of process check
  }

}
