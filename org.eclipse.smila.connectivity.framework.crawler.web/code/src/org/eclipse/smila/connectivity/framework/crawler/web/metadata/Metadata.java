/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 *
 * This File is based on the MetaData.java from Nutch 0.8.1 (see below the licene). 
 * The original File was modified by the Smila Team 
 **********************************************************************************************************************/
/**
 * Copyright 2005 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.eclipse.smila.connectivity.framework.crawler.web.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * A syntax tolerant and multi-valued meta data container.
 * 
 * All the static String fields declared by this class are used as reference names for syntax correction on metadata
 * naming.
 * 
 * @author Chris Mattmann
 * @author J&eacute;r&ocirc;me Charron
 * @author Dmitry Hazin (brox IT Solutions GmbH) - updates
 * @author Sebastian Voigt (brox IT Solutions GmbH) - updates
 */
public class Metadata implements HttpHeaders, Crawler {

  /** Used to format DC dates for the DATE meta data field. */
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /** The Constant THRESHOLD_FACTOR. */
  public static final int THRESHOLD_FACTOR = 3;

  /** The names index. */
  private static Map<String, String> s_namesIdx = new HashMap<String, String>();

  /** The s_normalized. */
  private static String[] s_normalized;

  // Uses self introspection to fill the meta names index and the
  // meta names list.
  static {
    final Field[] fields = Metadata.class.getFields();
    for (int i = 0; i < fields.length; i++) {
      final int mods = fields[i].getModifiers();
      if (Modifier.isFinal(mods) && Modifier.isPublic(mods) && Modifier.isStatic(mods)
        && fields[i].getType().equals(String.class)) {
        try {
          final String val = (String) fields[i].get(null);
          s_namesIdx.put(normalize(val), val);
        } catch (final Exception e) {
          // Simply ignore.
          ;
        }
      }
    }
    s_normalized = s_namesIdx.keySet().toArray(new String[s_namesIdx.size()]);
  }

  /** A map of all meta data attributes. */
  private final Map<String, Object> _metadata;

  /** Constructs a new, empty meta data. */
  public Metadata() {
    _metadata = new HashMap<String, Object>();
  }

  /**
   * @param name
   *          Name of the meta
   * @return boolean
   */
  public boolean isMultiValued(final String name) {
    return getValues(name).length > 1;
  }

  /**
   * Returns an array of the names contained in the meta data.
   * 
   * @return String[]
   */
  public String[] names() {
    final Iterator<String> iter = _metadata.keySet().iterator();
    final List<String> names = new ArrayList<String>();
    while (iter.hasNext()) {
      names.add(getNormalizedName(iter.next()));
    }
    return names.toArray(new String[names.size()]);
  }

  /**
   * Get the value associated to a _metadata name. If many values are associated to the specified name, then the first
   * one is returned.
   * 
   * @param name
   *          of the meta data.
   * @return the value associated to the specified meta data name.
   */
  public String get(final String name) {
    final Object values = _metadata.get(getNormalizedName(name));
    if ((values != null) && (values instanceof List)) {
      return (String) ((List<?>) values).get(0);
    } else {
      return (String) values;
    }
  }

  /**
   * Get the values associated to a meta data name.
   * 
   * @param name
   *          of the meta data.
   * @return the values associated to a meta data name.
   */
  public String[] getValues(final String name) {
    final Object values = _metadata.get(getNormalizedName(name));
    if (values != null) {
      if (values instanceof List) {
        final List list = (List) values;
        return (String[]) list.toArray(new String[list.size()]);
      } else {
        return new String[] { (String) values };
      }
    }
    return new String[0];
  }

  /**
   * Add a meta data name/value mapping. Add the specified value to the list of values associated to the specified meta
   * data name.
   * 
   * @param name
   *          the meta data name.
   * @param value
   *          the meta data value.
   */
  public void add(final String name, final String value) {
    final String normalized = getNormalizedName(name);
    final Object values = _metadata.get(normalized);
    if (values != null) {
      if (values instanceof String) {
        final List<String> list = new ArrayList<String>();
        list.add((String) values);
        list.add(value);
        _metadata.put(normalized, list);
      } else if (values instanceof List) {
        ((List) values).add(value);
      }
    } else {
      _metadata.put(normalized, value);
    }
  }

  /**
   * Assigns a meta data names/values mapping from the given properties.
   * 
   * @param properties
   *          set of properties representing name/value mapping.
   */
  public void setAll(final Properties properties) {
    final Enumeration<?> names = properties.propertyNames();
    while (names.hasMoreElements()) {
      final String name = (String) names.nextElement();
      set(name, properties.getProperty(name));
    }
  }

  /**
   * Set _metadata name/value. Associate the specified value to the specified _metadata name. If some previous values
   * were associated to this name, they are removed.
   * 
   * @param name
   *          the _metadata name.
   * @param value
   *          the _metadata value.
   */
  public void set(final String name, final String value) {
    remove(name);
    add(name, value);
  }

  /**
   * Remove a meta data and all its associated values.
   * 
   * @param name
   *          Name of the meta data element.
   */
  public void remove(final String name) {
    _metadata.remove(getNormalizedName(name));
  }

  /**
   * Returns the number of meta data names in this meta data.
   * 
   * @return size
   */
  public int size() {
    return _metadata.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object o) {

    if (o == null) {
      return false;
    }

    Metadata other = null;
    try {
      other = (Metadata) o;
    } catch (final ClassCastException cce) {
      return false;
    }

    if (other.size() != size()) {
      return false;
    }

    final String[] names = names();
    for (int i = 0; i < names.length; i++) {
      final String[] otherValues = other.getValues(names[i]);
      final String[] thisValues = getValues(names[i]);
      if (otherValues.length != thisValues.length) {
        return false;
      }
      for (int j = 0; j < otherValues.length; j++) {
        if (!otherValues[j].equals(thisValues[j])) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return names().hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuffer buf = new StringBuffer();
    final String[] names = names();
    for (int i = 0; i < names.length; i++) {
      final String[] values = getValues(names[i]);
      for (int j = 0; j < values.length; j++) {
        buf.append(names[i]).append("=").append(values[j]).append(" ");
      }
    }
    return buf.toString();
  }

  /**
   * Returns ArrayList representation of the meta data for further indexing.
   * 
   * @return ArrayList
   */
  public List<String> toArrayList() {
    final List<String> metadataArray = new ArrayList<String>();
    final String[] names = names();
    for (int i = 0; i < names.length; i++) {
      final String[] values = getValues(names[i]);
      for (int j = 0; j < values.length; j++) {
        metadataArray.add(names[i] + ":" + values[j]);
      }
    }
    return metadataArray;
  }

  /**
   * Get the normalized name of meta data attribute name. This method tries to find a well-known meta data name (one of
   * the meta data names defined in this class) that matches the specified name. The matching is error tolerent. For
   * instance,
   * <ul>
   * <li>content-type gives Content-Type</li>
   * <li>CoNtEntType gives Content-Type</li>
   * <li>ConTnTtYpe gives Content-Type</li>
   * </ul>
   * If no matching with a well-known meta data name is found, then the original name is returned.
   * 
   * @param name
   *          Meta data attribute name.
   * @return String
   */
  public static String getNormalizedName(final String name) {
    final String searched = normalize(name);
    String value = s_namesIdx.get(searched);

    if ((value == null) && (s_normalized != null)) {
      final int threshold = searched.length() / THRESHOLD_FACTOR;
      for (int i = 0; i < s_normalized.length && value == null; i++) {
        if (StringUtils.getLevenshteinDistance(searched, s_normalized[i]) < threshold) {
          value = s_namesIdx.get(s_normalized[i]);
        }
      }
    }
    if (value != null) {
      return value;
    } else {
      return name;
    }
  }

  /**
   * Normalize.
   * 
   * @param str
   *          the string to normalize
   * 
   * @return the string
   */
  private static String normalize(final String str) {
    char c;
    final StringBuffer buf = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      c = str.charAt(i);
      if (Character.isLetter(c)) {
        buf.append(Character.toLowerCase(c));
      }
    }
    return buf.toString();
  }

}
