/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx.client.helpers;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;

/**
 * The Class OutWriter.
 */
public final class OutWriter {

  /**
   * Private constructor to avoid instatiation.
   */
  private OutWriter() {
  }

  /**
   * Write out.
   * 
   * @param str
   *          the str
   */
  public static void writeOut(final String str) {
    System.out.println(str);
  }

  /**
   * Write.
   * 
   * @param echo
   *          the echo
   * @param log
   *          the log
   */
  public static void write(final String echo, final Log log) {
    log.info(echo);
  }

  /**
   * Write.
   * 
   * @param object
   *          the object
   * @param echo
   *          the echo
   * @param log
   *          the log
   */
  @SuppressWarnings("unchecked")
  public static void write(final String echo, final Object object, final Log log) {
    if (object == null) {
      writeLine(String.format("%s... %s.", echo, "NULL."), log);
      return;
    }
    // log result;
    if (object.getClass().isArray()) {
      final Object[] objectArray = (Object[]) object;
      int n = 0;
      if (objectArray.length == 0) {
        writeLine(String.format("%s... NO.", echo), log);
      } else {
        writeLine(echo, log);
        for (final Object o : objectArray) {
          writeItemList(++n, o, log);
        }
      }
    } else if (Collection.class.isAssignableFrom(object.getClass())) {
      final Collection collection = (Collection) object;
      if (collection.isEmpty()) {
        writeLine(String.format("%s... NO.", echo), log);
      } else {
        final Iterator it = collection.iterator();
        int n = 0;
        writeLine(echo, log);
        while (it.hasNext()) {
          writeItemList(++n, it.next(), log);
        }
      }
    } else {
      writeLine(String.format("%s... %s.", echo, object), log);
    }
  }

  /**
   * Write item list.
   * 
   * @param n
   *          the n
   * @param o
   *          the o
   * @param log
   *          the log
   */
  private static void writeItemList(final int n, final Object o, final Log log) {
    log.info(String.format("%d. %s", n, o));
  }

  /**
   * Write line.
   * 
   * @param o
   *          the o
   * @param log
   *          the log
   */
  private static void writeLine(final Object o, final Log log) {
    log.info(o);
  }
}
