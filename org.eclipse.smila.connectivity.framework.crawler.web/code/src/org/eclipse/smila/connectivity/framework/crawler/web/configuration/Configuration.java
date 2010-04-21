/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.Authentication;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.IFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.WorkTypeFiltersCollection;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.impl.CrawlScopeFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.CrawlScope;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.Filter;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FilterType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.FollowLinksType;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlLimits;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlingModel;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Proxy;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.Robotstxt;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.UserAgent;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlLimits.SizeLimits;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlLimits.TimeoutLimits;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.CrawlLimits.WaitLimits;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.WebSite.MetaTagFilters.MetaTagFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Class that handles all crawler configuration.
 * 
 * 
 */
public class Configuration {

  /** The Constant MILLIS_PER_SECOND. */
  public static final int MILLIS_PER_SECOND = 1000;

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(Configuration.class);

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant DEFAULT_PROPERTIES_FILE. */
  private static final String DEFAULT_PROPERTIES_FILE = "webcrawler.properties";

  /** The default properties. */
  private Properties _defaultProperties = new Properties();

  /** The web site properties. */
  private Properties _webSiteProperties;

  /** The follow links. */
  private FollowLinksType _followLinks;

  /** The crawl scope filter. */
  private CrawlScopeFilter _crawlScopeFilter;

  /** The url filters. */
  private WorkTypeFiltersCollection _urlFilters = new WorkTypeFiltersCollection();

  /** The content type filters. */
  private WorkTypeFiltersCollection _contentTypeFilters = new WorkTypeFiltersCollection();

  /** The meta tag filters. */
  private WorkTypeFiltersCollection _metaTagFilters = new WorkTypeFiltersCollection();

  /** The authentication. */
  private final Authentication _authentication = new Authentication();

  /** The seeds. */
  private final Set<Outlink> _seeds = new HashSet<Outlink>();

  /**
   * Creates a new Configuration with the default properties loaded from the properties file.
   * 
   * @throws IOException
   *           if there was a problem while loading default configuration file.
   */
  public Configuration() throws IOException {

    InputStream defaultPropertiesInputStream = null;

    try {
      // create and load default properties
      defaultPropertiesInputStream = getClass().getResourceAsStream(DEFAULT_PROPERTIES_FILE);
      if (defaultPropertiesInputStream == null) {
        throw new FileNotFoundException(DEFAULT_PROPERTIES_FILE);
      }
      _defaultProperties.load(defaultPropertiesInputStream);
      _webSiteProperties = new Properties(_defaultProperties);
    } finally {
      IOUtils.closeQuietly(defaultPropertiesInputStream);
    }
  }

  /**
   * Instantiates a new configuration.
   * 
   * @param defaultProperties
   *          the default properties
   */
  public Configuration(final Properties defaultProperties) {
    _defaultProperties = defaultProperties;
    _webSiteProperties = new Properties(_defaultProperties);
  }

  /**
   * Create a crawler configuration.
   * 
   * @param webSite
   *          is the DWebSite whose parameters must override default configuration parameters.
   * 
   * @throws IllegalAccessException .
   * @throws InvocationTargetException .
   */
  public void loadConfiguration(final WebSite webSite) throws IllegalAccessException, InvocationTargetException {
    if (webSite != null) {
      // set crawl properties

      // Attributes
      setNotEmptyProperty(HttpProperties.REFERER, webSite.getReferer());
      setNotEmptyProperty(HttpProperties.HEADERS, webSite.getHeader());
      setNotEmptyProperty(HttpProperties.ENABLE_COOKIES, String.valueOf(webSite.isEnableCookies()));
      setNotEmptyProperty(CrawlProperties.PROJECT_NAME, webSite.getProjectName());
      setNotEmptyProperty(FetcherProperties.USE_SITEMAPS, String.valueOf(webSite.isSitemaps()));

      // Elements
      final CrawlLimits crawlLimits = webSite.getCrawlLimits();
      if (crawlLimits != null) {
        final SizeLimits sizeLimits = crawlLimits.getSizeLimits();
        if (sizeLimits != null) {
          setNotEmptyProperty(CrawlProperties.MAX_TIME_SEC, sizeLimits.getMaxTimeSec().toString());
          setNotEmptyProperty(HttpProperties.MAX_LENGTH_BYTES, sizeLimits.getMaxLengthBytes().toString());
          setNotEmptyProperty(FetcherProperties.MAX_BYTES_DOWNLOAD, sizeLimits.getMaxBytesDownload().toString());
          setNotEmptyProperty(FetcherProperties.MAX_DOCUMENT_DOWNLOAD, sizeLimits.getMaxDocumentDownload()
            .toString());
        }

        final WaitLimits waitLimits = crawlLimits.getWaitLimits();
        if (waitLimits != null) {
          setNotEmptyProperty(CrawlProperties.WAIT, waitLimits.getWait().toString());
          setNotEmptyProperty(CrawlProperties.RANDOM_WAIT, String.valueOf(waitLimits.isRandomWait()));
          setNotEmptyProperty(FetcherProperties.MAX_RETRIES, waitLimits.getMaxRetries().toString());
          setNotEmptyProperty(FetcherProperties.WAIT_RETRY, waitLimits.getWaitRetry().toString());
        }

        final TimeoutLimits timeoutLimits = crawlLimits.getTimeoutLimits();
        if (timeoutLimits != null) {
          setNotEmptyProperty(HttpProperties.TIMEOUT, timeoutLimits.getTimeout().toString());
          setNotEmptyProperty(HttpProperties.CONNECT_TIMEOUT, timeoutLimits.getConnectTimeout().toString());
          setNotEmptyProperty(HttpProperties.READ_TIMEOUT, timeoutLimits.getReadTimeout().toString());
        }
      }

      final CrawlingModel crawlingModel = webSite.getCrawlingModel();
      if (crawlingModel != null) {
        setNotEmptyProperty(CrawlProperties.CRAWLING_MODEL_TYPE, crawlingModel.getType().value());
        setNotEmptyProperty(CrawlProperties.CRAWLING_MODEL_VALUE, crawlingModel.getValue().toString());
      }

      final UserAgent userAgent = webSite.getUserAgent();
      if (userAgent != null) {
        setNotEmptyProperty(HttpProperties.AGENT_NAME, userAgent.getName());
        setNotEmptyProperty(HttpProperties.AGENT_VERSION, userAgent.getVersion());
        setNotEmptyProperty(HttpProperties.AGENT_DESCRIPTION, userAgent.getDescription());
        setNotEmptyProperty(HttpProperties.AGENT_URL, userAgent.getUrl());
        setNotEmptyProperty(HttpProperties.AGENT_EMAIL, userAgent.getEmail());
      }

      final Robotstxt robotstxt = webSite.getRobotstxt();
      if (robotstxt != null) {
        if (webSite.getRobotstxt().getPolicy() != null) {
          setNotEmptyProperty(HttpProperties.ROBOTSTXT_POLICY, webSite.getRobotstxt().getPolicy().name());
        }
        setNotEmptyProperty(HttpProperties.ROBOTSTXT_VALUE, webSite.getRobotstxt().getValue());
        setNotEmptyProperty(HttpProperties.ROBOTSTXT_AGENT_NAMES, webSite.getRobotstxt().getAgentNames());
      }

      final Proxy proxy = webSite.getProxy();
      if (proxy != null) {
        setNotEmptyProperty(HttpProperties.PROXY_HOST, proxy.getProxyServer().getHost());
        setNotEmptyProperty(HttpProperties.PROXY_PORT, proxy.getProxyServer().getPort());
        setNotEmptyProperty(HttpProperties.PROXY_LOGIN, proxy.getProxyServer().getLogin());
        setNotEmptyProperty(HttpProperties.PROXY_PASSWORD, proxy.getProxyServer().getPassword());
      }

      if (webSite.getAuthentication() != null) {
        BeanUtils.copyProperties(_authentication, webSite.getAuthentication());
      }

      _followLinks = webSite.getSeeds().getFollowLinks();
      setSeeds(webSite.getSeeds().getSeed());
      setFilters(webSite);
    } else {
      throw new IllegalArgumentException("WebSite can't be null");
    }
  }

  /**
   * Sets the not empty property.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  private void setNotEmptyProperty(final String key, final String value) {
    if (StringUtils.isNotEmpty(value)) {
      _webSiteProperties.setProperty(key, value);
    }
  }

  /**
   * Returns a String property with the given key.
   * 
   * @param key
   *          a property key
   * @return property value
   */
  public String get(final String key) {
    return _webSiteProperties.getProperty(key);
  }

  /**
   * Returns a String property with the given key or given defaultValue if property with such key wasn't found.
   * 
   * @param key
   *          a property key
   * @param defaultValue
   *          default value to return if property wasn't set
   * @return property value
   */
  public String get(final String key, final String defaultValue) {
    return _webSiteProperties.getProperty(key, defaultValue);
  }

  /**
   * Returns a integer property with the given key.
   * 
   * @param key
   *          a property key
   * @return property value
   */
  public int getInt(final String key) {
    final String value = _webSiteProperties.getProperty(key);
    if (StringUtils.isNotEmpty(value)) {
      return Integer.valueOf(value);
    } else {
      return 0;
    }
  }

  /**
   * Returns a boolean property with the given key.
   * 
   * @param key
   *          a property key
   * @return property value
   */
  public boolean getBoolean(final String key) {
    return Boolean.valueOf(_webSiteProperties.getProperty(key));
  }

  /**
   * Returns a boolean property with the given key or given defaultValue if property with such key wasn't found.
   * 
   * @param key
   *          a property key
   * @param defaultValue
   *          default value to return if property wasn't set
   * @return property value
   */
  public boolean getBoolean(final String key, final boolean defaultValue) {
    return Boolean.valueOf(_webSiteProperties.getProperty(key, String.valueOf(defaultValue)));
  }

  /**
   * Returns the value of the <code>name</code> property, or null if no such property exists.
   * 
   * @param name
   *          object name
   * @return object
   */
  public Object getObject(final String name) {
    return _webSiteProperties.get(name);
  }

  /**
   * Sets the value of the <code>name</code> property.
   * 
   * @param name
   *          object name
   * @param value
   *          given object
   */
  public void setObject(final String name, final Object value) {
    _webSiteProperties.put(name, value);
  }

  /**
   * Returns a set of URL filters for this crawl job.
   * 
   * @return {@link WorkTypeFiltersCollection}
   */
  public WorkTypeFiltersCollection getUrlFilters() {
    return _urlFilters;
  }

  /**
   * Assigns URL filters for this crawl job.
   * 
   * @param filters
   *          {@link WorkTypeFiltersCollection}
   */
  public void setUrlFilters(final WorkTypeFiltersCollection filters) {
    _urlFilters = filters;
  }

  /**
   * Returns a value of follow links policy.
   * 
   * @return {@link FollowLinksType}
   */
  public FollowLinksType getFollowLinks() {
    return _followLinks;
  }

  /**
   * Assigns a value for follow links policy.
   * 
   * @param links
   *          {@link FollowLinksType}
   */
  public void setFollowLinks(final FollowLinksType links) {
    _followLinks = links;
  }

  /**
   * Sets the filters.
   * 
   * @param webSite
   *          the new filters
   * 
   * @throws IllegalAccessException
   *           the illegal access exception
   * @throws InvocationTargetException
   *           the invocation target exception
   */
  private void setFilters(final WebSite webSite) throws IllegalAccessException, InvocationTargetException {

    final FilterFactory filterFactory = new FilterFactory(this);

    if (webSite.getFilters() != null) {
      final List<WebSite.Filters.Filter> dFilters = webSite.getFilters().getFilter();
      for (final Filter dFilter : dFilters) {
        final IFilter<?> filter = filterFactory.getFilter(dFilter.getType());
        BeanUtils.copyProperties(filter, dFilter);
        if (dFilter.getType().equals(FilterType.CONTENT_TYPE)) {
          _contentTypeFilters.add(filter);
        } else {
          _urlFilters.add(filter);
        }
      }
    }

    if (webSite.getMetaTagFilters() != null) {
      final List<MetaTagFilter> dMetaTagFilters = webSite.getMetaTagFilters().getMetaTagFilter();
      for (final MetaTagFilter dMetaTagFilter : dMetaTagFilters) {
        final IFilter<?> filter = filterFactory.getFilter(FilterType.HTML_META_TAG);
        BeanUtils.copyProperties(filter, dMetaTagFilter);
        _metaTagFilters.add(filter);
      }
    }

    _crawlScopeFilter = (CrawlScopeFilter) filterFactory.getFilter(FilterType.CRAWL_SCOPE);
    _crawlScopeFilter.setSeeds(_seeds);

    final WebSite.CrawlScope crawlScope = webSite.getCrawlScope();
    if (crawlScope != null) {
      _crawlScopeFilter.setCrawlScope(webSite.getCrawlScope().getType());

      final WebSite.CrawlScope.Filters filtersElement = webSite.getCrawlScope().getFilters();
      if (filtersElement != null) {
        final List<WebSite.CrawlScope.Filters.Filter> dFilters = filtersElement.getFilter();
        for (final Filter dFilter : dFilters) {
          final IFilter<?> filter = filterFactory.getFilter(dFilter.getType());
          BeanUtils.copyProperties(filter, dFilter);
          _crawlScopeFilter.getUrlFilters().add(filter);
        }
      }
    } else {
      // default value by schema
      _crawlScopeFilter.setCrawlScope(CrawlScope.HOST);
    }

  }

  /**
   * Sets the seeds.
   * 
   * @param seeds
   *          the new seeds
   */
  private void setSeeds(final List<String> seeds) {
    for (final String seed : seeds) {
      try {
        _seeds.add(new Outlink(seed, seed, this));
      } catch (final MalformedURLException e) {
        LOG.error("bad seed url:" + seed);
      }
    }
  }

  /**
   * Returns the set of seeds for this crawl job.
   * 
   * @return a set of {@link Outlink}s
   */
  public Set<Outlink> getSeeds() {
    return _seeds;
  }

  /**
   * Returns {@link CrawlScopeFilter} for this crawl job.
   * 
   * @return {@link CrawlScopeFilter}
   */
  public CrawlScopeFilter getCrawlScopeFilter() {
    return _crawlScopeFilter;
  }

  /**
   * Assigns {@link CrawlScopeFilter} for this crawl job.
   * 
   * @param scopeFilter
   *          {@link CrawlScopeFilter}
   */
  public void setCrawlScopeFilter(final CrawlScopeFilter scopeFilter) {
    _crawlScopeFilter = scopeFilter;
  }

  /**
   * Returns metatag filters for this crawl job.
   * 
   * @return {@link WorkTypeFiltersCollection} meta tag filters
   */
  public WorkTypeFiltersCollection getMetaTagFilters() {
    return _metaTagFilters;
  }

  /**
   * Assigns meta tag filters for this crawl job.
   * 
   * @param tagFilters
   *          {@link WorkTypeFiltersCollection} meta tag filters
   */
  public void setMetaTagFilters(final WorkTypeFiltersCollection tagFilters) {
    _metaTagFilters = tagFilters;
  }

  /**
   * Returns content-type filters for this crawl job.
   * 
   * @return {@link WorkTypeFiltersCollection} meta tag filters
   */
  public WorkTypeFiltersCollection getContentTypeFilters() {
    return _contentTypeFilters;
  }

  /**
   * Assigns content-type filters for this crawl job.
   * 
   * @param filters
   *          {@link WorkTypeFiltersCollection} meta tag filters
   */
  public void setContentTypeFilters(final WorkTypeFiltersCollection filters) {
    _contentTypeFilters = filters;
  }

  /**
   * Returns {@link Authentication} options for this crawl job.
   * 
   * @return authentication value.
   */
  public Authentication getAuthentication() {
    return _authentication;
  }

}
