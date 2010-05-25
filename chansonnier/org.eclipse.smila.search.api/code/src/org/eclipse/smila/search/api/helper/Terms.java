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
import java.util.List;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * wrapper for annotations describing term lists (e.g. textminer token lists).
 * 
 * @author jschumacher
 * 
 */
public class Terms extends AnnotationListAccessor {
  /**
   * create instance from data.
   * 
   * @param attributeName
   *          annotated attribute
   * @param annotations
   *          terms annotations.
   */
  public Terms(String attributeName, List<Annotation> annotations) {
    super(attributeName, annotations);
  }

  /**
   * create instance from data.
   * 
   * @param attributeName
   *          annotated attribute
   * @param annotations
   *          terms annotations.
   */
  public Terms(String attributeName, Collection<Annotation> annotations) {
    super(attributeName, annotations);
  }

  /**
   * get concept property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return concept of term, or null for invalid indexes
   */
  public String getConcept(int index) {
    return getProperty(index, SearchAnnotations.TERM_CONCEPT);
  }

  /**
   * get token property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return token of term, or null for invalid indexes
   */
  public String getToken(int index) {
    return getProperty(index, SearchAnnotations.TERM_TOKEN);
  }

  /**
   * get target attribute property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return target attribute of term, or null for invalid indexes
   */
  public String getTargetAttributeName(int index) {
    return getProperty(index, SearchAnnotations.TERM_TARGET);
  }

  /**
   * get start character position property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return start character position of term, or null for invalid indexes
   */
  public Integer getStartCharPos(int index) {
    return getIntProperty(index, SearchAnnotations.TERM_START);
  }

  /**
   * get end character position property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return end character position of term, or null for invalid indexes
   */
  public Integer getEndCharPos(int index) {
    return getIntProperty(index, SearchAnnotations.TERM_END);
  }

  /**
   * get start word position property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return start word position of term, or null for invalid indexes
   */
  public Integer getStartWordPos(int index) {
    return getIntProperty(index, SearchAnnotations.TERM_STARTWORD);
  }

  /**
   * get end word position property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return end word position of term, or null for invalid indexes
   */
  public Integer getEndWordPos(int index) {
    return getIntProperty(index, SearchAnnotations.TERM_ENDWORD);
  }

  /**
   * get part-of-speech property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return part-of-speech descriptor of term, or null for invalid indexes
   */
  public String getPartOfSpeech(int index) {
    return getProperty(index, SearchAnnotations.TERM_PARTOFSPEECH);
  }

  /**
   * get method (of recognition, analysis, extraction) property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return method of term, or null for invalid indexes
   */
  public String getMethod(int index) {
    return getProperty(index, SearchAnnotations.TERM_METHOD);
  }

  /**
   * get quality property of n'th term.
   * 
   * @param index
   *          position in term list.
   * @return quality of term, or null for invalid indexes
   */
  public Double getQuality(int index) {
    return getFloatProperty(index, SearchAnnotations.TERM_QUALITY);
  }
}
