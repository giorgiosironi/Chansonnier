/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.CrawlProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.FetcherProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.crawl.CrawlMode;
import org.eclipse.smila.connectivity.framework.crawler.web.fetcher.Fetcher;
import org.eclipse.smila.connectivity.framework.crawler.web.fetcher.FetcherOutput;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.impl.FilterProcessorImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.ModelType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParserManager;
import org.eclipse.smila.connectivity.framework.performancecounters.CrawlerPerformanceCounterHelper;

/**
 * The Class WebSiteIterator.
 * 
 */
public class WebSiteIterator implements Iterator<IndexDocument> {

  /**
   * The Log.
   */
  private final Log _log = LogFactory.getLog(WebSiteIterator.class);

  /**
   * Set of links which are already "crawled". A set is used to avoid double entries.
   */
  private final Set<Outlink> _linksDone = new HashSet<Outlink>();

  /**
   * Set of links which are queued for "crawling". A set is used to avoid double entries.
   */
  private Set<Outlink> _linksToDo = new HashSet<Outlink>();

  /**
   * The links to do next level.
   */
  private Set<Outlink> _linksToDoNextLevel = new HashSet<Outlink>();

  /**
   * The iterations done.
   */
  private int _iterationsDone;

  /**
   * The current depth.
   */
  private int _currentDepth;

  /**
   * The configuration.
   */
  private Configuration _configuration;

  /**
   * The fetcher.
   */
  private Fetcher _fetcher;

  /**
   * The wait.
   */
  private int _wait;

  /**
   * The random wait.
   */
  private boolean _randomWait;

  /**
   * The filter processor.
   */
  private FilterProcessor _filterProcessor;

  /**
   * The start time.
   */
  private long _startTime;

  /**
   * Currently selected document in this iterator.
   */
  private IndexDocument _currentIndexDocument;

  /**
   * The _performance counters.
   */
  @SuppressWarnings("unused")
  private final CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent> _performanceCounters;

  /**
   * Initialize crawling.
   * 
   * @param webSite
   *          web site crawling configuration
   * @param performanceCounters
   *          the performance counters
   * @param parserManager
   *          webcrawler parsers manager
   * @throws CrawlerCriticalException
   *           the crawler critical exception
   */
  public WebSiteIterator(final WebSite webSite, final ParserManager parserManager,
    final CrawlerPerformanceCounterHelper<WebCrawlerPerformanceAgent> performanceCounters)
    throws CrawlerCriticalException {
    try {
      _performanceCounters = performanceCounters;
      _configuration = new Configuration();
      _configuration.loadConfiguration(webSite);

      _fetcher = new Fetcher(_configuration, parserManager, performanceCounters);
      _wait = _configuration.getInt(CrawlProperties.WAIT);
      _randomWait = _configuration.getBoolean(CrawlProperties.RANDOM_WAIT);

      if (_log.isDebugEnabled()) {
        _log.debug("Starting new project: " + _configuration.get(CrawlProperties.PROJECT_NAME));
      }

      _linksToDo = _configuration.getSeeds();
      _filterProcessor = new FilterProcessorImpl(_configuration);
      _startTime = System.currentTimeMillis();
    } catch (final IllegalAccessException exception) {
      throw new CrawlerCriticalException("Error loading configuration", exception);
    } catch (final InvocationTargetException exception) {
      throw new CrawlerCriticalException("Error loading configuration", exception);
    } catch (final IOException exception) {
      throw new CrawlerCriticalException("Error loading configuration", exception);
    }

  }

  /**
   * Checks if this iterator has a next document for indexing.
   * 
   * @return boolean
   */
  public boolean hasNext() {
    while (_linksToDo.size() > 0 && _currentIndexDocument == null) {
      _iterationsDone++;
      // check size limits
      if (limitExceeded(_fetcher.getBytes(), FetcherProperties.MAX_BYTES_DOWNLOAD)) {
        _log.info("Max bytes limit exceeded");
        return false;
      }
      if (limitExceeded(_fetcher.getPages(), FetcherProperties.MAX_DOCUMENT_DOWNLOAD)) {
        _log.info("Max pages limit exceeded");
        return false;
      }
      final float elapsedTime =
        (System.currentTimeMillis() - _startTime) / ((float) Configuration.MILLIS_PER_SECOND);
      if (limitExceeded((long) elapsedTime, CrawlProperties.MAX_TIME_SEC)) {
        _log.info("Max time exceeded");
        return false;
      }
      if (ModelType.MAX_ITERATIONS.value().equals(_configuration.get(CrawlProperties.CRAWLING_MODEL_TYPE))
        && (limitExceeded(_iterationsDone, CrawlProperties.CRAWLING_MODEL_VALUE))) {
        _log.info("Maximum number of iterations exceeded");
        return false;
      }
      final Outlink link = _linksToDo.iterator().next();
      _linksToDo.remove(link);
      if (!_linksDone.contains(link)) {
        _linksDone.add(link);
        // prove if the url matches crawl scope and all filters
        final CrawlMode crawlMode = _filterProcessor.evaluateUrlFilters(link);
        if (!crawlMode.equals(CrawlMode.Skip)) {
          try {
            _currentIndexDocument = indexDocs(link, _configuration, crawlMode);
          } catch (final InterruptedException exception) {
            _log.error("Error fetching link " + link.getUrlString());
          }
        } else {
          _log.debug("Link = " + link.getUrlString() + " not included");
        }
      }

      if ((_linksToDo.size() == 0) && (_linksToDoNextLevel.size() > 0)) {
        _log.debug("Number of next level links: " + _linksToDoNextLevel.size());
        _linksToDo = _linksToDoNextLevel;
        _linksToDoNextLevel = new HashSet<Outlink>();
        _currentDepth++;
        _log.debug("Current depth is: " + _currentDepth);
        if (ModelType.MAX_DEPTH.value().equals(_configuration.get(CrawlProperties.CRAWLING_MODEL_TYPE))
          && limitExceeded(_currentDepth, CrawlProperties.CRAWLING_MODEL_VALUE)) {
          _log.info("Maximum depth exceeded!");
          return false;
        }
      }

    }

    return _currentIndexDocument != null;
  }

  /**
   * Gets the next index document.
   * 
   * @return IndexDocument
   */
  public IndexDocument next() {
    if (_currentIndexDocument == null) {
      hasNext();
    }
    final IndexDocument result = _currentIndexDocument;
    _currentIndexDocument = null;
    return result;
  }

  /**
   * Downloads the page and creates index document.
   * 
   * @param outlink
   *          Link to be fetched.
   * @param conf
   *          Crawler configuration
   * @param crawlMode
   *          One of Skip, Index or AnalyzeOnly
   * 
   * @return IndexDocument
   * 
   * @throws InterruptedException
   *           if error occured
   */
  private IndexDocument indexDocs(final Outlink outlink, final Configuration conf, CrawlMode crawlMode)
    throws InterruptedException {
    IndexDocument document = null;
    int delay = 0;
    if (_randomWait) {
      delay = (int) (Math.random() * _wait * 2);
    } else if (_wait > 0) {
      delay = _wait;
    }
    _log.debug("Wait before next retrieval, seconds: " + delay);
    Thread.sleep(delay * Configuration.MILLIS_PER_SECOND);
    final FetcherOutput fetcherOutput = _fetcher.fetch(outlink, _filterProcessor, _linksDone);
    // Check if fetching and parsing successfully finished
    if (fetcherOutput.getParse() != null) {
      if (crawlMode.equals(CrawlMode.Index)) {
        // XXX: Temporary workaround that is needed to avoid indexing of non-text content.
        if (fetcherOutput.getContent().getContentType().toLowerCase().contains("text")) {
          // run html metatags filters
          crawlMode =
            _filterProcessor.evaluateHtmlMetaTagFilters(fetcherOutput.getParse().getData().getHtmlMetaTags());
          // if we still want to index let's do it now
          if (crawlMode.equals(CrawlMode.Index)) {
            final String url = fetcherOutput.getContent().getUrl();
            final String title = fetcherOutput.getParse().getData().getTitle();
            // String content = fetcherOutput.getParse().getText();
            final byte[] content = fetcherOutput.getContent().getContent();

            final List<String> responseHeaders = fetcherOutput.getParse().getData().getContentMeta().toArrayList();
            final List<String> htmlMetaData = fetcherOutput.getParse().getData().getHtmlMetaTags().toArrayList();

            final List<String> metaDataWithResponseHeaderFallBack = new ArrayList<String>();
            metaDataWithResponseHeaderFallBack.addAll(responseHeaders);
            metaDataWithResponseHeaderFallBack.addAll(htmlMetaData);

            document =
              new IndexDocument(url, title, content, responseHeaders, htmlMetaData,
                metaDataWithResponseHeaderFallBack);
          }
        }
      }
      if (!crawlMode.equals(CrawlMode.Skip)) {
        // update links to do (for further indexing)
        final Outlink[] outlinks = fetcherOutput.getParse().getData().getOutlinks();
        if ((outlinks != null) && (outlinks.length > 0)) {
          for (final Outlink link : outlinks) {
            // links from the page are added to the next level
            _linksToDoNextLevel.add(link);
            _log.debug("added new link to do:" + link.toString());
          }
        }
        final Outlink[] sitemapOutlinks = fetcherOutput.getSitemapLinks();
        if ((sitemapOutlinks != null) && (sitemapOutlinks.length > 0)) {
          for (final Outlink link : sitemapOutlinks) {
            // links from sitemap file are added to the same level
            _linksToDo.add(link);
            _log.debug("added new link from sitemap file:" + link.toString());
          }
        }

      }
    }
    return document;
  }

  /**
   * Limit exceeded.
   * 
   * @param test
   *          the test
   * @param propertyName
   *          the property name
   * 
   * @return true, if successful
   */
  private boolean limitExceeded(final long test, final String propertyName) {
    if ((_configuration.getInt(propertyName) > 0) && (test >= _configuration.getInt(propertyName))) {
      return true;
    }
    return false;
  }

  /**
   * Empty implementation of the Iterator method.
   */
  public void remove() {
    ;
  }

}
