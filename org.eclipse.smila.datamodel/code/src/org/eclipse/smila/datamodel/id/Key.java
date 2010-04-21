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

package org.eclipse.smila.datamodel.id;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * Interface of ID Keys. Keys can be simple and consist just of a single key value with an optional name (e.g. a file
 * path or an URL), or they can be composite keys containing a mapping of names to key values (e.g. database primary
 * keys consisting of multiple columns).
 * 
 * Keys must be immutable objects to make them usable as hash keys. This means that the must also define sensible
 * equals() and hashCode() methods.
 * 
 * @author jschumacher
 * 
 */
public interface Key extends Serializable {
  /**
   * Checks if this is a simple or composite key.
   * 
   * @return true, if this is a composite key. false, if it is a simple key.
   */
  boolean isCompositeKey();

  /**
   * Get the value of a simple key.
   * 
   * @return the simple key value. returns null for composite keys.
   */
  String getKey();

  /**
   * Get the name of a simple key.
   * 
   * @return the simple key name. returns null for unnamed simple keys or composite keys.
   */
  String getKeyName();

  /**
   * Get the names of this key. This also creates an iterator for simple keys containing either a single name for named
   * simple keys or no elements unnamed simple keys. It must not be possible to modify the key using this iterator.
   * 
   * @return an iterator on all key names.
   */
  Iterator<String> getKeyNames();

  /**
   * Get the key for the given name. Works for simple keys, too, by returning the simple key value if the key name is
   * correct (use null for unnamed keys) or null, else.
   * 
   * @param name
   *          a key name
   * @return the associated key value or null if no such value exists.
   */
  String getKey(String name);

  /**
   * create a map representation of this key. If this is a simple unnamed key, the key name in the map will be null. The
   * implementation must ensure that it is not possible do modify the key using this map.
   * 
   * @return map representation of the key values.
   */
  Map<String, String> getKeyValues();

}
