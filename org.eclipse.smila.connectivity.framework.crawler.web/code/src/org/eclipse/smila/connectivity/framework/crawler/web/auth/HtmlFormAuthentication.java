/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * HTML form authentication configuration.
 * 
 */
public class HtmlFormAuthentication {

  /**
   * Available methods for sending html form.
   */
  public enum HttpMethod {

    /** The GET. */
    GET,
    /** The POST. */
    POST,
  }

  /** The Credential domain. */
  private String _credentialDomain;

  /** The Login URI. */
  private String _loginUri;

  /** The form items. */
  private final Map<String, String> _formItems = new HashMap<String, String>();

  /** The http method. */
  private HttpMethod _httpMethod;

  /**
   * Returns the credential domain. This equates to the canonical root URI of RFC2617.
   * 
   * @return String
   */
  public String getCredentialDomain() {
    return _credentialDomain;
  }

  /**
   * Assigns credential domain.
   * 
   * @param domain
   *          String
   */
  public void setCredentialDomain(String domain) {
    _credentialDomain = domain;
  }

  /**
   * Returns the mapping of key/value form items pairs.
   * 
   * @return Map
   */
  public Map<String, String> getFormItems() {
    return _formItems;
  }

  /**
   * Returns relative or absolute URI to the page that the HTML Form submits to.
   * 
   * @return String
   */
  public String getLoginUri() {
    if (_loginUri.startsWith("/")) {
      if (_credentialDomain.endsWith("/")) {
        return _credentialDomain.substring(0, _credentialDomain.length() - 1) + _loginUri;
      } else {
        return _credentialDomain + _loginUri;
      }
    } else {
      return _loginUri;
    }
  }

  /**
   * Assigns relative or absolute URI to the page that the HTML Form submits to.
   * 
   * @param uri
   *          String
   */
  public void setLoginUri(String uri) {

    _loginUri = uri;
  }

  /**
   * Returns form http method (GET or POST).
   * 
   * @return HttpMethod
   */
  public HttpMethod getHttpMethod() {
    return _httpMethod;
  }

  /**
   * Assigns form HTTP method.
   * 
   * @param method
   *          HttpMethod
   */
  public void setHttpMethod(HttpMethod method) {
    _httpMethod = method;
  }

  /**
   * Adds a key/value pair to the map of form item.
   * 
   * @param key
   *          String
   * @param value
   *          String
   * 
   * @return String
   */
  public String putFormItem(String key, String value) {
    return _formItems.put(key, value);
  }
}
