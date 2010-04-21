/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

import org.eclipse.smila.connectivity.framework.crawler.web.messages.FilterWorkType;

/**
 * Abstract class that holds common functionality for all work type filters, i.e. filters that have an attribute work
 * type with possible values Select or Unselect.
 * 
 * 
 * @param <Type>
 */
public abstract class WorkTypeFilter<Type> implements IFilter<Type> {

  /** The work type. */
  protected FilterWorkType _workType;

  /**
   * Returns filters' work type.
   * 
   * @return FilterWorkType
   */
  public FilterWorkType getWorkType() {
    return _workType;
  }

  /**
   * Assigns filters' work type.
   * 
   * @param theWorkType
   *          FilterWorkType
   */
  public void setWorkType(final FilterWorkType theWorkType) {
    this._workType = theWorkType;
  }

}
