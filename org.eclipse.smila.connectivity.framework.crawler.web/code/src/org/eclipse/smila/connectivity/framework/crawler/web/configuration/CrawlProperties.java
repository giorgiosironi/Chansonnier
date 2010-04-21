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
 * Crawler keys for crawl specific options.
 * 
 */
public class CrawlProperties {

  /** The Constant PROJECT_NAME. */
  public static final String PROJECT_NAME = "crawl.project.name";

  /** The Constant MAX_TIME_SEC. */
  public static final String MAX_TIME_SEC = "crawl.max.time";

  /** The Constant WAIT. */
  public static final String WAIT = "crawl.wait";

  /** The Constant RANDOM_WAIT. */
  public static final String RANDOM_WAIT = "crawl.random.wait";

  /** The Constant CRAWLING_MODEL_TYPE. */
  public static final String CRAWLING_MODEL_TYPE = "crawl.model.type";

  /** The Constant CRAWLING_MODEL_VALUE. */
  public static final String CRAWLING_MODEL_VALUE = "crawl.model.value";

  /**
   * Instantiates a new crawl properties.
   */
  protected CrawlProperties() {
    super();
  }

}
