/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * helper class for formatting and parsing literal values. all methods synchronize on the used local formatter object,
 * so you can use the shared instance. Using multiple instances may improve performance, though, because of less
 * synchronization.
 * 
 * @author jschumacher
 * 
 */
public class LiteralFormatHelper {
  /**
   * shared global helper instance.
   */
  public static final LiteralFormatHelper INSTANCE = new LiteralFormatHelper();

  /**
   * formatter to create and parse standard string representations of Date values: "yyyy-MM-dd".
   */
  private final DateFormat _formatDate = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * formatter to create and parse standard string representations of Time values: "HH:mm:ss.SSS".
   */
  private final DateFormat _formatTime = new SimpleDateFormat("HH:mm:ss.SSS");

  /**
   * formatter to create and parse standard string representations of DateTime values: "yyyy-MM-dd HH:mm:ss.SSS".
   */
  private final DateFormat _formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  /**
   * create local instance.
   */
  public LiteralFormatHelper() {
    // nothing to do
  }

  /**
   * format value as Date string.
   * 
   * @param value
   *          a date value
   * @return formatted date.
   */
  public String formatDate(Date value) {
    synchronized (_formatDate) {
      return _formatDate.format(value);
    }
  }

  /**
   * format value as Time string.
   * 
   * @param value
   *          a time value
   * @return formatted time string
   */
  public String formatTime(Date value) {
    synchronized (_formatTime) {
      return _formatTime.format(value);
    }
  }

  /**
   * format value as DateTime string.
   * 
   * @param value
   *          a datetime value
   * @return formatted datetime string
   */
  public String formatDateTime(Date value) {
    synchronized (_formatDateTime) {
      return _formatDateTime.format(value);
    }
  }

  /**
   * parse a date string.
   * 
   * @param dateString
   *          a date string
   * @return parsed Date
   * @throws ParseException
   *           string has wrong format
   */
  public Date parseDate(String dateString) throws ParseException {
    synchronized (_formatDate) {
      return _formatDate.parse(dateString);
    }
  }

  /**
   * parse a time string.
   * 
   * @param timeString
   *          a time value
   * @return parsed Date
   * @throws ParseException
   *           string has wrong format
   */
  public Date parseTime(String timeString) throws ParseException {
    synchronized (_formatTime) {
      return _formatTime.parse(timeString);
    }
  }

  /**
   * parse datetime string.
   * 
   * @param dateTimeString
   *          a datetime string
   * @return parsed Date
   * @throws ParseException
   *           string has wrong format
   */
  public Date parseDateTime(String dateTimeString) throws ParseException {
    synchronized (_formatDateTime) {
      return _formatDateTime.parse(dateTimeString);
    }
  }

}
