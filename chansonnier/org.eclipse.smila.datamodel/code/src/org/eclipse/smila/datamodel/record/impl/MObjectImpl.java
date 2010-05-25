/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, #
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.MObject;

/**
 * Default implementation of SMILA Metadata Objects.
 * 
 * @author jschumacher
 * 
 */
public class MObjectImpl extends AttributeValueImpl implements MObject {

  /**
   * same procedure as every class...
   */
  private static final long serialVersionUID = 1L;

  /**
   * map of attribute names to attributes, used linked map to preserve order .
   */
  private final Map<String, AttributeImpl> _attributes = new LinkedHashMap<String, AttributeImpl>();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#getAttribute(java.lang.String)
   */
  public Attribute getAttribute(String name) {
    return _attributes.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#getAttributeNames()
   */
  public Iterator<String> getAttributeNames() {
    return _attributes.keySet().iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#hasAttribute(java.lang.String)
   */
  public boolean hasAttribute(String name) {
    return _attributes.containsKey(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#hasAttributes()
   */
  public boolean hasAttributes() {
    return !_attributes.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#setAttribute(java.lang.String,
   *      org.eclipse.smila.datamodel.record.Attribute)
   */
  public void setAttribute(String name, Attribute attribute) {
    if (attribute instanceof AttributeImpl) {
      attribute.setName(name);
      _attributes.put(name, (AttributeImpl) attribute);
    } else {
      throw new IllegalArgumentException("Cannot use attribute of class " + attribute.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#size()
   */
  public int size() {
    return _attributes.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.MObject#removeAttribute(java.lang.String)
   */
  public Attribute removeAttribute(String name) {
    return _attributes.remove(name);
  }
}
