/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.  
 * All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * Default implementation of SMILA Records.
 * 
 * @author jschumacher
 * 
 */
public class RecordImpl implements Record, Serializable {

  /**
   * record factory for this record implementation.
   */
  public static final RecordFactory FACTORY = new DefaultRecordFactoryImpl();

  /**
   * serializable, of course.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Id of record.
   */
  private Id _id;

  /**
   * metadata of record, i.e. attributes and annotations.
   */
  private MObject _metadata = new MObjectImpl();

  /**
   * attachments of record (used linked map to preserve order).
   */
  private final Map<String, byte[]> _attachments = new LinkedHashMap<String, byte[]>();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#getFactory()
   */
  public RecordFactory getFactory() {
    return FACTORY;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#attachmentSize()
   */
  public int attachmentSize() {
    return _attachments.size();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#getAttachment(java.lang.String)
   */
  public byte[] getAttachment(String name) {
    return _attachments.get(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#getAttachmentNames()
   */
  public Iterator<String> getAttachmentNames() {
    return _attachments.keySet().iterator();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#getId()
   */
  public Id getId() {
    return _id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#getMetadata()
   */
  public MObject getMetadata() {
    return _metadata;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#hasAttachments()
   */
  public boolean hasAttachments() {
    return !_attachments.isEmpty();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#hasAttachment(java.lang.String)
   */
  public boolean hasAttachment(String name) {
    return _attachments.containsKey(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#removeAttachment(java.lang.String)
   */
  public void removeAttachment(String name) {
    _attachments.remove(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#removeAttachments()
   */
  public void removeAttachments() {
    _attachments.clear();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#setAttachment(java.lang.String, java.io.Serializable)
   */
  public void setAttachment(String name, byte[] attachment) {
    _attachments.put(name, attachment);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#setId(org.eclipse.smila.datamodel.id.Id)
   */
  public void setId(Id id) {
    _id = id;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.datamodel.record.Record#setMetadata(org.eclipse.smila.datamodel.record.MObject)
   */
  public void setMetadata(MObject metadata) {
    _metadata = metadata;
  }

}
