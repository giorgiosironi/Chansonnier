/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH) 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.connectivity.framework.schema.exceptions.SchemaRuntimeException;

/**
 * The Class DataConnectionIDAdapter.
 */
public class DataConnectionIDAdapter extends
  XmlAdapter<DataSourceConnectionConfig.DataConnectionIDOriginal, DataConnectionID> {

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
   */
  @Override
  public DataSourceConnectionConfig.DataConnectionIDOriginal marshal(final DataConnectionID v) throws Exception {
    final DataSourceConnectionConfig.DataConnectionIDOriginal o =
      new DataSourceConnectionConfig.DataConnectionIDOriginal();
    switch (v.getType()) {
      case AGENT:
        o.setAgent(v.getId());
        break;
      case CRAWLER:
        o.setCrawler(v.getId());
        break;
      default:
        throw new SchemaRuntimeException("Unknown data connection type");
    }
    return o;
  }

  /**
   * {@inheritDoc}
   * 
   * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
   */
  @Override
  public DataConnectionID unmarshal(final DataSourceConnectionConfig.DataConnectionIDOriginal v) throws Exception {
    final DataConnectionID dataConnectionID = new DataConnectionID();
    if (v.getAgent() != null && !"".equals(v.getAgent())) {
      dataConnectionID.setType(DataConnectionType.AGENT);
      dataConnectionID.setId(v.getAgent());
    } else if (v.getCrawler() != null && !"".equals(v.getCrawler())) {
      dataConnectionID.setType(DataConnectionType.CRAWLER);
      dataConnectionID.setId(v.getCrawler());
    } else {
      throw new SchemaRuntimeException("Unknown data connection type");
    }
    return dataConnectionID;
  }

}
