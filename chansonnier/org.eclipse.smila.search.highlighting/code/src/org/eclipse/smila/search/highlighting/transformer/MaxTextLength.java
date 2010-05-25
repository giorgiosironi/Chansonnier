/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.highlighting.transformer;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.processing.parameters.SearchAnnotations;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.ParameterSet;
import org.eclipse.smila.search.utils.param.set.DParameterSet;

/**
 * @author brox IT-Solutions GmbH
 */
public class MaxTextLength extends HighlightingTransformer {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.highlighting.transformer.HighlightingTransformer#transform(Annotation, DParameterSet)
   */
  @Override
  public Annotation transform(Annotation highlight, final DParameterSet highlightConfig)
    throws HighlightingTransformerException, ParameterException {
    // get parameters
    final ParameterSet parameterSet = createParameterSet(highlightConfig);
    final String markupPrefix = parameterSet.getStringParameter(PARAM_MARKUP_PREFIX);
    final String markupSuffix = parameterSet.getStringParameter(PARAM_MARKUP_SUFFIX);
    final int maxLength = parameterSet.getIntegerParameter(PARAM_MAX_LENGTH);

    final String originalText = highlight.getNamedValue(SearchAnnotations.HIGHLIGHT_TEXT);
    final StringBuffer buffer = new StringBuffer();
    int pos = 0;
    final Collection<Annotation> highlightPositions =
      highlight.getAnnotations(SearchAnnotations.HIGHLIGHT_POSITIONS);
    for (Annotation highlightPos : highlightPositions) {
      final int start = Integer.valueOf(highlightPos.getNamedValue(SearchAnnotations.HIGHLIGHT_POS_START));
      final int end = Integer.valueOf(highlightPos.getNamedValue(SearchAnnotations.HIGHLIGHT_POS_END));

      if (start > maxLength) {
        buffer.append(originalText.substring(pos, maxLength));
        pos = maxLength;
        break;
      } else {
        buffer.append(originalText.substring(pos, start));
      }

      if (end > maxLength) {
        pos = maxLength;
        break;
      } else {
        buffer.append(markupPrefix);
        buffer.append(originalText.substring(start, end));
        buffer.append(markupSuffix);
      }
      pos = end;
    } // for
    if (pos < maxLength) {
      if (originalText.length() < maxLength) {
        buffer.append(originalText.substring(pos));
      } else {
        buffer.append(originalText.substring(pos, maxLength));
      }
    }
    highlight.setNamedValue(SearchAnnotations.HIGHLIGHT_TEXT, buffer.toString());

    // remove the position annotations
    highlight.removeAnnotations(SearchAnnotations.HIGHLIGHT_POSITIONS);

    return highlight;
  }

  /**
   * {@inheritDoc} Not implemented. Use transform(Annotation highlight, final DParameterSet highlightConfig) instead.
   * 
   * @see org.eclipse.smila.search.highlighting.transformer.HighlightingTransformer#transform(ArrayList, ParameterSet)
   */
  @Override
  public ArrayList<Object> transform(ArrayList<Object> data, final ParameterSet parameterSet)
    throws HighlightingTransformerException {
    return data;
  }
}
