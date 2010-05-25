/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.processing.parameters.SearchAnnotations;

/**
 * Implementation of a lucene Formatter that creates highlighting annotations.
 */
public class AnnotationFormatter implements Formatter {

  /**
   * Original text.
   */
  private String _originalText;

  /**
   * A list of highlightPosition Annotations.
   */
  private ArrayList<Annotation> _highlightPositions;

  /**
   * The factory used to create the annotations.
   */
  private RecordFactory _factory;

  /**
   * Conversion constructor. 
   * @param factory the RecordFactory to use.
   */
  public AnnotationFormatter(RecordFactory factory) {
    this._factory = factory;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.lucene.search.highlight.Formatter#highlightTerm(String, TokenGroup)
   */
  public String highlightTerm(String originalText, TokenGroup tokenGroup) {

    for (int index = 0; index < tokenGroup.getNumTokens(); index++) {
      final Token token = tokenGroup.getToken(index);
      final float score = tokenGroup.getScore(index);
      final int startOffset = token.startOffset();
      final int endOffset = token.endOffset();

      if (score > 0) {
        final Annotation highlightPos = _factory.createAnnotation();
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_START, Integer.toString(startOffset));
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_END, Integer.toString(endOffset));
        highlightPos.setNamedValue(SearchAnnotations.HIGHLIGHT_POS_QUALITY, Integer.toString(mapScore(score)));
        _highlightPositions.add(highlightPos);
      }
    }
    return originalText;
  }

  /**
   * Reset this formatter.
   * 
   * @param originalText
   *          the original text
   */
  public void reset(String originalText) {
    _originalText = originalText;
    if (_highlightPositions != null) {
      _highlightPositions.clear();
    }
    _highlightPositions = new ArrayList<Annotation>();
    ;
  }

  /**
   * Returns the highlight positions.
   * 
   * @return a list of highligth position annotations
   */
  public List<Annotation> getHighlightPositions() {
    return _highlightPositions;
  }

  /**
   * Return the original text.
   * 
   * @return the original text
   */
  public String getOriginalText() {
    return _originalText;
  }

  /**
   * Compute the score.
   * 
   * @param score
   *          the original score
   * @return the computed score
   */
  private static int mapScore(float score) {
    return Math.round(score * 250);
  }
}
