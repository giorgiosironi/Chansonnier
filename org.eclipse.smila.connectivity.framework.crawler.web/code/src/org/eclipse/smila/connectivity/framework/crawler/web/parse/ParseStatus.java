/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 * 
 *  This File is based on the ParseStatus.java from Nutch 0.8.1 (see below the licene). 
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
package org.eclipse.smila.connectivity.framework.crawler.web.parse;

import org.eclipse.smila.connectivity.framework.crawler.web.configuration.Configuration;
import org.eclipse.smila.connectivity.framework.crawler.web.metadata.Metadata;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

/**
 * Class to handle parse status messages and codes.
 */
public class ParseStatus {

  // Primary status codes:

  /** Parsing was not performed. */
  public static final byte NOTPARSED = 0;

  /** Parsing succeeded. */
  public static final byte SUCCESS = 1;

  /** General failure. There may be a more specific error message in arguments. */
  public static final byte FAILED = 2;

  /** The Constant MAJOR_CODES. */
  public static final String[] MAJOR_CODES = { "notparsed", "success", "failed" };

  // Secondary success codes go here:

  /**
   * Parsed content contains a directive to redirect to another URL. The target URL can be retrieved from the arguments.
   */
  public static final short SUCCESS_REDIRECT = 100;

  // Secondary failure codes go here:

  /** Parsing failed. An Exception occured (which may be retrieved from the arguments). */
  public static final short FAILED_EXCEPTION = 200;

  /** Parsing failed. Content was truncated, but the parser cannot handle incomplete content. */
  public static final short FAILED_TRUNCATED = 202;

  /** Parsing failed. Invalid format - the content may be corrupted or of wrong type. */
  public static final short FAILED_INVALID_FORMAT = 203;

  /**
   * Parsing failed. Other related parts of the content are needed to complete parsing. The list of URLs to missing
   * parts may be provided in arguments. The Fetcher may decide to fetch these parts at once, then put them into
   * Content.metadata, and supply them for re-parsing.
   */
  public static final short FAILED_MISSING_PARTS = 204;

  /** Parsing failed. There was no content to be parsed - probably caused by errors at protocol stage. */
  public static final short FAILED_MISSING_CONTENT = 205;

  /** The Constant STATUS_NOTPARSED. */
  public static final ParseStatus STATUS_NOTPARSED = new ParseStatus(NOTPARSED);

  /** The Constant STATUS_SUCCESS. */
  public static final ParseStatus STATUS_SUCCESS = new ParseStatus(SUCCESS);

  /** The Constant STATUS_FAILURE. */
  public static final ParseStatus STATUS_FAILURE = new ParseStatus(FAILED);

  /** The major code. */
  private byte _majorCode;

  /** The minor code. */
  private short _minorCode;

  /** The message. */
  private String _message;

  /**
   * Empty constructor.
   */
  public ParseStatus() {

  }

  /**
   * Creates new object with given configuration.
   * 
   * @param majorCode
   *          Major status code (notparsed, success, failed)
   * @param minorCode
   *          Minor status code that specifies major code.
   * @param message
   *          Text message.
   */
  public ParseStatus(int majorCode, int minorCode, String message) {
    this._message = message;
    this._majorCode = (byte) majorCode;
    this._minorCode = (short) minorCode;
  }

  /**
   * Simplified constructor for passing just a major code.
   * 
   * @param majorCode
   *          major status code
   */
  public ParseStatus(int majorCode) {
    this(majorCode, 0, null);
  }

  /**
   * Simplified constructor for passing just a major status code and text message.
   * 
   * @param majorCode
   *          major status code
   * @param message
   *          text message
   */
  public ParseStatus(int majorCode, String message) {
    this(majorCode, 0, message);
  }

  /**
   * Simplified constructor for passing just a major and minor codes.
   * 
   * @param majorCode
   *          major status code
   * @param minorCode
   *          minor status code
   */
  public ParseStatus(int majorCode, int minorCode) {
    this(majorCode, minorCode, null);
  }

  /**
   * Constructor for passing throwable.
   * 
   * @param throwable
   *          Throwable
   */
  public ParseStatus(Throwable throwable) {
    this(FAILED, FAILED_EXCEPTION, throwable.toString());
  }

  /**
   * A convenience method. Returns true if majorCode is SUCCESS, false otherwise.
   * 
   * @return boolean
   */
  public boolean isSuccess() {
    return _majorCode == SUCCESS;
  }

  /**
   * Return a message.
   * 
   * @return String
   */
  public String getMessage() {
    return _message;
  }

  /**
   * Returns a major status code.
   * 
   * @return int
   */
  public int getMajorCode() {
    return _majorCode;
  }

  /**
   * Returns a minor status code.
   * 
   * @return int
   */
  public int getMinorCode() {
    return _minorCode;
  }

  /**
   * A convenience method. Creates an empty Parse instance, which returns this status.
   * 
   * @param conf
   *          Configuration
   * 
   * @return Parse
   */
  public Parse getEmptyParse(Configuration conf) {
    return new EmptyParseImpl(this, conf);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer res = new StringBuffer();
    String name = null;
    if (_majorCode >= 0 && _majorCode < MAJOR_CODES.length) {
      name = MAJOR_CODES[_majorCode];
    } else {
      name = "UNKNOWN!";
    }
    res.append(name + "(" + _majorCode + "," + _minorCode + ")");
    if (_message != null) {
      res.append("message=" + _message);
    }
    return res.toString();
  }

  /**
   * Assigns text message.
   * 
   * @param message
   *          text message
   */
  public void setMessage(String message) {
    this._message = message;
  }

  /**
   * Assigns major status code.
   * 
   * @param majorCode
   *          byte
   */
  public void setMajorCode(byte majorCode) {
    this._majorCode = majorCode;
  }

  /**
   * Assigns minor status code.
   * 
   * @param minorCode
   *          short
   */
  public void setMinorCode(short minorCode) {
    this._minorCode = minorCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof ParseStatus)) {
      return false;
    }
    boolean res = true;
    final ParseStatus other = (ParseStatus) o;
    res = res && (this._majorCode == other._majorCode) && (this._minorCode == other._minorCode);
    if (!res) {
      return res;
    }
    if (this._message == null) {
      return (other._message == null);
    } else {
      if (other._message == null) {
        return false;
      }
      if (!other._message.equals(this._message)) {
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
    return _majorCode;
  }

  /**
   * The Class EmptyParseImpl.
   */
  private static class EmptyParseImpl implements Parse {

    /** The _data. */
    private final ParseData _data;

    /**
     * Instantiates a new empty parse impl.
     * 
     * @param status
     *          the status
     * @param conf
     *          the conf
     */
    public EmptyParseImpl(ParseStatus status, Configuration conf) {
      _data = new ParseData(status, "", new Outlink[0], new Metadata(), new Metadata(), new HTMLMetaTags());
      _data.setConf(conf);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.smila.connectivity.framework.crawler.web.parse.Parse#getData()
     */
    public ParseData getData() {
      return _data;
    }

    /**
     * Gets the text.
     * 
     * @return the text
     */
    public String getText() {
      return "";
    }
  }

}
