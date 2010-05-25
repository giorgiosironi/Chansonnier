/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.blackboard;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Literal;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.tools.record.filter.RecordFilterNotFoundException;

/**
 * The Blackboard is a container for a set of records that are processed in a single step, e.g. to add a crawled record
 * to SMILA in connectivity or to process a single queue message. The blackboard can be connected to storage services
 * (binary storage, record storage) so that existing versions of records can be loaded before the processing starts and
 * the result records can be persisted afterwards. However, a blackboard can also be just "transient", i.e. not
 * connected to any storage services. Then all record data is lost after the processing is finished.
 */
public interface Blackboard {
  // blackboard life cycle methods
  /**
   * commit ALL records on this blackboard to storages (if any) and release resources. It is guaranteed that the
   * blackboard is empty after the operation, but it throws an exception if at least one record could not be committed
   * (e.g. written to storages) successfully. However, the implementation should try to commit as many records as
   * possible and not stop on the first failed commit and invalidate the remaining records.
   * 
   * @throws BlackboardAccessException
   *           at least one record could not be committed.
   */
  void commit() throws BlackboardAccessException;

  /**
   * remove ALL records from blackboard and release all associated resources. Nothing is written to connected storage
   * services.
   */
  void invalidate();

  // record life cycle methods
  /**
   * Create a new record with a given ID. No data is loaded from persistence, if a record with this ID exists already in
   * the storages it will be overwritten when the created record is committed. E.g. used by Connectivity to initialize
   * the record from incoming data. If the record with the given Id is already present on the blackboard, no action
   * happens.
   * 
   * @param id
   *          the id
   */
  void create(Id id);

  /**
   * Loads record data for the given ID from persistence (or prepare it to be loaded). Used by a client to indicate that
   * it wants to process this record.
   * 
   * @param id
   *          the id
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void load(Id id) throws BlackboardAccessException;

  /**
   * Adds a record to the blackboard.
   * 
   * @param record
   *          the record
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setRecord(Record record) throws BlackboardAccessException;

  /**
   * Creates a fragment of a given record, i.e. the record content is copied to a new ID derived from the given by
   * adding a frament name.
   * 
   * @param id
   *          the id
   * @param fragmentName
   *          the fragment name
   * 
   * @return the id
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Id split(Id id, String fragmentName) throws BlackboardAccessException;

  /**
   * All changes are written to the storages before the record is removed. The record is unlocked in the database.
   * 
   * @param id
   *          the id
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void commit(Id id) throws BlackboardAccessException;

  /**
   * The record is removed from the blackboard. The record is unlocked in the database. If the record was created new
   * (not overwritten) on this blackboard it should be removed from the storage completely.
   * 
   * @param id
   *          the id
   */
  void invalidate(Id id);

  // factory methods for attribute values and annotation objects
  // SMILALiteral and SMILAAnnotation are just interfaces,
  // blackboard implementation can determine the actual types for optimization
  /**
   * Creates the literal.
   * 
   * @param id
   *          the id
   * 
   * @return the literal
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Literal createLiteral(Id id) throws BlackboardAccessException;

  /**
   * Creates the annotation.
   * 
   * @param id
   *          the id
   * 
   * @return the annotation
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Annotation createAnnotation(Id id) throws BlackboardAccessException;

  // record content methods
  // - record metadata
  // for referenced types see interfaces proposed in [Data Model and XML Representation]
  // for string format of an attribute path see definition of AttributePath class below.
  // -- basic navigation
  /**
   * Returns iterator over attributes names or empty iterator if attribute was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the attribute names
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Iterator<String> getAttributeNames(Id id, Path path) throws BlackboardAccessException;

  /**
   * Returns iterator over attributes names by given record id.
   * 
   * @param id
   *          the id
   * 
   * @return the attribute names
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Iterator<String> getAttributeNames(Id id) throws BlackboardAccessException; // convenience for getAttributeNames(Id,

  // null);

  /**
   * Checks for attribute. index of last step is irrelevant.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasAttribute(Id id, Path path) throws BlackboardAccessException;

  // -- handling of literal values
  // navigation support
  /**
   * Checks if attribute at the given path has literals.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasLiterals(Id id, Path path) throws BlackboardAccessException;

  /**
   * Returns number of attribute literals at the given path. Returns 0 if attribute was not found.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the value size
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  int getLiteralsSize(Id id, Path path) throws BlackboardAccessException;

  /**
   * Get all literal attribute values of an attribute (index of last step is irrelevant) Returns empty list if attribute
   * was not found.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the values
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  List<Literal> getLiterals(Id id, Path path) throws BlackboardAccessException;

  /**
   * Get single attribute value, index is specified in last step of path, defaults to 0. Returns null if attribute or
   * literal was not found.
   * 
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the value
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Literal getLiteral(Id id, Path path) throws BlackboardAccessException;

  // modification of attribute values on blackboard
  /**
   * Sets the values.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param values
   *          the values
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setLiterals(Id id, Path path, List<Literal> values) throws BlackboardAccessException;

  // set single literal value, index of last attribute step is irrelevant
  /**
   * Sets the value.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param value
   *          the value
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setLiteral(Id id, Path path, Literal value) throws BlackboardAccessException;

  // add a single literal value, index of last attribute step is irrelevant
  /**
   * Adds the value.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param value
   *          the value
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void addLiteral(Id id, Path path, Literal value) throws BlackboardAccessException;

  // remove literal specified by index in last step
  /**
   * Removes the value.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeLiteral(Id id, Path path) throws BlackboardAccessException;

  // remove all literals of specified attribute
  /**
   * Removes the values.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeLiterals(Id id, Path path) throws BlackboardAccessException;

  // -- handling of sub-objects
  // navigation: check if an attribute has sub-objects and get their number.
  /**
   * Checks if attribute has sub metadata objects. Returns false if attribute was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasObjects(Id id, Path path) throws BlackboardAccessException;

  /**
   * Gets size of metadata objects contained in this attribute. Returns 0 if attribute was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the object size
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  int getObjectSize(Id id, Path path) throws BlackboardAccessException;

  // deleting sub-objects
  /**
   * Removes sub-objects specified by index in last step.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeObject(Id id, Path path) throws BlackboardAccessException;

  /**
   * Removes all sub-objects of specified attribute.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeObjects(Id id, Path path) throws BlackboardAccessException;

  // access semantic type of sub-object attribute values.
  // semantic types of literals are modified at literal object
  /**
   * Gets the metadata object semantic type. Return null if attribute was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the object semantic type
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  String getObjectSemanticType(Id id, Path path) throws BlackboardAccessException;

  /**
   * Sets the object semantic type.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param typename
   *          the typename
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setObjectSemanticType(Id id, Path path, String typename) throws BlackboardAccessException;

  // -- annotations of attributes and sub-objects.
  /**
   * Gets the annotation names. Returns empty iterator if annotatable object was not found at the given path.
   * Annotations of literals are accessed via the Literal object. Use null, "" or an empty attribute path to access root
   * annotations of record. Use PathStep.ATTRIBUTE_ANNOTATION as index in final step to access the annotation of the
   * attribute itself.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return the annotation names
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Iterator<String> getAnnotationNames(Id id, Path path) throws BlackboardAccessException;

  /**
   * Checks for annotations. Returns false if annotatable object was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasAnnotations(Id id, Path path) throws BlackboardAccessException;

  /**
   * Checks for annotation. Returns false if annotatable object was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasAnnotation(Id id, Path path, String name) throws BlackboardAccessException;

  /**
   * Gets the annotations. Returns empty list if annotatable object was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * 
   * @return the annotations
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  List<Annotation> getAnnotations(Id id, Path path, String name) throws BlackboardAccessException;

  /**
   * Gets the first annotation if it exists. Returns null if annotatable object was not found at the given path.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * 
   * @return the annotation
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Annotation getAnnotation(Id id, Path path, String name) throws BlackboardAccessException;

  /**
   * Sets the annotations.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * @param annotations
   *          the annotations
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setAnnotations(Id id, Path path, String name, List<Annotation> annotations) throws BlackboardAccessException;

  /**
   * Sets the annotation.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * @param annotation
   *          the annotation
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setAnnotation(Id id, Path path, String name, Annotation annotation) throws BlackboardAccessException;

  /**
   * Adds the annotation.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * @param annotation
   *          the annotation
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void addAnnotation(Id id, Path path, String name, Annotation annotation) throws BlackboardAccessException;

  /**
   * Removes the annotation.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * @param name
   *          the name
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeAnnotation(Id id, Path path, String name) throws BlackboardAccessException;

  /**
   * Removes the annotations.
   * 
   * @param id
   *          the id
   * @param path
   *          the path
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeAnnotations(Id id, Path path) throws BlackboardAccessException;

  // - record attachments
  /**
   * Checks for attachment.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasAttachment(Id id, String name) throws BlackboardAccessException;

  /**
   * Gets the attachment.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return the attachment
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  byte[] getAttachment(Id id, String name) throws BlackboardAccessException;

  /**
   * Gets the attachment as stream.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return the attachment as stream
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  InputStream getAttachmentAsStream(Id id, String name) throws BlackboardAccessException;

  /**
   * Gets attachment as file. Creates file containing copy of the attachment into blackboard bundle's working directory.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return File
   * 
   * @throws BlackboardAccessException
   *           BlackboardAccessException
   */
  File getAttachmentAsFile(final Id id, final String name) throws BlackboardAccessException;

  /**
   * Sets the attachment.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * @param attachment
   *          the attachment
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setAttachment(Id id, String name, byte[] attachment) throws BlackboardAccessException;

  /**
   * Sets the attachment from stream.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * @param attachmentStream
   *          the attachment stream
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setAttachmentFromStream(Id id, String name, InputStream attachmentStream) throws BlackboardAccessException;

  /**
   * Sets attachment from file.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * @param attachmentFile
   *          attachment file
   * 
   * @throws BlackboardAccessException
   *           BlackboardAccessException
   */
  void setAttachmentFromFile(Id id, String name, File attachmentFile) throws BlackboardAccessException;

  /**
   * Removes the attachment.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void removeAttachment(Id id, String name) throws BlackboardAccessException;

  // - notes methods
  /**
   * Checks for global note.
   * 
   * @param name
   *          the name
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasGlobalNote(String name) throws BlackboardAccessException;

  /**
   * Gets the global note.
   * 
   * @param name
   *          the name
   * 
   * @return the global note
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Serializable getGlobalNote(String name) throws BlackboardAccessException;

  /**
   * Sets the global note.
   * 
   * @param name
   *          the name
   * @param object
   *          the object
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setGlobalNote(String name, Serializable object) throws BlackboardAccessException;

  /**
   * Checks for record note.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return true, if successful
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  boolean hasRecordNote(Id id, String name) throws BlackboardAccessException;

  /**
   * Gets the record note.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * 
   * @return the record note
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Serializable getRecordNote(Id id, String name) throws BlackboardAccessException;

  /**
   * Sets the record note.
   * 
   * @param id
   *          the id
   * @param name
   *          the name
   * @param object
   *          the object
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void setRecordNote(Id id, String name, Serializable object) throws BlackboardAccessException;

  /**
   * Synchronize.
   * 
   * @param record
   *          the record
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  void synchronize(Record record) throws BlackboardAccessException;

  /**
   * Gets the record.
   * 
   * @param id
   *          the id
   * 
   * @return the record
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   */
  Record getRecord(Id id) throws BlackboardAccessException;

  /**
   * Gets the record.
   * 
   * @param id
   *          the id
   * @param filterName
   *          the filter name
   * 
   * @return the record
   * 
   * @throws BlackboardAccessException
   *           the blackboard access exception
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   */
  Record getRecord(Id id, String filterName) throws BlackboardAccessException, RecordFilterNotFoundException;

  /**
   * Filter record.
   * 
   * @param record
   *          the record
   * @param filterName
   *          the filter name
   * 
   * @return the record
   * 
   * @throws RecordFilterNotFoundException
   *           the record filter not found exception
   */
  Record filterRecord(final Record record, final String filterName) throws RecordFilterNotFoundException;

}
