/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Alexander Eliseyev (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.schema.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.eclipse.smila.connectivity.framework.schema.tools.DateParseException;
import org.eclipse.smila.connectivity.framework.schema.tools.SimpleDateFormatter;

/**
 * The Class SchemaToolsTest.
 * 
 * @author Alexander Eliseyev
 */
public class TestSimpleDateFormatter extends TestCase {

  /**
   * The Constant MINUTES.
   */
  private static final int MINUTES = 5;

  /**
   * The Constant SECONDS.
   */
  private static final int SECONDS = 40;

  /**
   * The Constant HOUR.
   */
  private static final int HOUR = 13;

  /**
   * The Constant DATE.
   */
  private static final int DATE = 30;

  /**
   * The Constant MONTH.
   */
  private static final int MONTH = 11;

  /**
   * The Constant YEAR.
   */
  private static final int YEAR = 2008;

  /**
   * Test parse.
   * 
   * @throws Exception
   *           the exception
   */
  public void testParse() throws Exception {
    String dateStr = "2008-12-30T13:05:40";
    final Date date = SimpleDateFormatter.parse(dateStr);
    assertNotNull(date);
    // assertEquals("Tue Dec 30 13:05:40 NOVT 2008", date.toString());
    // It was wrong because depends on locale
    // correct is:
    final Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    assertEquals(YEAR, calendar.get(Calendar.YEAR));
    assertEquals(MONTH, calendar.get(Calendar.MONTH));
    assertEquals(DATE, calendar.get(Calendar.DATE));
    assertEquals(HOUR, calendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(MINUTES, calendar.get(Calendar.MINUTE));
    assertEquals(SECONDS, calendar.get(Calendar.SECOND));

    dateStr = "wrongDateString";
    try {
      SimpleDateFormatter.parse(dateStr);
      fail("DateParseException must be thrown");
    } catch (final DateParseException e) {
      assertEquals(dateStr, e.getMessage());
    }
  }

  /**
   * Test print.
   * 
   * @throws Exception
   *           the exception
   */
  public void testPrint() throws Exception {

  }

}
