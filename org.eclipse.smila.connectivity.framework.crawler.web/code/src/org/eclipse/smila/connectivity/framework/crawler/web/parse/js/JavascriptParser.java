/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.parse.js;

import org.eclipse.smila.connectivity.framework.crawler.web.parse.Outlink;

/**
 * Javascript parser interface.
 * 
 */
public interface JavascriptParser {
  /**
   * Returns links found in given javascript code.
   * 
   * @param scriptCode
   *          Javascript code to analyze
   * @param anchor
   *          Links title
   * @param base
   *          Base url for links
   * @return Array of Outlinks
   */
  Outlink[] getOutlinks(String scriptCode, String anchor, String base);
}
