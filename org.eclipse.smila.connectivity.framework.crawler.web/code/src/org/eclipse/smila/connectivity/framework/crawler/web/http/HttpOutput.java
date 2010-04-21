/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File is based on the HttpOutput.java from Nutch 0.8.1 (see below the licene). 
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
package org.eclipse.smila.connectivity.framework.crawler.web.http;

import org.eclipse.smila.connectivity.framework.crawler.web.parse.Content;

/**
 * Class that represents HTTP response information.
 */
public class HttpOutput {

  /** The content. */
  private Content _content;

  /** The status. */
  private HttpStatus _status;

  /**
   * Creates new object with the given Content and HttpStatus.
   * 
   * @param content
   *          Content
   * @param status
   *          HttpStatus
   */
  public HttpOutput(Content content, HttpStatus status) {
    _content = content;
    _status = status;
  }

  /**
   * Creates new object with STATUS_SUCCESS HttpStatus.
   * 
   * @param content
   *          Content
   */
  public HttpOutput(Content content) {
    _content = content;
    _status = HttpStatus.STATUS_SUCCESS;
  }

  /**
   * Returns content.
   * 
   * @return Content
   */
  public Content getContent() {
    return _content;
  }

  /**
   * Assigns content.
   * 
   * @param content
   *          Content
   */
  public void setContent(Content content) {
    _content = content;
  }

  /**
   * Returns HttpStatus.
   * 
   * @return HttpStatus
   */
  public HttpStatus getStatus() {
    return _status;
  }

  /**
   * Assigns HttpStatus.
   * 
   * @param status
   *          HttpStatus
   */
  public void setStatus(HttpStatus status) {
    _status = status;
  }
}
