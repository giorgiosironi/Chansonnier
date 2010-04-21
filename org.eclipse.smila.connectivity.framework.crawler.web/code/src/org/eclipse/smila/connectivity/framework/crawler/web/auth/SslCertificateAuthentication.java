/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.auth;

/**
 * This class holds configuration for SSL authentication.
 * 
 */
public class SslCertificateAuthentication {

  /** The Protocol name. */
  private String _protocolName;

  /** The Port. */
  private int _port;

  /** The TrustStore URL. */
  private String _truststoreUrl;

  /** The TrustStore password. */
  private String _truststorePassword;

  /** The KeyStore URL. */
  private String _keystoreUrl;

  /** The KeyStore password. */
  private String _keystorePassword;

  /**
   * Return Java KeyStore password.
   * 
   * @return String
   */
  public String getKeystorePassword() {
    return _keystorePassword;
  }

  /**
   * Assigns Java KeyStore password.
   * 
   * @param password
   *          String
   */
  public void setKeystorePassword(String password) {
    _keystorePassword = password;
  }

  /**
   * Returns Java KeyStore URL.
   * 
   * @return String
   */
  public String getKeystoreUrl() {
    return _keystoreUrl;
  }

  /**
   * Assigns Java KeyStore URL.
   * 
   * @param url
   *          String
   */
  public void setKeystoreUrl(String url) {
    _keystoreUrl = url;
  }

  /**
   * Returns SSL port.
   * 
   * @return SSL port
   */
  public int getPort() {
    return _port;
  }

  /**
   * Assigns SSL port.
   * 
   * @param port
   *          SSL port
   */
  public void setPort(int port) {
    _port = port;
  }

  /**
   * Returns SSL protocol name.
   * 
   * @return String
   */
  public String getProtocolName() {
    return _protocolName;
  }

  /**
   * Assigns protocol name.
   * 
   * @param name
   *          String
   */
  public void setProtocolName(String name) {
    _protocolName = name;
  }

  /**
   * Returns the location of SSL TrustStore.
   * 
   * @return String
   */
  public String getTruststoreUrl() {
    return _truststoreUrl;
  }

  /**
   * Assigns the location of SSL TrustStore.
   * 
   * @param url
   *          String
   */
  public void setTruststoreUrl(String url) {
    _truststoreUrl = url;
  }

  /**
   * Returns TrustStore password.
   * 
   * @return String
   */
  public String getTruststorePassword() {
    return _truststorePassword;
  }

  /**
   * Assigns TrustStore password.
   * 
   * @param password
   *          String
   */
  public void setTruststorePassword(String password) {
    _truststorePassword = password;
  }

}
