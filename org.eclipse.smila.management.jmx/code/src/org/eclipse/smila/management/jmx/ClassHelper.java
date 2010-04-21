/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Class ClassHelper.
 */
@SuppressWarnings("unchecked")
public final class ClassHelper {

  /**
   * The Constant EMPTY_SIGNATURE.
   */
  public static final String[] EMPTY_SIGNATURE = new String[] {};

  /**
   * The Constant EMPTY_PARAMS.
   */
  public static final Object[] EMPTY_PARAMS = new Object[] {};

  /**
   * The Constant PRIMITIVES.
   */
  private static final Class[] PRIMITIVES = {// 
    boolean.class,// 
      short.class, //
      int.class, //
      long.class, //
      double.class, //
      float.class, //
      byte.class, //
      char.class //
    };

  /**
   * The Constant PRIMITIVES_MAP.
   */
  private static final Map<String, Class> PRIMITIVES_MAP = new HashMap<String, Class>();

  static {
    for (int i = 0; i < PRIMITIVES.length; i++) {
      final Class c = PRIMITIVES[i];
      PRIMITIVES_MAP.put(c.getName(), c);
    }
  }

  /**
   * The _log.
   */
  private static final Log LOG = LogFactory.getLog(ClassHelper.class);

  /**
   * prevents class helper instance.
   */
  private ClassHelper() {

  }

  /**
   * Search class.
   * 
   * @param className
   *          the class name
   * 
   * @return the class
   * 
   * @throws ReflectionException
   *           the reflection exception
   */
  public static Class searchClass(final String className) throws ReflectionException {
    final Class pClass = PRIMITIVES_MAP.get(className);
    if (pClass != null) {
      return pClass;
    }
    try {
      return Class.forName(className);
    } catch (final ClassNotFoundException e) {
      LOG.error(e);
      throw new ReflectionException(e);
    }
  }

}
