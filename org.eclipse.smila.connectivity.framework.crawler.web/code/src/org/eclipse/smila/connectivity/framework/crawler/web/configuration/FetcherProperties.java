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
 * Crawler keys for Fetcher specific options.
 * 
 */
public class FetcherProperties {

  /** The Constant MAX_BYTES_DOWNLOAD. */
  public static final String MAX_BYTES_DOWNLOAD = "fetcher.max.bytes";

  /** The Constant MAX_DOCUMENT_DOWNLOAD. */
  public static final String MAX_DOCUMENT_DOWNLOAD = "fetcher.max.documents";

  /** The Constant MAX_REDIRECTS. */
  public static final String MAX_REDIRECTS = "fetcher.max.redirects";

  /** The Constant MAX_RETRIES. */
  public static final String MAX_RETRIES = "fetcher.max.retries";

  /** The Constant WAIT_RETRY. */
  public static final String WAIT_RETRY = "fetcher.wait.retry";

  /** The Constant USE_SITEMAPS. */
  public static final String USE_SITEMAPS = "fetcher.use.sitemaps";

  /**
   * Instantiates a new fetcher properties.
   */
  protected FetcherProperties() {
    super();
  }

}
