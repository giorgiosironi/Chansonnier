/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Manage Webservice properties.
 * 
 * @author jschumacher
 * 
 */
public class WebserviceProperties {
  /**
   * default hostname, if local hostname cannot be determined: "localhost".
   */
  public static final String DEFAULT_HOST = "localhost";

  /**
   * default port: 8081 (because 8080 is used by the HTTP service in SMILA).
   */
  public static final String DEFAULT_PORT = "8081";

  /**
   * default webroot: "".
   */
  public static final String DEFAULT_WEBROOT = "/";

  /**
   * config property for hostname.
   */
  public static final String PROP_HOSTNAME = "webservice.hostname";

  /**
   * config propertiy for TCP/IP port.
   */
  public static final String PROP_PORT = "webservice.port";

  /**
   * config property for webroot.
   */
  public static final String PROP_WEBROOT = "webservice.webroot";

  /**
   * hostname to bind the webservice to.
   */
  private String _hostname = findDefaultHost();

  /**
   * TCP/IP port for the webservice server.
   */
  private String _port = DEFAULT_PORT;

  /**
   * common URL part for all webservices.
   */
  private String _webroot = DEFAULT_WEBROOT;

  /**
   * create default instance.
   */
  public WebserviceProperties() {
    super();
  }

  /**
   * override defaults from properties.
   * 
   * @param props
   *          properties.
   */
  public WebserviceProperties(final Properties props) {
    this();
    initialize(props);
  }

  /**
   * read property file and override defaults.
   * 
   * @param propertyFile
   *          property file.
   * @throws IOException
   *           error reading the stream.
   */
  public WebserviceProperties(final InputStream propertyFile) throws IOException {
    this();
    final Properties props = new Properties();
    props.load(propertyFile);
    initialize(props);
  }

  /**
   * read my property values from the props.
   * 
   * @param props
   *          properties.
   */
  private void initialize(final Properties props) {
    setHostname(props.getProperty(PROP_HOSTNAME, _hostname));
    setPort(props.getProperty(PROP_PORT, _port));
    setWebroot(props.getProperty(PROP_WEBROOT, _webroot));
  }

  /**
   * try to determine local host name.
   * 
   * @return local hostname, IP adress or "localhost" if nothing works.
   */
  private static String findDefaultHost() {
    try {
      final InetAddress localhost = InetAddress.getLocalHost();
      return localhost.getHostName();
    } catch (final Exception ex) {
      return DEFAULT_HOST;
    }
  }

  /**
   * @return hostname
   */
  public String getHostname() {
    return _hostname;
  }

  /**
   * @return TCP/IP port number
   */
  public String getPort() {
    return _port;
  }

  /**
   * @return root for webservice URLs.
   */
  public String getWebroot() {
    return _webroot;
  }

  /**
   * @param hostname
   *          new hostname
   */
  public void setHostname(final String hostname) {
    _hostname = hostname;
  }

  /**
   * @param port
   *          new TCP/IP port number
   */
  public void setPort(final String port) {
    _port = port;
  }

  /**
   * @param webroot
   *          new root for webservice URLs.
   */
  public void setWebroot(final String webroot) {
    _webroot = webroot.trim();
    if (_webroot == null && webroot.length() == 0) {
      _webroot = DEFAULT_WEBROOT;
    } else {
      if (_webroot.charAt(0) != '/') {
        _webroot = '/' + _webroot;
      }
      if (_webroot.charAt(_webroot.length() - 1) != '/') {
        _webroot = _webroot + '/';
      }
    }
  }

  /**
   * @return base URL for published webservices.
   */
  public String getBaseURL() {
    return "http://" + _hostname + ":" + _port + _webroot;
  }
}
