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

import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * Implementation of RecordFactory for default Record implementation.
 * 
 * @author jschumacher
 * 
 */
public class DefaultRecordFactoryImpl implements RecordFactory {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.RecordFactory#createRecord(org.eclipse.smila.datamodel.id.Id)
   */
  public Record createRecord() {
    return new RecordImpl();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.RecordFactory#createAnnotation()
   */
  public Annotation createAnnotation() {
    return new AnnotationImpl();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.RecordFactory#createAttribute()
   */
  public Attribute createAttribute() {
    return new AttributeImpl();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.RecordFactory#createLiteral()
   */
  public Literal createLiteral() {
    return new LiteralImpl();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.RecordFactory#createMetadataObject()
   */
  public MObject createMetadataObject() {
    return new MObjectImpl();
  }

}
