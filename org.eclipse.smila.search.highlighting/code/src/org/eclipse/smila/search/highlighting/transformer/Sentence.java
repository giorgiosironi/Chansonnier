/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.highlighting.transformer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.smila.search.highlighting.transformer.ComplexHLResultAggregation.HlTriple;
import org.eclipse.smila.search.highlighting.transformer.ComplexHLResultAggregation.SortAlgorithm;
import org.eclipse.smila.search.highlighting.transformer.ComplexHLResultAggregation.TextHandling;
import org.eclipse.smila.search.utils.param.ParameterSet;
import org.eclipse.smila.search.utils.searchresult.DHighLighted;

/**
 * The sentence highlighting transformer is able to highlight information taking sentence boundaries into credit.
 * 
 * @author brox IT-Solutions GmbH
 */
public class Sentence extends HighlightingTransformer {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.highlighting.transformer.HighlightingTransformer#transform(java.util.ArrayList)
   */
  @Override
  public ArrayList<Object> transform(ArrayList<Object> data, final ParameterSet parameterSet)
    throws HighlightingTransformerException {

    if (data == null) {
      throw new HighlightingTransformerException("no data specified for highlighting transformation");
    }

    // get parameters
    final Integer maxSucceedingChars = parameterSet.getIntegerParameter(PARAM_MAX_SUCCEEDING_CHARACTERS);
    final ComplexHLResultAggregation.SortAlgorithm sortAlgorithm =
      ComplexHLResultAggregation.SortAlgorithm.valueOf(parameterSet.getStringParameter(PARAM_SORT_ALGORITHM));
    final Integer maxHLElements = parameterSet.getIntegerParameter(PARAM_MAX_HL_ELEMENTS);
    final Integer maxLength = parameterSet.getIntegerParameter(PARAM_MAX_LENGTH);
    final ComplexHLResultAggregation.TextHandling textHandling =
      ComplexHLResultAggregation.TextHandling.valueOf(parameterSet.getStringParameter(PARAM_TEXT_HANDLING));
    final String succeedingCharacters = parameterSet.getStringParameter(PARAM_SUCCEEDING_CHARACTERS);

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

      hlTriple.setPrefix(prefix.toString());
      prefix = new StringBuffer();
      if (data.get(i) instanceof DHighLighted) {
        hlTriple.setHighLighted((DHighLighted) data.get(i));
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

      hlTriple.setSuffix(suffix.toString());
      suffix = new StringBuffer();

      hlTriples.add(hlTriple);
      prefix = prefix.append(hlTriple.getSuffix());

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
    if ((hlTriplesArray.length == 1) && (hlTriplesArray[0].getHighLighted() == null)
      && (textHandling == TextHandling.ReturnFullText)) {
      // no text for highlighting is available... return full text extract if configured
      final String s = (String) data.get(0);
      final int length = (s.length() < maxLength.intValue() ? s.length() : maxLength.intValue());
      String hlExtract = s.substring(0, length);

      if (s.length() > maxLength.intValue()) {
        hlExtract = hlExtract + succeedingCharacters;
      }
      newData.add(hlExtract);
    } else if ((hlTriplesArray.length == 1) && (hlTriplesArray[0].getHighLighted() == null)
      && (textHandling == TextHandling.ReturnNoText)) {
      ; // do nothing
    } else {
      for (int i = 0; i < hlTriplesArray.length; i++) {
        if (hlTriplesArray[i].getPrefix() != null) {
          final String s = getLastSentence(hlTriplesArray[i].getPrefix());

          charCount += s.length();

          hlTriplesArray[i].setPrefix(s);
          newData.add(s);
        }
        if (hlTriplesArray[i].getHighLighted() != null) {
          charCount += hlTriplesArray[i].getHighLighted().getText().length();
          newData.add(hlTriplesArray[i].getHighLighted());
        }
        if (hlTriplesArray[i].getSuffix() != null) {
          String s = getFirstSentence(hlTriplesArray[i].getSuffix());

          // just text up to max length
          final boolean appendSuccedingCharacters = !(s.length() <= maxSucceedingChars);

          int charPosition = 0;
          if (s.length() > maxSucceedingChars) {
            for (int j = maxSucceedingChars; j > 0; j--) {
              if (s.charAt(j) == ' ') {
                charPosition = j;
                break;
              }
            }
          }

          s = (s.length() <= maxSucceedingChars ? s : s.substring(0, charPosition));

          charCount += s.length();

          if (!("".equals(s) || " ".equals(s))) {
            if (appendSuccedingCharacters) {
              s = s + succeedingCharacters;
            }
          }

          hlTriplesArray[i].setSuffix(s);
          newData.add(s);
        }

        if (maxLength != null) {
          if (charCount >= maxLength.intValue()) {
            break;
          }
        }
      }
    }

    final ArrayList<Object> newData2 = new ArrayList<Object>();
    for (int i = 0; i < newData.size(); i++) {
      if (newData.get(i) instanceof String) {
        if (!((newData2.size() > 0) && (newData2.get(newData2.size() - 1).equals(newData.get(i))))) {
          if (!newData.get(i).equals("")) {
            newData2.add(newData.get(i));
          }
        }
      } else {
        newData2.add(newData.get(i));
      }
    }

    return newData2;
  }

  /**
   * Returns the first sentence from the given text.
   * 
   * @param text
   *          the text
   * @return a String containing the first sentence
   */
  private String getFirstSentence(String text) {
    final BreakIterator bi = BreakIterator.getSentenceInstance();
    bi.setText(text);

    if ("".equals(text)) {
      return text;
    }

    final int start = bi.first();
    final int end = bi.next();
    final String sentence = text.substring(start, end);

    return sentence;
  }

  /**
   * Returns the last sentence from the given text.
   * 
   * @param text
   *          the text
   * @return a String containing the last sentence
   */
  private String getLastSentence(String text) {
    final BreakIterator bi = BreakIterator.getSentenceInstance();

    text = text + " a";

    bi.setText(text);

    String sentence = text;
    int start = bi.first();
    for (int end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next()) {
      sentence = text.substring(start, end);
    }

    final int length = sentence.length();
    if (length >= 2) {
      return sentence.substring(0, length - 2);
    } else {
      return "";
    }
  }
}
