/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.UrlFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Implementation of the filter that checks given link for conformity with the regex specified in the configuration.
 * 
 * 
 */
public class RegExpFilter extends UrlFilter {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(RegExpFilter.class);

  /**
   * Default empty constructor.
   */
  public RegExpFilter() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public boolean matches(Outlink test) {
    if (isEnabled(test)) {
      LOG.debug("RegExp filter: source: " + test + " filter value: " + getValue());
      final Pattern pattern = Pattern.compile(getValue());

      if (pattern.matcher(test.getUrlString()).find()) {
        return true;
      }
    }
    return false;
  }
}
