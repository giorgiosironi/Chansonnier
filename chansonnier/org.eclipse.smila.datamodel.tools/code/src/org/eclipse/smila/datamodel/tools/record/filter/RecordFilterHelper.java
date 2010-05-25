/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.datamodel.tools.record.filter;

import java.io.InputStream;

import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.record.filter.messages.Filter;
import org.eclipse.smila.datamodel.tools.record.filter.messages.RecordFilters;

/**
 * The Class RecordFilterHelper.
 */
public class RecordFilterHelper {

  /** The _filters. */
  private final RecordFilters _filters;

  /**
   * Instantiates a new record filter helper.
   * 
   * @param inputStream
   *          the input stream
   * 
   * @throws RecordFilterLoadSaveException
   *           the record filter load exception
   */
  public RecordFilterHelper(final InputStream inputStream) throws RecordFilterLoadSaveException {
    _filters = RecordFilterUtils.unmarshall(inputStream);
  }

  /**
   * Instantiates a new record filter helper.
   * 
   * @param filters
   *          the filters
   */
  public RecordFilterHelper(final RecordFilters filters) {
    if (filters == null) {
      throw new IllegalArgumentException("filters parameter cannot be null!");
    }
    _filters = filters;
  }

  /**
   * Filter.
   * 
   * @param record
   *          the record
   * @param filterName
   *          the filter name
   * 
   * @return the record
   * 
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   */
  public Record filter(final Record record, final String filterName) throws RecordFilterNotFoundException {
    final Filter filter = RecordFilterUtils.findFilter(_filters, filterName);
    return RecordFilterUtils.filter(filter, record);
  }

  /**
   * Filter.
   * 
   * @param mObject
   *          the m object
   * @param filterName
   *          the filter name
   * @param factory
   *          the factory
   * 
   * @return the m object
   * 
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   */
  public MObject filter(final MObject mObject, final String filterName, final RecordFactory factory)
    throws RecordFilterNotFoundException {
    final Filter filter = RecordFilterUtils.findFilter(_filters, filterName);
    return RecordFilterUtils.filter(filter, mObject, factory);
  }

}
