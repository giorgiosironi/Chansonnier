/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Java class for Simple JAXB DataSourceConnectionConfig.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "_dataSourceID", "_schemaID" })
@XmlRootElement(name = "DataSourceConnectionConfig")
public class DataSourceConnectionConfigSimple {

  /** The _data source id. */
  @XmlElement(name = "DataSourceID", required = true)
  protected String _dataSourceID;

  /** The _schema id. */
  @XmlJavaTypeAdapter(SchemaIdAdapter.class)
  @XmlElement(name = "SchemaID", required = true)
  protected String _schemaID;

  /**
   * Gets the value of the dataSourceID property.
   *
   * @return possible object is {@link String }
   */
  public String getDataSourceID() {
    return _dataSourceID;
  }

  /**
   * Sets the value of the dataSourceID property.
   *
   * @param value
   *          allowed object is {@link String }
   */
  public void setDataSourceID(final String value) {
    this._dataSourceID = value;
  }

  /**
   * Gets the schema id.
   *
   * @return schema ID
   */
  public String getSchemaID() {
    return this._schemaID;
  }

  /**
   * sets schema ID.
   *
   * @param value
   *          the value
   */
  public void setSchemaID(final String value) {
    this._schemaID = value;
  }

}
