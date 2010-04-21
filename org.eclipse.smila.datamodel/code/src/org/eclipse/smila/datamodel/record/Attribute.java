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

import java.util.Collection;
import java.util.List;

/**
 * Interface of Attribute object. Attributes have a name and are part of Metadata Objects. They can contain a set of
 * literal values and a set of further metadata object.
 * 
 * @see MObject
 * @author jschumacher
 * 
 */
public interface Attribute extends Annotatable {
  /**
   * get name of attribute.
   * 
   * @return name of attribute
   */
  String getName();

  /**
   * set name of attribute.
   * 
   * @param name
   *          name of attribute
   */
  void setName(String name);

  /**
   * check if attribute has literal values.
   * 
   * @return true if attribute has literal values, else false
   */
  boolean hasLiterals();

  /**
   * get number of literal values.
   * 
   * @return number of literal values.
   */
  int literalSize();

  /**
   * get list of literal values in this attribute.
   * 
   * @return list of literal values of this attribute. null, if attribute does not have literal values.
   */
  List<Literal> getLiterals();

  /**
   * get one literal value of this attribute. To be used as a conventience methods when a client can be sure that the
   * attribute contains at most one literal value because an implementation of the data model might not be able to
   * ensure that the order of literal values is the same each time the record is accessed.
   * 
   * @return a single literal value if any are contained, else null.
   */
  Literal getLiteral();

  /**
   * set the literal values of this attribute. All currently existing values are replaced.
   * 
   * @param literals
   *          new literal values
   */
  void setLiterals(Collection<? extends Literal> literals);

  /**
   * set a single literal value for this attribute. All currently existing values are replaced.
   * 
   * @param literal
   *          new literal value
   */
  void setLiteral(Literal literal);

  /**
   * add a literal value to this attribute. The value is appended to the current collection of values.
   * 
   * @param literal
   *          new literal value.
   */
  void addLiteral(Literal literal);

  /**
   * remove all current literal values from this attribute.
   */
  void removeLiterals();

  /**
   * check if attribute has sub metadata objects.
   * 
   * @return true if attributes contains metadata objects, else false
   */
  boolean hasObjects();

  /**
   * get number of metadata objects in this attribute.
   * 
   * @return number of metadata objects.
   */
  int objectSize();

  /**
   * get metadata objects contained in this attribute.
   * 
   * @return contained metadata objects.
   */
  List<MObject> getObjects();

  /**
   * get one metadata object of this attribute. To be used as a conventience methods when a client can be sure that the
   * attribute contains at most one metadata object because an implementation of the data model might not be able to
   * ensure that the order of lmetadata objects is the same each time the record is accessed.
   * 
   * @return a single literal value if any are contained, else null.
   */
  MObject getObject();

  /**
   * set the metadata objects of this attribute. All currently existing objects are replaced.
   * 
   * @param objects
   *          new metadata objects
   */
  void setObjects(Collection<? extends MObject> objects);

  /**
   * set the metadata object of this attribute. All currently existing objects are replaced.
   * 
   * @param object
   *          new metadata object
   */
  void setObject(MObject object);

  /**
   * add a metadata object to this attribute. The object is appended to the current collection of objects.
   * 
   * @param object
   *          new metadata object
   */
  void addObject(MObject object);

  /**
   * remove all current metadata objects from this attribute.
   */
  void removeObjects();
}
