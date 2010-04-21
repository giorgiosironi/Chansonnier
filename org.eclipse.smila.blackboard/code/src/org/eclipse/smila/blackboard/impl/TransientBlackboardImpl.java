/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.blackboard.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.blackboard.path.PathStep;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Attribute;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.MObject;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.datamodel.tools.DatamodelCopyUtils;
import org.eclipse.smila.datamodel.tools.DatamodelSerializationUtils;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterHelper;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterNotFoundException;
import org.eclipse.smila.utils.digest.DigestHelper;

/**
 * The Class BlackboardServiceImpl.
 */
public class TransientBlackboardImpl implements Blackboard {
  /**
   * The empty iterator.
   */
  @SuppressWarnings("unchecked")
  private static final Iterator<String> EMPTY_STRING_ITERATOR = ((Set<String>) Collections.EMPTY_SET).iterator();

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * Blackboard records map.
   */
  private final Map<Id, Record> _recordMap = new HashMap<Id, Record>();

  /**
   * Cached attachments map.
   */
  private final Map<Id, Map<String, File>> _attachmentMap = new HashMap<Id, Map<String, File>>();

  /**
   * Global notes map.
   */
  private final Map<String, Serializable> _globalNotes = new HashMap<String, Serializable>();

  /**
   * Record notes map.
   */
  private final Map<Id, Map<String, Serializable>> _recordNotesMap = new HashMap<Id, Map<String, Serializable>>();

  /**
   * The record filter helper.
   */
  private final RecordFilterHelper _filterHelper;

  /**
   * Directory where cached attachments are stored.
   */
  private final File _attachmentsTempDir;

  /**
   * create instance.
   *
   * @param filterHelper
   *          record filter manager.
   * @param attachmentsTempDir
   *          directory to use for temporary attachment files.
   */
  public TransientBlackboardImpl(final RecordFilterHelper filterHelper, final File attachmentsTempDir) {
    super();
    _filterHelper = filterHelper;
    _attachmentsTempDir = attachmentsTempDir;
  }

  /**
   * {@inheritDoc}
   */
  public void commit() throws BlackboardAccessException {
    // try to commit each single record.
    final int numberOfRecords = _recordMap.size();
    int commitFailures = 0;
    for (final Id id : getIds()) {
      try {
        commit(id);
      } catch (final Exception ex) {
        commitFailures++;
        _log.error("failed to commit " + id, ex);
      }
    }
    // make sure that the blackboard is empty afterwards.
    invalidate();
    if (commitFailures > 0) {
      throw new BlackboardAccessException("Failed to commit " + commitFailures + " of " + numberOfRecords
        + " records on blackboard, see log for IDs.");
    }
  }

  /**
   * {@inheritDoc}
   */
  public void invalidate() {
    // invalidate each single record
    for (final Id id : getIds()) {
      try {
        invalidate(id);
      } catch (final Exception ex) {
        _log.error("failed to invalidate " + id, ex);
      }
    }
    // just to be sure. Usually each of these files should have been removed in the record-invalidations already.
    for (final Map<String, File> attachmentFiles : _attachmentMap.values()) {
      for (final File attachmentFile : attachmentFiles.values()) {
        FileUtils.deleteQuietly(attachmentFile);
      }
    }
    _recordMap.clear();
    _globalNotes.clear();
    _recordNotesMap.clear();
    _attachmentMap.clear();
  }

  // record life cycle methods
  /**
   * {@inheritDoc}
   */
  public void create(final Id id) {
    Record record = null;
    if (id == null) {
      throw new IllegalArgumentException("Record Id cannot be null");
    }
    if (!_recordMap.containsKey(id)) {
      record = RecordFactory.DEFAULT_INSTANCE.createRecord();
      record.setId(id);
      _recordMap.put(id, record);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void load(final Id id) throws BlackboardAccessException {
    create(id);
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public void setRecord(final Record record) throws BlackboardAccessException {
    final Id id = record.getId();
    synchronized (_recordMap) {
      // TODO: what to do if record was already in the bb cache?
      _recordMap.put(id, record);
    }
  }

  /**
   * {@inheritDoc}
   */
  public Id split(final Id id, final String fragmentName) throws BlackboardAccessException {
    final Id splittedId = id.createFragmentId(fragmentName);
    final Record record = _recordMap.get(id);
    final Record splitRecord = RecordFactory.DEFAULT_INSTANCE.createRecord();
    splitRecord.setId(splittedId);
    splitRecord.setMetadata(DatamodelCopyUtils.cloneMObject(record.getMetadata(), RecordFactory.DEFAULT_INSTANCE));
    _recordMap.put(splittedId, splitRecord);
    // Copy record notes to the splitted id
    if (_recordNotesMap.containsKey(id)) {
      _recordNotesMap.put(splittedId, _recordNotesMap.get(id));
    }
    return splittedId;
  }

  /**
   * {@inheritDoc}
   */
  public void commit(final Id id) throws BlackboardAccessException {
    invalidate(id);
  }

  /**
   * {@inheritDoc}
   */
  public void invalidate(final Id id) {
    // remove cached attachments files
    final Map<String, File> recordFileAttachments = _attachmentMap.get(id);
    if (recordFileAttachments != null) {
      final Set<String> attachmentNames = recordFileAttachments.keySet();
      for (final String attachmentName : attachmentNames) {
        final File attachment = recordFileAttachments.get(attachmentName);
        FileUtils.deleteQuietly(attachment);
      }
    }
    _recordNotesMap.remove(id);
    _attachmentMap.remove(id);
    _recordMap.remove(id);
  }

  // factory methods for attribute values and annotation objects
  /**
   * {@inheritDoc}
   */
  public Literal createLiteral(final Id id) throws BlackboardAccessException {
    final Record record = getRecord(id);
    final Literal literal = record.getFactory().createLiteral();
    return literal;
  }

  /**
   * {@inheritDoc}
   */
  public Annotation createAnnotation(final Id id) throws BlackboardAccessException {
    final Record record = getRecord(id);
    final Annotation annotation = record.getFactory().createAnnotation();
    return annotation;
  }

  // record content methods
  /**
   * {@inheritDoc}
   */
  public Iterator<String> getAttributeNames(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      final int index = getLastIndex(path);
      final List<MObject> mObjects = attribute.getObjects();
      if (mObjects.size() > index) {
        return mObjects.get(index).getAttributeNames();
      }
    }
    return EMPTY_STRING_ITERATOR;
  }

  /**
   * {@inheritDoc}
   */
  public Iterator<String> getAttributeNames(final Id id) throws BlackboardAccessException {
    final Record record = getRecord(id);
    return record.getMetadata().getAttributeNames();
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAttribute(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    return attribute != null;
  }

  // -- handling of literal values
  // navigation support
  /**
   * {@inheritDoc}
   */
  public boolean hasLiterals(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    return attribute != null && attribute.hasLiterals();
  }

  /**
   * {@inheritDoc}
   */
  public int getLiteralsSize(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      return attribute.literalSize();
    } else {
      return 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<Literal> getLiterals(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      return attribute.getLiterals();
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   */
  public Literal getLiteral(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    final int index = getLastIndex(path);
    if (attribute != null && index < attribute.literalSize()) {
      return attribute.getLiterals().get(index);
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setLiterals(final Id id, final Path path, final List<Literal> literals)
    throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path, true);
    attribute.setLiterals(literals);
  }

  /**
   * {@inheritDoc}
   */
  public void setLiteral(final Id id, final Path path, final Literal literal) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path, true);
    attribute.setLiteral(literal);
  }

  /**
   * {@inheritDoc}
   */
  public void addLiteral(final Id id, final Path path, final Literal literal) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path, true);
    attribute.addLiteral(literal);
  }

  /**
   * {@inheritDoc}
   */
  public void removeLiteral(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path, true);
    final int index = getLastIndex(path);
    final List<Literal> literals = attribute.getLiterals();
    if (index < literals.size()) {
      literals.remove(index);
      attribute.setLiterals(literals);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void removeLiterals(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path, true);
    attribute.removeLiterals();
  }

  // -- handling of sub-objects
  /**
   * {@inheritDoc}
   */
  public boolean hasObjects(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      return attribute.hasObjects();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public int getObjectSize(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      return attribute.getObjects().size();
    }
    return 0;
  }

  // -- deleting sub-objects
  /**
   * {@inheritDoc}
   */
  public void removeObject(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      final int index = getLastIndex(path);
      final List<MObject> objects = attribute.getObjects();
      if (objects.size() > index) {
        objects.remove(index);
        attribute.setObjects(objects);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void removeObjects(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    if (attribute != null) {
      attribute.removeObjects();
    }
  }

  // -- access semantic type of sub-object attribute values.
  /**
   * {@inheritDoc}
   */
  public String getObjectSemanticType(final Id id, final Path path) throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    final int index = getLastIndex(path);
    if (attribute != null && attribute.getObjects().size() > index) {
      return attribute.getObjects().get(index).getSemanticType();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void setObjectSemanticType(final Id id, final Path path, final String typeName)
    throws BlackboardAccessException {
    final Attribute attribute = getAttributeByPath(id, path);
    final int index = getLastIndex(path);
    attribute.getObjects().get(index).setSemanticType(typeName);
  }

  // -- annotations of attributes and sub-objects.
  /**
   * {@inheritDoc}
   */
  public Iterator<String> getAnnotationNames(final Id id, final Path path) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    if (annotatable != null) {
      return annotatable.getAnnotationNames();
    }
    return EMPTY_STRING_ITERATOR;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAnnotation(final Id id, final Path path, final String name) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    return annotatable != null && annotatable.hasAnnotation(name);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasAnnotations(final Id id, final Path path) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    return annotatable != null && annotatable.hasAnnotations();
  }

  /**
   * {@inheritDoc}
   */
  public List<Annotation> getAnnotations(final Id id, final Path path, final String name)
    throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    if (annotatable != null) {
      return (List<Annotation>) annotatable.getAnnotations(name);
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public Annotation getAnnotation(final Id id, final Path path, final String name) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    if (annotatable != null) {
      return annotatable.getAnnotation(name);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void setAnnotations(final Id id, final Path path, final String name, final List<Annotation> annotations)
    throws BlackboardAccessException {
    getAnnotableObject(id, path, true).setAnnotations(name, annotations);
  }

  /**
   * {@inheritDoc}
   */
  public void setAnnotation(final Id id, final Path path, final String name, final Annotation annotation)
    throws BlackboardAccessException {
    getAnnotableObject(id, path, true).setAnnotation(name, annotation);
  }

  /**
   * {@inheritDoc}
   */
  public void addAnnotation(final Id id, final Path path, final String name, final Annotation annotation)
    throws BlackboardAccessException {
    getAnnotableObject(id, path, true).addAnnotation(name, annotation);
  }

  /**
   * {@inheritDoc}
   */
  public void removeAnnotation(final Id id, final Path path, final String name) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    if (annotatable != null) {
      annotatable.removeAnnotations(name);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void removeAnnotations(final Id id, final Path path) throws BlackboardAccessException {
    final Annotatable annotatable = getAnnotatableObject(id, path);
    if (annotatable != null) {
      annotatable.removeAnnotations();
    }
  }

  // - record attachments
  /**
   * {@inheritDoc}
   */
  public boolean hasAttachment(final Id id, final String name) throws BlackboardAccessException {
    // replaced by getRecord because errors...
    final Record record = getRecord(id);
    // final Record record = getCachedRecord(id);
    return record.hasAttachment(name);
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public byte[] getAttachment(final Id id, final String name) throws BlackboardAccessException {
    final Record record = getRecord(id);
    // TODO: other methods return null if result object is missing. why not this?
    if (!record.hasAttachment(name)) {
      throw new BlackboardAccessException("Record with idHash = " + id.getIdHash()
        + " doesn't have the attachment [" + name + "]");
    }
    return record.getAttachment(name);
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public InputStream getAttachmentAsStream(final Id id, final String name) throws BlackboardAccessException {
    return new ByteArrayInputStream(getAttachment(id, name));
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public File getAttachmentAsFile(final Id id, final String name) throws BlackboardAccessException {
    synchronized (_attachmentMap) {
      File attachmentFile = null;
      if (_attachmentMap.get(id) != null) {
        attachmentFile = _attachmentMap.get(id).get(name);
        if (attachmentFile != null) {
          return attachmentFile;
        }
      }
      try {
        attachmentFile = new File(_attachmentsTempDir, getAttachmentId(id, name));
        FileUtils.writeByteArrayToFile(attachmentFile, getAttachment(id, name));
        // put attachment into cache
        Map<String, File> recordAtttachmentFiles = _attachmentMap.get(id);
        if (recordAtttachmentFiles == null) {
          recordAtttachmentFiles = new HashMap<String, File>();
        }
        recordAtttachmentFiles.put(name, attachmentFile);
        _attachmentMap.put(id, recordAtttachmentFiles);
      } catch (final IOException ex) {
        throw new BlackboardAccessException("Error getting attachment as file, record id: " + id.getIdHash(), ex);
      }
      return attachmentFile;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public void setAttachment(final Id id, final String name, final byte[] attachment)
    throws BlackboardAccessException {
    final Record record = getRecord(id);
    checkCachedFileAttachment(id, name);
    record.setAttachment(name, attachment);
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public void setAttachmentFromStream(final Id id, final String name, final InputStream attachmentStream)
    throws BlackboardAccessException {
    try {
      final Record record = getRecord(id);
      checkCachedFileAttachment(id, name);
      record.setAttachment(name, IOUtils.toByteArray(attachmentStream));
    } catch (final IOException ex) {
      throw new BlackboardAccessException(ex);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public void setAttachmentFromFile(final Id id, final String name, final File attachmentFile)
    throws BlackboardAccessException {
    try {
      final Record record = getRecord(id);
      checkCachedFileAttachment(id, name);
      record.setAttachment(name, FileUtils.readFileToByteArray(attachmentFile));
    } catch (final IOException ex) {
      throw new BlackboardAccessException(ex);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.blackboard.Blackboard#removeAttachment(org.eclipse.smila.datamodel.id.Id, java.lang.String)
   */
  public void removeAttachment(final Id id, final String name) throws BlackboardAccessException {
    final Record record = getRecord(id);
    checkCachedFileAttachment(id, name);
    record.removeAttachment(name);
  }

  // - notes methods
  /**
   * {@inheritDoc}
   */
  public boolean hasGlobalNote(final String name) throws BlackboardAccessException {
    return _globalNotes.containsKey(name);
  }

  /**
   * {@inheritDoc}
   */
  public Serializable getGlobalNote(final String name) throws BlackboardAccessException {
    return _globalNotes.get(name);
  }

  /**
   * {@inheritDoc}
   */
  public void setGlobalNote(final String name, final Serializable object) throws BlackboardAccessException {
    _globalNotes.put(name, object);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasRecordNote(final Id id, final String name) throws BlackboardAccessException {
    final Map<String, Serializable> recordNotes = _recordNotesMap.get(id);
    if (recordNotes == null) {
      return false;
    } else {
      return recordNotes.containsKey(name);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws BlackboardAccessException
   */
  public Serializable getRecordNote(final Id id, final String name) throws BlackboardAccessException {
    final Map<String, Serializable> recordNotes = _recordNotesMap.get(id);
    if (recordNotes != null) {
      return recordNotes.get(name);
    } else {
      throw new BlackboardAccessException("Record note not found");
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setRecordNote(final Id id, final String name, final Serializable object)
    throws BlackboardAccessException {
    Map<String, Serializable> recordNotes = _recordNotesMap.get(id);
    if (recordNotes == null) {
      recordNotes = new HashMap<String, Serializable>();
      recordNotes.put(name, object);
      _recordNotesMap.put(id, recordNotes);
    } else {
      recordNotes.put(name, object);
      _recordNotesMap.put(id, recordNotes);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.blackboard.Blackboard#synchronize(org.eclipse.smila.datamodel.record.Record)
   */
  public void synchronize(final Record record) throws BlackboardAccessException {
    if (record == null) {
      throw new IllegalArgumentException("record must not be null!");
    }
    if (record.getId() == null) {
      throw new IllegalArgumentException("record.id must not be null!");
    }
    if (record.getMetadata() == null) {
      throw new IllegalArgumentException("record.metadata must not be null!");
    }
    Record oldRecord = null;
    synchronized (_recordMap) {
      try {
        // try to load
        this.load(record.getId());
        oldRecord = _recordMap.get(record.getId());
      } catch (final Exception e) {
        _log
          .warn("Error synchronizing record " + record.getId() + " with record storage, creating a new record", e);
      }
    }
    if (oldRecord == null) {
      // no old version exists or can be loaded -> use new record as is
      this.setRecord(record);
      return;
    }
    synchronized (oldRecord) {
      final MObject mobject = record.getMetadata();
      if (mobject.hasAttributes()) {
        DatamodelCopyUtils.copyAttributes(mobject, oldRecord.getMetadata(), record.getFactory());
      }
      for (final Iterator<String> attachmentNames = record.getAttachmentNames(); attachmentNames.hasNext();) {
        final String attachmentName = attachmentNames.next();
        oldRecord.setAttachment(attachmentName, record.getAttachment(attachmentName));
      }
    }
    this.setRecord(oldRecord);
  }

  /**
   * {@inheritDoc}
   *
   *
   * @see org.eclipse.smila.blackboard.Blackboard#getRecord(org.eclipse.smila.datamodel.id.Id)
   */
  public Record getRecord(final Id id) throws BlackboardAccessException {
    synchronized (_recordMap) {
      final Record record = _recordMap.get(id);
      if (record != null) {
        return record;
      }
      this.load(id);
      return _recordMap.get(id);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.blackboard.Blackboard#getRecord(org.eclipse.smila.datamodel.id.Id, java.lang.String)
   */
  public Record getRecord(final Id id, final String filterName) throws RecordFilterNotFoundException,
    BlackboardAccessException {
    final Record record = getRecord(id);
    return filterRecord(record, filterName);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.blackboard.Blackboard#filterRecord(org.eclipse.smila.datamodel.record.Record,
   *      java.lang.String)
   */
  public Record filterRecord(final Record record, final String filterName) throws RecordFilterNotFoundException {
    Record newRecord;
    newRecord = _filterHelper.filter(record, filterName);
    if (_log.isDebugEnabled()) {
      _log.debug("RECORD BEFORE FILTERING:" + DatamodelSerializationUtils.serialize2string(record));
      _log.debug("RECORD AFTER  FILTERING:" + DatamodelSerializationUtils.serialize2string(newRecord));
    }
    return newRecord;
  }

  // Utility methods
  /**
   * Calculates the attachment id that will be used as a key in binsary storage.
   *
   * @param id
   *          the id
   * @param name
   *          the name
   *
   * @return the attachment id
   */
  protected String getAttachmentId(final Id id, final String name) {
    return DigestHelper.calculateDigest(id.getIdHash() + name);
  }

  /**
   * Checks if there is cached File attachment for given Id and prevents overwriting it.
   *
   * @param id
   *          Id
   * @param name
   *          attachment name
   * @throws BlackboardAccessException
   *           file exists.
   */
  protected void checkCachedFileAttachment(final Id id, final String name) throws BlackboardAccessException {
    final Map<String, File> recordFileAttachments = _attachmentMap.get(id);
    if ((recordFileAttachments != null) && (recordFileAttachments.get(name) != null)) {
      throw new BlackboardAccessException("Attachment [" + name + "] of record with idHash=" + id.getIdHash()
        + " was previously loaded by getAttachmentAsFile method");
    }
  }

  /**
   * Returns the latest attribute specified by the given path; the last index is ignored.
   *
   * @param id
   *          the id
   * @param path
   *          the path
   * @return the attribute by path
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  protected Attribute getAttributeByPath(final Id id, final Path path) throws BlackboardAccessException {
    return getAttributeByPath(id, path, false);
  }

  /**
   * Returns the latest attribute specified by the given path; the last index is ignored. Optionally create the
   * attributes and MObjects on the path if not present.
   *
   * @param id
   *          the id
   * @param path
   *          the path
   * @param create
   *          true: create missing objects on path, false: return null, if path cannot be followed.
   *
   * @return the attribute by path
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  protected Attribute getAttributeByPath(final Id id, final Path path, final boolean create)
    throws BlackboardAccessException {
    // replaced by getRecord because errors...
    final Record record = getRecord(id);
    // final Record record = getCachedRecord(id);
    MObject metadata = record.getMetadata();
    final PathStep lastStep = getLastStep(path);
    for (int i = 0; i < path.length() - 1; i++) {
      final PathStep pathStep = path.get(i);
      final int index = pathStep.getIndex();
      final String name = pathStep.getName();
      final Attribute attribute = getAttributeByName(record, metadata, name, create);
      if (attribute != null) {
        if (attribute.objectSize() > index) {
          metadata = attribute.getObjects().get(index);
        } else if (create) {
          while (attribute.objectSize() <= index) {
            metadata = record.getFactory().createMetadataObject();
            attribute.addObject(metadata);
          }
        } else {
          return null;
        }
      }
    }
    return getAttributeByName(record, metadata, lastStep.getName(), create);
  }

  /**
   * get and optionally create named attribute of given mobject.
   *
   * @param record
   *          containing record providing access to factory.
   * @param metadata
   *          metadata object to access
   * @param name
   *          name of attribute
   * @param create
   *          true to create a missing attribute, false to return null for missing attributes
   *
   * @return named attribute if it exists or has been created, else null.
   */
  protected Attribute getAttributeByName(final Record record, final MObject metadata, final String name,
    final boolean create) {
    Attribute attribute = null;
    if (metadata.hasAttribute(name)) {
      attribute = metadata.getAttribute(name);
    } else if (create) {
      attribute = record.getFactory().createAttribute();
      metadata.setAttribute(name, attribute);
    }
    return attribute;
  }

  /**
   * Returns the last index in the path.
   *
   * @param path
   *          the path
   *
   * @return the last index
   */
  protected int getLastIndex(final Path path) {
    final PathStep lastStep = getLastStep(path);
    return lastStep.getIndex();
  }

  /**
   * Returns the last PathStep in the path.
   *
   * @param path
   *          the path
   *
   * @return the last step
   */
  protected PathStep getLastStep(final Path path) {
    return path.get(path.length() - 1);
  }

  /**
   * Checks if given path is empty.
   *
   * @param path
   *          the path
   *
   * @return true, if checks if is empty
   */
  protected boolean isEmpty(final Path path) {
    return ((path == null) || (path.isEmpty() || ("".equals(path.toString()))));
  }

  /**
   * Returns annotable object at the specified path.
   *
   * @param id
   *          the id
   * @param path
   *          the path
   *
   * @return the annotable object
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  protected Annotatable getAnnotatableObject(final Id id, final Path path) throws BlackboardAccessException {
    return getAnnotableObject(id, path, false);
  }

  /**
   * Returns annotable object at the specified path, optionally create missing objects on the path.
   *
   * @param id
   *          the id
   * @param path
   *          the path
   * @param create
   *          true to create missing objects on path
   *
   * @return the annotable object
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  protected Annotatable getAnnotableObject(final Id id, final Path path, final boolean create)
    throws BlackboardAccessException {
    if (isEmpty(path)) {
      // replaced by getRecord because errors...
      final Record record = getRecord(id);
      // final Record record = getCachedRecord(id);
      return record.getMetadata();
    }
    final Attribute attribute = getAttributeByPath(id, path, create);
    if (attribute != null) {
      final int index = getLastIndex(path);
      if (index == PathStep.ATTRIBUTE_ANNOTATION) {
        return attribute;
      } else {
        final List<MObject> mObjects = attribute.getObjects();
        if (mObjects.size() > index) {
          return mObjects.get(index);
        }
      }
    }
    return null;
  }

  /**
   * Returns cached record by id or null if record is not loaded into blackboard.
   *
   * @param id
   *          Record id
   *
   * @return Record
   */
  protected Record getCachedRecord(final Id id) {
    if (_log.isDebugEnabled()) {
      _log.debug("Getting cached record with idHash=" + id.getIdHash());
    }
    return _recordMap.get(id);
  }

  /**
   * check if record exists on blackboard.
   *
   * @param id
   *          record ID
   * @return true if a record with this ID is currently loaded.
   */
  protected boolean containsRecord(final Id id) {
    return _recordMap.containsKey(id);
  }

  /**
   * create a collection of all IDs of records on the blackboard.
   *
   * @return collection containing IDs of all currently loaded records.
   */
  protected Collection<Id> getIds() {
    return new ArrayList<Id>(_recordMap.keySet());
  }
}
