/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.tools.MObjectHelper;

/**
 * A factory to create Id objects in Crawlers and Agents.
 */
public final class ConnectivityIdFactory {

  /**
   * singleton instance.
   */
  private static ConnectivityIdFactory s_instance;

  /**
   * The datamodel IdFactory.
   */
  private IdFactory _idFactory = IdFactory.DEFAULT_INSTANCE;

  /**
   * Default Constructor.
   */
  private ConnectivityIdFactory() {
  }

  /**
   * Returns the singleton instance of the IdFactory.
   * 
   * @return the IdFactory
   */
  public static ConnectivityIdFactory getInstance() {
    if (s_instance == null) {
      s_instance = new ConnectivityIdFactory();
    }
    return s_instance;
  }

  /**
   * Create an Id object based on the given Attributes.
   * @param dataSourceId the dataSourceId. Must not be null or empty.
   * @param idAttributes
   *          an array of Attribute objects whose values are used to create the Id. Must not be null or empty.
   * @return the Id object
   */
  public Id createId(final String dataSourceId, final Attribute[] idAttributes) {
    if (dataSourceId == null || dataSourceId.trim().length() == 0) {
      throw new IllegalArgumentException("Parameter dataSourceId must not be null or empty");
    }
    if (idAttributes == null || idAttributes.length == 0) {
      throw new IllegalArgumentException("Parameter idAttributes must not be null or empty");
    }
    final Map<String, String> nameValues = new HashMap<String, String>();
    // concatenate literals of attributes with multiple values
    for (final Attribute attribute : idAttributes) {
      final String value = MObjectHelper.glueLiterals(attribute);
      nameValues.put(attribute.getName(), value);
    }
    return _idFactory.createId(dataSourceId, nameValues);
  }

}
