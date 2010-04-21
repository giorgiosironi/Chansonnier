/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator 
 * Sebastian Voigt (brox IT Solutions GmbH) 
 * This File is based on the Http.java from Nutch 0.8.1 (see below the licene). The original File was modified by the 
 * Smila Team
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
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.Rfc2617Authentication;
import org.eclipse.smila.connectivity.framework.crawler.web.auth.SslCertificateAuthentication;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.configuration.HttpProperties;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.FilterProcessor;

/**
 * Holds the HTTP protocol-specific options.
 */
public class Http extends HttpBase {

  /** The connection manager. */
  private static MultiThreadedHttpConnectionManager s_connectionManager = new MultiThreadedHttpConnectionManager();

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(Http.class);

  /** The Constant COLON. */
  private static final String COLON = ":";

  /** The Constant SEMICOLON. */
  private static final String SEMICOLON = ";";

  /** Since the Configuration has not yet been set, then not configured client is returned. */
  private static HttpClient s_client = new HttpClient(s_connectionManager);

  /** The HTTPS protocol. */
  private Protocol _https;

  /**
   * Creates a new instance of HTTP.
   */
  public Http() {
    super();
  }

  /**
   * Gets the client.
   * 
   * @return the client
   */
  static synchronized HttpClient getClient() {
    return s_client;
  }

  /**
   * Loads HttpBase and Client configurations.
   * 
   * @param conf
   *          Configuration
   */
  @Override
  public void setConf(Configuration conf) {
    super.setConf(conf);
    configureClient();
  }

  /**
   * Returns HttpResponse for the given URL.
   * 
   * @param urlString
   *          String
   * 
   * @return HttpResponse
   * 
   * @throws IOException
   *           if there was a error retrieving URL.
   */
  @Override
  protected Response getResponse(String urlString) throws IOException {
    return getResponse(urlString, null);
  }

  /**
   * Returns HttpResponse for the given URL and filter processor.
   * 
   * @param filterProcessor
   *          filterProcessor implementation
   * @param urlString
   *          the url string
   * 
   * @return HttpResponse
   * 
   * @throws IOException
   *           if there was a error retrieving URL.
   */
  @Override
  protected Response getResponse(String urlString, FilterProcessor filterProcessor) throws IOException {
    return new HttpResponse(this, urlString, filterProcessor);
  }

  /**
   * Loads HTTP client configuration for this web site.
   */
  private void configureClient() {
    final HttpConnectionManagerParams params = s_connectionManager.getParams();
    if (_timeout != 0) {
      params.setConnectionTimeout(_timeout);
      params.setSoTimeout(_timeout);
    } else {
      params.setConnectionTimeout(_connectTimeout);
      params.setSoTimeout(_readTimeout);
    }
    params.setSendBufferSize(BUFFER_SIZE);
    params.setReceiveBufferSize(BUFFER_SIZE);
    final HostConfiguration hostConf = s_client.getHostConfiguration();
    final List<Header> headers = new ArrayList<Header>();
    // prefer English
    headers.add(new Header("Accept-Language", "en-us,en-gb,en;q=0.7,*;q=0.3"));
    // prefer UTF-8
    headers.add(new Header("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.7"));
    // prefer understandable formats
    headers.add(new Header("Accept",
      "text/html,application/xml;q=0.9,application/xhtml+xml,text/xml;q=0.9,text/plain;q=0.8"));
    // accept GZIP content
    headers.add(new Header("Accept-Encoding", "x-gzip, gzip"));
    final String[] webSiteHeaders = getConf().get(HttpProperties.HEADERS).split(SEMICOLON);
    for (String header : webSiteHeaders) {
      final String[] headerInformation = header.split(COLON);
      if (headerInformation.length > 2) {
        headers.add(new Header(headerInformation[0].trim(), headerInformation[1].trim()));
      }
    }
    hostConf.getParams().setParameter("http.default-headers", headers);
    if (_useProxy) {
      hostConf.setProxy(_proxyHost, _proxyPort);
      if (_proxyLogin.length() > 0) {
        final Credentials proxyCreds = new UsernamePasswordCredentials(_proxyLogin, _proxyPassword);
        s_client.getState().setProxyCredentials(new AuthScope(AuthScope.ANY), proxyCreds);
      }
    }
    final List<Rfc2617Authentication> httpAuthentications = _authentication.getRfc2617Authentications();

    for (Rfc2617Authentication auth : httpAuthentications) {
      s_client.getState().setCredentials(
        new AuthScope(auth.getHost(), Integer.valueOf(auth.getPort()), auth.getRealm()),
        new UsernamePasswordCredentials(auth.getLogin(), auth.getPassword()));
    }

    final SslCertificateAuthentication sslAuth = _authentication.getSslCertificateAuthentication();
    if (sslAuth != null) {
      try {
        final URL truststoreURL = new File(sslAuth.getTruststoreUrl()).toURL();
        final URL keystoreURL = new File(sslAuth.getKeystoreUrl()).toURL();

        final ProtocolSocketFactory sslFactory =
          new AuthSSLProtocolSocketFactory(keystoreURL, sslAuth.getKeystorePassword(), truststoreURL, sslAuth
            .getTruststorePassword());
        _https = new Protocol(sslAuth.getProtocolName(), sslFactory, Integer.valueOf(sslAuth.getPort()));
        Protocol.registerProtocol(sslAuth.getProtocolName(), _https);
      } catch (MalformedURLException exception) {
        LOG.error("unable to bind https protocol" + exception.toString());
      }
    }
  }

}
