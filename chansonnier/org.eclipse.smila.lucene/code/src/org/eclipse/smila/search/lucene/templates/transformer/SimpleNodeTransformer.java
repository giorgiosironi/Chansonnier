/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.templates.transformer;

import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.TokenStream;
import org.eclipse.smila.search.lucene.index.IndexConnection;
import org.eclipse.smila.search.lucene.messages.advsearch.DDateField;
import org.eclipse.smila.search.lucene.messages.advsearch.DNumField;
import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTextField;
import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexField;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.search.DField;


public class SimpleNodeTransformer extends NodeTransformer {
  public SimpleNodeTransformer() throws NodeTransformerException {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.templates.NodeTransformer#transformNode(org.eclipse.smila.search.utils.search.DField)
   */
  @Override
  public ITermContent transformNode(DField dField) throws NodeTransformerException {
    if (dField instanceof org.eclipse.smila.search.utils.search.DTextField) {
      final org.eclipse.smila.search.utils.search.DTextField dTextField =
        (org.eclipse.smila.search.utils.search.DTextField) dField;

      final DTextFieldParameter tfp = (DTextFieldParameter) dTextField.getParameter();
      final StringTokenizer st = new StringTokenizer(dTextField.getText().trim(), " ", false);

      final DTextFieldParameter.DOperator operator = tfp.getOperator();
      final DTextFieldParameter.DTolerance tolerance = tfp.getTolerance();

      boolean fuzzy = false;
      boolean parseWildcards = false;
      int slop = 0;
      if (tolerance == DTextFieldParameter.DTolerance.TOLERANT) {
        fuzzy = true;
        parseWildcards = true;

        final IndexConnection ic = (IndexConnection) getIndexConnection();
        final DIndexField indexField =
          (DIndexField) ic.getIndex().getIndexStructure().getField(dField.getFieldNo());

        // phrase
        if (indexField.getTokenize() && (operator == DTextFieldParameter.DOperator.PHRASE)) {
          fuzzy = false;
          parseWildcards = false;
        }

        final TokenStream ts =
          ic.getAnalyzer().tokenStream(indexField.getName(), new StringReader(dTextField.getText().trim()));
        try {
          while (ts.next() != null) {
            slop++;
          }
        } catch (final IOException e) {
        }
        slop = (slop - 1) * 2;
      }
      if ((st.countTokens() > 1) && (operator != DTextFieldParameter.DOperator.PHRASE)) {
        final DTextField[] textFields = new DTextField[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
          textFields[i] = new DTextField(dTextField.getFieldNo(), st.nextToken(), fuzzy, parseWildcards, slop);
        }
        final DOPN op = new DOPN();

        if (operator == DTextFieldParameter.DOperator.AND) {
          op.setOperation("AND");
        } else if (operator == DTextFieldParameter.DOperator.OR) {
          op.setOperation("OR");
        } else {
          op.setOperation("WMEAN");
        }
        for (int i = 0; i < textFields.length; i++) {
          op.addTerm(new DTerm(textFields[i]));
        }
        return op;
      } else {
        return new DTextField(dTextField.getFieldNo(), dTextField.getText(), fuzzy, parseWildcards, slop);
      }
    } else if (dField instanceof org.eclipse.smila.search.utils.search.DNumberField) {
      final org.eclipse.smila.search.utils.search.DNumberField dNumberField =
        (org.eclipse.smila.search.utils.search.DNumberField) dField;
      return new DNumField(dNumberField.getFieldNo(), dNumberField.getMin().longValue(), dNumberField.getMax()
        .longValue());
    } else if (dField instanceof org.eclipse.smila.search.utils.search.DDateField) {
      final org.eclipse.smila.search.utils.search.DDateField dDateField =
        (org.eclipse.smila.search.utils.search.DDateField) dField;
      return new DDateField(dDateField.getFieldNo(), dDateField.getDateMin().getTime(), dDateField.getDateMax()
        .getTime());
    } else {
      throw new NodeTransformerException("unknown node type [" + dField.getClass().getName() + "]");
    }
  }

}
