/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.templates.transformer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.smila.search.lucene.messages.advsearch.DOP1;
import org.eclipse.smila.search.lucene.messages.advsearch.DOPN;
import org.eclipse.smila.search.lucene.messages.advsearch.DTerm;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameter.DOperator;
import org.eclipse.smila.search.templates.NodeTransformer;
import org.eclipse.smila.search.templates.NodeTransformerException;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.search.DField;
import org.eclipse.smila.search.utils.search.DTextField;

/**
 * The SynonymNodeTransformer is able to expand a search query using synonyms. The search term is supporting wildcards.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class SynonymNodeTransformer extends NodeTransformer {

  // {(-)phrase(#-->#abbildung)} {(-)phrase(#-->#abbildung)} ...

  /**
   * @throws NodeTransformerException
   *           Unable to create node transformer.
   */
  public SynonymNodeTransformer() throws NodeTransformerException {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.templates.NodeTransformer#transformNode(org.eclipse.smila.search.utils.search.DField)
   */
  @Override
  public ITermContent transformNode(DField dField) throws NodeTransformerException {

    if (dField == null) {
      throw new NodeTransformerException("parameter must not be null [dField]");
    }

    if (!(dField instanceof DTextField)) {
      throw new NodeTransformerException("node transformer supports text fields only");
    }

    final DTextField textField = (DTextField) dField;

    final String text = textField.getText();

    final String[] tokens = splitByWholeSeparator(text, "} {", 0);
    final Hashtable<PhraseInformation, Set<String>> tokensAndSynonyms =
      new Hashtable<PhraseInformation, Set<String>>();
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i];
      if (token.startsWith("{")) {
        token = token.substring(1);
      }
      if (token.endsWith("}")) {
        token = token.substring(0, token.length() - 1);
      }

      PhraseInformation name = null;
      String synonym = null;
      if (token.contains("#-->#")) {
        final String[] items = splitByWholeSeparator(token, "#-->#", 0);
        name = new PhraseInformation(items[0]);
        synonym = items[1];
      } else {
        name = new PhraseInformation(token);
      }

      Set<String> synonyms = null;
      if (tokensAndSynonyms.containsKey(name)) {
        synonyms = tokensAndSynonyms.get(name);
      } else {
        synonyms = new HashSet<String>();
        tokensAndSynonyms.put(name, synonyms);
      }

      if (synonym != null) {
        synonyms.add(synonym);
      }
    }

    if (tokensAndSynonyms.size() > 1) {
      final DOPN opMain = new DOPN();
      final DTextFieldParameter textFieldParameter = (DTextFieldParameter) textField.getParameter();
      final DOperator operator = textFieldParameter.getOperator();

      if ((operator == DOperator.AND) || (operator == DOperator.PHRASE)) {
        opMain.setOperation("AND");
      } else if (operator == DOperator.OR) {
        opMain.setOperation("OR");
      }

      for (final Enumeration<PhraseInformation> phraseInformations = tokensAndSynonyms.keys(); phraseInformations
        .hasMoreElements();) {
        final PhraseInformation phraseInformation = phraseInformations.nextElement();

        final ITermContent termContent = preparePhrase(tokensAndSynonyms, textField, phraseInformation);

        opMain.addTerm(new DTerm(termContent));
      }

      return opMain;
    } else if (tokensAndSynonyms.size() == 1) {
      final PhraseInformation phraseInformation = tokensAndSynonyms.keySet().iterator().next();
      return preparePhrase(tokensAndSynonyms, textField, phraseInformation);
    } else {
      throw new NodeTransformerException("no search term available");
    }
  }

  /**
   * @param tokensAndSynonyms -
   * @param textField
   *          Text field.
   * @param phraseInformation
   *          Phrase information.
   * @return Term structure containing sub node.
   */
  private ITermContent preparePhrase(Hashtable<PhraseInformation, Set<String>> tokensAndSynonyms,
    DTextField textField, PhraseInformation phraseInformation) {

    final Set<String> synonyms = tokensAndSynonyms.get(phraseInformation);

    ITermContent termContent = null;
    if (synonyms.size() > 0) {
      final DOPN opOR = new DOPN();
      opOR.setOperation("OR");
      opOR.addTerm(new DTerm(new org.eclipse.smila.search.lucene.messages.advsearch.DTextField(textField
        .getFieldNo(), phraseInformation.getPhrase(), false, true, 0)));

      for (String synonym : synonyms) {
        opOR.addTerm(new DTerm(new org.eclipse.smila.search.lucene.messages.advsearch.DTextField(textField
          .getFieldNo(), synonym, false, false, 0)));
      }

      termContent = opOR;
    } else {
      termContent =
        new org.eclipse.smila.search.lucene.messages.advsearch.DTextField(textField.getFieldNo(), phraseInformation
          .getPhrase(), false, true, 0);
    }

    if (phraseInformation.isFilter()) {
      termContent = new DOP1("NOT", new DTerm(termContent));
    }

    return termContent;
  }

  /**
   * <p>
   * Splits the provided text into an array, separator string specified. Returns a maximum of <code>max</code>
   * substrings.
   * </p>
   * 
   * <p>
   * The separator(s) will not be included in the returned String array. Adjacent separators are treated as one
   * separator.
   * </p>
   * 
   * <p>
   * A <code>null</code> input String returns <code>null</code>. A <code>null</code> separator splits on
   * whitespace.
   * </p>
   * 
   * <pre>
   *     StringUtils.splitByWholeSeparator(null, *, *)               = null
   *     StringUtils.splitByWholeSeparator(&quot;&quot;, *, *)                 = []
   *     StringUtils.splitByWholeSeparator(&quot;ab de fg&quot;, null, 0)      = [&quot;ab&quot;, &quot;de&quot;, &quot;fg&quot;]
   *     StringUtils.splitByWholeSeparator(&quot;ab   de fg&quot;, null, 0)    = [&quot;ab&quot;, &quot;de&quot;, &quot;fg&quot;]
   *     StringUtils.splitByWholeSeparator(&quot;ab:cd:ef&quot;, &quot;:&quot;, 2)       = [&quot;ab&quot;, &quot;cd:ef&quot;]
   *     StringUtils.splitByWholeSeparator(&quot;ab-!-cd-!-ef&quot;, &quot;-!-&quot;, 5) = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
   *     StringUtils.splitByWholeSeparator(&quot;ab-!-cd-!-ef&quot;, &quot;-!-&quot;, 2) = [&quot;ab&quot;, &quot;cd-!-ef&quot;]
   * </pre>
   * 
   * @param str
   *          the String to parse, may be null
   * @param separator
   *          String containing the String to be used as a delimiter, <code>null</code> splits on whitespace
   * @param max
   *          the maximum number of elements to include in the returned array. A zero or negative value implies no
   *          limit.
   * @return an array of parsed Strings, <code>null</code> if null String was input
   */
  public static String[] splitByWholeSeparator(String str, String separator, int max) {
    if (str == null) {
      return null;
    }

    final int len = str.length();

    if (len == 0) {
      return new String[] {};
    }

    /*
     * if ( ( separator == null ) || ( "".equals( separator ) ) ) { // Split on whitespace. return split( str, null, max ) ; }
     */

    final int separatorLength = separator.length();

    final ArrayList<String> substrings = new ArrayList<String>();
    int numberOfSubstrings = 0;
    int beg = 0;
    int end = 0;
    while (end < len) {
      end = str.indexOf(separator, beg);

      if (end > -1) {
        if (end > beg) {
          numberOfSubstrings += 1;

          if (numberOfSubstrings == max) {
            end = len;
            substrings.add(str.substring(beg));
          } else {
            // The following is OK, because String.substring( beg, end ) excludes
            // the character at the position 'end'.
            substrings.add(str.substring(beg, end));

            // Set the starting point for the next search.
            // The following is equivalent to beg = end + (separatorLength - 1) + 1,
            // which is the right calculation:
            beg = end + separatorLength;
          }
        } else {
          // We found a consecutive occurrence of the separator, so skip it.
          beg = end + separatorLength;
        }
      } else {
        // String.substring( beg ) goes from 'beg' to the end of the String.
        substrings.add(str.substring(beg));
        end = len;
      }
    }

    return substrings.toArray(new String[substrings.size()]);
  }

  /**
   * @param args
   */

  // public static void main(String[] args) {
  //  
  // final Log log = LogFactory.getLog(SynonymNodeTransformer.class);
  //
  // try {
  // SynonymNodeTransformer nt = new SynonymNodeTransformer();
  //
  // DTextField textField = new DTextField();
  // textField.setFieldNo(0);
  // DTextFieldParameter textFieldParameter = new DTextFieldParameter();
  // textFieldParameter.setOperator(DOperator.AND);
  // textField.setParameter(textFieldParameter); //
  // textField.setText("");
  // textField.setText("{a#-->#b} {-e#-->#b} {c} {-d}"); // textField.setText("{a#-->#b}
  // // {a#-->#b} {a#-->#b} {a#-->#b} {a#-->#b} {a#-->#b}");
  //
  // ITermContent termContent = nt.transformNode(textField);
  //
  // log.info(termContent);
  //
  // } catch (NodeTransformerException e) {
  // if (log.isErrorEnabled()) {
  // log.error(e);
  // }
  // }
  // }
}
