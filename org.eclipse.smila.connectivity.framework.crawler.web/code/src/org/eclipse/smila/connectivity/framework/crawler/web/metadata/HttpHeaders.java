/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the HttpHeaders.java from Nutch 0.8.1 (see below the licene). 
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
package org.eclipse.smila.connectivity.framework.crawler.web.metadata;

/**
 * A collection of HTTP header names.
 */
public interface HttpHeaders {

  /** The CONTENT_ENCODING. */
  String CONTENT_ENCODING = "Content-Encoding";

  /** The CONTENT_LANGUAGE. */
  String CONTENT_LANGUAGE = "Content-Language";

  /** The CONTENT_LENGTH. */
  String CONTENT_LENGTH = "Content-Length";

  /** The CONTENT_LOCATION. */
  String CONTENT_LOCATION = "Content-Location";

  /** The CONTENT_DISPOSITION. */
  String CONTENT_DISPOSITION = "Content-Disposition";

  /** The CONTENT_MD5. */
  String CONTENT_MD5 = "Content-MD5";

  /** The CONTENT_TYPE. */
  String CONTENT_TYPE = "Content-Type";

  /** The LAST_MODIFIED. */
  String LAST_MODIFIED = "Last-Modified";

  /** The LOCATION. */
  String LOCATION = "Location";

}
