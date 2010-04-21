/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.Authentication;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.HttpProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.util.GZIPUtils;

/**
 * Common configurations and methods for HTTP protocol.
 */
public abstract class HttpBase {

  /** The Constant BUFFER_SIZE. */
  public static final int BUFFER_SIZE = 8 * 1024;

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(HttpBase.class);

  /** The Constant EMPTY_CONTENT. */
  private static final byte[] EMPTY_CONTENT = new byte[0];

  /** The 'User-Agent' request header. */
  protected String _userAgent;

  /** The referrer. */
  protected String _referrer;

  /** The headers. */
  protected List<Header> _headers = new ArrayList<Header>();

  /** Do we use HTTP/1.1? */
  protected boolean _useHttp11;

  /** The proxy host. */
  protected String _proxyHost;

  /** The proxy port. */
  protected int _proxyPort;

  /** The proxy login. */
  protected String _proxyLogin;

  /** The proxy password. */
  protected String _proxyPassword;

  /** The use proxy. */
  protected boolean _useProxy;

  /** The max length bytes. */
  protected int _maxLengthBytes;

  /** The timeout. */
  protected int _timeout;

  /** The connect timeout. */
  protected int _connectTimeout;

  /** The read timeout. */
  protected int _readTimeout;

  /** The authentication. */
  protected Authentication _authentication;

  /** The cookies enabled. */
  protected boolean _cookiesEnabled;

  /** The _robots. */
  private final RobotRulesParser _robots;

  /** The _conf. */
  private Configuration _conf;

  /**
   * Creates a new instance of HttpBase.
   */
  public HttpBase() {
    _robots = new RobotRulesParser();
  }

  /**
   * Loads configuration.
   * 
   * @param conf Configuration
   */
  public void setConf(Configuration conf) {
    this._conf = conf;

    this._proxyHost = conf.get(HttpProperties.PROXY_HOST);
    this._proxyPort = conf.getInt(HttpProperties.PROXY_PORT);
    this._proxyLogin = conf.get(HttpProperties.PROXY_LOGIN);
    this._proxyPassword = conf.get(HttpProperties.PROXY_PASSWORD);
    this._useProxy = (_proxyHost != null && _proxyHost.length() > 0);

    this._timeout = conf.getInt(HttpProperties.TIMEOUT);
    this._connectTimeout = conf.getInt(HttpProperties.CONNECT_TIMEOUT);
    this._readTimeout = conf.getInt(HttpProperties.READ_TIMEOUT);

    this._userAgent =
      getAgentString(conf.get(HttpProperties.AGENT_NAME), conf.get(HttpProperties.AGENT_VERSION), conf
        .get(HttpProperties.AGENT_DESCRIPTION), conf.get(HttpProperties.AGENT_URL), conf
        .get(HttpProperties.AGENT_EMAIL));
    this._maxLengthBytes = conf.getInt(HttpProperties.MAX_LENGTH_BYTES);
    this._useHttp11 = conf.getBoolean(HttpProperties.HTTP11);

    this._authentication = conf.getAuthentication();
    this._cookiesEnabled = conf.getBoolean(HttpProperties.ENABLE_COOKIES);
    this._referrer = conf.get(HttpProperties.REFERER);

    _robots.setConf(conf);
  }

  /**
   * Return the configuration used by this object.
   * 
   * @return Configuration
   */
  public Configuration getConf() {
    return _conf;
  }

  /**
   * Returns retrieved page information in the HttpOutput format.
   * 
   * @param link Out link to retrieve.
   * @param filterProcessor FilterProcessor implementation
   * 
   * @return HttpOutput
   */
  public HttpOutput getHttpOutput(Outlink link, FilterProcessor filterProcessor) {
    try {
      final String urlString = link.getUrlString();
      URL url = link.getUrl();
      try {
        if (!_robots.isAllowed(this, url)) {
          LOG.info("Url " + urlString + " is denied by robots.txt rules");
          return new HttpOutput(null, new HttpStatus(HttpStatus.ROBOTS_DENIED, urlString));
        }
      } catch (Exception exception) {
        LOG.info("Exception checking robot rules for " + urlString + ": " + exception);
      }
      // make a request
      final Response response = getResponse(link.getUrlString(), filterProcessor);
      final int code = response.getCode();
      final byte[] content = response.getContent();
      Content c;
      if (content == null) {
        c =
          new Content(urlString, urlString, EMPTY_CONTENT, response.getHeader("Content-Type"), response
            .getHeaders());
      } else {
        c = new Content(urlString, urlString, content, response.getHeader("Content-Type"), response.getHeaders());
      }
      if (code == HttpResponseCode.CODE_200) { // got a good response
        return new HttpOutput(c); // return it
      } else if (code == HttpResponseCode.CODE_410) { // page is gone
        return new HttpOutput(c, new HttpStatus(HttpStatus.GONE, "Http: " + code + " url=" + urlString));
      } else if (code >= HttpResponseCode.CODE_300 && code < HttpResponseCode.CODE_400) { // handle redirect
        String location = response.getHeader("Location");
        // some broken servers, such as MS IIS, use lower case header name...
        if (location == null) {
          location = response.getHeader("location");
        }
        if (location == null) {
          location = "";
        }
        url = new URL(url, location);
        int protocolStatusCode;
        switch (code) {
          case HttpResponseCode.CODE_300: // multiple choices, preferred value in Location
            protocolStatusCode = HttpStatus.MOVED;
            break;
          case HttpResponseCode.CODE_301: // moved permanently
          case HttpResponseCode.CODE_305: // use proxy (Location is URL of proxy)
            protocolStatusCode = HttpStatus.MOVED;
            break;
          case HttpResponseCode.CODE_302: // found (temporarily moved)
          case HttpResponseCode.CODE_303: // see other (redirect after POST)
          case HttpResponseCode.CODE_307: // temporary redirect
            protocolStatusCode = HttpStatus.TEMP_MOVED;
            break;
          case HttpResponseCode.CODE_304: // not modified
            protocolStatusCode = HttpStatus.NOTMODIFIED;
            break;
          default:
            protocolStatusCode = HttpStatus.MOVED;
        }
        // handle this in the higher layer.
        return new HttpOutput(c, new HttpStatus(protocolStatusCode, url));
      } else if (code == HttpResponseCode.CODE_400) { // bad request, mark as GONE
        LOG.debug("400 Bad request: " + url);
        return new HttpOutput(c, new HttpStatus(HttpStatus.GONE, url));
      } else if (code == HttpResponseCode.CODE_401) { // requires authorization, but no valid authentication provided.
        LOG.debug("401 Authentication Required");
        return new HttpOutput(c, new HttpStatus(HttpStatus.ACCESS_DENIED, "Authentication required: " + urlString));
      } else if (code == HttpResponseCode.CODE_404) {
        return new HttpOutput(c, new HttpStatus(HttpStatus.NOTFOUND, url));
      } else if (code == HttpResponseCode.CODE_410) { // permanently GONE
        return new HttpOutput(c, new HttpStatus(HttpStatus.GONE, url));
      } else if (code == -1) { // URL wasn't fetched
        return new HttpOutput(c, new HttpStatus(HttpStatus.NOTFETCHING, url));
      } else {
        return new HttpOutput(c, new HttpStatus(HttpStatus.EXCEPTION, "Http code=" + code + ", url=" + url));
      }
    } catch (IOException exception) {
      // error is logged in fetcher
      // LOG.error("Error downloading link " + exception.getMessage());
      return new HttpOutput(null, new HttpStatus(exception));
    }
  }

  /**
   * Gets the agent string.
   * 
   * @param agentName the agent name
   * @param agentVersion the agent version
   * @param agentDesc the agent desc
   * @param agentURL the agent URL
   * @param agentEmail the agent email
   * 
   * @return the agent string
   */
  private static String getAgentString(String agentName, String agentVersion, String agentDesc, String agentURL,
    String agentEmail) {

    if ((agentName == null) || (agentName.trim().length() == 0)) {
      if (LOG.isFatalEnabled()) {
        LOG.fatal("No User-Agent set!");
      }
    }

    final StringBuffer buf = new StringBuffer();

    buf.append(agentName);
    if (agentVersion != null) {
      buf.append("/");
      buf.append(agentVersion);
    }
    if (((agentDesc != null) && (agentDesc.length() != 0)) || ((agentEmail != null) && (agentEmail.length() != 0))
      || ((agentURL != null) && (agentURL.length() != 0))) {
      buf.append(" (");

      if ((agentDesc != null) && (agentDesc.length() != 0)) {
        buf.append(agentDesc);
        if ((agentURL != null) || (agentEmail != null)) {
          buf.append("; ");
        }
      }

      if ((agentURL != null) && (agentURL.length() != 0)) {
        buf.append(agentURL);
        if (agentEmail != null) {
          buf.append("; ");
        }
      }

      if ((agentEmail != null) && (agentEmail.length() != 0)) {
        buf.append(agentEmail);
      }

      buf.append(")");
    }
    return buf.toString();
  }

  /**
   * Holds uncompressing of GZIP content.
   * 
   * @param compressed GZIP byte array
   * @param url URL string
   * 
   * @return byte array
   * 
   * @throws IOException if uncompressing error occur
   */
  public byte[] processGzipEncoded(byte[] compressed, String url) throws IOException {

    if (LOG.isTraceEnabled()) {
      LOG.trace("uncompressing....");
    }

    final byte[] content = GZIPUtils.unzipBestEffort(compressed, getMaxLengthBytes());

    if (content == null) {
      throw new IOException("unzipBestEffort returned null");
    }
    if (LOG.isTraceEnabled()) {
      LOG.trace("fetched " + compressed.length + " bytes of compressed content (expanded to " + content.length
        + " bytes) from " + url);
    }

    return content;

  }

  /**
   * Returns User-Agent value.
   * 
   * @return String
   */
  public String getUserAgent() {
    return _userAgent;
  }

  /**
   * Returns whether HTTP version 1.1 will be used or not.
   * 
   * @return true or false
   */
  public boolean getUseHttp11() {
    return _useHttp11;
  }

  /**
   * Returns maximum length of document.
   * 
   * @return maximum length
   */
  public int getMaxLengthBytes() {
    return _maxLengthBytes;
  }

  /**
   * Returns list of request headers.
   * 
   * @return List of headers
   */
  public List<Header> getHeaders() {
    return _headers;
  }

  /**
   * Assigns list of request headers.
   * 
   * @param headersList List of headers
   */
  public void setHeaders(List<Header> headersList) {
    this._headers = headersList;
  }

  /**
   * Returns the Referrer header value.
   * 
   * @return String
   */
  public String getReferer() {
    return _referrer;
  }

  /**
   * Assigns the Referrer header value.
   * 
   * @param refererValue String
   */
  public void setReferer(String refererValue) {
    this._referrer = refererValue;
  }

  /**
   * Returns if cookies are enabled or not.
   * 
   * @return boolean
   */
  public boolean isCookiesEnabled() {
    return _cookiesEnabled;
  }

  /**
   * Assigns boolean value to enable or disable cookies.
   * 
   * @param enableCookies boolean
   */
  public void setCookiesEnabled(boolean enableCookies) {
    this._cookiesEnabled = enableCookies;
  }

  /**
   * Returns HttpResponse for the given URL.
   * 
   * @param urlString the url string
   * 
   * @return HttpResponse
   * 
   * @throws IOException if there was a error retrieving URL.
   */
  protected abstract Response getResponse(String urlString) throws IOException;

  /**
   * Returns HttpResponse for the given URL and filter processor.
   * 
   * @param filterProcessor filterProcessor implementation
   * @param urlString the url string
   * 
   * @return HttpResponse
   * 
   * @throws IOException if there was a error retrieving URL.
   */
  protected abstract Response getResponse(String urlString, FilterProcessor filterProcessor) throws IOException;

}
