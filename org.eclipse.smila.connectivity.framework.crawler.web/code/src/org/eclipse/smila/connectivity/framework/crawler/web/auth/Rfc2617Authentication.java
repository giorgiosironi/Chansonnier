/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.auth;

/**
 * RFC2617 authentication configuration.
 * 
 */
public class Rfc2617Authentication {

  /** The Host. */
  private String _host;

  /** The Port. */
  private String _port;

  /** The Realm. */
  private String _realm;

  /** The Login. */
  private String _login;

  /** The Password. */
  private String _password;

  /**
   * Returns the login string.
   * 
   * @return String
   */
  public String getLogin() {
    return _login;
  }

  /**
   * Assigns the login string.
   * 
   * @param login
   *          String
   */
  public void setLogin(String login) {
    _login = login;
  }

  /**
   * Returns password string.
   * 
   * @return String
   */
  public String getPassword() {
    return _password;
  }

  /**
   * Assigns password string.
   * 
   * @param password
   *          String
   */
  public void setPassword(String password) {
    _password = password;
  }

  /**
   * Realm as per RFC2617. The realm string must match exactly the realm name presented in the authentication challenge
   * served up by the web server.
   * 
   * @return String
   */
  public String getRealm() {
    return _realm;
  }

  /**
   * Assigns realm.
   * 
   * @param realm
   *          String
   */
  public void setRealm(String realm) {
    _realm = realm;
  }

  /**
   * Returns host value. This equates to the canonical root URI of RFC2617 host.
   * 
   * @return String
   */
  public String getHost() {
    return _host;
  }

  /**
   * Assigns host value. This equates to the canonical root URI of RFC2617 host.
   * 
   * @param host
   *          String
   */
  public void setHost(String host) {
    _host = host;
  }

  /**
   * Returns port number. This equates to the canonical root URI of RFC2617 port.
   * 
   * @return String
   */
  public String getPort() {
    return _port;
  }

  /**
   * Returns port number. This equates to the canonical root URI of RFC2617 port.
   * 
   * @param port
   *          String
   */
  public void setPort(String port) {
    _port = port;
  }
}
