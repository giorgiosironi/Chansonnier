/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.id.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdHandlingException;
import org.eclipse.smila.datamodel.id.Key;
import org.eclipse.smila.utils.digest.DigestHelper;

/**
 * default implementation of SMILA record IDs.
 * 
 * @author jschumacher
 * 
 */
public class IdImpl implements Id {

  /**
   * Ids are serializable.
   */
  private static final long serialVersionUID = 2L;

  /**
   * data source name.
   */
  private final String _source;

  /**
   * source object key.
   */
  private final Key _key;

  /**
   * list of container element keys.
   */
  private List<KeyImpl> _elementKeys;

  /**
   * list of fragment names.
   */
  private List<String> _fragmentNames;

  /**
   * cache hashcode for performance reasons.
   */
  private int _hashCode;

  /**
   * cache Id hash for performance reasons.
   */
  private String _idHash;

  /**
   * cache toString result for performance reasons.
   */
  private String _toString;

  /**
   * create new Id.
   * 
   * @param sourceName
   *          data source name
   * @param sourceKey
   *          source object key
   */
  public IdImpl(final String sourceName, final Key sourceKey) {
    this._source = sourceName;
    this._key = sourceKey;
  }

  /**
   * create a complete Id with optional element keys and fragment names.
   * 
   * @param sourceName
   *          data source name
   * @param sourceKey
   *          name-value mapping for key
   * @param newElementKeys
   *          container element keys. can be null or empty for non-container-element Ids
   * @param newFragmentNames
   *          fragment names, can be null or empty for non-fragment Ids
   */
  public IdImpl(final String sourceName, final Key sourceKey, final List<KeyImpl> newElementKeys,
    final List<String> newFragmentNames) {
    this._source = sourceName;
    this._key = sourceKey;
    if (newElementKeys != null && !newElementKeys.isEmpty()) {
      this._elementKeys = Collections.unmodifiableList(new ArrayList<KeyImpl>(newElementKeys));
    }
    if (newFragmentNames != null && !newFragmentNames.isEmpty()) {
      this._fragmentNames = Collections.unmodifiableList(new ArrayList<String>(newFragmentNames));
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#getSource()
   */
  public String getSource() {
    return _source;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#getKey()
   */
  public Key getKey() {
    return _key;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#hasElementKeys()
   */
  public boolean hasElementKeys() {
    return _elementKeys != null && !_elementKeys.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#getElementKeys()
   */
  public List<? extends Key> getElementKeys() {
    if (_elementKeys == null) {
      return Collections.emptyList();
    }
    return _elementKeys;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#hasFragmentNames()
   */
  public boolean hasFragmentNames() {
    return _fragmentNames != null && !_fragmentNames.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#getFragmentNames()
   */
  public List<String> getFragmentNames() {
    if (_fragmentNames == null) {
      return Collections.emptyList();
    }
    return _fragmentNames;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#createElementId(java.lang.String)
   */
  public Id createElementId(final String elementName) throws IdHandlingException {
    final KeyImpl elementKey = new KeyImpl(elementName);
    return createElementId(elementKey);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#createElementId(org.eclipse.smila.datamodel.id.Key)
   */
  public Id createElementId(final Key elementKey) throws IdHandlingException {
    if (hasFragmentNames()) {
      throw new IdHandlingException("Cannot create an element Id from a fragment Id.");
    }
    final KeyImpl elementKeyImpl = KeyImpl.ensureImpl(elementKey);
    ArrayList<KeyImpl> subElementKeys = null;
    if (hasElementKeys()) {
      subElementKeys = new ArrayList<KeyImpl>(_elementKeys.size() + 1);
      subElementKeys.addAll(_elementKeys);
    } else {
      subElementKeys = new ArrayList<KeyImpl>(1);
    }
    subElementKeys.add(elementKeyImpl);
    return new IdImpl(_source, _key, subElementKeys, null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#createFragmentId(java.lang.String)
   */
  public Id createFragmentId(final String fragmentName) {
    ArrayList<String> subFragmentsNames = null;
    if (hasFragmentNames()) {
      subFragmentsNames = new ArrayList<String>(_fragmentNames.size() + 1);
      subFragmentsNames.addAll(_fragmentNames);
    } else {
      subFragmentsNames = new ArrayList<String>(1);
    }
    subFragmentsNames.add(fragmentName);
    return new IdImpl(_source, _key, _elementKeys, subFragmentsNames);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#createCompoundId()
   */
  public Id createCompoundId() throws IdHandlingException {
    if (!hasFragmentNames() && !hasElementKeys()) {
      throw new IdHandlingException("Cannot create a compound Id from a source object Id.");
    }
    List<KeyImpl> compoundElementKeys = null;
    List<String> compoundFragmentNames = null;
    if (hasFragmentNames()) {
      compoundElementKeys = _elementKeys;
      if (_fragmentNames.size() > 1) {
        compoundFragmentNames = _fragmentNames.subList(0, _fragmentNames.size() - 1);
      }
    } else if (_elementKeys.size() > 1) {
      compoundElementKeys = _elementKeys.subList(0, _elementKeys.size() - 1);
    }
    return new IdImpl(_source, _key, compoundElementKeys, compoundFragmentNames);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.id.Id#getIdHash()
   */
  public String getIdHash() {
    if (_idHash == null) {
      final String idString = toString();
      _idHash = DigestHelper.calculateDigest(idString);
    }
    return _idHash;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (_toString == null) {
      final StringBuilder str = new StringBuilder("src:").append(_source).append("|");
      str.append("key:").append(_key);
      if (_elementKeys != null) {
        for (final Key element : _elementKeys) {
          str.append("|elem:").append(element);
        }
      }
      if (_fragmentNames != null) {
        str.append("|frag:[");
        for (final Iterator<String> names = _fragmentNames.iterator(); names.hasNext();) {
          str.append(names.next());
          if (names.hasNext()) {
            str.append(";");
          } else {
            str.append("]");
          }
        }
      }
      _toString = str.toString();
    }
    return _toString;
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
    if (obj instanceof IdImpl) {
      final IdImpl otherId = (IdImpl) obj;
      if (!_source.equals(otherId._source)) {
        return false;
      }
      if (!_key.equals(otherId._key)) {
        return false;
      }
      if (_elementKeys == null) {
        if (otherId._elementKeys != null) {
          return false;
        }
      } else if (!_elementKeys.equals(otherId._elementKeys)) {
        return false;
      }
      if (_fragmentNames == null) {
        if (otherId._fragmentNames != null) {
          return false;
        }
      } else if (!_fragmentNames.equals(otherId._fragmentNames)) {
        return false;
      }
      return true;
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
      int hashCode = _source.hashCode() + _key.hashCode();
      if (_elementKeys != null) {
        for (final Key element : _elementKeys) {
          hashCode += element.hashCode();
        }
      }
      if (_fragmentNames != null) {
        for (final String fragment : _fragmentNames) {
          hashCode += fragment.hashCode();
        }
      }
      _hashCode = hashCode;
    }
    return _hashCode;
  }
}
