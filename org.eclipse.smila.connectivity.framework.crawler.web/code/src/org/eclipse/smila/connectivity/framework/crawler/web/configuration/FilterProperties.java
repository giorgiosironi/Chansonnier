/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.configuration;

/**
 * Crawler keys for filters class names.
 * 
 */
public class FilterProperties {

  /** The Constant BEGINNING_PATH. */
  public static final String BEGINNING_PATH = "filter.beginningpath.class";

  /** The Constant REGEXP. */
  public static final String REGEXP = "filter.regexp.class";

  /** The Constant CONTENT_TYPE. */
  public static final String CONTENT_TYPE = "filter.contenttype.class";

  /** The Constant CRAWL_SCOPE. */
  public static final String CRAWL_SCOPE = "filter.crawlscope.class";

  /** The Constant HTML_META_TAG. */
  public static final String HTML_META_TAG = "filter.htmlmetatag.class";

  /**
   * Instantiates a new filter properties.
   */
  protected FilterProperties() {
    super();
  }
}
