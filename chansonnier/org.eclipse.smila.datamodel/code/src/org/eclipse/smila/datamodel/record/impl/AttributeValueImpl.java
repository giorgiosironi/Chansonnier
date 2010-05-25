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

import org.eclipse.smila.datamodel.record.AttributeValue;

/**
 * Default implementation of SMILA AttributeValue, as abstract base class for LiteralImpl and MObjectImpl.
 * 
 * @author jschumacher
 * 
 */
public abstract class AttributeValueImpl extends AnnotatableImpl implements AttributeValue {
  /**
   * serializable, you know ...
   */
  private static final long serialVersionUID = 1L;

  /**
   * semantic type of attribute value.
   */
  private String _semanticType;

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.AttributeValue#getSemanticType()
   */
  public String getSemanticType() {
    return _semanticType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.AttributeValue#setSemanticType(java.lang.String)
   */
  public void setSemanticType(String semanticType) {
    _semanticType = semanticType;

  }

}
