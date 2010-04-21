/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.parse;

/**
 * Manages webcrawler parsers. ParserManager is registered as declarative service.
 * 
 */
public interface ParserManager {

  /**
   * Adds parser to the list.
   * 
   * @param parser
   *          Parser
   */
  void addParser(Parser parser);

  /**
   * Removes parser.
   * 
   * @param parser
   *          Parser
   */
  void removeParser(Parser parser);

  /**
   * Returns Parser implementation that handles given content-type. If no parser is found, {{null}} is returned.
   * 
   * @param contentType
   *          contentType
   * @return Parser instance
   */
  Parser getParser(String contentType);

  /**
   * Returns Parser by class or null if parser is not available.
   * 
   * @param clazz
   *          Parser class
   * @return Parser
   */
  @SuppressWarnings("unchecked")
  Parser getParser(Class clazz);

}
