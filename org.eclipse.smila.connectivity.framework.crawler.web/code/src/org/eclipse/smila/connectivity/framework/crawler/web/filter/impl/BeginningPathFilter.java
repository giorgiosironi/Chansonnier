/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.UrlFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Implementation of beginning path URL filter that checks if the link starts with the given path.
 * 
 * 
 */
public class BeginningPathFilter extends UrlFilter {

  /** The Log. */
  private static final Log LOG = LogFactory.getLog(BeginningPathFilter.class);

  /**
   * Default empty constructor.
   */
  public BeginningPathFilter() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public boolean matches(Outlink test) {
    if (isEnabled(test)) {
      LOG.debug("Beginning path filter: url: " + test + " filter value: " + this.getValue());
      if (test.getUrl().getPath().startsWith(this.getValue())) {
        return true;
      }
    }
    return false;
  }

}
