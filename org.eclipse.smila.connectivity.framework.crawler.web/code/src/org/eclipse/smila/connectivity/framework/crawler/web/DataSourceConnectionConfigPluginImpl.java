/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Andrey Basalaev (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web;

import org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin;

/**
 * The Class DataSourceConnectionConfigPluginImpl.
 */
public class DataSourceConnectionConfigPluginImpl implements DataSourceConnectionConfigPlugin {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin#getSchemaLocation()
   */
  public String getSchemaLocation() {
    return "schemas/WebDataSourceConnectionConfigSchema.xsd";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.schema.DataSourceConnectionConfigPlugin#getMessagesPackage()
   */
  public String getMessagesPackage() {
    return "org.eclipse.smila.connectivity.framework.crawler.web.messages";
  }

}
