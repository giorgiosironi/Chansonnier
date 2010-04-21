/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the plugin/parse-html/src/java/org/apache/nutch/parse/html/HtmlParser.java from Nutch 0.8.1 
 * (see below the licene). 
 * The original File was modified by the Smila Team
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
package org.eclipse.smila.connectivity.framework.crawler.web.parse.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.html.dom.HTMLDocumentImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configurable;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.ParserProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.http.Response;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseData;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseImpl;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.ParseStatus;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Parser;
import org.eclipse.smila.connectivity.framework.crawler.web.util.StringUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class HtmlParser.
 */
public class HtmlParser implements Parser, Configurable {

  /**
   * Array of MIME content-types that are supported by this parser.
   */
  private static final String[] CONTENT_TYPES = { "text/html", "text/plain" };

  /** The Constant CHUNK_SIZE. */
  private static final int CHUNK_SIZE = 2000;

  /** The text pattern. */
  private static Pattern s_textPattern = Pattern.compile("text");

  /** The html pattern. */
  private static Pattern s_htmlPattern = Pattern.compile("html");

  /** The meta pattern. */
  private static Pattern s_metaPattern =
    Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", Pattern.CASE_INSENSITIVE);

  /** The charset pattern. */
  private static Pattern s_charsetPattern =
    Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);

  /** The Log. */
  private final Log _log = LogFactory.getLog(HtmlParser.class);

  /** The default char encoding. */
  private String _defaultCharEncoding;

  /** The configuration. */
  private Configuration _configuration;

  /** The utils. */
  private DOMContentUtils _utils;

  /** Javascript parser to find javascript links in the DOM tree. */
  private Parser _javascriptParser;

  /**
   * Given a <code>byte[]</code> representing an html file of an <em>unknown</em> encoding, read out 'charset'
   * parameter in the meta tag from the first <code>CHUNK_SIZE</code> bytes. If there's no meta tag for Content-Type
   * or no charset is specified, <code>null</code> is returned.<br />
   * FIXME: non-byte oriented character encodings (UTF-16, UTF-32) can't be handled with this. We need to do something
   * similar to what's done by mozilla
   * (http://lxr.mozilla.org/seamonkey/source/parser/htmlparser/src/nsParser.cpp#1993). See also
   * http://www.w3.org/TR/REC-xml/#sec-guessing <br />
   * 
   * @param content
   *          <code>byte[]</code> representation of an html file
   * 
   * @return the string
   */
  @SuppressWarnings("deprecation")
  private static String sniffCharacterEncoding(byte[] content) {
    int length;
    if (content.length < CHUNK_SIZE) {
      length = content.length;
    } else {
      length = CHUNK_SIZE;
    }

    final String string = new String(content, 0, 0, length);
    // We don't care about non-ASCII parts so that it's sufficient
    // to just inflate each byte to a 16-bit value by padding.
    // For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
    // {U+0041, U+0082, U+00B7}.
    final String str = string;

    final Matcher metaMatcher = s_metaPattern.matcher(str);
    String encoding = null;
    if (metaMatcher.find()) {
      final Matcher charsetMatcher = s_charsetPattern.matcher(metaMatcher.group(1));
      if (charsetMatcher.find()) {
        encoding = new String(charsetMatcher.group(1));
      }
    }

    return encoding;
  }

  /**
   * Returns the {@link Parse} result for the given {@link Content}.
   * 
   * @param content
   *          Content to be parsed.
   * 
   * @return Parse
   */
  public Parse getParse(Content content) {
    final HTMLMetaTags metaTags = new HTMLMetaTags();

    URL base = null;
    try {
      base = new URL(content.getBaseUrl());
    } catch (MalformedURLException exception) {
      return new ParseStatus(exception).getEmptyParse(getConf());
    }

    String title = "";
    String text = "";
    Outlink[] outlinks = new Outlink[0];
    final List<Outlink> links = new ArrayList<Outlink>();

    final Metadata metadata = new Metadata();

    // parse the content
    DocumentFragment root = null;
    try {
      final byte[] contentInOctets = content.getContent();
      final InputSource input = new InputSource(new ByteArrayInputStream(contentInOctets));
      final String contentType = content.getMetadata().get(Response.CONTENT_TYPE);

      if (!(s_textPattern.matcher(contentType).find() || s_htmlPattern.matcher(contentType).find())) {
        final ParseStatus status = new ParseStatus(ParseStatus.SUCCESS);
        final ParseData parseData =
          new ParseData(status, title, outlinks, content.getMetadata(), metadata, metaTags);
        parseData.setConf(this._configuration);
        final Parse parse = new ParseImpl(text, parseData);
        return parse;
      }

      String encoding = StringUtil.parseCharacterEncoding(contentType);
      if ((encoding != null) && !("".equals(encoding))) {
        metadata.set(Metadata.ORIGINAL_CHAR_ENCODING, encoding);
        encoding = StringUtil.resolveEncodingAlias(encoding);
        if (encoding != null) {
          metadata.set(Metadata.CHAR_ENCODING_FOR_CONVERSION, encoding);
          if (_log.isTraceEnabled()) {
            _log.trace(base + ": setting encoding to " + encoding);
          }
        }
      }

      // sniff out 'charset' value from the beginning of a document
      if ((encoding == null) || ("".equals(encoding))) {
        encoding = sniffCharacterEncoding(contentInOctets);
        if (encoding != null) {
          metadata.set(Metadata.ORIGINAL_CHAR_ENCODING, encoding);
          encoding = StringUtil.resolveEncodingAlias(encoding);
          if (encoding != null) {
            metadata.set(Metadata.CHAR_ENCODING_FOR_CONVERSION, encoding);
            if (_log.isTraceEnabled()) {
              _log.trace(base + ": setting encoding to " + encoding);
            }
          }
        }
      }

      if (encoding == null) {
        // fallback encoding.
        // FIXME : In addition to the global fallback value,
        // we should make it possible to specify fallback encodings for each
        // ccTLD.
        // (e.g. se: windows-1252, kr: x-windows-949, cn: gb18030, tw: big5
        // doesn't work for jp because euc-jp and shift_jis have about the
        // same share)
        encoding = _defaultCharEncoding;
        metadata.set(Metadata.CHAR_ENCODING_FOR_CONVERSION, _defaultCharEncoding);
        if (_log.isTraceEnabled()) {
          _log.trace(base + ": falling back to " + _defaultCharEncoding);
        }
      }

      input.setEncoding(encoding);
      if (_log.isTraceEnabled()) {
        _log.trace("Parsing...");
      }
      root = parse(input);
    } catch (IOException exception) {
      return new ParseStatus(exception).getEmptyParse(getConf());
    } catch (DOMException exception) {
      return new ParseStatus(exception).getEmptyParse(getConf());
    } catch (SAXException exception) {
      return new ParseStatus(exception).getEmptyParse(getConf());
    } catch (Exception exception) {
      _log.error("Unknown parsing error", exception);
      return new ParseStatus(exception).getEmptyParse(getConf());
    }

    // get meta directives
    HTMLMetaProcessor.getMetaTags(metaTags, root, base);
    if (_log.isTraceEnabled()) {
      _log.trace("Meta tags for " + base + ": " + metaTags.toString());
    }
    // check meta directives
    // ok to index
    if (!metaTags.getNoIndex()) {
      if (_log.isDebugEnabled()) {
        _log.debug("Getting title");
      }
      final StringBuffer textBuffer = new StringBuffer();
      _utils.getText(textBuffer, root);
      text = textBuffer.toString();

      if (_log.isDebugEnabled()) {
        _log.debug("Getting title");
      }
      final StringBuffer titleBuffer = new StringBuffer();
      _utils.getTitle(titleBuffer, root);
      title = titleBuffer.toString().trim();
    }

    // ok to follow links
    if (!metaTags.getNoFollow()) {
      // extract outlinks
      final URL baseTag = _utils.getBase(root);
      if (_log.isTraceEnabled()) {
        _log.trace("Getting links...");
      }
      if (baseTag == null) {
        _utils.getOutlinks(base, links, root);
      } else {
        _utils.getOutlinks(baseTag, links, root);
      }
      if (_log.isDebugEnabled()) {
        _log.debug("found " + links.size() + " outlinks in " + content.getUrl());
        for (Outlink outlink : links) {
          _log.debug(outlink.toString());
        }
      }
    }

    final ParseStatus status = new ParseStatus(ParseStatus.SUCCESS);
    if (metaTags.getRefresh()) {
      status.setMinorCode(ParseStatus.SUCCESS_REDIRECT);
      status.setMessage(metaTags.getRefreshHref().toString());
    }

    if (_javascriptParser != null) {
      final List<Outlink> javascriptLinks = new ArrayList<Outlink>();
      _javascriptParser.setConf(_configuration);
      _utils.setJavascriptParser(_javascriptParser);
      _utils.getJavascriptOutlinks(base.toString(), javascriptLinks, root);
      if (_log.isDebugEnabled()) {
        _log.debug("found " + javascriptLinks.size() + " javascript outlinks in " + content.getUrl());
        for (Outlink outlink : javascriptLinks) {
          _log.debug(outlink.toString());
        }
      }
      links.addAll(javascriptLinks);
    }

    outlinks = (Outlink[]) links.toArray(new Outlink[links.size()]);
    final ParseData parseData = new ParseData(status, title, outlinks, content.getMetadata(), metadata, metaTags);
    parseData.setConf(_configuration);
    final Parse parse = new ParseImpl(text, parseData);
    return parse;
  }

  /**
   * Parses the.
   * 
   * @param input
   *          the input
   * 
   * @return the document fragment
   * 
   * @throws Exception
   *           the exception
   */
  private DocumentFragment parse(InputSource input) throws Exception {
    return parseTagSoup(input);
  }

  /**
   * Parses the tag soup.
   * 
   * @param input
   *          the input
   * 
   * @return the document fragment
   * 
   * @throws Exception
   *           the exception
   */
  private DocumentFragment parseTagSoup(InputSource input) throws Exception {
    final HTMLDocumentImpl doc = new HTMLDocumentImpl();
    final DocumentFragment frag = doc.createDocumentFragment();
    final DOMBuilder builder = new DOMBuilder(doc, frag);
    final org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
    reader.setContentHandler(builder);
    reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature, true);
    reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature, false);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
    reader.parse(input);
    return frag;
  }

  /**
   * {@inheritDoc}
   */
  public void setConf(Configuration configuration) {
    _configuration = configuration;
    _defaultCharEncoding = getConf().get(ParserProperties.DEFAULT_CHARACTER_ENCODING, "windows-1252");
    _utils = new DOMContentUtils(configuration);
  }

  /**
   * {@inheritDoc}
   */
  public Configuration getConf() {
    return _configuration;
  }

  /**
   * {@inheritDoc}
   */
  public String[] getContentTypes() {
    return CONTENT_TYPES;
  }

  /**
   * Sets javascript parser reference that is needed for extracting js links.
   * 
   * @param parser
   *          Javascript parser reference.
   */
  public void setJavascriptParser(Parser parser) {
    _javascriptParser = parser;
    if (_log.isDebugEnabled()) {
      _log.debug("Javascript parser bound");
    }
  }

  /**
   * Removes javascript parser reference.
   * 
   * @param parser
   *          javascript parser reference
   */
  public void unsetJavascriptParser(Parser parser) {
    _javascriptParser = null;
    if (_log.isDebugEnabled()) {
      _log.debug("Javascript parser unbound");
    }
  }

}
