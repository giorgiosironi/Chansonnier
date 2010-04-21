/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt(Brox IT Solutions GmbH) - inital creator
 * 
 * This File is based on the plugin/parse-html/src/java/org/apache/nutch/parse/html/HTMLMetaTags.java from Nutch 0.8.1 
 * (see below the licene). 
 * The original File was modified by the Smila Team
 **********************************************************************************************************************/
/** 
 * Copyright 2005 The Apache Software Foundation 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.eclipse.smila.connectivity.framework.crawler.web.parse.html;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * This class holds the information about HTML "meta" tags extracted from a page. Some special tags have convenience
 * methods for easy checking.
 */
public class HTMLMetaTags {

  /** The Constant COLON. */
  private static final String COLON = ":";

  /** The no index. */
  private boolean _noIndex;

  /** The no follow. */
  private boolean _noFollow;

  /** The no cache. */
  private boolean _noCache;

  /** The base href. */
  private URL _baseHref;

  /** The refresh. */
  private boolean _refresh;

  /** The refresh time. */
  private int _refreshTime;

  /** The refresh href. */
  private URL _refreshHref;

  /** The general tags. */
  private final Properties _generalTags = new Properties();

  /** The http equiv tags. */
  private final Properties _httpEquivTags = new Properties();

  /**
   * Sets all boolean values to <code>false</code>. Clears all other tags.
   */
  public void reset() {
    _noIndex = false;
    _noFollow = false;
    _noCache = false;
    _refresh = false;
    _refreshTime = 0;
    _baseHref = null;
    _refreshHref = null;
    _generalTags.clear();
    _httpEquivTags.clear();
  }

  /**
   * Sets <code>_noFollow</code> to <code>true</code>.
   */
  public void setNoFollow() {
    _noFollow = true;
  }

  /**
   * Sets <code>_noIndex</code> to <code>true</code>.
   */
  public void setNoIndex() {
    _noIndex = true;
  }

  /**
   * Sets <code>_noCache</code> to <code>true</code>.
   */
  public void setNoCache() {
    _noCache = true;
  }

  /**
   * Sets <code>_refresh</code> to the supplied value.
   * 
   * @param refresh
   *          boolean
   */
  public void setRefresh(boolean refresh) {
    this._refresh = refresh;
  }

  /**
   * Sets the <code>_baseHref</code>.
   * 
   * @param baseHref
   *          Base URL
   */
  public void setBaseHref(URL baseHref) {
    this._baseHref = baseHref;
  }

  /**
   * Sets the <code>_refreshHref</code>.
   * 
   * @param refreshHref
   *          URL
   */
  public void setRefreshHref(URL refreshHref) {
    this._refreshHref = refreshHref;
  }

  /**
   * Sets the <code>_refreshTime</code>.
   * 
   * @param refreshTime
   *          int time
   */
  public void setRefreshTime(int refreshTime) {
    this._refreshTime = refreshTime;
  }

  /**
   * A convenience method. Returns the current value of <code>_noIndex</code>.
   * 
   * @return boolean
   */
  public boolean getNoIndex() {
    return _noIndex;
  }

  /**
   * A convenience method. Returns the current value of <code>_noFollow</code>.
   * 
   * @return boolean
   */
  public boolean getNoFollow() {
    return _noFollow;
  }

  /**
   * A convenience method. Returns the current value of <code>_noCache</code>.
   * 
   * @return boolean
   */
  public boolean getNoCache() {
    return _noCache;
  }

  /**
   * A convenience method. Returns the current value of <code>_refresh</code>.
   * 
   * @return boolean
   */
  public boolean getRefresh() {
    return _refresh;
  }

  /**
   * A convenience method. Returns the <code>_baseHref</code>, if set, or <code>null</code> otherwise.
   * 
   * @return URL
   */
  public URL getBaseHref() {
    return _baseHref;
  }

  /**
   * A convenience method. Returns the <code>_refreshHref</code>, if set, or <code>null</code> otherwise. The value
   * may be invalid if {@link #getRefresh()}returns <code>false</code>.
   * 
   * @return URL
   */
  public URL getRefreshHref() {
    return _refreshHref;
  }

  /**
   * A convenience method. Returns the current value of <code>_refreshTime</code>. The value may be invalid if
   * {@link #getRefresh()}returns <code>false</code>.
   * 
   * @return int
   */
  public int getRefreshTime() {
    return _refreshTime;
  }

  /**
   * Returns all collected values of the general meta tags. Property names are tag names, property values are "content"
   * values.
   * 
   * @return Properties
   */
  public Properties getGeneralTags() {
    return _generalTags;
  }

  /**
   * Returns all collected values of the "http-equiv" meta tags. Property names are tag names, property values are
   * "content" values.
   * 
   * @return Properties
   */
  public Properties getHttpEquivTags() {
    return _httpEquivTags;
  }

  /**
   * Returns information about html meta tags.
   * 
   * @return String
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("base=" + _baseHref + ", noCache=" + _noCache + ", noFollow=" + _noFollow + ", noIndex=" + _noIndex
      + ", refresh=" + _refresh + ", refreshHref=" + _refreshHref + "\n");
    sb.append(" * general tags:\n");
    Iterator<Object> it = _generalTags.keySet().iterator();
    while (it.hasNext()) {
      final String key = (String) it.next();
      sb.append("   - " + key + "\t=\t" + _generalTags.get(key) + "\n");
    }
    sb.append(" * http-equiv tags:\n");
    it = _httpEquivTags.keySet().iterator();
    while (it.hasNext()) {
      final String key = (String) it.next();
      sb.append("   - " + key + "\t=\t" + _httpEquivTags.get(key) + "\n");
    }
    return sb.toString();
  }

  /**
   * Returns ArrayList representation of the HTML meta tags for further indexing.
   * 
   * @return ArrayList
   */
  public List<String> toArrayList() {
    final List<String> htmlMetaTagsArray = new ArrayList<String>();

    htmlMetaTagsArray.add("base" + COLON + _baseHref);
    htmlMetaTagsArray.add("noCache" + COLON + _noCache);
    htmlMetaTagsArray.add("noFollow" + COLON + _noFollow);
    htmlMetaTagsArray.add("noIndex" + COLON + _noIndex);
    htmlMetaTagsArray.add("refresh" + COLON + _refresh);
    htmlMetaTagsArray.add("refreshHref" + COLON + _refreshHref);

    Iterator<Object> it = _generalTags.keySet().iterator();
    while (it.hasNext()) {
      final String key = (String) it.next();
      htmlMetaTagsArray.add(key + COLON + _generalTags.get(key));
    }

    it = _httpEquivTags.keySet().iterator();
    while (it.hasNext()) {
      final String key = (String) it.next();
      htmlMetaTagsArray.add(key + COLON + _httpEquivTags.get(key));
    }

    return htmlMetaTagsArray;
  }
}
