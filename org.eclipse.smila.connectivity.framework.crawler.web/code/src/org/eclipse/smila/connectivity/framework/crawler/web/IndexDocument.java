/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)s
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class for indexing contains all relevant data rom the web page.
 */
public class IndexDocument implements Serializable {

  /** The serial version UID. */
  private static final long serialVersionUID = 1L;

  /** The URL. */
  private String _url;

  /** The title. */
  private String _title;

  /** The content. */
  private byte[] _content;

  /** The response headers. */
  private List<String> _responseHeaders;

  /** The HTML meta data. */
  private List<String> _htmlMetaData;

  /** The meta data with response header fall back. */
  private List<String> _metaDataWithResponseHeaderFallBack;

  /**
   * Constructor.
   *
   * @param url
   *          URL of the web page
   * @param title
   *          title of the web page
   * @param content
   *          extracted content
   * @param responseHeaders
   *          list of response headers
   * @param htmlMetaData
   *          list of extracted HTML meta data
   * @param metaDataWithResponseHeaderFallBack
   *          responseHeaders and htmlMetaData merged together
   */
  public IndexDocument(final String url, final String title, final byte[] content,
    final List<String> responseHeaders, final List<String> htmlMetaData,
    final List<String> metaDataWithResponseHeaderFallBack) {
    _url = url;
    _title = title;
    _content = content;
    _responseHeaders = responseHeaders;
    _htmlMetaData = htmlMetaData;
    _metaDataWithResponseHeaderFallBack = metaDataWithResponseHeaderFallBack;
  }

  /**
   * Returns content of the downloaded document.
   *
   * @return byte[]
   */
  public byte[] getContent() {
    return _content;
  }

  /**
   * Assigns text content of the web page to the index document.
   *
   * @param content
   *          String
   */
  public void setContent(final byte[] content) {
    _content = content;
  }

  /**
   * Returns title of the web page.
   *
   * @return String
   */
  public String getTitle() {
    return _title;
  }

  /**
   * Assigns title of the page.
   *
   * @param title
   *          String
   */
  public void setTitle(final String title) {
    _title = title;
  }

  /**
   * Returns url of the page.
   *
   * @return String
   */
  public String getUrl() {
    return _url;
  }

  /**
   * Assigns URL of the page.
   *
   * @param url
   *          String
   */
  public void setUrl(final String url) {
    _url = url;
  }

  /**
   * Returns the list of HTML meta data extracted from HTML meta tags.
   *
   * @return List of strings
   */
  public List<String> getHtmlMetaData() {
    return _htmlMetaData;
  }

  /**
   * Assigns HTML meta data to the index document.
   *
   * @param metaData
   *          List
   */
  public void setHtmlMetaData(final List<String> metaData) {
    _htmlMetaData = metaData;
  }

  /**
   * Returns response headers.
   *
   * @return List
   */
  public List<String> getResponseHeaders() {
    return _responseHeaders;
  }

  /**
   * Assigns response headers to the index document.
   *
   * @param headers
   *          List
   */
  public void setResponseHeaders(final List<String> headers) {
    _responseHeaders = headers;
  }

  /**
   * Returns combination of response headers and HTML meta data.
   *
   * @return List
   */
  public List<String> getMetaDataWithResponseHeaderFallBack() {
    return _metaDataWithResponseHeaderFallBack;
  }

  /**
   * Assigns combination of response headers and HTML meta data to the index document.
   *
   * @param metaDataWithResponseHeaderFallBack
   *          List
   */
  public void setMetaDataWithResponseHeaderFallBack(final List<String> metaDataWithResponseHeaderFallBack) {
    _metaDataWithResponseHeaderFallBack = metaDataWithResponseHeaderFallBack;
  }

  /**
   * extract something from response headers. The pattern is tested on all response headers until one matches, then the
   * value of requested group is returned.
   *
   * @param pattern
   *          a regular expression
   * @param group
   *          index of group in regular expression to return
   * @return MimeType, if any could be found.
   */
  public String extractFromResponseHeaders(final Pattern pattern, final int group) {
    String value = null;
    if (getResponseHeaders() != null && !getResponseHeaders().isEmpty()) {
      for (final String responseHeader : getResponseHeaders()) {
        final Matcher matcher = pattern.matcher(responseHeader);
        if (matcher.find()) {
          value = matcher.group(group);
          break;
        }
      }
    }
    return value;
  }
}
