/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter;

import org.eclipse.smila.connectivity.framework.crawler.web.crawl.CrawlMode;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

/**
 * Interface for filter processors.
 */
public interface FilterProcessor {
  /**
   * Checks if the link satisfies crawler url filters.
   * 
   * @param link
   *          Outlink to be tested
   * @return one of the {@link CrawlMode} values
   */
  CrawlMode evaluateUrlFilters(Outlink link);

  /**
   * Checks if the given content-type string satisfies content-type filters.
   * 
   * @param contentType
   *          content-type string
   * @return true or false
   */
  boolean evaluateContentTypeFilters(String contentType);

  /**
   * Checks if the given set of html metatags satisfies meta tag filters.
   * 
   * @param htmlMetaTags
   *          set of meta tags
   * @return one of the {@link CrawlMode} values
   */
  CrawlMode evaluateHtmlMetaTagFilters(HTMLMetaTags htmlMetaTags);
}
