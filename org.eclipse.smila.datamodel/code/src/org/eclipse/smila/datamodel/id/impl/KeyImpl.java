/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.id.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.SingletonIterator;
import org.eclipse.smila.datamodel.id.Key;

/**
 * standard implementation of ID Keys.
 * 
 * @author jschumacher
 * 
 */
public class KeyImpl implements Key {

  /**
   * keys are serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * value of simple key. is null for composite keys.
   */
  private String _simpleKey;

  /**
   * optional name of simple key.
   */
  private String _simpleKeyName;

  /**
   * name-keyvalue map for composite keys. is null for simple keys.
   */
  private Map<String, String> _compositeKey;

  /**
   * cache hashcode for performance optimization.
   */
  private int _hashCode;

  /**
   * create a key with the given name-value-mapping. If the map has a size of 1, a simple key is created, else it will
   * be a composite key.
   * 
   * @param keyValues
   *          names and key values to use
   */
  public KeyImpl(final Map<String, String> keyValues) {
    if (keyValues.size() == 1) {
      _simpleKeyName = keyValues.keySet().iterator().next();
      _simpleKey = keyValues.get(_simpleKeyName);
    } else {
      this._compositeKey = Collections.unmodifiableMap(new TreeMap<String, String>(keyValues));
    }
  }

  /**
   * create a simple named key.
   * 
   * @param name
   *          key name
   * @param keyValue
   *          key value.
   */
  public KeyImpl(final String name, final String keyValue) {
    this._simpleKeyName = name;
    this._simpleKey = keyValue;
  }

  /**
   * create simple unnamed key.
   * 
   * @param key
   *          the key value
   */
  public KeyImpl(final String key) {
    this(null, key);
  }

  /**
   * convert other key implementations to the default KeyImpl, if necessary.
   * 
   * @param someKey
   *          key in deliberate implementation
   * @return same key as KeyImpl instance.
   */
  public static KeyImpl ensureImpl(final Key someKey) {
    if (someKey instanceof KeyImpl) {
      return (KeyImpl) someKey;
    } else {
      return new KeyImpl(someKey.getKeyValues());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#getKey()
   */
  public String getKey() {
    return _simpleKey;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#getKeyName()
   */
  public String getKeyName() {
    return _simpleKeyName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#isCompositeKey()
   */
  public boolean isCompositeKey() {
    return _compositeKey != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#getKeyNames()
   */
  public Iterator<String> getKeyNames() {
    if (_compositeKey == null) {
      if (_simpleKeyName == null) {
        return EmptyIterator.INSTANCE;
      } else {
        return new SingletonIterator(_simpleKeyName, false);
      }
    } else {
      return _compositeKey.keySet().iterator();
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#getKey(java.lang.String)
   */
  public String getKey(final String name) {
    if (_compositeKey == null) {
      if (_simpleKeyName == name || (name != null && name.equals(_simpleKeyName))) {
        return _simpleKey;
      } else {
        return null;
      }
    } else {
      return _compositeKey.get(name);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Key#getKeyValues()
   */
  public Map<String, String> getKeyValues() {
    if (isCompositeKey()) {
      return _compositeKey;
    } else {
      return Collections.singletonMap(_simpleKeyName, _simpleKey);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (_simpleKey == null && _compositeKey == null) {
      return null;
    }
    
    final StringBuilder str = new StringBuilder("<");
    if (_simpleKey != null) {
      if (_simpleKeyName != null) {
        str.append(_simpleKeyName).append('=');
      }
      str.append(_simpleKey).append('>');
    } else {
      for (final Map.Entry<String, String> entry : _compositeKey.entrySet()) {
        str.append(entry.getKey()).append('=');
        str.append(entry.getValue()).append(';');
      }
      if (str.length() > 0) {
        str.setCharAt(str.length() - 1, '>');
      } else {
        str.append('>');
      }
    }
    return str.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof KeyImpl) {
      final KeyImpl otherKey = (KeyImpl) obj;
      if (_simpleKey == null) {
        if (otherKey._simpleKey != null) {
          return false;
        }
        return this._compositeKey.equals(otherKey._compositeKey);
      } else {
        if (!_simpleKey.equals(otherKey._simpleKey)) {
          return false;
        }
        if (_simpleKeyName == null) {
          return otherKey._simpleKeyName == null;
        } else {
          return _simpleKeyName.equals(otherKey._simpleKeyName);
        }
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    if (_hashCode == 0) {
      // hash code calculated in local variables because exceptions in m-threads
      int hashCode = 0;
      if (_simpleKey == null) {
        for (final Map.Entry<String, String> entry : _compositeKey.entrySet()) {
          hashCode += entry.getKey().hashCode() + entry.getValue().hashCode();
        }
      } else {
        hashCode += _simpleKey.hashCode();
        if (_simpleKeyName != null) {
          hashCode += _simpleKeyName.hashCode();
        }
      }
      _hashCode = hashCode;
    }
    return _hashCode;
  }
}
