/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the Outlink.java from Nutch 0.8.1 (see below the licene). 
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.net.UrlNormalizerFactory;

/**
 * An outgoing link from a page.
 */
public class Outlink {

  /** The Constant HTTP_PORT. */
  private static final int HTTP_PORT = 80;

  /** The Constant HTTPS_PORT. */
  private static final int HTTPS_PORT = 443;

  /** The Constant DEFAULT_PORTS. */
  private static final Map<String, Integer> DEFAULT_PORTS = new HashMap<String, Integer>();

  static {
    DEFAULT_PORTS.put("http", new Integer(HTTP_PORT));
    DEFAULT_PORTS.put("https", new Integer(HTTPS_PORT));
  }

  /** The URL. */
  private URL _url;

  /** The URI. */
  private URI _uri;

  /** The URL string. */
  private String _urlString;

  /** The anchor. */
  private String _anchor;

  /**
   * Empty constructor.
   * 
   */
  public Outlink() {

  }

  /**
   * Creates new OutLink.
   * 
   * @param urlString
   *          URL of the link.
   * @param anchor
   *          text anchor that is associated with this link
   * @param conf
   *          crawler configuration
   * @throws MalformedURLException
   *           if URL is broken
   * @throws URISyntaxException
   * @throws URIException
   * @throws URIException
   */
  public Outlink(final String urlString, final String anchor, final Configuration conf)
    throws MalformedURLException {
    _urlString = new UrlNormalizerFactory(conf).getNormalizer().normalize(urlString);
    _url = new URL(_urlString);
    _anchor = anchor;
    try {
      _uri = new URI(_urlString);
    } catch (final URISyntaxException exception) {
      try {
        _uri = new URI(URIUtil.encodePathQuery(_urlString));
      } catch (final URIException e) {
        _uri = null;
      } catch (final URISyntaxException e) {
        _uri = null;
      }

    }

  }

  /**
   * Returns url of the link.
   * 
   * @return String
   */
  public String getUrlString() {
    return _urlString;
  }

  /**
   * Returns text anchor associated with the link.
   * 
   * @return String
   */
  public String getAnchor() {
    return _anchor;
  }

  /**
   * Returns url of the link.
   * 
   * @return URL
   */
  public URL getUrl() {
    return _url;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Outlink)) {
      return false;
    }
    final Outlink other = (Outlink) o;
    if (_uri != null) {
      return _uri.equals(other._uri);
    } else {
      return _urlString.equals(other._urlString);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    if (_uri != null) {
      return _uri.hashCode();
    } else {
      return _urlString.hashCode();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "url: " + _urlString + " anchor: " + _anchor;
  }

  /**
   * Returns the default port number for the specified protocol.
   * 
   * @param protocol
   *          a particular access protocol
   * @return default port number
   */
  public static int getDefaultPortNumber(String protocol) {
    if (protocol == null) {
      return -1;
    }
    protocol = protocol.toLowerCase();
    final Integer defaultPort = DEFAULT_PORTS.get(protocol);
    if (defaultPort != null) {
      return defaultPort.intValue();
    }
    return -1;
  }

}
