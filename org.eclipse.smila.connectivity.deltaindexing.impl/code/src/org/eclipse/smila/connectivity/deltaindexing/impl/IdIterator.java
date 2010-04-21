/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.impl;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.id.IdHandlingException;

/**
 * The Class IdIterator.
 */
public class IdIterator implements Iterator<Id> {

  /**
   * The _iterator.
   */
  private final Iterator<Id> _oldIndexIterator;

  /**
   * new index.
   */
  private final Set<Id> _newIndex;

  /**
   * Set of modified compound objects.
   */
  private final Set<Id> _modifiedCompounds;

  /**
   * The next.
   */
  private Id _next;

  /**
   * Instantiates a new data source iterator.
   * 
   * @param oldIndex
   *          the old index
   * @param newIndex
   *          the new index
   * @param modifiedCompounds
   *          the set of modified compound ids
   */
  public IdIterator(final Set<Id> oldIndex, final Set<Id> newIndex, final Set<Id> modifiedCompounds) {
    _oldIndexIterator = oldIndex.iterator();
    _newIndex = newIndex;
    _modifiedCompounds = modifiedCompounds;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    if (_next != null) {
      return true;
    }
    tryMoveNext();
    return _next != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#next()
   */
  public Id next() {
    if (_next != null) {
      return shift();
    }
    tryMoveNext();
    return shift();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    throw new UnsupportedOperationException("use delete(Id) method of delta indexing manager instead");
  }

  /**
   * Try move next.
   */
  private void tryMoveNext() {
    _next = null;
    while (_oldIndexIterator.hasNext()) {
      final Id id = _oldIndexIterator.next();
      if (!_newIndex.contains(id) && isNotCompoundOrCompoundIsModified(id)) {
        _next = id;
        return;
      }
    }
  }

  /**
   * Shift.
   * 
   * @return the id
   */
  private Id shift() {
    final Id next;
    next = _next;
    _next = null;
    return next;
  }

  /**
   * Determines if the id is part of a compound and if that compound was modified.
   * 
   * @param id
   *          the Id
   * @return true if the id is NOT part of a compound or the compound was modified, false otherwise
   */
  private boolean isNotCompoundOrCompoundIsModified(final Id id) {
    Id parentId = null;
    try {
      parentId = id.createCompoundId();
    } catch (IdHandlingException e) {
      ;// ignore IdHandlingException, it is thrown if no parent exists
    }

    if (parentId == null) {
      // id is not part of a compound
      return true;
    }

    // just for compound elements
    final boolean parentVisited = _newIndex.contains(parentId);
    final boolean modifiedCompound = _modifiedCompounds.contains(parentId);
    return (!parentVisited || modifiedCompound);
  }
}
