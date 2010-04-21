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

import java.util.List;
import java.util.Map;

/**
 * Interface of factories for Ids and keys.
 * 
 * @author jschumacher
 * 
 */
public interface IdFactory {
  /**
   * The Id factory for default Id/key implementation.
   */
  IdFactory DEFAULT_INSTANCE = IdFactoryCreator.createDefaultFactory();

  /**
   * create a new Id.
   * 
   * @param source
   *          data source name
   * @param key
   *          key of object in data source
   * @return record Id
   */
  Id createId(String source, Key key);

  /**
   * create an Id with a simple unnamed key.
   * 
   * @param source
   *          data source name
   * @param simpleKeyValue
   *          key value
   * @return record Id
   */
  Id createId(String source, String simpleKeyValue);

  /**
   * create an Id with a simple named key.
   * 
   * @param source
   *          data source name
   * @param simpleKeyName
   *          key name
   * @param simpleKeyValue
   *          key value
   * @return record Id
   */
  Id createId(String source, String simpleKeyName, String simpleKeyValue);

  /**
   * create an Id with a key created from the given name-value mapping. If the mapping contains only one key value, a
   * simple key is created. Else it will be a composite key.
   * 
   * @param source
   *          data source name
   * @param keyValues
   *          name-value mapping for key
   * @return record Id
   */
  Id createId(String source, Map<String, String> keyValues);

  /**
   * create a complete Id with optional element keys and fragment names.
   * 
   * @param source
   *          data source name
   * @param sourceKey
   *          name-value mapping for key
   * @param elementKeys
   *          container element keys. can be null or empty for non-container-element Ids
   * @param fragmentNames
   *          fragment names, can be null or empty for non-fragment Ids
   * @return record Id
   */
  Id createId(String source, Key sourceKey, List<Key> elementKeys, List<String> fragmentNames);

  /**
   * create a simple unnamed key.
   * 
   * @param simpleKeyValue
   *          key value
   * @return simple unnamed key
   */
  Key createKey(String simpleKeyValue);

  /**
   * create a simple named key.
   * 
   * @param simpleKeyName
   *          key name
   * @param simpleKeyValue
   *          key value
   * @return simple named key
   */
  Key createKey(String simpleKeyName, String simpleKeyValue);

  /**
   * create a key from the given name-value mapping. If the mapping contains only one key value, a simple key is
   * created. Else it will be a composite key.
   * 
   * @param keyValues
   *          name-value mapping for key
   * @return composite key
   */
  Key createKey(Map<String, String> keyValues);

}
