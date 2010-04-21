/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.FilterProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FilterType;

/**
 * Class that creates filters implementation objects.
 * 
 */
public class FilterFactory {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(FilterFactory.class);

  /** The Configuration. */
  private final Configuration _conf;

  /**
   * Creates new {@link FilterFactory} instance with the given configuration.
   * 
   * @param conf
   *          {@link Configuration} crawler configuration
   */
  public FilterFactory(final Configuration conf) {
    _conf = conf;
  }

  /**
   * Returns the filter implementation.
   * 
   * @param filterType
   *          one of {@link FilterType} available values
   * 
   * @return filter implementation
   */
  public IFilter<?> getFilter(final FilterType filterType) {
    String filterClassName = null;

    IFilter<?> filter = null;
    try {
      if (filterType.equals(FilterType.BEGINNING_PATH)) {
        filterClassName = _conf.get(FilterProperties.BEGINNING_PATH);
      } else if (filterType.equals(FilterType.REG_EXP)) {
        filterClassName = _conf.get(FilterProperties.REGEXP);
      } else if (filterType.equals(FilterType.CONTENT_TYPE)) {
        filterClassName = _conf.get(FilterProperties.CONTENT_TYPE);
      } else if (filterType.equals(FilterType.CRAWL_SCOPE)) {
        filterClassName = _conf.get(FilterProperties.CRAWL_SCOPE);
      } else if (filterType.equals(FilterType.HTML_META_TAG)) {
        filterClassName = _conf.get(FilterProperties.HTML_META_TAG);
      }
      if (LOG.isInfoEnabled()) {
        LOG.info("Using URL filter: " + filterClassName);
      }
      final Class<?> filterClass = Class.forName(filterClassName);
      filter = (IFilter<?>) filterClass.newInstance();

      _conf.setObject(filterType.name(), filter);
      // CHECKSTYLE:OFF
      // Reason: the same action for all possible exceptions required
    } catch (final Exception exception) {
      // CHECKSTYLE:ON
      LOG.error("Error creating filter: " + filterClassName);
      throw new RuntimeException("Couldn't create " + filterClassName, exception);
    }
    return filter;
  }

}
