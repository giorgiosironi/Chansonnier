/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DWMEAN extends DTermContent implements Cloneable {

  /**
   * operation.
   */
  private String _operation;

  /**
   * terms.
   */
  private ArrayList<DTerm> _terms = new ArrayList<DTerm>();

  /**
   * boosts.
   */
  private ArrayList<Float> _boosts = new ArrayList<Float>();

  /**
   * constraints.
   */
  private ArrayList<String> _constraints = new ArrayList<String>();

  /**
   * Constructor.
   */
  public DWMEAN() {
  }

  /**
   * Constructor.
   * 
   * @param operation -
   * @param terms -
   * @param boosts -
   * @param constraints -
   */
  public DWMEAN(String operation, DTerm[] terms, float[] boosts, String[] constraints) {
    setOperation(operation);
    for (int i = 0; i < terms.length; i++) {
      addTerm(terms[i], boosts[i], constraints[i]);
    }
  }

  @Override
  public Object clone() {
    final DWMEAN obj = (DWMEAN) super.clone();

    obj._terms = new ArrayList<DTerm>();
    obj._boosts = new ArrayList<Float>();
    obj._constraints = new ArrayList<String>();

    for (int i = 0; i < _terms.size(); i++) {
      obj.addTerm((DTerm) _terms.get(i).clone(), _boosts.get(i), _constraints.get(i));
    }

    return obj;
  }

  /**
   * @return String
   */
  public String getOperation() {
    return _operation;
  }

  /**
   * setOperation.
   * 
   * @param operation -
   */
  public void setOperation(String operation) {
    if (operation == null || operation.equals("")) {
      operation = "MEAN";
    }
    this._operation = operation;
  }

  /**
   * @param dTerm -
   * @param boost -
   * @param constraint -
   */
  public void addTerm(DTerm dTerm, Float boost, String constraint) {
    _terms.add(dTerm);
    if (constraint == null || constraint.equals("")) {
      constraint = "optional";
    }
    _constraints.add(constraint);
    if (boost.floatValue() < 0) {
      _boosts.add(new Float(0f));
    } else {
      _boosts.add(boost);
    }
  }

  /**
   * @param dTerm -
   * @param boost -
   * @param constraint -
   */
  public void addTerm(DTerm dTerm, float boost, String constraint) {
    addTerm(dTerm, new Float(boost), constraint);
  }

  /**
   * @param pos -
   */
  public void removeTerm(int pos) {
    _terms.remove(pos);
    _boosts.remove(pos);
    _constraints.remove(pos);
  }

  /**
   * @return Iterator
   */
  public Iterator<DTerm> getTerms() {
    return _terms.iterator();
  }

  /**
   * @return Iterator
   */
  public Iterator<Float> getBoosts() {
    return _boosts.iterator();
  }

  /**
   * @return Iterator
   */
  public Iterator<String> getConstraints() {
    return _constraints.iterator();
  }

  /**
   * @param term -
   * @return float
   */
  public float getBoostForTerm(DTerm term) {
    final int index = _terms.indexOf(term);
    if (index < 0) {
      return 0;
    }
    return _boosts.get(index).floatValue();
  }

  /**
   * @param term -
   * @return String
   */
  public String getConstraintForTerm(DTerm term) {
    final int index = _terms.indexOf(term);
    return _constraints.get(index);
  }

  /**
   * @param pos -
   * @return DTerm
   */
  public DTerm getTerm(int pos) {
    return _terms.get(pos);
  }

  /**
   * @param pos -
   * @return float
   */
  public float getBoost(int pos) {
    return _boosts.get(pos).floatValue();
  }

  /**
   * @param pos -
   * @return String
   */
  public String getConstraint(int pos) {
    return _constraints.get(pos);
  }

  /**
   * @param pos -
   * @param term -
   * @param boost -
   * @param constraint -
   * @return DTerm
   */
  public DTerm setTerm(int pos, DTerm term, long boost, String constraint) {
    return setTerm(pos, term, new Long(boost), constraint);
  }

  /**
   * @param pos -
   * @param term -
   * @param boost -
   * @param constraint -
   * @return DTerm
   */
  public DTerm setTerm(int pos, DTerm term, Float boost, String constraint) {
    if (boost.floatValue() < 0) {
      _boosts.set(pos, new Float(0f));
    } else {
      _boosts.set(pos, boost);
    }
    if (constraint == null || constraint.equals("")) {
      constraint = "optional";
    }
    _constraints.set(pos, constraint);
    return _terms.set(pos, term);
  }

  /**
   * @return int
   */
  public int getTermCount() {
    return _terms.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.messages.advsearch.DTermContent#getType()
   */
  @Override
  public String getType() {
    return TC_WMEAN;
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

  /**
   * Returns this instance's contents as an XML String.
   * 
   * @return String
   */
  @Override
  public String toString() {
    try {
      final Element el = DWMEANCodec.encode(this, XMLUtils.getDocument().createElement("T"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }
}
