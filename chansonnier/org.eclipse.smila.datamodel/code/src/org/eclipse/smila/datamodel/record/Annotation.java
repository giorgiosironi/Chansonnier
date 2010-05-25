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
import java.util.Iterator;

/**
 * Interface of annotation objects. Annotations are "annotatable" themselves which means that they can have
 * sub-annotations. This makes it possible to create very big, structured annotations.
 * 
 * Annotations can have a set of "anonymous values" and a set of "named values", where single values are associated to
 * value names (like in a Java Map).
 * 
 * Annotations values are always strings currently. This may be extended in a later version.
 * 
 * 
 * @author jschumacher
 * 
 */
public interface Annotation extends Annotatable {
  /**
   * check if this annotation has anonymous or named values.
   * 
   * @return true if anonymous values are set, else false.
   */
  boolean hasValues();

  /**
   * check if this annotation has anonymous values.
   * 
   * @return true if anonymous values are set, else false.
   */
  boolean hasAnonValues();

  /**
   * get number of anonymous values.
   * 
   * @return number of anonymous values
   */
  int anonValuesSize();

  /**
   * get the anonymous values set in this annotaton.
   * 
   * @return the list of values of this annotation. Yields null, if no values are set,
   */
  Collection<String> getAnonValues();

  /**
   * set a new set of anonymous values. Existing anonymous values are replaced.
   * 
   * @param value
   *          new list of values.
   */
  void setAnonValues(Collection<String> value);

  /**
   * add another value. The value is appended to the current anonymous values.
   * 
   * @param value
   *          an additional value
   */
  void addAnonValue(String value);

  /**
   * remove the given anonymous value from this annotation.
   * @param value the value to remove
   */
  void removeAnonValue(String value);
  
  /**
   * remove all anonymous values from this annotation.
   */
  void removeAnonValues();

  /**
   * check if this annotation has named values.
   * 
   * @return true if named values are set, else false.
   */
  boolean hasNamedValues();

  /**
   * get number of named values.
   * 
   * @return number of named values
   */
  int namedValuesSize();

  /**
   * get an iterator on the names of named values.
   * 
   * @return iterator on named value names.
   */
  Iterator<String> getValueNames();

  /**
   * get named value for specified.
   * 
   * @param name
   *          named value name
   * @return named value
   */
  String getNamedValue(String name);

  /**
   * set a named value.
   * 
   * @param name
   *          named value name
   * @param value
   *          named value
   */
  void setNamedValue(String name, String value);

  /**
   * remove a named value.
   * 
   * @param name
   *          named value name
   */
  void removeNamedValue(String name);

  /**
   * remove all named values.
   */
  void removeNamedValues();

  /**
   * remove all named and anonymous values from this annotation.
   */
  void removeValues();

}
