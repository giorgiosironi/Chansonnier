/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.configuration;

/**
 * Crawler keys for HTTP specific options.
 * 
 */
public class HttpProperties {

  /** The Constant AGENT_NAME. */
  public static final String AGENT_NAME = "http.agent.name";

  /** The Constant AGENT_VERSION. */
  public static final String AGENT_VERSION = "http.agent.version";

  /** The Constant AGENT_DESCRIPTION. */
  public static final String AGENT_DESCRIPTION = "http.agent.description";

  /** The Constant AGENT_URL. */
  public static final String AGENT_URL = "http.agent.url";

  /** The Constant AGENT_EMAIL. */
  public static final String AGENT_EMAIL = "http.agent.email";

  /** The Constant REFERER. */
  public static final String REFERER = "http.referer";

  /** The Constant HEADERS. */
  public static final String HEADERS = "http.headers";

  /** The Constant HTTP11. */
  public static final String HTTP11 = "http.http11";

  /** The Constant PROXY_HOST. */
  public static final String PROXY_HOST = "http.proxy.host";

  /** The Constant PROXY_PORT. */
  public static final String PROXY_PORT = "http.proxy.port";

  /** The Constant PROXY_LOGIN. */
  public static final String PROXY_LOGIN = "http.proxy.login";

  /** The Constant PROXY_PASSWORD. */
  public static final String PROXY_PASSWORD = "http.proxy.password";

  /** The Constant MAX_LENGTH_BYTES. */
  public static final String MAX_LENGTH_BYTES = "http.max.length.bytes";

  /** The Constant TIMEOUT. */
  public static final String TIMEOUT = "http.timeout";

  /** The Constant CONNECT_TIMEOUT. */
  public static final String CONNECT_TIMEOUT = "http.connect.timeout";

  /** The Constant READ_TIMEOUT. */
  public static final String READ_TIMEOUT = "http.read.timeout";

  /** The Constant ENABLE_COOKIES. */
  public static final String ENABLE_COOKIES = "http.enable.cookies";

  /** The Constant ROBOTSTXT_POLICY. */
  public static final String ROBOTSTXT_POLICY = "http.robotstxt.policy";

  /** The Constant ROBOTSTXT_VALUE. */
  public static final String ROBOTSTXT_VALUE = "http.robotstxt.value";

  /** The Constant ROBOTSTXT_AGENT_NAMES. */
  public static final String ROBOTSTXT_AGENT_NAMES = "http.robots.agents";

  /** The Constant ROBOTSTXT_403_ALLOW. */
  public static final String ROBOTSTXT_403_ALLOW = "http.robots.403.allow";

  /**
   * Instantiates a new http properties.
   */
  protected HttpProperties() {
    super();
  }
}
