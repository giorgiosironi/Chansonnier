/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.IQueryExpression;
import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author gschmidt
 * 
 */
public class DQueryExpression implements IQueryExpression {
  /**
   * indexName.
   */
  private String _indexName;

  /**
   * term.
   */
  private ITerm _term;

  /**
   * maxHits.
   */
  private int _maxHits;

  /**
   * minSimilarity.
   */
  private int _minSimilarity;

  /**
   * showHitDistribution.
   */
  private boolean _showHitDistribution;

  /**
   * startHits.
   */
  private Integer _startHits;

  /**
   * Constructs a new QueryExpression.
   * 
   * @param indexName
   *          name of the index to use with this query
   * @param maxHits
   *          maximum number of hits to be retrieved
   */
  public DQueryExpression(String indexName, int maxHits) {
    setIndexName(indexName);
    setMaxHits(maxHits);
  }

  /**
   * Constructs a new QueryExpression.
   */
  public DQueryExpression() {
  }

  /**
   * Creates a new instance of QueryExpression with the exact same properties as the current instance.
   * 
   * @return Object
   */
  @Override
  public Object clone() {

    DQueryExpression obj = null;
    try {
      obj = (DQueryExpression) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DQueryExpression", e);
    }

    if (_term != null) {
      obj.setTerm((ITerm) ((DTerm) _term).clone());
    }

    if (_startHits != null) {
      obj.setStartHits(new Integer(_startHits.intValue()));
    }

    return obj;
  }

  /**
   * Returns the maximum number of hits in the search result.
   * 
   * @return maximum number of hits.
   */
  public int getMaxHits() {
    return _maxHits;
  }

  /**
   * Sets the maximum number of hits to be retrieved by the search.
   * 
   * @param maxHits
   *          maximum number of hits that will be included in the search result.
   */
  public void setMaxHits(int maxHits) {
    this._maxHits = maxHits;
  }

  /**
   * Returns the name of the index that will be used for this QueryExpression.
   * 
   * @return name of the index.
   */
  public String getIndexName() {
    return _indexName;
  }

  /**
   * Sets the name of the index that will be used for this QueryExpression.
   * 
   * @param indexName
   *          of the index.
   */
  public void setIndexName(String indexName) {
    this._indexName = indexName;
  }

  public void setShowHitDistribution(boolean showHitDistribution) {
    this._showHitDistribution = showHitDistribution;
  }

  public boolean getShowHitDistribution() {
    return _showHitDistribution;
  }

  /**
   * Returns the <code>term</code> below this QueryExpression.
   * 
   * @return contents of the <code>term</code> field.
   */
  public ITerm getTerm() {
    return _term;
  }

  /**
   * Sets the <code>term</code> below this QueryExpression. Each QueryExpression must have exactly one
   * <code>term</code>.
   * 
   * @return contents of this QueryExpression's <code>term</code> field.
   * @param term -
   */
  public ITerm setTerm(ITerm term) {
    this._term = term;
    return term;
  }

  /**
   * Returns the minimum similarity score for matches in this QueryExpression.
   * 
   * @return minimum similarity
   */
  public int getMinSimilarity() {
    return _minSimilarity;
  }

  /**
   * Sets the minimum similarity score for matches in this QueryExpression.
   * 
   * @param minSimilarity
   *          similarity to be set for this QueryExpression.
   */
  public void setMinSimilarity(int minSimilarity) {
    this._minSimilarity = minSimilarity;
  }

  /**
   * Returns this instance's contents as an XML String.
   * 
   * @return String
   */
  @Override
  public String toString() {
    try {
      final Element el = DQueryExpressionCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Calls the toString() method on the given Object and THIS instance and then compares the resulting Strings with the
   * equals() method.
   * 
   * @return boolean -
   * @param obj -
   */
  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.advsearch.IQueryExpression#getStartHits()
   */
  public Integer getStartHits() {
    return _startHits;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.advsearch.IQueryExpression#setStartHits(java.lang.Integer)
   */
  public void setStartHits(Integer startHits) {
    this._startHits = startHits;
  }
}
