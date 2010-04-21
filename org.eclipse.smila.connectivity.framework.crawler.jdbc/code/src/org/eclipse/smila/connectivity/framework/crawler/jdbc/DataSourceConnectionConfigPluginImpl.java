/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Michael Breidenband (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.jdbc;

import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;

/**
 * Implementation of the {@link DataSourceConnectionConfigPlugin}-interface of the Jdbc-Crawler-Bundle.
 * 
 * @author mbreidenband
 * 
 */
public class DataSourceConnectionConfigPluginImpl implements DataSourceConnectionConfigPlugin {

  /**
   * {@inheritDoc}
   */
  public String getSchemaLocation() {
    return "schemas/JdbcDataSourceConnectionConfigSchema.xsd";
  }

  /**
   * {@inheritDoc}
   */
  public String getMessagesPackage() {
    return "org.eclipse.smila.connectivity.framework.crawler.jdbc.messages";
  }

}
