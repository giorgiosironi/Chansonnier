/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the HttpResponse.java from Nutch 0.8.1 (see below the licene). 
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
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.HtmlFormAuthentication;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.HtmlFormAuthentication.HttpMethod;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.util.RequestUtil;

/**
 * Retrieves page checks content-type filters and performs HTML form authentication if needed.
 */
public class HttpResponse implements Response {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(HttpResponse.class);

  /** The HTTP. */
  private final HttpBase _http;

  /** The status code. */
  private int _statusCode;

  /** The URL string. */
  private String _urlString;

  /** The headers. */
  private final Metadata _headers = new Metadata();

  /** The content. */
  private byte[] _content;

  /**
   * Creates new object and fills it with retrieved URL information.
   * 
   * @param http HTTP protocol options
   * HTTP to retrieve
   * @param filterProcessor filters to perform
   * @param urlString the url string
   * 
   * @throws IOException if error while retrieving URL occur
   */
  public HttpResponse(HttpBase http, String urlString, FilterProcessor filterProcessor) throws IOException {
    _http = http;
    _urlString = urlString;
    HttpMethodBase httpMethod = null;
    try {
      httpMethod = getHttpMethod();
      setHttpParameters(http, httpMethod);
      _statusCode = Http.getClient().executeMethod(httpMethod);
      final Header[] headers = httpMethod.getResponseHeaders();
      for (int i = 0; i < headers.length; i++) {
        _headers.set(headers[i].getName(), headers[i].getValue());
      }
      // Content-Type filter should go here
      // TODO: Guess content type when Content-Type header is empty
      boolean contentTypeMatches = true;
      final String contentType = _headers.get(Response.CONTENT_TYPE);
      if ((contentType != null) && (filterProcessor != null)) {
        contentTypeMatches = filterProcessor.evaluateContentTypeFilters(contentType);
        LOG.debug("Content type header: " + contentType + ", passed filters: " + contentTypeMatches);
      }
      if (contentTypeMatches) {
        // always read content. Sometimes content is useful to find a cause for error.
        try {
          final InputStream in = httpMethod.getResponseBodyAsStream();
          final byte[] buffer = new byte[HttpBase.BUFFER_SIZE];
          int totalRead = 0;
          final ByteArrayOutputStream out = new ByteArrayOutputStream();
          int tryAndRead = calculateTryToRead(totalRead);
          int bufferFilled = in.read(buffer, 0, buffer.length);
          while (bufferFilled != -1 && tryAndRead > 0) {
            totalRead += bufferFilled;
            out.write(buffer, 0, bufferFilled);
            tryAndRead = calculateTryToRead(totalRead);
            bufferFilled = in.read(buffer, 0, buffer.length);
          }
          _content = out.toByteArray();
          in.close();
        } catch (HttpException exception) {
          LOG.error("Http error occured ", exception);
          throw new IOException(exception.toString());
        } catch (IOException exception) {
          if (_statusCode == HttpResponseCode.CODE_200) {
            throw new IOException(exception.toString());
          }
          // for codes other than 200 OK, we are fine with empty content
        }
        if (_content != null) {
          // check if we have to uncompress it
          final String contentEncoding = _headers.get(Response.CONTENT_ENCODING);
          if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
            _content = http.processGzipEncoded(_content, urlString);
          }
          if (("application/x-gzip".equals(contentType)) || ("application/gzip".equals(contentType))) {
            _content = http.processGzipEncoded(_content, urlString);
          }
        }
      } else {
        // URL wasn't fetched
        _statusCode = -1;
      }
    } catch (ProtocolException exception) {
      if (LOG.isErrorEnabled()) {
        LOG.error(exception);
      }
      throw new IOException(exception.toString());
    } catch (URISyntaxException exception) {
      if (LOG.isErrorEnabled()) {
        LOG.error(exception);
      }
      throw new IOException(exception.toString());
    } finally {
      if (httpMethod != null) {
        httpMethod.releaseConnection();
      }
    }
  }

  /**
   * Calculate try to read.
   * 
   * @param totalRead the total read
   * 
   * @return the count byte to read
   */
  private int calculateTryToRead(int totalRead) {
    int tryToRead = Http.BUFFER_SIZE;
    if (_http.getMaxLengthBytes() <= 0) {
      return HttpBase.BUFFER_SIZE;
    } else if (_http.getMaxLengthBytes() - totalRead < HttpBase.BUFFER_SIZE) {
      tryToRead = _http.getMaxLengthBytes() - totalRead;
    }
    return tryToRead;
  }

  /**
   * Gets the HTTP method.
   * 
   * @return the HTTP method
   * 
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws URISyntaxException URISyntaxException
   */
  private HttpMethodBase getHttpMethod() throws IOException, URISyntaxException {
    URI uri;
    try {
      uri = new URI(_urlString);
    } catch (URISyntaxException exception) {
      uri = new URI(URIUtil.encodePathQuery(_urlString));
    }
    final List<HtmlFormAuthentication> authentications = _http._authentication.getHtmlFormAuthentications();
    for (HtmlFormAuthentication auth : authentications) {
      if (auth.getCredentialDomain().equals(uri.toString())) {
        // get login page in order to set required cookies
        final GetMethod getMethod = new GetMethod(uri.toString());
        setHttpParameters(_http, getMethod);
        try {
          final int result = Http.getClient().executeMethod(getMethod);
          LOG.debug("Response status code from login page: " + result);
        } catch (ProtocolException exception) {
          if (LOG.isErrorEnabled()) {
            LOG.error(exception);
          }
          throw new IOException(exception.toString());
        } finally {
          getMethod.releaseConnection();
        }
        // remove used authentication from the list
        authentications.remove(auth);
        // prepare authentication method
        return getAuthenticationMethod(auth);
      }
    }
    try {
      return new GetMethod(uri.toString());
    } catch (IllegalArgumentException exception) {
      throw new IOException("Can't get method. " + exception.getMessage());
    }
  }

  /**
   * Gets the authentication method.
   * 
   * @param auth the HTML form authentication
   * 
   * @return the authentication method
   */
  private HttpMethodBase getAuthenticationMethod(HtmlFormAuthentication auth) {
    HttpMethodBase authenticationMethod = null;
    if (auth.getHttpMethod().equals(HttpMethod.POST)) {
      final PostMethod postMethod = new PostMethod((auth.getLoginUri()));

      final NameValuePair[] postData = new NameValuePair[auth.getFormItems().size()];
      int i = 0;
      for (String key : auth.getFormItems().keySet()) {
        postData[i] = new NameValuePair(key, auth.getFormItems().get(key));
        i++;
      }
      postMethod.addParameters(postData);
      authenticationMethod = postMethod;
    } else if (auth.getHttpMethod().equals(HttpMethod.GET)) {
      final String requestString = RequestUtil.appendParams(auth.getLoginUri(), auth.getFormItems());
      LOG.debug("GET request string for authentication: " + requestString);
      final GetMethod getMethod = new GetMethod(requestString);
      authenticationMethod = getMethod;
    }

    return authenticationMethod;
  }

  /**
   * Sets the http parameters.
   * 
   * @param http the http
   * @param httpMethod the http method
   */
  private void setHttpParameters(HttpBase http, HttpMethodBase httpMethod) {
    httpMethod.setFollowRedirects(false);
    httpMethod.setRequestHeader("User-Agent", http.getUserAgent());
    httpMethod.setRequestHeader("Referer", http.getReferer());

    httpMethod.setDoAuthentication(true);

    for (Header header : http.getHeaders()) {
      httpMethod.addRequestHeader(header);
    }

    final HttpMethodParams params = httpMethod.getParams();
    if (http.getUseHttp11()) {
      params.setVersion(HttpVersion.HTTP_1_1);
    } else {
      params.setVersion(HttpVersion.HTTP_1_0);
    }
    params.makeLenient();
    params.setContentCharset("UTF-8");

    if (http.isCookiesEnabled()) {
      params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    } else {
      params.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
    }
    params.setBooleanParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
    // the default is to retry 3 times; if
    // the request body was sent the method is not retried, so there is
    // little danger in retrying
    // retries are handled on the higher level
    params.setParameter(HttpMethodParams.RETRY_HANDLER, null);
  }

  /**
   * {@inheritDoc}
   */
  public String getUrl() {
    return _urlString;
  }

  /**
   * {@inheritDoc}
   */
  public int getCode() {
    return _statusCode;
  }

  /**
   * {@inheritDoc}
   */
  public String getHeader(String name) {
    return _headers.get(name);
  }

  /**
   * {@inheritDoc}
   */
  public Metadata getHeaders() {
    return _headers;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getContent() {
    return _content;
  }

}
