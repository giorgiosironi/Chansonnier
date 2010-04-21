/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.crawler.web.filter.impl;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smila.connectivity.framework.crawler.web.filter.WorkTypeFilter;
import org.eclipse.smila.connectivity.framework.crawler.web.messages.HtmlMetaTagType;
import org.eclipse.smila.connectivity.framework.crawler.web.parse.html.HTMLMetaTags;

/**
 * Implementation of the filter for omitting content by meta tags.
 * 
 * 
 */
public class MetaTagFilter extends WorkTypeFilter<HTMLMetaTags> {

  /** The type. */
  private HtmlMetaTagType _type;

  /** The name. */
  private String _name;

  /** The content. */
  private String _content;

  /**
   * Returns filter content.
   * 
   * @return String
   */
  public String getContent() {
    return _content;
  }

  /**
   * Assigns filter content.
   * 
   * @param content
   *          String
   */
  public void setContent(final String content) {
    _content = content;
  }

  /**
   * Returns filter type (Name or HttpEquiv).
   * 
   * @return HtmlMetaTagType
   */
  public HtmlMetaTagType getType() {
    return _type;
  }

  /**
   * Assigns filter type (Name or HttpEquiv).
   * 
   * @param type
   *          HtmlMetaTagType
   */
  public void setType(final HtmlMetaTagType type) {
    _type = type;
  }

  /**
   * Returns the name of meta tag.
   * 
   * @return String
   */
  public String getName() {
    return _name;
  }

  /**
   * Assigns the name of meta tag.
   * 
   * @param name
   *          String
   */
  public void setName(final String name) {
    _name = name;
  }

  /**
   * {@inheritDoc}
   */
  public boolean matches(final HTMLMetaTags test) {
    boolean result = false;
    Properties metaTags;
    if (_type.equals(HtmlMetaTagType.HTTP_EQUIV)) {
      metaTags = test.getHttpEquivTags();
    } else {
      metaTags = test.getGeneralTags();
    }
    if (StringUtils.isNotEmpty(_name)) {
      if (metaTags.containsKey(_name) && (metaTags.getProperty(_name).equals(_content))) {
        result = true;
      }
    } else {
      if (metaTags.containsValue(_content)) {
        result = true;
      }
    }
    return result;
  }

}
