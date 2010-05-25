/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.config;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The Class DataConnectionID.
 */
@XmlJavaTypeAdapter(DataConnectionIDAdapter.class)
public class DataConnectionID {

  /**
   * The Enum DataConnectionType.
   */
  public enum DataConnectionType {

    /** The AGENT. */
    AGENT,

    /** The CRAWLER. */
    CRAWLER
  }

  /** The _id. */
  private String _id;

  /** The _type. */
  private DataConnectionType _type;

  /**
   * Gets id property.
   * 
   * @return the id
   */
  public String getId() {
    return _id;
  }

  /**
   * Sets the id property.
   * 
   * @param id
   *          the new id
   */
  public void setId(final String id) {
    _id = id;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public DataConnectionType getType() {
    return _type;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          the new type
   */
  public void setType(final DataConnectionType type) {
    _type = type;
  }

}
