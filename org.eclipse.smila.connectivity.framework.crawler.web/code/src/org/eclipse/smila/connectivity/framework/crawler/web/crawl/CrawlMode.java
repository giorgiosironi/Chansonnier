/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.crawl;

/**
 * Available crawl modes.
 * 
 * 
 */
public enum CrawlMode {
  /**
   * The Skip mode.
   */
  Skip,
  /**
   * The Index mode.
   */
  Index,
  /**
   * The AnalyzeOnly mode.
   */
  AnalyzeOnly
}
