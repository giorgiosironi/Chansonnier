/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the ParseData.java from Nutch 0.8.1 (see below the licene). 
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
package org.eclipse.smila.connectivity.framework.crawler.web.parse;

import java.util.Arrays;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configured;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

/**
 * Data extracted from a page's content.
 */
public final class ParseData extends Configured {

  /** The title. */
  private String _title;

  /** The OutLinks. */
  private Outlink[] _outlinks;

  /** The content meta. */
  private Metadata _contentMeta;

  /** The parse meta. */
  private Metadata _parseMeta;

  /** The status. */
  private ParseStatus _status;

  /** The _html meta tags. */
  private HTMLMetaTags _htmlMetaTags;

  /**
   * Empty constructor.
   */
  public ParseData() {
  }

  /**
   * Creates new object with empty html metatags.
   * 
   * @param status
   *          ParseStatus
   * @param title
   *          String title of the page
   * @param outlinks
   *          OutLinks array
   * @param contentMeta
   *          Meta data extracted from content
   */  
  public ParseData(ParseStatus status, String title, Outlink[] outlinks, Metadata contentMeta) {
    this(status, title, outlinks, contentMeta, new Metadata(), new HTMLMetaTags());
  }
  
  /**
   * Creates new object with empty parse meta data.
   * 
   * @param status
   *          ParseStatus
   * @param title
   *          String title of the page
   * @param outlinks
   *          OutLinks array
   * @param contentMeta
   *          Meta data extracted from content
   * @param htmlMetaTags
   *          Meta data extracted from HTML tags
   */
  public ParseData(ParseStatus status, String title, Outlink[] outlinks, Metadata contentMeta,
    HTMLMetaTags htmlMetaTags) {
    this(status, title, outlinks, contentMeta, new Metadata(), htmlMetaTags);
  }

  /**
   * Creates new ParseData object with given configuration.
   * 
   * @param status
   *          ParseStatus
   * @param title
   *          String title of the page
   * @param outlinks
   *          OutLinks array
   * @param contentMeta
   *          Meta data extracted from content
   * @param parseMeta
   *          Meta data parse Meta data
   * @param htmlMetaTags
   *          Meta data extracted from HTML tags
   */
  public ParseData(ParseStatus status, String title, Outlink[] outlinks, Metadata contentMeta, Metadata parseMeta,
    HTMLMetaTags htmlMetaTags) {
    _status = status;
    _title = title;
    _outlinks = outlinks;
    _contentMeta = contentMeta;
    _parseMeta = parseMeta;
    _htmlMetaTags = htmlMetaTags;
  }

  /**
   * The status of parsing the page.
   * 
   * @return ParseStatus
   */
  public ParseStatus getStatus() {
    return _status;
  }

  /**
   * The title of the page.
   * 
   * @return String
   */
  public String getTitle() {
    return _title;
  }

  /**
   * The outlinks of the page.
   * 
   * @return Outlinks array
   */
  public Outlink[] getOutlinks() {
    return _outlinks;
  }

  /**
   * The original Meta data retrieved from content.
   * 
   * @return Meta data
   */
  public Metadata getContentMeta() {
    return _contentMeta;
  }

  /**
   * Other content properties.
   * 
   * @return Meta data
   */
  public Metadata getParseMeta() {
    return _parseMeta;
  }

  /**
   * Assigns parse meta data.
   * 
   * @param parseMeta
   *          parser specific content properties.
   */
  public void setParseMeta(Metadata parseMeta) {
    _parseMeta = parseMeta;
  }

  /**
   * Get a meta data single value. This method first looks for the meta data value in the parse meta data. If no value
   * is found it the looks for the meta data in the content meta data.
   * 
   * @param name
   *          Name of meta data element
   * 
   * @return String Meta data value
   * 
   * @see #getContentMeta()
   * @see #getParseMeta()
   */
  public String getMeta(String name) {
    String value = _parseMeta.get(name);
    if (value == null) {
      value = _contentMeta.get(name);
    }
    return value;
  }

  /**
   * Returns HTML meta tags information.
   * 
   * @return meta tags extracted from HTML tags
   */
  public HTMLMetaTags getHtmlMetaTags() {
    return _htmlMetaTags;
  }

  /**
   * Assigns HTML meta tags information.
   * 
   * @param htmlMetaTags
   *          meta tags extracted from HTML tags
   */
  public void setHtmlMetaTags(HTMLMetaTags htmlMetaTags) {
    _htmlMetaTags = htmlMetaTags;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ParseData)) {
      return false;
    }
    final ParseData other = (ParseData) o;
    return this._status.equals(other._status) && this._title.equals(other._title)
      && Arrays.equals(this._outlinks, other._outlinks) && this._contentMeta.equals(other._contentMeta)
      && this._parseMeta.equals(other._parseMeta);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _title.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();

    buffer.append("Status: " + _status + "\n");
    buffer.append("Title: " + _title + "\n");

    if (_outlinks != null) {
      buffer.append("Outlinks: " + _outlinks.length + "\n");
      for (int i = 0; i < _outlinks.length; i++) {
        buffer.append("  outlink: " + _outlinks[i] + "\n");
      }
    }

    buffer.append("Content Metadata: " + _contentMeta + "\n");
    buffer.append("Parse Metadata: " + _parseMeta + "\n");

    return buffer.toString();
  }

}
