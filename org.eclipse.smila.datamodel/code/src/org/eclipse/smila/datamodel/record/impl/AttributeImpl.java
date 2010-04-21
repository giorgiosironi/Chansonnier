/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;

/**
 * Default implementation of SMILA Attribute.
 * 
 * @author jschumacher
 * 
 */
public class AttributeImpl extends AnnotatableImpl implements Attribute {

  /**
   * yeah, that again.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * name of attribute.
   */
  private String _name;

  /**
   * list of literal values of this attribute.
   */
  private List<LiteralImpl> _literalValues;

  /**
   * list of metadata objects of this attribute.
   */
  private List<MObjectImpl> _metadataObjects;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#addObject(org.eclipse.smila.datamodel.record.MObject)
   */
  public void addObject(MObject object) {
    if (object instanceof MObjectImpl) {
      if (_metadataObjects == null) {
        _metadataObjects = new ArrayList<MObjectImpl>();
      }
      _metadataObjects.add((MObjectImpl) object);
    } else {
      throw new IllegalArgumentException("Cannot add MObjects that are not instance of MObjectImpl.");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#addLiteral(org.eclipse.smila.datamodel.record.Literal)
   */
  public void addLiteral(Literal literal) {
    final LiteralImpl literalImpl = LiteralImpl.ensureImpl(literal);
    if (_literalValues == null) {
      _literalValues = new ArrayList<LiteralImpl>();
    }
    _literalValues.add(literalImpl);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#getName()
   */
  public String getName() {
    return _name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#getObject()
   */
  public MObject getObject() {
    if (hasObjects()) {
      return _metadataObjects.get(0);
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#getObjects()
   */
  public List<MObject> getObjects() {
    if (hasObjects()) {
      return new ArrayList<MObject>(_metadataObjects);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#getLiteral()
   */
  public Literal getLiteral() {
    if (hasLiterals()) {
      return _literalValues.get(0);
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#getLiterals()
   */
  public List<Literal> getLiterals() {
    if (hasLiterals()) {
      return new ArrayList<Literal>(_literalValues);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#hasObjects()
   */
  public boolean hasObjects() {
    return _metadataObjects != null && !_metadataObjects.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#hasLiterals()
   */
  public boolean hasLiterals() {
    return _literalValues != null && !_literalValues.isEmpty();

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#objectSize()
   */
  public int objectSize() {
    if (hasObjects()) {
      return _metadataObjects.size();
    } else {
      return 0;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#removeObjects()
   */
  public void removeObjects() {
    _metadataObjects = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#removeLiterals()
   */
  public void removeLiterals() {
    _literalValues = null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#setName(java.lang.String)
   */
  public void setName(String name) {
    _name = name;

  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#setObject(org.eclipse.smila.datamodel.record.MObject)
   */
  public void setObject(MObject object) {
    removeObjects();
    addObject(object);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#setObjects(java.util.Collection)
   */
  public void setObjects(Collection<? extends MObject> objects) {
    removeObjects();
    if (!objects.isEmpty()) {
      for (MObject object : objects) {
        addObject(object);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#setLiteral(org.eclipse.smila.datamodel.record.Literal)
   */
  public void setLiteral(Literal literal) {
    removeLiterals();
    addLiteral(literal);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#setLiterals(java.util.Collection)
   */
  public void setLiterals(Collection<? extends Literal> literals) {
    removeLiterals();
    for (Literal value : literals) {
      addLiteral(value);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Attribute#literalSize()
   */
  public int literalSize() {
    if (hasLiterals()) {
      return _literalValues.size();
    } else {
      return 0;
    }
  }

}
