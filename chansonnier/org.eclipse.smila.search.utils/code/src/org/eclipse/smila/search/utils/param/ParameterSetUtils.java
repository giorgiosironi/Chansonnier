/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param;

import java.lang.reflect.Method;
import java.util.Enumeration;

import org.eclipse.smila.search.utils.param.def.DParameterDefinition;
import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author brox IT-Solutions GmbH
 * 
 * This utility class is responsible for assigning parameter settings to objects. This mapping is done by reflection
 * using properties.
 * 
 * Copyright by BROX IT-Solutions GmbH
 */
public abstract class ParameterSetUtils {

  /**
   * Constructor.
   */
  private ParameterSetUtils() {

  }

  /**
   * @param dParameterSet -
   * @param dParameterDefinition -
   * @param target -
   * @throws ParameterException -
   */
  public static void assingParameterSet(DParameterSet dParameterSet, DParameterDefinition dParameterDefinition,
    Object target) throws ParameterException {
    ParameterSet paramSet;
    try {
      paramSet = new ParameterSet(dParameterSet, dParameterDefinition);
    } catch (final ParameterException e) {
      throw new ParameterException("unable to parse parameters", e);
    }

    for (final Enumeration params = paramSet.getParameterNames(); params.hasMoreElements();) {

      final String paramName = (String) params.nextElement();
      final Object o = paramSet.getParameter(paramName);
      /*
       * WARNING: formerly supported types Long, Double, File not yet implemented in ParameterSet. See comment at top of
       * org.eclipse.smila.utils.param.ParameterSet for details.
       */
      // TODO: implement support for parameter types Double, Long, File. Not yet implemented in new parameter scheme.
      try {
        final Class javaType = o.getClass();
        final Method property = target.getClass().getMethod("set" + paramName, new Class[] { javaType });
        property.invoke(target, new Object[] { o });
      } catch (final Throwable e) {
        throw new ParameterException("unable to assign parameter [" + paramName + "]", e);
      }
    }

  }
}
