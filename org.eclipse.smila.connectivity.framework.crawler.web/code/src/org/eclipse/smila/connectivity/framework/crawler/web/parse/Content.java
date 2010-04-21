/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the Content.java from Nutch 0.8.1 (see below the licene). 
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

import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;

/**
 * Class that represents the content of the retrieved page.
 */
public final class Content {

  /** The URL. */
  private String _url;

  /** The base. */
  private String _base;

  /** The content. */
  private byte[] _content;

  /** The content type. */
  private String _contentType;

  /** The meta data. */
  private Metadata _metadata;

  /**
   * Empty constructor.
   */
  public Content() {
  }

  public Content(String url, String base, byte[] content, String contentType) {
    this(url, base, content, contentType, new Metadata());
  }
  
  /**
   * The Constructor.
   * 
   * @param url
   *          URL
   * @param base
   *          Base URL for relative links
   * @param content
   *          byte array
   * @param contentType
   *          Page Content-type
   * @param metadata
   *          Extracted meta data
   */
  public Content(String url, String base, byte[] content, String contentType, Metadata metadata) {

    if (url == null) {
      throw new IllegalArgumentException("null url");
    }
    if (base == null) {
      throw new IllegalArgumentException("null base");
    }
    if (content == null) {
      throw new IllegalArgumentException("null content");
    }
    if (metadata == null) {
      throw new IllegalArgumentException("null metadata");
    }

    _url = url;
    _base = base;
    _content = content;
    _contentType = contentType;
    _metadata = metadata;

  }

  /**
   * The URL fetched.
   * 
   * @return String
   */
  public String getUrl() {
    return _url;
  }

  /**
   * The base URL for relative links contained in the content. Maybe be different from url if the request redirected.
   * 
   * @return String
   */
  public String getBaseUrl() {
    return _base;
  }

  /**
   * Returns the binary content retrieved.
   * 
   * @return byte array
   */
  public byte[] getContent() {
    return _content;
  }

  /**
   * Assigns the binary content retrieved.
   * 
   * @param content
   *          byte array
   */
  public void setContent(byte[] content) {
    _content = content;
  }

  /**
   * Returns the media type of the retrieved content.
   * 
   * @return String
   */
  public String getContentType() {
    return _contentType;
  }

  /**
   * Assigns the the media type of the retrieved content.
   * 
   * @param contentType
   *          String
   */
  public void setContentType(String contentType) {
    _contentType = contentType;
  }

  /**
   * Returns other protocol-specific data.
   * 
   * @return Meta data
   */
  public Metadata getMetadata() {
    return _metadata;
  }

  /**
   * Assigns other protocol-specific data.
   * 
   * @param metadata
   *          meta data
   */
  public void setMetadata(Metadata metadata) {
    _metadata = metadata;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Content)) {
      return false;
    }
    final Content that = (Content) o;
    return this._url.equals(that._url) && this._base.equals(that._base)
      && Arrays.equals(this.getContent(), that.getContent()) && this._contentType.equals(that._contentType)
      && this._metadata.equals(that._metadata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _url.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();

    buffer.append("_url: " + _url + "\n");
    buffer.append("_base: " + _base + "\n");
    buffer.append("_contentType: " + _contentType + "\n");
    buffer.append("_metadata: " + _metadata + "\n");
    buffer.append("Content:\n");
    buffer.append(new String(_content)); // try default encoding

    return buffer.toString();

  }

}
