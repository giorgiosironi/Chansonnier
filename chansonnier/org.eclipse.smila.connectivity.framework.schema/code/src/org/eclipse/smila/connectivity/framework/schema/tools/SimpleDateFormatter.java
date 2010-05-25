/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH) 
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class SimpleDateFormatter.
 */
public final class SimpleDateFormatter {

  /** The date format. */
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * doesnt instantiates a new simple date formatter.
   */
  private SimpleDateFormatter() {
  }

  /**
   * Parses the.
   * 
   * @param date
   *          the date
   * 
   * @return the java.util. date
   */
  public static java.util.Date parse(final String date) {
    try {
      return new SimpleDateFormat(DATE_FORMAT).parse(date);
    } catch (final ParseException e) {
      throw new DateParseException(date);
    }
  }

  /**
   * Prints the.
   * 
   * @param date
   *          the date
   * 
   * @return the string
   */
  public static String print(final Date date) {
    String result = null;
    if (date != null) {
      result = new SimpleDateFormat(DATE_FORMAT).format(date);
    }
    return result;
  }

}
