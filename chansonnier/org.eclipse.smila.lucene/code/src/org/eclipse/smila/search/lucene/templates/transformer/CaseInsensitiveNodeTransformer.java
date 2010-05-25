/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.templates.transformer;



import java.util.StringTokenizer;

import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTermContent;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.advsearch.AdvSearchException;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.search.DField;

/**
 * @author August Georg Schmidt (BROX)
 */
public class CaseInsensitiveNodeTransformer extends SimpleNodeTransformer {

  /**
   * @throws NodeTransformerException
   *           Unable to create node transformer.
   */
  public CaseInsensitiveNodeTransformer() throws NodeTransformerException {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.templates.NodeTransformer#transformNode(org.eclipse.smila.search.utils.search.DField)
   */
  @Override
  public ITermContent transformNode(DField dField) throws NodeTransformerException {
    try {
      if (dField instanceof org.eclipse.smila.search.utils.search.DTextField) {
        final org.eclipse.smila.search.utils.search.DTextField tf =
          (org.eclipse.smila.search.utils.search.DTextField) ((org.eclipse.smila.search.utils.search.DTextField) dField)
            .clone();
        final DTextFieldParameter tfp = (DTextFieldParameter) tf.getParameter();
        if (tfp.getOperator() != DTextFieldParameter.DOperator.PHRASE) {
          final String fieldText = tf.getText().trim();
          final StringTokenizer st = new StringTokenizer(fieldText, " ");
          final int tokenCount = st.countTokens();
          if (tokenCount == 1) {
            return convertTerm(tf);
          } else {
            final DOPN op = new DOPN();
            if (tfp.getOperator() == DTextFieldParameter.DOperator.AND) {
              op.setOperation("AND");
            } else if (tfp.getOperator() == DTextFieldParameter.DOperator.OR) {
              op.setOperation("OR");
            } else {
              op.setOperation("WMEAN");
            }
            for (int i = 0; i < tokenCount; i++) {
              tf.setText(st.nextToken());
              op.addTerm(new DTerm(convertTerm(tf)));
            }
            return op;
          }
        } else {
          return super.transformNode(dField);
        }
      } else {
        return super.transformNode(dField);
      }
    } catch (final AdvSearchException e) {
      throw new NodeTransformerException("unable to transform node [" + e.getMessage() + "]");
    }
  }

  private DTermContent convertTerm(org.eclipse.smila.search.utils.search.DTextField tf) throws NodeTransformerException,
    AdvSearchException {
    final String fieldText = tf.getText().trim();
    final String[] words = convertWord(fieldText);
    if (words.length == 0) {
      return (DTermContent) super.transformNode(tf);
    }
    tf.setText(words[0]);
    if (words.length == 1) {
      return (DTermContent) super.transformNode(tf);
    } else {
      final DOPN op = new DOPN();
      op.setOperation("OR");
      op.addTerm(new DTerm(super.transformNode(tf)));
      for (int i = 1; i < words.length; i++) {
        final org.eclipse.smila.search.utils.search.DTextField f =
          (org.eclipse.smila.search.utils.search.DTextField) tf.clone();
        f.setText(words[i]);
        op.addTerm(new DTerm(super.transformNode(f)));
      }
      return op;
    }
  }

  private String[] convertWord(String word) {
    if (word == null || word.length() == 0) {
      return new String[0];
    }
    String lowerCase = word.toLowerCase();
    String capitalCase = String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    int arrayCnt = 1;
    if (!word.equals(lowerCase)) {
      arrayCnt++;
    } else {
      lowerCase = null;
    }
    if (!word.equals(capitalCase)) {
      arrayCnt++;
    } else {
      capitalCase = null;
    }
    final String[] result = new String[arrayCnt];
    arrayCnt--;
    result[arrayCnt--] = word;
    if (lowerCase != null) {
      result[arrayCnt--] = lowerCase;
    }
    if (capitalCase != null) {
      result[arrayCnt--] = capitalCase;
    }
    return result;
  }
}
