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

package org.eclipse.smila.datamodel.record;

/**
 * Simple common interface of Literals and MObject. Currently only makes it possible to define a semantic (ontology)
 * type of an attribute value, and defines all attribute values to be annotatable.
 * 
 * @author jschumacher
 * 
 */
public interface AttributeValue extends Annotatable {
  /**
   * get the semantic type name of this value.
   * 
   * @return semantic type name.
   */
  String getSemanticType();

  /**
   * set semantic type name of this value.
   * 
   * @param typeName
   *          semantic type name.
   */
  void setSemanticType(String typeName);
}
