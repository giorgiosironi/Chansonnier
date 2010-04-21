/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.smila.connectivity.framework.crawler.web.filter.IFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.WorkTypeFiltersCollection;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Implemenation of the filter that allows to add or exclude some urls from crawlscope.
 * 
 */
public class CrawlScopeFilter implements IFilter<Outlink> {

  /** The Constant SLASH. */
  private static final char SLASH = '/';

  /** The seed hosts. */
  private final Set<String> _seedHosts = new HashSet<String>();

  /** The seed paths. */
  private final List<String> _seedPaths = new ArrayList<String>();

  /** The crawl scope. */
  private CrawlScope _crawlScope = CrawlScope.BROAD;

  /** The URL filters. */
  private WorkTypeFiltersCollection _urlFilters = new WorkTypeFiltersCollection();

  /**
   * Empty constructor.
   */
  public CrawlScopeFilter() {
    ;
  }

  /**
   * Assigns website seeds for initial filter configuration.
   * 
   * @param seeds
   *          Seed outlinks
   */
  public void setSeeds(final Set<Outlink> seeds) {
    for (final Outlink seed : seeds) {
      final String host = seed.getUrl().getHost();
      _seedHosts.add(host);
      final String fullPath = seed.getUrl().getPath();
      String path;
      if (fullPath.lastIndexOf(SLASH) != -1) {
        path = host + fullPath.substring(0, fullPath.lastIndexOf(SLASH));
      } else {
        path = host + fullPath;
      }
      _seedPaths.add(path);
    }
  }

  /**
   * {@inheritDoc}
   */
  public boolean matches(final Outlink link) {
    boolean linkMatches = false;
    final String linkHost = link.getUrl().getHost();
    if (_crawlScope.equals(CrawlScope.BROAD)) {
      linkMatches = true;
    } else if (_crawlScope.equals(CrawlScope.HOST)) {
      linkMatches = _seedHosts.contains(linkHost);
    } else if (_crawlScope.equals(CrawlScope.DOMAIN)) {
      final String inversedHost = new StringBuffer(linkHost).reverse().toString();
      final Iterator<String> seedHostsIterator = _seedHosts.iterator();
      while (seedHostsIterator.hasNext() && !linkMatches) {
        final String testHost = seedHostsIterator.next();
        final String inversedTestHost = new StringBuffer(testHost).reverse().toString();
        linkMatches = inversedHost.startsWith(inversedTestHost);
      }
    } else if (_crawlScope.equals(CrawlScope.PATH)) {
      final String linkPath = linkHost + link.getUrl().getPath();
      for (final String path : _seedPaths) {
        if (linkPath.startsWith(path)) {
          linkMatches = true;
          break;
        }
      }
    }

    if (linkMatches) {
      linkMatches = !matchesFilter(getUrlFilters().getUnselectFilters(), link);
    } else {
      linkMatches = matchesFilter(getUrlFilters().getSelectFilters(), link);
    }

    return linkMatches;
  }

  /**
   * Returns crawl scope type.
   * 
   * @return CrawlScope
   */
  public CrawlScope getCrawlScope() {
    return _crawlScope;
  }

  /**
   * Assigns crawl scope type.
   * 
   * @param scope
   *          CrawlScope
   */
  public void setCrawlScope(final CrawlScope scope) {
    _crawlScope = scope;
  }

  /**
   * Returns the collection of the URL filters from CrawlScope element.
   * 
   * @return WorkTypeFiltersCollection
   */
  public WorkTypeFiltersCollection getUrlFilters() {
    return _urlFilters;
  }

  /**
   * Assigns the collection of the URL filters from CrawlScope element.
   * 
   * @param filters
   *          WorkTypeFiltersCollection
   */
  public void setUrlFilters(final WorkTypeFiltersCollection filters) {
    _urlFilters = filters;
  }

  /**
   * Matches filter.
   * 
   * @param filters
   *          the filters
   * @param link
   *          the link
   * 
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  private boolean matchesFilter(final List<IFilter> filters, final Outlink link) {
    boolean matches = false;
    for (final IFilter filter : filters) {
      if (filter.matches(link)) {
        matches = true;
        break;
      }
    }
    return matches;
  }
}
