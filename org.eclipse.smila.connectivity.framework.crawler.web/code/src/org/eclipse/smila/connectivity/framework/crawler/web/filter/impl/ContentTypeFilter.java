/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.WorkTypeFilter;

/**
 * Implementation of the filter that filters content type on a regular expression.
 * 
 * 
 */
public class ContentTypeFilter extends WorkTypeFilter<String> {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(ContentTypeFilter.class);

  /** The value. */
  private String _value;

  /**
   * Default empty constructor.
   */
  public ContentTypeFilter() {

  }

  /**
   * Returns the filter value.
   * 
   * @return String
   */
  public String getValue() {
    return _value;
  }

  /**
   * Assigns the filter value.
   * 
   * @param value
   *          String
   */
  public void setValue(String value) {
    _value = value;
  }

  /**
   * {@inheritDoc}
   */
  public boolean matches(String test) {

    LOG.debug("Content-Type filter: test: " + test + " filter value: " + this.getValue());

    final Pattern pattern = Pattern.compile(getValue());

    if (pattern.matcher(test).find()) {
      return true;
    }

    return false;

  }

}
