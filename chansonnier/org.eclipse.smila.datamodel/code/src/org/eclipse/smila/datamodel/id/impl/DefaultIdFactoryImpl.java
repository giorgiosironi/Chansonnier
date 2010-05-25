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

package org.eclipse.smila.datamodel.id.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.id.Key;

/**
 * ID factory for the default ID/Key implementation.
 * 
 * @author jschumacher
 */
public class DefaultIdFactoryImpl implements IdFactory {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createId(java.lang.String, org.eclipse.smila.datamodel.id.Key)
   */
  public Id createId(String source, Key key) {
    return new IdImpl(source, key);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createId(java.lang.String, java.lang.String)
   */
  public Id createId(String source, String simpleKeyValue) {
    return new IdImpl(source, createKey(simpleKeyValue));
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createId(java.lang.String, java.lang.String, java.lang.String)
   */
  public Id createId(String source, String simpleKeyName, String simpleKeyValue) {
    return new IdImpl(source, createKey(simpleKeyName, simpleKeyValue));
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createId(java.lang.String, java.util.Map)
   */
  public Id createId(String source, Map<String, String> keyValues) {
    return new IdImpl(source, createKey(keyValues));
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createId(java.lang.String, org.eclipse.smila.datamodel.id.Key,
   *      java.util.List, java.util.List)
   */
  public Id createId(String source, Key sourceKey, List<Key> elementKeys, List<String> fragmentNames) {

    List<KeyImpl> elementKeyImpls = null;
    if (elementKeys != null && elementKeys.size() > 0) {
      elementKeyImpls = new ArrayList<KeyImpl>(elementKeys.size());
      for (Key elementKey : elementKeys) {
        elementKeyImpls.add(KeyImpl.ensureImpl(elementKey));
      }
    }
    return new IdImpl(source, KeyImpl.ensureImpl(sourceKey), elementKeyImpls, fragmentNames);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createKey(java.lang.String)
   */
  public KeyImpl createKey(String simpleKeyValue) {
    return new KeyImpl(simpleKeyValue);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createKey(java.lang.String, java.lang.String)
   */
  public KeyImpl createKey(String simpleKeyName, String simpleKeyValue) {
    return new KeyImpl(simpleKeyName, simpleKeyValue);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.IdFactory#createKey(java.util.Map)
   */
  public KeyImpl createKey(Map<String, String> keyValues) {
    return new KeyImpl(keyValues);
  }

}
