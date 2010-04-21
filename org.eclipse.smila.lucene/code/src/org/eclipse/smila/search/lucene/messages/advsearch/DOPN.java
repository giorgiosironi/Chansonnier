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

/**
 * @author gschmidt
 * 
 */
public class DOPN extends DTermContent implements Cloneable {

  /**
   * operation.
   */
  private String _operation;

  /**
   * terms.
   */
  private ArrayList<DTerm> _terms = new ArrayList<DTerm>();

  /**
   * Constructor.
   */
  public DOPN() {
  }

  /**
   * Constructor.
   * 
   * @param operation -
   * @param terms -
   */
  public DOPN(String operation, DTerm[] terms) {
    setOperation(operation);
    for (int i = 0; i < terms.length; i++) {
      addTerm(terms[i]);
    }
  }

  @Override
  public Object clone() {
    final DOPN obj = (DOPN) super.clone();

    obj._terms = new ArrayList<DTerm>();

    for (int i = 0; i < _terms.size(); i++) {
      obj.addTerm((DTerm) _terms.get(i).clone());
    }

    return obj;
  }

  public String getOperation() {
    return _operation;
  }

  public void setOperation(String operation) {
    this._operation = operation;
  }

  public void addTerm(DTerm dTerm) {
    _terms.add(dTerm);
  }

  public void removeTerm(int pos) {
    _terms.remove(pos);
  }

  public Iterator<DTerm> getTerms() {
    return _terms.iterator();
  }

  public DTerm getTerm(int pos) {
    return _terms.get(pos);
  }

  public DTerm setTerm(int pos, DTerm term) {
    return _terms.set(pos, term);
  }

  public int getTermCount() {
    return _terms.size();
  }

  @Override
  public String getType() {
    return TC_OP_N;
  }

  /**
   * ************************************************************************ Calls the toString() method on the given
   * Object and THIS instance and then compares the resultant Strings with the equals() method.
   * 
   * @param obj -
   * @return boolean -
   */
  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  /**
   * Returns this instance's contents as an XML String.
   * 
   * @return String-
   */
  @Override
  public String toString() {
    try {
      final Element el = DOPNCodec.encode(this, XMLUtils.getDocument().createElement("T"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }

}
