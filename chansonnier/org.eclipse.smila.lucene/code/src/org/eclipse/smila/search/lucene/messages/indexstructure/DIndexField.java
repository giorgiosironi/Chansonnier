/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.indexstructure;

import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

public class DIndexField extends org.eclipse.smila.search.utils.indexstructure.DIndexField {

  private int _fieldNo;

  private String _name;

  private String _type;

  private boolean _tokenize;

  private boolean _indexValue;

  private boolean _storeText;

  private DAnalyzer _analyzer;

  public DIndexField(int fieldNo, String name, String type, boolean tokenize, boolean indexValue, boolean storeText) {
    this._fieldNo = fieldNo;
    this._name = name;
    this._type = type;
    this._tokenize = tokenize;
    this._indexValue = indexValue;
    this._storeText = storeText;
  }

  protected DIndexField() {
  }

  @Override
  public int getFieldNo() {
    return this._fieldNo;
  }

  @Override
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  @Override
  public String getName() {
    return this._name;
  }

  @Override
  public void setName(String name) {
    this._name = name;
  }

  @Override
  public void setType(String type) {
    this._type = type;
  }

  @Override
  public String getType() {
    return _type;
  }

  public boolean getTokenize() {
    return this._tokenize;
  }

  public void setTokenize(boolean tokenize) {
    this._tokenize = tokenize;
  }

  public boolean getIndexValue() {
    return this._indexValue;
  }

  public void setIndexValue(boolean indexValue) {
    this._indexValue = indexValue;
  }

  @Override
  public boolean getStoreText() {
    return this._storeText;
  }

  @Override
  public void setStoreText(boolean storeText) {
    this._storeText = storeText;
  }

  public DAnalyzer getAnalyzer() {
    return _analyzer;
  }

  public void setAnalyzer(DAnalyzer analyzer) {
    this._analyzer = analyzer;
  }

  @Override
  public String toString() {
    try {
      final Element el = DIndexFieldCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DIndexField) {
      return equals((DIndexField) obj);
    } else {
      return false;
    }
  } // End Method equals

  @Override
  public boolean equals(org.eclipse.smila.search.utils.indexstructure.DIndexField dIF) {
    if (!(dIF instanceof DIndexField)) {
      return false;
    }
    final DIndexField o = (DIndexField) dIF;
    if (o.getFieldNo() != getFieldNo()) {
      return false;
    }
    if (!o.getName().equals(getName())) {
      return false;
    }
    if (!o.getType().equals(getType())) {
      return false;
    }
    if (o.getTokenize() != getTokenize()) {
      return false;
    }
    if (o.getIndexValue() != getIndexValue()) {
      return false;
    }
    if (o.getStoreText() != getStoreText()) {
      return false;
    }

    if ((o.getAnalyzer() == null) && (getAnalyzer() != null)) {
      return false;
    }
    if ((o.getAnalyzer() != null) && (getAnalyzer() == null)) {
      return false;
    }
    if ((o.getAnalyzer() != null) && (!o.getAnalyzer().equals(getAnalyzer()))) {
      return false;
    }

    return true;
  }

  @Override
  public boolean equalsStructure(org.eclipse.smila.search.utils.indexstructure.DIndexField dIF,
    boolean throwException) throws ISException {
    return false;
  }

} // End class def.
