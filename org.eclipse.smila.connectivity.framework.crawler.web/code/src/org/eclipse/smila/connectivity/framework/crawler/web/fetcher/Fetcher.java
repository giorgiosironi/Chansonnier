/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the Fetcher.java from Nutch 0.8.1 (see below the licene). The original File was modified by the
 * Smila Team
 **********************************************************************************************************************/
/**
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.eclipse.smila.connectivity.framework.crawler.web.fetcher;

import java.net.MalformedURLException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.WebCrawler;
import org.eclipse.smila.connectivity.framework.crawler.web.WebCrawlerPerformanceAgent;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configured;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.FetcherProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.crawl.CrawlMode;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.http.Http;
import org.eclipse.smila.connectivity.framework.crawler.web.http.HttpOutput;
import org.eclipse.smila.connectivity.framework.crawler.web.http.HttpStatus;
import org.eclipse.smila.connectivity.framework.crawler.web.http.SitemapParser;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseStatus;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parser;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParserManager;
import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceCounterHelper;

/**
 * Class that fetches the link and updates crawler status.
 */
public class Fetcher extends Configured {

  /**
   * Milliseconds in second constant.
   */
  private static final int MILLISECS_IN_SEC = 1000;

  /**
   * The Log.
   */
  private final Log _log = LogFactory.getLog(Fetcher.class);

  /**
   * Total bytes fetched.
   */
  private long _bytes;

  /**
   * Total pages fetched.
   */
  private int _pages;

  /**
   * Total pages occur.
   */
  private int _errors;

  /**
   * The max redirect.
   */
  private final int _maxRedirect;

  /**
   * The max retries.
   */
  private final int _maxRetries;

  /**
   * The wait retry.
   */
  private final int _waitRetry;

  /**
   * The output.
   */
  private FetcherOutput _output;

  /**
   * The site maps enabled.
   */
  private final boolean _sitemapsEnabled;

  /**
   * The site map.
   */
  private SitemapParser _sitemap;

  /**
   * The _performance counters.
   */
  private final CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent> _performanceCounters;

  /**
   * Webcrawler parsers manager.
   */
  private final ParserManager _parserManager;

  /**
   * Creates object with given Configuration.
   * 
   * @param configuration
   *          Configuration
   * @param performanceCounters
   *          the performance counters
   * @param parserManager
   *          parser manager
   */
  public Fetcher(final Configuration configuration, final ParserManager parserManager,
    final CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent> performanceCounters) {
    super(configuration);
    _performanceCounters = performanceCounters;
    _maxRedirect = configuration.getInt(FetcherProperties.MAX_REDIRECTS);
    _maxRetries = configuration.getInt(FetcherProperties.MAX_RETRIES);
    _waitRetry = configuration.getInt(FetcherProperties.WAIT_RETRY) * Configuration.MILLIS_PER_SECOND;
    _sitemapsEnabled = configuration.getBoolean(FetcherProperties.USE_SITEMAPS);
    if (_sitemapsEnabled) {
      _sitemap = new SitemapParser(configuration);
    }
    _parserManager = parserManager;
  }

  /**
   * Update status.
   * 
   * @param bytesInPage
   *          the bytes in page
   */
  private synchronized void updateStatus(final int bytesInPage) {
    _pages++;
    _performanceCounters.increment(WebCrawler.POC_PAGES);
    _bytes += bytesInPage;
    _performanceCounters.incrementBy(WebCrawler.POC_BYTES, bytesInPage);
  }

  /**
   * Fetches and parses the link.
   * 
   * @param link
   *          link to fetch
   * @param filterProcessor
   *          filters to perform
   * @param linksDone
   *          list of already crawled links
   * 
   * @return FetcherOutput
   */
  public FetcherOutput fetch(Outlink link, final FilterProcessor filterProcessor, final Set<Outlink> linksDone) {
    try {
      // fetch the page
      boolean redirecting;
      boolean retrying;
      int redirectCount = 0;
      int retriesCount = 0;
      boolean continueFetching;
      final String url = link.toString();
      final Http http = new Http();
      http.setConf(getConf());
      Outlink[] sitemapLinks = new Outlink[0];

      if (_sitemapsEnabled) {
        if (_log.isDebugEnabled()) {
          _log.debug("Trying to download sitemap.xml");
        }
        sitemapLinks = _sitemap.getSitemapLinks(http, link.getUrl());
      }

      if (_log.isDebugEnabled()) {
        _log.debug("processing link: " + url);
      }
      final long start = System.currentTimeMillis();
      do {
        if (_log.isDebugEnabled()) {
          _log.debug("redirectCount=" + redirectCount);
          _log.debug("retriesCount=" + retriesCount);
        }
        redirecting = false;
        retrying = false;

        final HttpOutput output = http.getHttpOutput(link, filterProcessor);

        final HttpStatus status = output.getStatus();
        final Content content = output.getContent();
        ParseStatus pstatus = null;

        switch (status.getCode()) {

          case HttpStatus.SUCCESS: // got a page
            pstatus = output(url, content, HttpStatus.SUCCESS, sitemapLinks);
            updateStatus(content.getContent().length);
            if (pstatus != null && pstatus.isSuccess() && pstatus.getMinorCode() == ParseStatus.SUCCESS_REDIRECT) {
              link = getRedirectLink(link, pstatus.getMessage(), filterProcessor, linksDone);
              if (link != null) {
                redirecting = true;
                redirectCount++;
              }
            }
            break;

          case HttpStatus.MOVED: // redirect
          case HttpStatus.TEMP_MOVED:
            link = getRedirectLink(link, status.getMessage(), filterProcessor, linksDone);
            if (link != null) {
              redirecting = true;
              redirectCount++;
            }
            break;

          // failures - increase the retry counter
          case HttpStatus.EXCEPTION:
            logError(url, status.getMessage());
            // Fallthrough
          case HttpStatus.RETRY:
            Thread.sleep(_waitRetry);
            retriesCount++;
            retrying = true;
            break;
          // permanent failures
          case HttpStatus.GONE: // gone
          case HttpStatus.NOTFOUND:
          case HttpStatus.ACCESS_DENIED:
          case HttpStatus.ROBOTS_DENIED:
          case HttpStatus.NOTMODIFIED:
            output(url, null, HttpStatus.GONE, sitemapLinks);
            break;
          case HttpStatus.NOTFETCHING:
            if (_log.isDebugEnabled()) {
              _log.debug("Won't fetch url " + url);
            }
            output(url, null, HttpStatus.NOTFETCHING, sitemapLinks);
            break;
          default:
            if (_log.isWarnEnabled()) {
              _log.warn("Unknown HttpStatus: " + status.getCode());
            }
            output(url, null, HttpStatus.GONE, sitemapLinks);
        }

        if (redirecting && redirectCount >= _maxRedirect) {
          if (_log.isInfoEnabled()) {
            _log.info(" - redirect count exceeded " + url);
          }
          output(url, null, HttpStatus.GONE, sitemapLinks);
        }

        if (retrying && retriesCount >= _maxRetries) {
          if (_log.isInfoEnabled()) {
            _log.info(" - retries count exceeded " + url);
          }
          output(url, null, HttpStatus.GONE, sitemapLinks);
        }

        continueFetching =
          (redirecting && (redirectCount < _maxRedirect)) || (retrying && (retriesCount < _maxRetries));

      } while (continueFetching);
      _performanceCounters.incrementBy(WebCrawler.POC_AVEREGE_TIME_TO_FETCH, (System.currentTimeMillis() - start)
        / MILLISECS_IN_SEC);
    } catch (final InterruptedException exception) {
      logError(link.getUrlString(), exception.getMessage());
    }
    // FetcherOutput
    return _output;
  }

  /**
   * Log error.
   * 
   * @param url
   *          the url
   * @param message
   *          the message
   */
  private void logError(final String url, final String message) {
    _log.error("fetch of " + url + " failed with " + message);
    // record failure
    _errors++;
  }

  /**
   * Output.
   * 
   * @param url
   *          the URL
   * @param content
   *          the content
   * @param status
   *          the status
   * @param sitemapLinks
   *          the site map links
   * 
   * @return the parses the status
   */
  private ParseStatus output(final String url, Content content, final int status, final Outlink[] sitemapLinks) {

    if (content == null) {
      content = new Content(url, url, new byte[0], "", new Metadata());
    }

    Parse parse = null;
    if ((status == HttpStatus.SUCCESS) && (_parserManager != null)) {
      ParseStatus parseStatus = null;
      if (_parserManager != null) {
        final Parser parser = _parserManager.getParser(content.getContentType());
        if (parser != null) {
          if (_log.isDebugEnabled()) {
            _log.debug("Using webcrawler parser: " + parser.getClass().getName() + " for content-type "
              + content.getContentType());
          }
          parser.setConf(getConf());
          parse = parser.getParse(content);
          parseStatus = parse.getData().getStatus();
          if (!parseStatus.isSuccess()) {
            _log.error("Error parsing: " + url + ": " + parseStatus);
            parse = parseStatus.getEmptyParse(getConf());
          }
        } else {
          if (_log.isWarnEnabled()) {
            _log.warn("Parser for content-type: " + content.getContentType() + " not found");
          }
        }
      } else {
        if (_log.isErrorEnabled()) {
          _log.error("Parser manager is not set! Unable to get any parsers.");
        }
      }
    }
    if (parse == null) {
      _output = new FetcherOutput(content, null, sitemapLinks);
    } else {
      _output = new FetcherOutput(content, new ParseImpl(parse), sitemapLinks);
    }

    if (parse != null) {
      return parse.getData().getStatus();
    } else {
      return null;
    }
  }

  /**
   * Gets the redirect link.
   * 
   * @param fromLink
   *          the from link
   * @param toUrlString
   *          the to URL string
   * @param filterProcessor
   *          the filter processor
   * @param linksDone
   *          list of already crawled links
   * 
   * @return the redirect link
   */
  private Outlink getRedirectLink(final Outlink fromLink, final String toUrlString,
    final FilterProcessor filterProcessor, final Set<Outlink> linksDone) {
    Outlink newLink = null;
    try {
      newLink = new Outlink(toUrlString, fromLink.getAnchor(), getConf());
      final CrawlMode crawlMode = filterProcessor.evaluateUrlFilters(newLink);
      if ((!crawlMode.equals(CrawlMode.Skip)) && (!newLink.equals(fromLink)) && (!linksDone.contains(newLink))) {
        if (_log.isDebugEnabled()) {
          _log.debug("redirect to " + newLink.getUrlString());
        }
      } else {
        if (_log.isDebugEnabled()) {
          if (crawlMode.equals(CrawlMode.Skip)) {
            _log.debug("Won't redirect: CrawlMode=Skip, url = " + newLink.getUrlString());
          } else if (linksDone.contains(newLink)) {
            _log.debug("Won't redirect: url already crawled, url = " + newLink.getUrlString());
          } else if (newLink.equals(fromLink)) {
            _log.debug("Won't redirect: redirect to the same url, url = " + newLink.getUrlString());
          }
        }
        return null;
      }
    } catch (final MalformedURLException exception) {
      _log.error("Malformed redirect url: " + toUrlString);
    }
    return newLink;
  }

  /**
   * Returns amount of bytes fetched so far.
   * 
   * @return long
   */
  public long getBytes() {
    return _bytes;
  }

  /**
   * Assigns the amount of bytes fetched.
   * 
   * @param bytes
   *          long
   */
  public void setBytes(final long bytes) {
    this._bytes = bytes;
  }

  /**
   * Returns number of fetching errors happened so far.
   * 
   * @return errors
   */
  public int getErrors() {
    return _errors;
  }

  /**
   * Assigns number of fetching errors.
   * 
   * @param errors
   *          errors
   */
  public void setErrors(final int errors) {
    _errors = errors;
  }

  /**
   * Returns number of fetched pages so far.
   * 
   * @return pages
   */
  public int getPages() {
    return _pages;
  }

  /**
   * Assigns number of fetched pages.
   * 
   * @param pages
   *          pages
   */
  public void setPages(final int pages) {
    _pages = pages;
  }

}
