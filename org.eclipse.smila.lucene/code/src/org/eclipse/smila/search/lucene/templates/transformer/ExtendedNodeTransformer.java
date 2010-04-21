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
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.eclipse.smila.search.lucene.index.IndexConnection;
import org.eclipse.smila.search.lucene.messages.advsearch.DDateField;
import org.eclipse.smila.search.lucene.messages.advsearch.DNumField;
import org.eclipse.smila.search.lucene.messages.advsearch.DOP1;
import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.messages.advsearch.DTextField;
import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexField;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.search.DField;


public class ExtendedNodeTransformer extends NodeTransformer {

  private static class TermInfo {

    private boolean _prohibited;

    private String _text;

    public TermInfo(boolean prohibited, String text) {
      _prohibited = prohibited;
      _text = text;
    }

    public boolean isProhibited() {
      return _prohibited;
    }

    public void setProhibited(boolean prohibited) {
      _prohibited = prohibited;
    }

    public String getText() {
      return _text;
    }

    public void setText(String text) {
      _text = text;
    }
  }

  public ExtendedNodeTransformer() throws NodeTransformerException {
    super();
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

      final boolean supportWildcardsForExactSearch =
        getParameterSet().getBooleanParameter("SupportWildcardsForExactSearch").booleanValue();

      final DTextFieldParameter tfp = (DTextFieldParameter) dTextField.getParameter();

      final List<TermInfo> terms = getTerms(dField, dTextField);

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

        // TODO: check this slop calculation
        final TokenStream ts =
          ic.getAnalyzer().tokenStream(indexField.getName(), new StringReader(dTextField.getText().trim()));
        try {
          while (ts.next() != null) {
            slop++;
          }
        } catch (IOException e) {
        }
        slop = (slop - 1) * 2;
      }
      if ((terms.size() > 1) && (operator != DTextFieldParameter.DOperator.PHRASE)) {
        final DTextField[] textFields = new DTextField[terms.size()];
        for (int i = 0; i < terms.size(); i++) {

          final String text = terms.get(i).getText();
          boolean parseWildcardsParam = parseWildcards;
          if ((supportWildcardsForExactSearch == true) && (text.contains("*") || text.contains("?"))) {
            parseWildcardsParam = true;
          }

          textFields[i] = new DTextField(dTextField.getFieldNo(), text, fuzzy, parseWildcardsParam, slop);
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
          if (terms.get(i).isProhibited()) {
            op.addTerm(new DTerm(new DOP1("NOT", new DTerm(textFields[i]))));
          } else {
            op.addTerm(new DTerm(textFields[i]));
          }
        }
        return op;
      } else {

        String term = "";

        if (terms.size() != 0) {
          term = terms.get(0).getText();
        }

        boolean parseWildcardsParam = parseWildcards;
        if ((supportWildcardsForExactSearch == true) && (term.contains("*") || term.contains("?"))) {
          parseWildcardsParam = true;
        }

        return new DTextField(dTextField.getFieldNo(), term, fuzzy, parseWildcardsParam, slop);
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

  private List<TermInfo> getTerms(DField dField, org.eclipse.smila.search.utils.search.DTextField dTextField)
    throws NodeTransformerException {
    final IndexConnection ic = (IndexConnection) getIndexConnection();

    final QueryParser qp =
      new QueryParser(ic.getIndex().getIndexStructure().getField(dField.getFieldNo()).getName(), ic.getAnalyzer());
    
    // TODO: make it configurable
    qp.setAllowLeadingWildcard(true);
    final List<TermInfo> terms = new ArrayList<TermInfo>();
    try {
      final Query q = qp.parse(dTextField.getText());

      if (q instanceof BooleanQuery) {
        final BooleanQuery bq = (BooleanQuery) q;
        final BooleanClause[] clauses = bq.getClauses();

        for (BooleanClause clause : clauses) {
          final String term = getTerm(clause.getQuery());
          terms.add(new TermInfo(clause.isProhibited(), term));
        }
      } else {
        final String term = getTerm(q);
        terms.add(new TermInfo(false, term));
      }

    } catch (ParseException e) {
      throw new NodeTransformerException("unable to analyze query", e);
    }
    return terms;
  }

  private String getTerm(Query q) {

    if (q instanceof TermQuery) {
      final TermQuery termQuery = (TermQuery) q;

      return termQuery.getTerm().text();
    } else if (q instanceof WildcardQuery) {
      final WildcardQuery wildcardQuery = (WildcardQuery) q;
      return wildcardQuery.getTerm().text();
    } else if (q instanceof PhraseQuery) {
      final PhraseQuery phraseQuery = (PhraseQuery) q;

      final StringBuffer sb = new StringBuffer();
      final Term[] terms = phraseQuery.getTerms();
      for (Term term : terms) {
        sb.append(term.text()).append(' ');
      }
      return sb.toString().trim();
    } else if (q instanceof PrefixQuery) {
      final PrefixQuery prefixQuery = (PrefixQuery) q;
      return prefixQuery.getPrefix().text() + "*";
    }

    return q.toString();
  }

}
