/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.datamodel.record;

import java.util.Iterator;

import org.eclipse.smila.datamodel.id.Id;

/**
 * SMILA record interface. This is the main object that describes entities that are created and processed in the system.
 * 
 * @author jschumacher
 * 
 */
public interface Record {

  /**
   * get record factory to use for adding objects to this record.
   * 
   * @return record factory for this record.
   */
  RecordFactory getFactory();

  /**
   * Get the ID of this record.
   * 
   * @return record ID
   */
  Id getId();

  /**
   * Set the ID of this record.
   * 
   * @param id
   *          new record ID
   */
  void setId(Id id);

  /**
   * Get the metadata object of this record. Contains attributes and annotations about this record.
   * 
   * @return the metadata object describing this record.
   */
  MObject getMetadata();

  /**
   * Set the metadata object of this record.
   * 
   * @param metadata
   *          new metadata object describing this record.
   */
  void setMetadata(MObject metadata);

  /**
   * check if this record has attachments.
   * 
   * @return true if this record has attachments, else false.
   */
  boolean hasAttachments();

  /**
   * check if this record has an attachment of the specified name.
   * 
   * @param name
   *          attachment name.
   * @return true if this record has an attachment for this name, else false.
   */
  boolean hasAttachment(String name);

  /**
   * get number of attachments.
   * 
   * @return number of attachments.
   */
  int attachmentSize();

  /**
   * Get iterator on names of attachments of this record. Returns empty iterator if record has no attachments.
   * 
   * @return iterator on attachment names.
   */
  Iterator<String> getAttachmentNames();

  /**
   * get attachment value for the specified name.
   * 
   * @param name
   *          attachment name.
   * @return attachment value.
   */
  byte[] getAttachment(String name);

  /**
   * set attachment value for the specified name.
   * 
   * @param name
   *          attachment name.
   * @param attachment
   *          attachment value.
   */
  void setAttachment(String name, byte[] attachment);

  /**
   * remove attachment for specified name.
   * 
   * @param name
   *          attachment name.
   */
  void removeAttachment(String name);

  /**
   * remove attachments.
   * 
   */
  void removeAttachments();

}
