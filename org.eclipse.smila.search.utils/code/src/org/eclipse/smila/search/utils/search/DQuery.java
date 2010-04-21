/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2001 Company: BROX IT-Solutions GmbH
 * 
 * @author brox IT-Solutions GmbH
 * @version 1.0
 */

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Element;

@SuppressWarnings("unchecked")
public class DQuery implements Cloneable {

  private Vector _fields = new Vector();

  private int _maxHits;

  private int _minSimilarity;

  private String _indexName;

  private boolean _showHitDistribution = true;

  private String _templateSelectorName;

  private Integer _startHits;

  private Collection<Integer> _resultFields;

  private Collection<Integer> _highlightFields;

  public DQuery() {
  }

  public void addField(DField dField) {
    _fields.addElement(dField);
  }

  @Override
  public Object clone() {
    DQuery query = null;

    try {
      query = (DQuery) super.clone();
    } catch (final CloneNotSupportedException ex) {
      throw new RuntimeException("unable to clone query");
    }

    if (_startHits != null) {
      query._startHits = new Integer(_startHits.intValue());
    }

    query._fields = new Vector();
    final Enumeration enm = getFields();
    while (enm.hasMoreElements()) {
      final DField field = (DField) enm.nextElement();
      query.addField((DField) field.clone());
    }
    query.setHighlightFields(_highlightFields);
    return query;
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  public Enumeration getFields() {
    return _fields.elements();
  }

  public int getFieldsCount() {
    return _fields.size();
  }

  public String getIndexName() {
    return this._indexName;
  }

  public int getMaxHits() {
    return this._maxHits;
  }

  public int getMinSimilarity() {
    return this._minSimilarity;
  }

  public boolean getShowHitDistribution() {
    return _showHitDistribution;
  }

  public Integer getStartHits() {
    return this._startHits;
  }

  public String getTemplateSelectorName() {
    return this._templateSelectorName;
  }

  public void removeField(DField dField) {
    _fields.removeElement(dField);
  }

  public void setIndexName(String value) {
    this._indexName = value;
  }

  public void setMaxHits(int value) {
    this._maxHits = value;
  }

  public void setMinSimilarity(int minSimilarity) {
    this._minSimilarity = minSimilarity;
  }

  public void setShowHitDistribution(boolean showHitDistribution) {
    this._showHitDistribution = showHitDistribution;
  }

  public void setStartHits(Integer value) {
    this._startHits = value;
  }

  public void setTemplateSelectorName(String templateSelectorName) {
    this._templateSelectorName = templateSelectorName;
  }

  public void setResultFields(Collection<Integer> resultFields) {
    _resultFields = resultFields;
  }

  public Collection<Integer> getResultFields() {
    return _resultFields;
  }

  public void setHighlightFields(Collection<Integer> highlightFields) {
    _highlightFields = highlightFields;
  }

  public Collection<Integer> getHighlightFields() {
    return _highlightFields;
  }

  @Override
  public String toString() {
    try {
      final Element el = DQueryCodec.encode(this, XMLUtils.getDocument().createElement("Dummy"));
      el.getOwnerDocument().appendChild(el);
      final String s = new String(XMLUtils.stream(el, false));

      return s;
    } catch (final Exception e) {
      return null;
    }
  } // End Method toString

} // End Class Def.
