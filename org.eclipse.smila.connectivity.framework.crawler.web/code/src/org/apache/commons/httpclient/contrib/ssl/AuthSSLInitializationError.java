// CHECKSTYLE:OFF
/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt(Brox IT Solutions GmbH) - inital creator
 * 
 * This File is based on the AuthSSLInitializationError.java (Contrib) from commons-httpclient-3.0.1-src
 * (see below the licene). 
 * The original File was modified by the Smila Team
 **********************************************************************************************************************/
/*
 * $Header:
 * /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/contrib/org/apache/commons/httpclient/contrib/ssl/AuthSSLInitializationError.java,v
 * 1.2 2004/06/10 18:25:24 olegk Exp $ $Revision: 155418 $ $Date: 2005-02-26 08:01:52 -0500 (Sat, 26 Feb 2005) $
 * 
 * ====================================================================
 * 
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the Apache Software
 * Foundation. For more information on the Apache Software Foundation, please see <http://www.apache.org/>.
 * 
 * [Additional notices, if required by prior licensing conditions]
 * 
 */
package org.apache.commons.httpclient.contrib.ssl;

/**
 * <p>
 * Signals fatal error in initialization of {@link AuthSSLProtocolSocketFactory}.
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component. The component is provided as a reference
 * material, which may be inappropriate for use without additional customization.
 * </p>
 */

public class AuthSSLInitializationError extends Error {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new AuthSSLInitializationError.
   */
  public AuthSSLInitializationError() {
    super();
  }

  /**
   * Creates a new AuthSSLInitializationError with the specified message.
   * 
   * @param message
   *          error message
   */
  public AuthSSLInitializationError(String message) {
    super(message);
  }
}
