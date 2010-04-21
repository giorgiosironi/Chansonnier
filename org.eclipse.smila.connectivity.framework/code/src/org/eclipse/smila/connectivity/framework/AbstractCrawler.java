/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;

/**
 * Abstract base class for Crawler implementations.
 */
public abstract class AbstractCrawler implements Crawler, CrawlerCallback {
  /**
   * The crawler ID.
   */
  private String _crawlerId;

  /**
   * Default Constructor.
   */
  public AbstractCrawler() {
  }

  /**
   * Returns the Crawler Id, which is the OSGi DecarativeService Component Name.
   * 
   * @return the CrawlerId
   * @throws CrawlerException
   *           if any error occurs
   */
  public String getCrawlerId() throws CrawlerException {
    return _crawlerId;
  }

  /**
   * Activate the component.
   * 
   * @param context
   *          the ComponentContext
   */
  @SuppressWarnings("unchecked")
  protected void activate(final ComponentContext context) {
    final Dictionary<String, String> dictionary = context.getProperties();
    _crawlerId = dictionary.get("component.name");
  }
}
