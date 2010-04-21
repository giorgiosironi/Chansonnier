/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 *
 * This File is based on the FetcherOutput.java from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team                                 
 **********************************************************************************************************************/
/** 
 * Copyright 2005 The Apache Software Foundation 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package org.eclipse.smila.connectivity.framework.crawler.web.fetcher;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configured;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;

/**
 * A result of {@link Fetcher} job.
 */
public final class FetcherOutput extends Configured {

  /** The Content. */
  private Content _content;

  /** The Parse. */
  private Parse _parse;

  /** The SiteMap links. */
  private Outlink[] _sitemapLinks;

  /**
   * Empty constructor.
   */
  public FetcherOutput() {
  }

  /**
   * Creates new object with the given configuration.
   * 
   * @param content
   *          Content for output
   * @param parse
   *          Result of parsing raw page content
   * @param sitemapLinks
   *          Outlinks extracted from sitemap.xml file
   */
  public FetcherOutput(Content content, Parse parse, Outlink[] sitemapLinks) {
    _content = content;
    _parse = parse;
    _sitemapLinks = sitemapLinks;
  }

  /**
   * Returns page information in the Content format.
   * 
   * @return Content
   */
  public Content getContent() {
    return _content;
  }

  /**
   * Returns result of raw content parsing.
   * 
   * @return Parse
   */
  public Parse getParse() {
    return _parse;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FetcherOutput)) {
      return false;
    }
    final FetcherOutput other = (FetcherOutput) o;
    return this._content.equals(other._content);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _content.hashCode();
  }

  /**
   * Returns the array of links extracted from sitemap.xml file.
   * 
   * @return Outlink array
   */
  public Outlink[] getSitemapLinks() {
    return _sitemapLinks;
  }

  /**
   * Assigns the array of links extracted from sitemap.xml file.
   * 
   * @param links
   *          Outlink array
   */
  public void setSitemapLinks(Outlink[] links) {
    _sitemapLinks = links;
  }

}
