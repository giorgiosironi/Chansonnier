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
package org.eclipse.smila.utils.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * allows multiple values to be stored against a single key.
 * 
 * @param <K>
 *          class of the keys of this map
 * @param <V>
 *          class of the map values. Note that the action map value type is List<V>, because this map stores multiple
 *          values for each key
 */
public class MultiValueMap<K, V> extends HashMap<K, List<V>> {

  /**
   * The default size of the internal values list.
   */
  private static final int DEFAULT_VALUELIST_SIZE = 5;

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 5153372325364062581L;

  /**
   * Default Constructor.
   */
  public MultiValueMap() {
    super();
  }

  /**
   * Conversion Constructor.
   * @param size the initial size of this map
   */
  public MultiValueMap(int size) {
    super(size);
  }

  /**
   * @param key
   *          a key
   * @return a list of values for this key
   */
  public List<V> getValues(K key) {
    return get(key);
  }

  /**
   * add another value to the given key.
   * 
   * @param key
   *          key
   * @param value
   *          a new value
   * @return the value.
   */
  public Object add(K key, V value) {
    List<V> l = super.get(key);
    if (l == null) {
      l = new ArrayList<V>(DEFAULT_VALUELIST_SIZE);
      super.put(key, l);
    }
    l.add(value);
    return value;
  }

  /**
   * set an empty value list. for the given key. Existing values will be overwritten.
   * 
   * @param key
   *          the key
   */
  public void addKey(K key) {
    put(key, new ArrayList<V>());
  }

}
