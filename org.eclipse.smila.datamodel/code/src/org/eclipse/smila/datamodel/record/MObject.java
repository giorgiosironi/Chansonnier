/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record;

import java.util.Iterator;

/**
 * Interface of Metadata Objects. Metadata Objects consist of a set of attributes and can have annotations. The root
 * metadata object of an SMILA record is a Metadata Object, but Metadata objects can also be used as attribute values
 * inside other metadata objects to create more structured records.
 * 
 * The interface name is MObject (= Metadata Object) in order to avoid the name clash with java.lang.Object.
 * 
 * @author jschumacher
 * 
 */
public interface MObject extends AttributeValue {
  /**
   * Check if this metadata object has attributes set. Existing attributes do not imply that they have already values or
   * annotations.
   * 
   * @return true if attributes are set, else false.
   */
  boolean hasAttributes();

  /**
   * Check if this metadata object has an attribute with the specified name. The existence of an attribute does not
   * imply that it has values or annotations yet.
   * 
   * @param name
   *          name of attribute to check for
   * @return true if an attribute exists with this name, else false.
   */
  boolean hasAttribute(String name);

  /**
   * get number of attributes.
   * 
   * @return number of attributes.
   */
  int size();

  /**
   * Get an iterator on the names of the currently existing attributes.
   * 
   * @return iterator on attributes names.
   */
  Iterator<String> getAttributeNames();

  /**
   * Get the attribute object for the specified name.
   * 
   * @param name
   *          name of an attribute.
   * @return an Attribute object if an attribute of this name exists, else null.
   */
  Attribute getAttribute(String name);

  /**
   * Set the attribute object for the specified name. If the attribute has been set before, the old attribute is
   * replaced by the new one.
   * 
   * @param name
   *          name of attribute to set
   * @param attribute
   *          new Attribute object.
   */
  void setAttribute(String name, Attribute attribute);

  /**
   * Removes the attribute object for the specified name. If no attribute with the given name exists null is returned.
   * 
   * @param name
   *          name of attribute to remove
   * @return the removed Attribute object or null
   */
  Attribute removeAttribute(String name);
}
