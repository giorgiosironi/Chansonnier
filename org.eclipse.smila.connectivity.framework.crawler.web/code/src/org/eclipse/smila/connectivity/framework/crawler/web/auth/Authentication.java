/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds authentication configurations.
 */
public class Authentication {

  /** The Rfc2617 authentications. */
  private List<Rfc2617Authentication> _rfc2617Authentications = new ArrayList<Rfc2617Authentication>();

  /** The HtmlForm authentications. */
  private List<HtmlFormAuthentication> _htmlFormAuthentications = new ArrayList<HtmlFormAuthentication>();

  /** The SSL certificate authentication. */
  private SslCertificateAuthentication _sslCertificateAuthentication;

  /**
   * Returns HTML form authentications.
   * 
   * @return List
   */
  public List<HtmlFormAuthentication> getHtmlFormAuthentications() {
    return _htmlFormAuthentications;
  }

  /**
   * Returns rfc2617 authentications.
   * 
   * @return List
   */
  public List<Rfc2617Authentication> getRfc2617Authentications() {
    return _rfc2617Authentications;
  }

  /**
   * Returns SSL certificate authentication.
   * 
   * @return SslCertificateAuthentication
   */
  public SslCertificateAuthentication getSslCertificateAuthentication() {
    return _sslCertificateAuthentication;
  }

  /**
   * Assigns HTML form authentications.
   * 
   * @param auth
   *          HTML form authentication instances list
   */
  public void setHtmlFormAuthentications(List<HtmlFormAuthentication> auth) {
    _htmlFormAuthentications = auth;
  }

  /**
   * Assigns rfc2617 authentications.
   * 
   * @param auth
   *          rfc2617 authentication instances list
   */
  public void setRfc2617Authentications(List<Rfc2617Authentication> auth) {
    _rfc2617Authentications = auth;
  }

  /**
   * Assigns SSL certificate authentication.
   * 
   * @param auth
   *          SSL certificate authentication instance
   */
  public void setSslCertificateAuthentications(SslCertificateAuthentication auth) {
    _sslCertificateAuthentication = auth;
  }
}
