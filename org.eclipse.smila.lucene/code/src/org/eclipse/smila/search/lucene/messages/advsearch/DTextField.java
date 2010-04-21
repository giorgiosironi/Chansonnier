/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

public class DTextField extends DTermContent implements Cloneable {

  private String _text;

  private int _fieldNo;

  private boolean _fuzzy;

  private boolean _parseWildcards;

  private int _slop;

  /**
   * DTextField.
   */
  public DTextField() {
  }

  /**
   * DTextField.
   * 
   * @param fieldNo -
   * @param text -
   * @param fuzzy -
   * @param parseWildcards -
   * @param slop -
   */
  public DTextField(int fieldNo, String text, boolean fuzzy, boolean parseWildcards, int slop) {
    this._fieldNo = fieldNo;
    this._text = text;
    this._fuzzy = fuzzy;
    this._parseWildcards = parseWildcards;
    if (slop < 0) {
      slop = 0;
    }
    this._slop = slop;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.messages.advsearch.DTermContent#clone()
   */
  @Override
  public Object clone() {
    return super.clone();
  }

  /**
   * getText.
   * 
   * @return String
   */
  public String getText() {
    return _text;
  }

  /**
   * setText.
   * 
   * @param text -
   */
  public void setText(String text) {
    this._text = text;
  }

  /**
   * getFieldNo.
   * 
   * @return int
   */
  public int getFieldNo() {
    return _fieldNo;
  }

  /**
   * setFieldNo.
   * 
   * @param fieldNo -
   */
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  /**
   * getFuzzy.
   * 
   * @return boolean
   */
  public boolean getFuzzy() {
    return _fuzzy;
  }

  /**
   * getFuzzyAsString.
   * 
   * @return String
   */
  public String getFuzzyAsString() {
    return _fuzzy ? "true" : "false";
  }

  /**
   * @param fuzzy -
   */
  public void setFuzzy(boolean fuzzy) {
    this._fuzzy = fuzzy;
  }

  /**
   * @param fuzzy -
   */
  public void setFuzzy(String fuzzy) {
    this._fuzzy = (fuzzy != null) && (fuzzy.equals("1") || fuzzy.toLowerCase().equals("true"));
  }

  /**
   * @return boolean
   */
  public boolean getParseWildcards() {
    return _parseWildcards;
  }

  /**
   * @return String
   */
  public String getParseWildcardsAsString() {
    return _parseWildcards ? "true" : "false";
  }

  /**
   * @param parseWildcards -
   */
  public void setParseWildcards(boolean parseWildcards) {
    this._parseWildcards = parseWildcards;
  }

  /**
   * @param parseWildcards -
   */
  public void setParseWildcards(String parseWildcards) {
    this._parseWildcards =
      (parseWildcards != null) && (parseWildcards.equals("1") || parseWildcards.toLowerCase().equals("true"));
  }

  /**
   * @return int
   */
  public int getSlop() {
    return _slop;
  }

  /**
   * @param slop -
   */
  public void setSlop(int slop) {
    if (slop < 0) {
      slop = 0;
    }
    this._slop = slop;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.messages.advsearch.DTermContent#getType()
   */
  @Override
  public String getType() {
    return TC_TEXTFIELD;
  }

  /**
   * ************************************************************************ Calls the toString() method on the given
   * Object and THIS instance and then compares the resultant Strings with the equals() method.
   * 
   * @param obj -
   * @return boolean
   */
  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

}
