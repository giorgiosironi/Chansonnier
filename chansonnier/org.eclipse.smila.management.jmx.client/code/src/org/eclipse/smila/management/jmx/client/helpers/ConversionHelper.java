/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.helpers;

import java.util.Date;

import org.apache.commons.beanutils.ConvertUtils;
import org.eclipse.smila.management.jmx.client.converters.DateConverter;

/**
 * The Class ConversionHelper.
 */
public final class ConversionHelper {

  static {
    ConvertUtils.register(new DateConverter(), Date.class);
  }

  /**
   * Private constructor to avoid instatiation.
   */
  private ConversionHelper() {
  }

  /**
   * Convert.
   * 
   * @param parameter
   *          the parameter
   * @param className
   *          the class name
   * 
   * @return the object
   */
  @SuppressWarnings("unchecked")
  public static Object convert(final String parameter, final String className) {
    final Class clazz;
    try {
      clazz = Class.forName(className);
    } catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    return ConvertUtils.convert(parameter, clazz);
  }
}
