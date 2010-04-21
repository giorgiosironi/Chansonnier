/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smila.connectivity.framework.crawler.web.messages.FilterWorkType;

/**
 * Class that holds collection of {@link WorkType} filters, delimited by Select and Unselect work types.
 * 
 * 
 */
public class WorkTypeFiltersCollection {

  /** The select filters. */
  @SuppressWarnings("unchecked")
  private List<IFilter> _selectFilters = new ArrayList<IFilter>();

  /** The unselect filters. */
  @SuppressWarnings("unchecked")
  private List<IFilter> _unselectFilters = new ArrayList<IFilter>();

  /**
   * Adds a filter to appropriate list by work type.
   * 
   * @param filter
   *          Filter
   */
  public void add(final IFilter<?> filter) {
    final WorkTypeFilter<?> filterCast = (WorkTypeFilter<?>) filter;
    if (filterCast.getWorkType().equals(FilterWorkType.SELECT)) {
      addSelectFilter(filterCast);
    } else {
      addUnselectFilter(filterCast);
    }
  }

  /**
   * Adds the select filter.
   * 
   * @param filter
   *          the filter
   */
  private void addSelectFilter(final IFilter<?> filter) {
    _selectFilters.add(filter);
  }

  /**
   * Adds the unselect filter.
   * 
   * @param filter
   *          the filter
   */
  private void addUnselectFilter(final IFilter<?> filter) {
    _unselectFilters.add(filter);
  }

  /**
   * Returns the list of filters with work type Select.
   * 
   * @return List
   */
  @SuppressWarnings("unchecked")
  public List<IFilter> getSelectFilters() {
    return _selectFilters;
  }

  /**
   * Assigns the list of filters with work type Select.
   * 
   * @param filters
   *          List
   */
  @SuppressWarnings("unchecked")
  public void setSelectFilters(final List<IFilter> filters) {
    _selectFilters = filters;
  }

  /**
   * Returns the list of filters with work type Unselect.
   * 
   * @return List of filters
   */
  @SuppressWarnings("unchecked")
  public List<IFilter> getUnselectFilters() {
    return _unselectFilters;
  }

  /**
   * Assigns the list of filters with work type Unselect.
   * 
   * @param filters
   *          List of filters
   */
  @SuppressWarnings("unchecked")
  public void setUnselectFilters(final List<IFilter> filters) {
    _unselectFilters = filters;
  }

}
