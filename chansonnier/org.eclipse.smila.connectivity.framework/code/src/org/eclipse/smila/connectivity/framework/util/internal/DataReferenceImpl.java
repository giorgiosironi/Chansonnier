/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.connectivity.framework.util.internal;

import org.eclipse.smila.connectivity.framework.CrawlerCallback;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.DataReference;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.InvalidTypeException;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * Default Implementation of the interface DataReference.
 * 
 * @see DataReference
 */
public class DataReferenceImpl implements DataReference {
  /**
   * The record id.
   */
  private Id _id;

  /**
   * The record hash.
   */
  private String _hash;

  /**
   * A reference to the CrawlerCallback.
   */
  private CrawlerCallback _callback;

  /**
   * The factory used to create new record objects.
   */
  private final RecordFactory _recordFactory = RecordFactory.DEFAULT_INSTANCE;

  /**
   * Conversion Constructor.
   * 
   * @param callback
   *          reference to a CrawlerCallback
   * @param id
   *          the record id
   * @param hash
   *          the record hash
   */
  public DataReferenceImpl(CrawlerCallback callback, Id id, String hash) {
    if (callback == null) {
      throw new IllegalArgumentException("Parameter callback must not be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("Parameter id must not be null");
    }
    if (hash == null) {
      throw new IllegalArgumentException("Parameter hash must not be null");
    }
    _callback = callback;
    _id = id;
    _hash = hash;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.DataReference#getId()
   */
  public Id getId() {
    return _id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.DataReference#getHash()
   */
  public String getHash() {
    return _hash;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.DataReference#getRecord()
   */
  public Record getRecord() throws CrawlerException, CrawlerCriticalException, InvalidTypeException {
    // create record
    final Record record = _recordFactory.createRecord();
    record.setId(_id);

    // set metadata
    record.setMetadata(_callback.getMObject(_id));

    // fill attachments
    final String[] attachmenNames = _callback.getAttachmentNames(_id);
    for (String name : attachmenNames) {
      record.setAttachment(name, _callback.getAttachment(_id, name));
    }
    return record;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.DataReference#dispose()
   */
  public void dispose() {
    _callback.dispose(_id);
  }
}
