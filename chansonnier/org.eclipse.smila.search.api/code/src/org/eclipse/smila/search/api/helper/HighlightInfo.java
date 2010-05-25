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

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * wrapper for highlight information annotations.
 * 
 * @author jschumacher
 * 
 */
public class HighlightInfo {
  /**
   * name of annotated attribute, or null, if this is a highlight info of the complete record.
   */
  private String _attributeName;

  /**
   * the highlight annotation.
   */
  private Annotation _annotation;

  /**
   * wrapper for access to the list of position annotations.
   */
  private AnnotationListAccessor _positionHelper;

  /**
   * create instance from data.
   * 
   * @param attributeName
   *          annotated attribute (can be null for record annotations).
   * @param annotation
   *          the highlight annotations.
   */
  public HighlightInfo(String attributeName, Annotation annotation) {
    _attributeName = attributeName;
    _annotation = annotation;
    _positionHelper =
      new AnnotationListAccessor(attributeName, _annotation.getAnnotations(SearchAnnotations.HIGHLIGHT_POSITIONS));
  }

  /**
   * 
   * @return name of annotated attribute.
   */
  public String getAttributeName() {
    return _attributeName;
  }

  /**
   * access named value of highlight annotation.
   * 
   * @param name
   *          name of property
   * @return value of property.
   */
  public String getProperty(String name) {
    return _annotation.getNamedValue(name);
  }

  /**
   * access text property of highlight annotation.
   * 
   * @return text property.
   */
  public String getText() {
    return _annotation.getNamedValue(SearchAnnotations.HIGHLIGHT_TEXT);
  }

  /**
   * check if the text property contains already highlighted text, or the plain text to be highlighted by the client
   * using the info from the position list.
   * 
   * @return true if text is highlighted already.
   */
  public boolean isHighlighted() {
    return positionLength() == 0;
  }

  /**
   * 
   * @return number of highlight positions.
   */
  public int positionLength() {
    return _positionHelper.length();
  }

  /**
   * get start character position property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @return start character position of highlight position, or null for invalid indexes
   */
  public Integer getStartPos(int index) {
    return _positionHelper.getIntProperty(index, SearchAnnotations.HIGHLIGHT_POS_START);
  }

  /**
   * get end character position property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @return start character position of highlight position, or null for invalid indexes
   */
  public Integer getEndPos(int index) {
    return _positionHelper.getIntProperty(index, SearchAnnotations.HIGHLIGHT_POS_END);
  }

  /**
   * get quality property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @return quality of highlight position, or null for invalid indexes
   */
  public Double getQuality(int index) {
    return _positionHelper.getFloatProperty(index, SearchAnnotations.HIGHLIGHT_POS_QUALITY);
  }

  /**
   * get group property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @return group of highlight position, or null for invalid indexes
   */
  public Integer getQueryGroup(int index) {
    return _positionHelper.getIntProperty(index, SearchAnnotations.HIGHLIGHT_POS_GROUP);
  }

  /**
   * get method property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @return method of highlight position, or null for invalid indexes
   */
  public String getMethod(int index) {
    return _positionHelper.getProperty(index, SearchAnnotations.HIGHLIGHT_POS_METHOD);
  }

  /**
   * get named property of n'th highlight position.
   * 
   * @param index
   *          position in term list.
   * @param name
   *          name of property.
   * @return named property or null for invalid indexes
   */
  public String getProperty(int index, String name) {
    return _positionHelper.getProperty(index, name);
  }

  /**
   * access to original object.
   * 
   * @return the underlying highlight info annotation.
   */
  public Annotation getSource() {
    return _annotation;
  }
}
