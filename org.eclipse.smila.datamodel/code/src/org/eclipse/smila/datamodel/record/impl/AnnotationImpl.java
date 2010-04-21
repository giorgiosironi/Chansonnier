/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.EmptyIterator;

/**
 * Default implementation of SMILA Annotations.
 * 
 * @author jschumacher
 * 
 */
public class AnnotationImpl extends AnnotatableImpl implements org.eclipse.smila.datamodel.record.Annotation {

  /**
   * class is serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * list of anon values. should be null when not needed.
   */
  private List<String> _anonValues;

  /**
   * map of named values. should be null when not needed.
   */
  private Map<String, String> _namedValues;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#addAnonValue(java.lang.String)
   */
  public void addAnonValue(String value) {
    if (_anonValues == null) {
      _anonValues = new ArrayList<String>();
    }
    _anonValues.add(value);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#anonValuesSize()
   */
  public int anonValuesSize() {
    if (hasAnonValues()) {
      return _anonValues.size();
    } else {
      return 0;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#getAnonValues()
   */
  public Collection<String> getAnonValues() {
    if (hasAnonValues()) {
      return _anonValues;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#getNamedValue(java.lang.String)
   */
  public String getNamedValue(String name) {
    if (_namedValues == null) {
      return null;
    }
    return _namedValues.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#getValueNames()
   */
  public Iterator<String> getValueNames() {
    if (_namedValues == null) {
      return EmptyIterator.INSTANCE;
    }
    return _namedValues.keySet().iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#hasAnonValues()
   */
  public boolean hasAnonValues() {
    return _anonValues != null && !_anonValues.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#hasNamedValues()
   */
  public boolean hasNamedValues() {
    return _namedValues != null && !_namedValues.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#hasValues()
   */
  public boolean hasValues() {
    return hasAnonValues() || hasNamedValues();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#namedValuesSize()
   */
  public int namedValuesSize() {
    if (hasNamedValues()) {
      return _namedValues.size();
    } else {
      return 0;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#removeAnonValue(java.lang.String)
   */
  public void removeAnonValue(String value) {
    _anonValues.remove(value);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#removeAnonValues()
   */
  public void removeAnonValues() {
    _anonValues = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#removeNamedValue(java.lang.String)
   */
  public void removeNamedValue(String name) {
    if (_namedValues != null) {
      _namedValues.remove(name);
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#removeNamedValues()
   */
  public void removeNamedValues() {
    _namedValues = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#removeValues()
   */
  public void removeValues() {
    removeAnonValues();
    removeNamedValues();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#setAnonValues(java.util.Collection)
   */
  public void setAnonValues(Collection<String> values) {
    _anonValues = new ArrayList<String>(values);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Annotation#setNamedValue(java.lang.String, java.lang.String)
   */
  public void setNamedValue(String name, String value) {
    if (_namedValues == null) {
      _namedValues = new HashMap<String, String>();
    }
    _namedValues.put(name, value);
  }

}
