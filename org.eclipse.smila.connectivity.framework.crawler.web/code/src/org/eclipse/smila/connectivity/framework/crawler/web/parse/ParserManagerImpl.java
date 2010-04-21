/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ParserManager implementation.
 */
public class ParserManagerImpl implements ParserManager {

  /**
   * List of parsers.
   */
  private List<Parser> _parsers = new ArrayList<Parser>();

  /**
   * Log.
   */
  private final Log _log = LogFactory.getLog(ParserManagerImpl.class);

  /**
   * {@inheritDoc}
   */
  public void addParser(Parser parser) {
    _parsers.add(parser);
    if (_log.isDebugEnabled()) {
      _log.debug("adding webcrawler parser: " + parser.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void removeParser(Parser parser) {
    _parsers.remove(parser);
    if (_log.isDebugEnabled()) {
      _log.debug("removing webcrawler parser: " + parser.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  public Parser getParser(String contentType) {
    // TODO: performance: put content types and parsers references into map.
    for (Parser parser : _parsers) {
      final String[] contentTypes = parser.getContentTypes();
      for (String parserContentType : contentTypes) {
        //TODO: content-type normalization for better content-type comparison
        if (contentType.toLowerCase().startsWith(parserContentType)) {
          return parser;
        }
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Parser getParser(Class clazz) {
    for (Parser parser : _parsers) {
      if (parser.getClass().equals(clazz)) {
        return parser;
      }
    }
    return null;
  }

}
