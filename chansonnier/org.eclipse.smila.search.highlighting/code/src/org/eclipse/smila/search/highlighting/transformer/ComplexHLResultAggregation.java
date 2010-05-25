/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.highlighting.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.eclipse.smila.search.utils.param.ParameterSet;
import org.eclipse.smila.search.utils.searchresult.DHighLighted;

/**
 * ComplexHLResultAggregation.
 */
public class ComplexHLResultAggregation extends HighlightingTransformer {

  /**
   * Enum SortAlgorithm.
   */
  public static enum SortAlgorithm {
    /**
     * Sort by Occurrence.
     */
    Occurrence,
    /**
     * Sort by Score.
     */
    Score
  }

  /**
   * Enum TextHandling.
   */
  public static enum TextHandling {
    /**
     * Return the full text.
     */
    ReturnFullText,
    /**
     * Return no text.
     */
    ReturnNoText,
    /**
     * Return text snipplet.
     */
    ReturnSnipplet
  }


  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.highlighting.transformer.HighlightingTransformer#transform(java.util.ArrayList)
   */
  @Override
  public ArrayList<Object> transform(final ArrayList<Object> data, final ParameterSet parameterSet)
    throws HighlightingTransformerException {

    if (data == null) {
      throw new NullPointerException("data");
    }
    
    
    // get parameters
    final int maxPrecedingChars = parameterSet.getIntegerParameter(PARAM_MAX_PRECEDING_CHARACTERS);
    final int maxSucceedingChars = parameterSet.getIntegerParameter(PARAM_MAX_SUCCEEDING_CHARACTERS);
    final ComplexHLResultAggregation.SortAlgorithm sortAlgorithm =
      ComplexHLResultAggregation.SortAlgorithm.valueOf(parameterSet.getStringParameter(PARAM_SORT_ALGORITHM));
    final Integer maxHLElements = parameterSet.getIntegerParameter(PARAM_MAX_HL_ELEMENTS);
    final Integer maxLength = parameterSet.getIntegerParameter(PARAM_MAX_LENGTH);
    final ComplexHLResultAggregation.TextHandling textHandling =
      ComplexHLResultAggregation.TextHandling.valueOf(parameterSet.getStringParameter(PARAM_TEXT_HANDLING));
    final String precedingCharacters = parameterSet.getStringParameter(PARAM_PRECEDING_CHARACTERS);
    final String succeedingCharacters = parameterSet.getStringParameter(PARAM_SUCCEEDING_CHARACTERS);
    final boolean elFilter = parameterSet.getBooleanParameter(PARAM_HL_ELEMENT_FILTER);

    final ArrayList<ComplexHLResultAggregation.HlTriple> hlTriples =
      new ArrayList<ComplexHLResultAggregation.HlTriple>();

    // build hl triples
    StringBuffer prefix = new StringBuffer();
    StringBuffer suffix = new StringBuffer();
    for (int i = 0; i < data.size();) {

      final ComplexHLResultAggregation.HlTriple hlTriple = new ComplexHLResultAggregation.HlTriple();

      while (!(data.get(i) instanceof DHighLighted)) {

        prefix = prefix.append((String) data.get(i));

        if (i < (data.size() - 1)) {
          i++;
        } else {
          break;
        }
      }

      hlTriple._prefix = prefix.toString();
      prefix = new StringBuffer();
      if (data.get(i) instanceof DHighLighted) {
        hlTriple._highLighted = (DHighLighted) data.get(i);
        if (i < data.size()) {
          i++;
        }
      }

      if (i < data.size()) {
        while (!(data.get(i) instanceof DHighLighted)) {

          suffix = suffix.append((String) data.get(i));

          if (i < (data.size() - 1)) {
            i++;
          } else {
            break;
          }
        }
      }

      hlTriple._suffix = suffix.toString();
      suffix = new StringBuffer();

      hlTriples.add(hlTriple);
      prefix = prefix.append(hlTriple._suffix);

      if (i == (data.size() - 1)) {
        i++;
      }
    }

    // execute sorting
    HlTriple[] hlTriplesArray = new HlTriple[0];
    hlTriplesArray = hlTriples.toArray(hlTriplesArray);
    if (sortAlgorithm == SortAlgorithm.Score) {
      Arrays.sort(hlTriplesArray);
    }

    // execute double element filter
    if (elFilter) {
      final Hashtable<String, ComplexHLResultAggregation.HlTriple> elements =
        new Hashtable<String, ComplexHLResultAggregation.HlTriple>();
      final ArrayList<ComplexHLResultAggregation.HlTriple> newElements =
        new ArrayList<ComplexHLResultAggregation.HlTriple>();
      for (int i = 0; i < hlTriplesArray.length; i++) {
        if (hlTriplesArray[i]._highLighted == null) {
          newElements.add(hlTriplesArray[i]);
        } else {
          if (!elements.containsKey(hlTriplesArray[i]._highLighted.getText())) {
            elements.put(hlTriplesArray[i]._highLighted.getText(), hlTriplesArray[i]);
            newElements.add(hlTriplesArray[i]);
          }
        }
      }
      hlTriplesArray = new HlTriple[newElements.size()];
      hlTriplesArray = newElements.toArray(hlTriplesArray);
    }

    // execute max hl element filter
    if (maxHLElements != null) {
      final int maxElements = maxHLElements.intValue();
      if (hlTriplesArray.length > maxElements) {
        final HlTriple[] temp = new HlTriple[maxElements];

        for (int i = 0; i < maxElements; i++) {
          temp[i] = hlTriplesArray[i];
        }

        hlTriplesArray = temp;
      }
    }

    final ArrayList<Object> newData = new ArrayList<Object>();
    int charCount = 0;
    // execute prefix and suffix truncation and build result vector
    if ((hlTriplesArray.length == 1) && (hlTriplesArray[0]._highLighted == null)
      && (textHandling == TextHandling.ReturnFullText)) {
      // no text for highlighting is available... return full text extract if configured
      final String s = (String) data.get(0);
      final int length = (s.length() < maxLength.intValue() ? s.length() : maxLength.intValue());
      String hlExtract = s.substring(0, length);

      if (s.length() > maxLength.intValue()) {
        hlExtract = hlExtract + succeedingCharacters;
      }
      newData.add(hlExtract);
    } else if ((hlTriplesArray.length == 1) && (hlTriplesArray[0]._highLighted == null)
      && (textHandling == TextHandling.ReturnNoText)) {
      ; // do nothing
    } else {
      for (int i = 0; i < hlTriplesArray.length; i++) {
        if (hlTriplesArray[i]._prefix != null) {
          String s =
            (hlTriplesArray[i]._prefix.length() <= maxPrecedingChars ? hlTriplesArray[i]._prefix
              : hlTriplesArray[i]._prefix.substring(hlTriplesArray[i]._prefix.length() - maxPrecedingChars));
          charCount += s.length();

          if (!("".equals(s) || " ".equals(s))) {
            if (precedingCharacters != null) {
              s = precedingCharacters + s;
            }
          }

          hlTriplesArray[i]._prefix = s;
          newData.add(s);
        }
        if (hlTriplesArray[i]._highLighted != null) {
          charCount += hlTriplesArray[i]._highLighted.getText().length();
          newData.add(hlTriplesArray[i]._highLighted);
        }
        if (hlTriplesArray[i]._suffix != null) {
          String s =
            (hlTriplesArray[i]._suffix.length() <= maxSucceedingChars ? hlTriplesArray[i]._suffix
              : hlTriplesArray[i]._suffix.substring(0, maxSucceedingChars));
          charCount += s.length();

          if (!("".equals(s) || " ".equals(s))) {
            if (succeedingCharacters != null) {
              s = s + succeedingCharacters;
            }
          }

          hlTriplesArray[i]._suffix = s;
          newData.add(s);
        }

        if (maxLength != null) {
          if (charCount >= maxLength.intValue()) {
            break;
          }
        }
      }
    }

    return newData;
  }

  /**
   * Inner class HlTriple.
   */
  public static class HlTriple implements Comparable<HlTriple> {

    /**
     * The prefix text.
     */
    private String _prefix;

    /**
     * The DHighLighted object.
     */
    private DHighLighted _highLighted;

    /**
     * The suffix text.
     */
    private String _suffix;

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final HlTriple obj) {
      if (obj == null) {
        throw new NullPointerException();
      }

      if ((_highLighted == null) && (obj._highLighted == null)) {
        return 0;
      }

      if ((_highLighted == null) && (obj._highLighted != null)) {
        return 1;
      }

      if ((_highLighted != null) && (obj._highLighted == null)) {
        return -1;
      }

      if (_highLighted.getScore() > obj._highLighted.getScore()) {
        return -1;
      }

      if (_highLighted.getScore() < obj._highLighted.getScore()) {
        return 1;
      }

      return 0;
    }

    /**
     * @return the highLighted
     */
    public DHighLighted getHighLighted() {
      return _highLighted;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
      return _prefix;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
      return _suffix;
    }

    /**
     * @param highLighted
     *          the highLighted to set
     */
    public void setHighLighted(final DHighLighted highLighted) {
      _highLighted = highLighted;
    }

    /**
     * @param prefix
     *          the prefix to set
     */
    public void setPrefix(final String prefix) {
      _prefix = prefix;
    }

    /**
     * @param suffix
     *          the suffix to set
     */
    public void setSuffix(final String suffix) {
      _suffix = suffix;
    }
  }
}
