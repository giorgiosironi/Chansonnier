/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.crawl.CrawlMode;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.IFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.WorkTypeFiltersCollection;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FollowLinksType;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

/**
 * Implementation of the {@link FilterProcessor}.
 * 
 * 
 */
public class FilterProcessorImpl implements FilterProcessor {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(FilterProcessorImpl.class);

  /** The configuration. */
  private final Configuration _conf;

  /**
   * Create new object with the given crawler configuration.
   * 
   * @param conf
   *          Configuration
   */
  public FilterProcessorImpl(final Configuration conf) {
    _conf = conf;
  }

  /**
   * {@inheritDoc}
   */
  public CrawlMode evaluateUrlFilters(final Outlink link) {
    CrawlMode result = CrawlMode.Skip;
    final CrawlScopeFilter crawlScopeFilter = _conf.getCrawlScopeFilter();
    if (crawlScopeFilter.matches(link)) {
      result = evaluateFilters(_conf.getUrlFilters(), link);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public CrawlMode evaluateHtmlMetaTagFilters(final HTMLMetaTags htmlMetaTags) {
    return evaluateFilters(_conf.getMetaTagFilters(), htmlMetaTags);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public CrawlMode evaluateFilters(final WorkTypeFiltersCollection filters, final Object test) {
    CrawlMode result = CrawlMode.Index;

    final List<IFilter> unselectFilters = filters.getUnselectFilters();

    if (unselectFilters.size() > 0) {
      for (final IFilter filter : unselectFilters) {
        if (filter.matches(test)) {
          final FollowLinksType followLinks = _conf.getFollowLinks();
          if (followLinks.equals(FollowLinksType.FOLLOW)) {
            result = CrawlMode.AnalyzeOnly;
            break;
          } else if (followLinks.equals(FollowLinksType.NO_FOLLOW)) {
            result = CrawlMode.Skip;
            break;
          } else if (followLinks.equals(FollowLinksType.FOLLOW_LINKS_WITH_CORRESPONDING_SELECT_FILTER)) {
            if (hasCorrespondingSelectFilter(filters, test)) {
              break;
            } else {
              result = CrawlMode.AnalyzeOnly;
              break;
            }
          }
        }
      }
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Crawl mode set to " + result.name());
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public boolean evaluateContentTypeFilters(final String contentType) {
    boolean result = true;
    final List<IFilter> unselectFilters = _conf.getContentTypeFilters().getUnselectFilters();

    for (final IFilter filter : unselectFilters) {
      if (filter.matches(contentType)) {
        result = false;
        break;
      }
    }
    return result;
  }

  /**
   * Checks for corresponding select filter.
   * 
   * @param filters
   *          the filters
   * @param test
   *          the test
   * 
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  private boolean hasCorrespondingSelectFilter(final WorkTypeFiltersCollection filters, final Object test) {
    boolean filterMatches = false;
    final List<IFilter> selectFilters = filters.getSelectFilters();
    if (selectFilters.size() > 0) {
      for (final IFilter filter : selectFilters) {
        if (filter.matches(test)) {
          filterMatches = true;
        }
      }
    }
    return filterMatches;
  }

}
