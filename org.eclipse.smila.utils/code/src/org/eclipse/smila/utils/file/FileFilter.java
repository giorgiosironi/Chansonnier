/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.utils.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Class for file filter.
 * 
 * @author brox IT-Solutions GmbH
 */
public class FileFilter implements FilenameFilter {

  /**
   * List of possible includes.
   */
  private final ArrayList<? extends Include> _includes;

  /**
   * List of possible excludes.
   */
  private final ArrayList<? extends Exclude> _excludes;

  /**
   * Switch either case sensitive (true) or case insensitive (false).
   */
  private final boolean _caseSensitive;

  /**
   * Constructor.
   * 
   * @param includes
   *          possible List of includes.
   * @param excludes
   *          possible List of excludes.
   * @param caseSensitive
   *          Switch true or false.
   */
  public FileFilter(ArrayList<? extends Include> includes, ArrayList<? extends Exclude> excludes,
    boolean caseSensitive) {
    _includes = includes;
    _excludes = excludes;
    _caseSensitive = caseSensitive;

  }

  /**
   * {@inheritDoc}
   */
  public boolean accept(File pathname, String name) {

    boolean accept = false;
    final File file = new File(pathname.getPath() + File.separatorChar + name);

    for (final Include include : _includes) {
      if (FileFilterUtils.matchPath(include.getName(), file.getPath(), _caseSensitive)) {
        final Date dateFrom = include.getDateFrom();
        Date dateTo = include.getDateTo();
        if (dateFrom != null) {
          if (dateTo == null) {
            dateTo = new Date();
          }
          if (file.lastModified() >= dateFrom.getTime() && file.lastModified() <= dateTo.getTime()) {
            continue;
          }
        } else if (include.getPeriod() != null) {
          final int interval = Integer.parseInt(include.getPeriod().substring(0, include.getPeriod().length() - 1));
          final char measure = include.getPeriod().charAt(include.getPeriod().length() - 1);
          final Date now = new Date();
          Date compareDate = (Date) now.clone();
          final GregorianCalendar cal = new GregorianCalendar();
          cal.setTime(compareDate);

          switch (measure) {
            case 'Y':
              cal.add(Calendar.YEAR, -interval);
              break;
            case 'M':
              cal.add(Calendar.MONTH, -interval);
              break;
            case 'D':
              cal.add(Calendar.DAY_OF_MONTH, -interval);
              break;
            case 'h':
              cal.add(Calendar.HOUR, -interval);
              break;
            case 'm':
              cal.add(Calendar.MINUTE, -interval);
              break;
            case 's':
              cal.add(Calendar.SECOND, -interval);
              break;
            default:
              break;

          }
          compareDate = cal.getTime();
          if (file.lastModified() < compareDate.getTime()) {
            continue;
          }
        }
        accept = true;
        break;
      }
    }
    for (final Exclude exclude : _excludes) {
      if (FileFilterUtils.matchPath(exclude.getName(), file.getAbsolutePath(), _caseSensitive)) {
        accept = false;
        break;
      }
    }
    if (!accept) {
      return false;
    }
    return true;
  }
}
