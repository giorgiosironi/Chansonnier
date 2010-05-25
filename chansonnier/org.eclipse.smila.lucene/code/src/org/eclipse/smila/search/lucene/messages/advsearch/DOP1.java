/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author gschmidt.
 * 
 */
public class DOP1 extends DTermContent implements Cloneable {

  /**
   * operation.
   */
  private String _operation;

  /**
   * term.
   */
  private DTerm _term;

  /**
   * Constructor.
   */
  public DOP1() {
  }

  /**
   * constructor.
   * 
   * @param operation -
   * @param term -
   */
  public DOP1(String operation, DTerm term) {
    setOperation(operation);
    setTerm(term);
  }

  @Override
  public Object clone() {
    final DOP1 obj = (DOP1) super.clone();

    if (_term != null) {
      obj.setTerm((DTerm) _term.clone());
    }

    return new DOP1(new String(_operation), (DTerm) _term.clone());
  }

  public String getOperation() {
    return _operation;
  }

  public void setOperation(String operation) {
    this._operation = operation;
  }

  public DTerm getTerm() {
    return _term;
  }

  public void setTerm(DTerm term) {
    this._term = term;
  }

  @Override
  public String getType() {
    return TC_OP_1;
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
   * @return String -
   */
  @Override
  public String toString() {
    try {
      final Element el = DOP1Codec.encode(this, XMLUtils.getDocument().createElement("T"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }

}
