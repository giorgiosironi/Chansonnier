/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 * 
 * This File uses code from the ProtocolStatus.java from Nutch 0.8.1 (see below the licene). The original File was modified by
 * the Smila Team
 * 
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

import java.util.HashMap;
import java.util.Map;

/**
 * Set of status codes for retrieved URL.
 */
public class HttpStatus {

  /** Content was retrieved without errors. */
  public static final int SUCCESS = 1;

  /** Content was not retrieved. Any further errors may be indicated in _message. */
  public static final int FAILED = 2;

  /** This protocol was not found. Application may attempt to retry later. */
  public static final int PROTO_NOT_FOUND = 10;

  /** Resource is gone. */
  public static final int GONE = 11;

  /** Resource has moved permanently. New URL should be found in _message. */
  public static final int MOVED = 12;

  /** Resource has moved temporarily. New URL should be found in _message. */
  public static final int TEMP_MOVED = 13;

  /** Resource was not found. */
  public static final int NOTFOUND = 14;

  /** Temporary failure. Application may retry immediately. */
  public static final int RETRY = 15;

  /** Unspecified exception occurred. Further information may be provided in _message. */
  public static final int EXCEPTION = 16;

  /** Access denied - authorization required, but missing/incorrect. */
  public static final int ACCESS_DENIED = 17;

  /** Access denied by robots.txt rules. */
  public static final int ROBOTS_DENIED = 18;

  /** Too many redirects. */
  public static final int REDIR_EXCEEDED = 19;

  /** Not fetching. */
  public static final int NOTFETCHING = 20;

  /** Unchanged since the last fetch. */
  public static final int NOTMODIFIED = 21;

  /**
   * Request was refused by protocol plug-ins, because it would block. The expected number of milliseconds to wait
   * before retry may be provided in _message.
   */
  public static final int WOULDBLOCK = 22;

  /** Thread was blocked HTTP.max.delays times during fetching. */
  public static final int BLOCKED = 23;

  // Useful static instances for status codes that don't usually require any
  // additional arguments.
  /** The Constant STATUS_SUCCESS. */
  public static final HttpStatus STATUS_SUCCESS = new HttpStatus(SUCCESS);

  /** The Constant STATUS_FAILED. */
  public static final HttpStatus STATUS_FAILED = new HttpStatus(FAILED);

  /** The Constant STATUS_GONE. */
  public static final HttpStatus STATUS_GONE = new HttpStatus(GONE);

  /** The Constant STATUS_NOTFOUND. */
  public static final HttpStatus STATUS_NOTFOUND = new HttpStatus(NOTFOUND);

  /** The Constant STATUS_RETRY. */
  public static final HttpStatus STATUS_RETRY = new HttpStatus(RETRY);

  /** The Constant STATUS_ROBOTS_DENIED. */
  public static final HttpStatus STATUS_ROBOTS_DENIED = new HttpStatus(ROBOTS_DENIED);

  /** The Constant STATUS_REDIR_EXCEEDED. */
  public static final HttpStatus STATUS_REDIR_EXCEEDED = new HttpStatus(REDIR_EXCEEDED);

  /** The Constant STATUS_NOTFETCHING. */
  public static final HttpStatus STATUS_NOTFETCHING = new HttpStatus(NOTFETCHING);

  /** The Constant STATUS_NOTMODIFIED. */
  public static final HttpStatus STATUS_NOTMODIFIED = new HttpStatus(NOTMODIFIED);

  /** The Constant STATUS_WOULDBLOCK. */
  public static final HttpStatus STATUS_WOULDBLOCK = new HttpStatus(WOULDBLOCK);

  /** The Constant STATUS_BLOCKED. */
  public static final HttpStatus STATUS_BLOCKED = new HttpStatus(BLOCKED);

  /** The code to name. */
  private static Map<Integer, String> s_codeToName = new HashMap<Integer, String>();

  /** The code. */
  private int _code;

  /** The message. */
  private String _message;

  static {
    s_codeToName.put(new Integer(SUCCESS), "success");
    s_codeToName.put(new Integer(FAILED), "failed");
    s_codeToName.put(new Integer(PROTO_NOT_FOUND), "proto_not_found");
    s_codeToName.put(new Integer(GONE), "gone");
    s_codeToName.put(new Integer(MOVED), "moved");
    s_codeToName.put(new Integer(TEMP_MOVED), "temp_moved");
    s_codeToName.put(new Integer(NOTFOUND), "notfound");
    s_codeToName.put(new Integer(RETRY), "retry");
    s_codeToName.put(new Integer(EXCEPTION), "exception");
    s_codeToName.put(new Integer(ACCESS_DENIED), "access_denied");
    s_codeToName.put(new Integer(ROBOTS_DENIED), "robots_denied");
    s_codeToName.put(new Integer(REDIR_EXCEEDED), "redir_exceeded");
    s_codeToName.put(new Integer(NOTFETCHING), "notfetching");
    s_codeToName.put(new Integer(NOTMODIFIED), "notmodified");
    s_codeToName.put(new Integer(WOULDBLOCK), "wouldblock");
    s_codeToName.put(new Integer(BLOCKED), "blocked");
  }

  /**
   * Empty constructor.
   */
  public HttpStatus() {

  }

  /**
   * Creates new object with the given status code and message.
   * 
   * @param code
   *          code
   * @param message
   *          String
   */
  public HttpStatus(int code, String message) {
    this._code = code;
    this._message = message;
  }

  /**
   * Creates new object with given status code and empty message.
   * 
   * @param code
   *          code
   */
  public HttpStatus(int code) {
    this(code, null);
  }

  /**
   * Creates new object with given status code and object as a message.
   * 
   * @param code
   *          status code
   * @param message
   *          Object
   */
  public HttpStatus(int code, Object message) {
    this._code = code;
    if (message != null) {
      _message = String.valueOf(message);
    }
  }

  /**
   * Creates new object with the EXCEPTION status.
   * 
   * @param throwable
   *          Throwable
   */
  public HttpStatus(Throwable throwable) {
    this(EXCEPTION, throwable);
  }

  /**
   * Returns status code.
   * 
   * @return code
   */
  public int getCode() {
    return _code;
  }

  /**
   * Assigns status code.
   * 
   * @param code
   *          the code
   */
  public void setCode(int code) {
    this._code = code;
  }

  /**
   * Checks if the status code is equal to SUCCESS.
   * 
   * @return boolean
   */
  public boolean isSuccess() {
    return _code == SUCCESS;
  }

  /**
   * Checks if the HTTP error is transient.
   * 
   * @return boolean
   */
  public boolean isTransientFailure() {
    return _code == ACCESS_DENIED || _code == EXCEPTION || _code == REDIR_EXCEEDED || _code == RETRY
      || _code == TEMP_MOVED || _code == WOULDBLOCK || _code == PROTO_NOT_FOUND;
  }

  /**
   * Checks if the HTTP error is permanent.
   * 
   * @return boolean
   */
  public boolean isPermanentFailure() {
    return _code == FAILED || _code == GONE || _code == MOVED || _code == NOTFOUND || _code == ROBOTS_DENIED;
  }

  /**
   * Returns status message.
   * 
   * @return String
   */
  public String getMessage() {
    return _message;
  }

  /**
   * Assigns status message.
   * 
   * @param message
   *          String
   */
  public void setMessage(String message) {
    _message = message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof HttpStatus)) {
      return false;
    }
    final HttpStatus other = (HttpStatus) o;
    if (this._code != other._code) {
      return false;
    }
    if (this._message == null) {
      return (other._message == null);
    } else {
      if (other._message == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return _code;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer res = new StringBuffer();
    res.append(s_codeToName.get(new Integer(_code)) + "(" + _code + ")");
    if (_message != null) {
      res.append(": " + String.valueOf(_message));
    }
    return res.toString();
  }

}
