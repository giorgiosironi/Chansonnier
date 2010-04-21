/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.search.api.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotation;

/**
 * base class for wrappers of list of attribute annotations (like terms or facets). Provides index based methods to the
 * elements in the list and their values and subannotations. Includes a few type conversion helper methods, too.
 * 
 * @author jschumacher
 * 
 */
public class AnnotationListAccessor {

  /**
   * name of attribute this annotation list belongs too.
   */
  private String _attributeName;

  /**
   * the source list of annotations.
   */
  private List<Annotation> _annotations;

  /**
   * create instance from given data.
   * 
   * @param attributeName
   *          name of annotated attribute.
   * @param annotations
   *          list of annotations.
   */
  public AnnotationListAccessor(String attributeName, List<Annotation> annotations) {
    super();
    _attributeName = attributeName;
    _annotations = annotations;
  }

  /**
   * create instance from given data. the collection is copied to a list (based on iteration order) if it is not an
   * instance of {@link List} already.
   * 
   * @param attributeName
   *          name of annotated attribute.
   * @param annotations
   *          collection of annotations.
   */
  public AnnotationListAccessor(String attributeName, Collection<Annotation> annotations) {
    super();
    _attributeName = attributeName;
    _annotations = RecordAccessor.unmodifiableList(annotations);
  }

  /**
   * 
   * @return name of annotated attribute. can be null, if irrelevant for this instance.
   */
  public String getAttributeName() {
    return _attributeName;
  }

  /**
   * 
   * @return size of annotation list.
   */
  public int length() {
    if (_annotations == null) {
      return 0;
    }
    return _annotations.size();
  }

  /**
   * get the named subannotation of the n'th annotation from the list. If the index is invalid, null is returned.
   * 
   * @param index
   *          position in list
   * @param name
   *          name of subannotation.
   * @return subannotation object, or null for invalid names or indexes.
   */
  public Annotation getAnnotation(int index, String name) {
    if (index >= 0 && index < length()) {
      return _annotations.get(index).getAnnotation(name);
    }
    return null;
  }

  /**
   * get list of named subannotations of the n'th annotation from the list. If the index is invalid, an empty list is
   * returned. The returned list cannot be modified.
   * 
   * @param index
   *          position in list
   * @param name
   *          name of subannotation.
   * @return list of subannotations, or an empty list for invalid names or indexes.
   */
  @SuppressWarnings("unchecked")
  public List<Annotation> getAnnotations(int index, String name) {
    if (index >= 0 && index < length()) {
      final Collection<Annotation> subAnnotations = _annotations.get(index).getAnnotations(name);
      return RecordAccessor.unmodifiableList(subAnnotations);
    }
    return Collections.EMPTY_LIST;
  }

  /**
   * access named value of n'th annotation in list.
   * 
   * @param index
   *          position in list
   * @param name
   *          name of named value.
   * @return named value, or null for invalid names or indexes.
   */
  public String getProperty(int index, String name) {
    if (index >= 0 && index < length()) {
      return _annotations.get(index).getNamedValue(name);
    }
    return null;
  }

  /**
   * access named value of n'th annotation in list and convert it to an integer.
   * 
   * @param index
   *          position in list
   * @param name
   *          name of named value.
   * @return Integer parsed from named value, or null for invalid names, indexes or values not in integer format.
   */
  public Integer getIntProperty(int index, String name) {
    final String value = getProperty(index, name);
    if (value != null) {
      try {
        return Integer.valueOf(value);
      } catch (Exception ex) {
        ex = null;
      }
    }
    return null;
  }

  /**
   * access named value of n'th annotation in list and convert it to an floating point value.
   * 
   * @param index
   *          position in list
   * @param name
   *          name of named value.
   * @return Double parsed from named value, or null for invalid names, indexes or values not in double format.
   */
  public Double getFloatProperty(int index, String name) {
    final String value = getProperty(index, name);
    if (value != null) {
      try {
        return Double.valueOf(value);
      } catch (Exception ex) {
        ex = null;
      }
    }
    return null;
  }

  /**
   * 
   * @return underlying list of annotations.
   */
  public List<Annotation> getSource() {
    return _annotations;
  }

}
