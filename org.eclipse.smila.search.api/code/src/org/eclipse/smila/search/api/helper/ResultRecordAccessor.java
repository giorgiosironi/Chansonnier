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

import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * wrapper for search result records. Provides easier access to relevance and highlight annotations.
 * 
 * @author jschumacher
 * 
 */
public class ResultRecordAccessor extends RecordAccessor {

  /**
   * create instance.
   * 
   * @param record
   *          result record.
   */
  public ResultRecordAccessor(Record record) {
    super(record);
  }

  /**
   * 
   * @return a relevance value attached to the record. Null, if no such annotation exists or its value cannot be parsed
   *         as an double.
   */
  public Double getRelevance() {
    return getResultAnnotationFloatValue(SearchAnnotations.RELEVANCE);
  }

  /**
   * @return top-level highlight.
   */
  public HighlightInfo getHighlightInfo() {
    if (hasAnnotation(SearchAnnotations.HIGHLIGHT)) {
      return new HighlightInfo(null, getAnnotation(SearchAnnotations.HIGHLIGHT));
    }
    return null;
  }

  /**
   * 
   * @param attributeName
   *          name of attribute
   * @return attribute hightlight
   */
  public HighlightInfo getHighlightInfo(String attributeName) {
    if (hasAnnotation(attributeName, SearchAnnotations.HIGHLIGHT)) {
      return new HighlightInfo(attributeName, getAnnotation(attributeName, SearchAnnotations.HIGHLIGHT));
    }
    return null;
  }
}
