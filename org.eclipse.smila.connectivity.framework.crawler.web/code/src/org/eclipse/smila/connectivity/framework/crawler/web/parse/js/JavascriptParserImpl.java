/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.parse.js;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configured;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ExtractUtils;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseData;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseStatus;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parser;

/**
 * Extracts links from given javascript code. Extraction is based on regular expressions.
 */
public class JavascriptParserImpl extends Configured implements Parser, JavascriptParser {

  /**
   * Array of MIME content-types that are supported by this parser.
   */
  private static final String[] CONTENT_TYPES = { "application/x-javascript", "text/javascript" };

  /**
   * Ampersand.
   */
  private static final String AMPERSAND = "&";

  /**
   * Escaped ampersand.
   */
  private static final String ESCAPED_AMPERSAND = "&amp;";

  /**
   * Prefix "www.".
   */
  private static final String WWW_PREFIX = "www.";

  /**
   * Prefix "http://".
   */
  private static final String HTTP_PREFIX = "http://";

  /**
   * Pattern that matches text in quotes (' and ").
   */
  private static final Pattern TEXT_PATTERN =
    Pattern.compile("(\\\\*(?:\"|\'))([^\\s\"\']+?)(?:\\1)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

  /** The Log. */
  private final Log _log = LogFactory.getLog(JavascriptParserImpl.class);

  /**
   * {@inheritDoc}
   */
  public String[] getContentTypes() {
    return CONTENT_TYPES;
  }

  /**
   * {@inheritDoc}
   */
  public Parse getParse(Content content) {
    final String scriptCode = new String(content.getContent());
    final Outlink[] outlinks = getOutlinks(scriptCode, content.getUrl(), content.getUrl());
    final ParseData parseData = new ParseData(ParseStatus.STATUS_SUCCESS, "", outlinks, content.getMetadata());
    parseData.setConf(_configuration);
    final Parse parse = new ParseImpl(parseData);
    return parse;
  }

  /**
   * 
   * @param scriptCode
   *          String containing javascript code that will be parsed.
   * @param anchor
   *          Outlink anchor
   * @param base
   *          URL of the page.
   * @return Array of Outlinks
   */
  public Outlink[] getOutlinks(String scriptCode, String anchor, String base) {
    final List<Outlink> outlinks = new ArrayList<Outlink>();
    URL baseUrl = null;
    try {
      baseUrl = new URL(base);
    } catch (MalformedURLException exception) {
      if (_log.isErrorEnabled()) {
        _log.error("Malformed base url: " + base, exception);
      }
    }
    final Matcher textMatcher = TEXT_PATTERN.matcher(scriptCode);
    String url = null;
    while (textMatcher.find()) {
      try {
        url = textMatcher.group(2);
        if (!ExtractUtils.isUrl(url)) {
          continue;
        }

        if ((url.startsWith(WWW_PREFIX))) {
          url = HTTP_PREFIX + url;
        } else {
          url = new URL(baseUrl, url).toString();
        }
        url = url.replaceAll(ESCAPED_AMPERSAND, AMPERSAND);
        if (_log.isDebugEnabled()) {
          _log.debug("Extracted url from javascript code: " + url);
        }
        outlinks.add(new Outlink(url, anchor, getConf()));
      } catch (MalformedURLException exception) {
        if (_log.isDebugEnabled()) {
          _log.debug("JavaScript Parser: Malformed extracted url: " + url + ", base url: " + base, exception);
        }
      }
    }

    return outlinks.toArray(new Outlink[0]);
  }
}
