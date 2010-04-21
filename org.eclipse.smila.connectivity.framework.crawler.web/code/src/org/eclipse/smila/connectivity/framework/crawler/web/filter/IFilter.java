/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

/**
 * Generic interface for filters.
 * 
 * @param <Type>
 */
public interface IFilter<Type> {
  /**
   * Returns true if the value of the parameter satisfies the filter and false otherwise.
   * 
   * @param test
   *          value that will be checked against the filter.
   * @return filter result
   */
  boolean matches(Type test);
}
