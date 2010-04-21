/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.ITerm;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class can contain exactly one of the possible terms at a time.
 * 
 * 
 */
public class DTerm implements ITerm {
  /**
   * This recieves not a DTerm as the name might suggest but takes either a DTextField, DNumField or a DOP_X.
   */
  private ITermContent _term;

  /**
   * Constructor.
   */
  public DTerm() {
  }

  /**
   * 
   * Constructor.
   * 
   * @param term -
   */
  public DTerm(DTermContent term) {
    setTerm(term);
  }

  /**
   * Constructor.
   * 
   * @param term -
   */
  public DTerm(ITermContent term) {
    if (!(term instanceof DTermContent)) {
      throw new IllegalArgumentException("invalid type for term [" + term.getClass().getName() + "]");
    }
    setTerm(term);
  }

  @Override
  public Object clone() {

    DTerm obj = null;
    try {
      obj = (DTerm) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DTerm", e);
    }

    if (_term != null) {
      obj.setTerm((ITermContent) ((DTermContent) _term).clone());
    }

    return obj;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.advsearch.ITerm#getTerm()
   * @return ITermContent
   */
  public ITermContent getTerm() {
    return _term;
  }

  /**
   * This recieves not a DTerm as the name might suggest but takes either a DTextField, DNumField or a DOP_X. Otherwise
   * an exception is thrown.
   * 
   * @param term -
   * @return ITermContent
   */
  public ITermContent setTerm(ITermContent term) {
    this._term = term;
    return term;
  }

  public DTextField getTextField() {
    return (DTextField) _term;
  }

  public DNumField getNumField() {
    return (DNumField) _term;
  }

  public DDateField getDateField() {
    return (DDateField) _term;
  }

  public DTemplateField getTemplateField() {
    return (DTemplateField) _term;
  }

  public DOP1 getOP1() {
    return (DOP1) _term;
  }

  public DOPN getOpN() {
    return (DOPN) _term;
  }

  public DWMEAN getWMEAN() {
    return (DWMEAN) _term;
  }

  /**
   * Returns this instance's contents as an XML String.
   * 
   * @return String
   */
  @Override
  public String toString() {
    try {
      final Element el = DTermCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  }
}
