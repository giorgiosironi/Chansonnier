/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.recordstorage.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;

/**
 * A JPA Entity to store Records.
 */
public class RecordDao implements Serializable {

  /**
   * Constant for the named query RecordDao.findBySource.
   */
  public static final String NAMED_QUERY_FIND_BY_SOURCE = "RecordDao.findBySource";

  /**
   * Constant for the entity attribute _source.
   */
  public static final String NAMED_QUERY_PARAM_SOURCE = "source";

  /**
   * Constant for the entity attribute _attributes.
   */
  public static final String ENTITY_MEMBER_ATTRIBUTES = "_attributes";

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 6500268394234442139L;

  /**
   * The string representation of a record Id.
   */
  private String _idHash;

  /**
   * The source attribute of the Id for easy querying over sources.
   */
  private String _source;

  /**
   * The serialized components of a record.
   */
  private byte[] _serializedRecord;

  /**
   * Default Constructor, used by JPA.
   */
  protected RecordDao() {
  }

  /**
   * Conversion Constructor. Converts a Record into a RecordDao object.
   * 
   * @param record
   *          a Record
   * @throws IOException
   *           if any exception occurs
   */
  public RecordDao(Record record) throws IOException {
    if (record == null) {
      throw new IllegalArgumentException("parameter record is null");
    }
    if (record.getId() == null) {
      throw new IllegalArgumentException("parameter record has not Id set");
    }

    final List<String> attachmentNames = new ArrayList<String>();
    final Iterator<String> names = record.getAttachmentNames();
    while (names.hasNext()) {
      attachmentNames.add(names.next());
    }

    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    final ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
    objectStream.writeObject(record.getId());
    objectStream.writeObject(record.getMetadata());
    objectStream.writeObject(attachmentNames);
    objectStream.close();

    _serializedRecord = byteStream.toByteArray();
    _idHash = record.getId().getIdHash();
    _source = record.getId().getSource();
  }

  /**
   * Converts this RecordDao into a Record object.
   * 
   * @return a Record object.
   * @throws IOException
   *           if any exception occurs
   * @throws ClassNotFoundException
   *           if any exception occurs
   */
  public Record toRecord() throws IOException, ClassNotFoundException {
    final ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(_serializedRecord));
    final org.eclipse.smila.datamodel.id.Id id = (org.eclipse.smila.datamodel.id.Id) objectStream.readObject();
    final MObject metadata = (MObject) objectStream.readObject();
    final List<String> attachmentNames = (List<String>) objectStream.readObject();
    final Record record = RecordFactory.DEFAULT_INSTANCE.createRecord();
    record.setId(id);
    record.setMetadata(metadata);
    for (String name : attachmentNames) {
      record.setAttachment(name, null);
    }
    objectStream.close();
    return record;
  }

}
