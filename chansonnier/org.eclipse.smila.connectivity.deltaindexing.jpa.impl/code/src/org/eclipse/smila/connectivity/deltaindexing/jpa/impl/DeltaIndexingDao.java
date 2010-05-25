/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.deltaindexing.jpa.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.smila.datamodel.id.IdHandlingException;

/**
 * A JPA Entity to store DataSource locked state.
 */
//@NamedQueries( {
//  @NamedQuery(name = "DeltaIndexingDao.findSubCompounds", query = "SELECT d FROM DeltaIndexingDao d WHERE d._parentIdHash = :parentIdHash AND d._isCompound = 1"),
//  @NamedQuery(name = "DeltaIndexingDao.findObsoleteIdsBySource", query = "SELECT d FROM DeltaIndexingDao d WHERE d._dataSourceId = :source AND d._visited = 0 AND (d._parentIdHash IS NULL OR d._parentIdHash IN (SELECT dd._idHash FROM DeltaIndexingDao dd WHERE dd._isCompound = 1 AND (dd._visited = 0 OR dd._modified = 1)))"),
//  @NamedQuery(name = "DeltaIndexingDao.deleteBySource", query = "DELETE FROM DeltaIndexingDao d WHERE d._dataSourceId = :source"),
//  @NamedQuery(name = "DeltaIndexingDao.deleteAll", query = "DELETE FROM DeltaIndexingDao"),
//  @NamedQuery(name = "DeltaIndexingDao.resetFlags", query = "UPDATE DeltaIndexingDao d SET d._visited = 0, d._modified = 0 WHERE d._dataSourceId = :source"),
//  @NamedQuery(name = "DeltaIndexingDao.countEntries", query = "SELECT d._dataSourceId, COUNT(d._dataSourceId) FROM DeltaIndexingDao d GROUP BY d._dataSourceId"),
//  @NamedQuery(name = "DeltaIndexingDao.countEntriesBySource", query = "SELECT COUNT(d) FROM DeltaIndexingDao d WHERE d._dataSourceId = :source") })
public class DeltaIndexingDao implements Serializable {

  /**
   * Constant for the named query DeltaIndexingDao.findObsoleteIdsBySource. This query returns all ids of the data
   * source that were not visited (visited=false) and that either don't have a parentId (parentId == null) or have a
   * parent and that parent was not visited or was modified (visited == false || modified == true)
   */
  public static final String NAMED_QUERY_FIND_OBSOLETE_IDS_BY_SOURCE = "DeltaIndexingDao.findObsoleteIdsBySource";

  /**
   * Constant for the named query DeltaIndexingDao.findSubCompounds. It returns all elements of a compound that are
   * compounds themselves.
   */
  public static final String NAMED_QUERY_FIND_SUB_COMPOUNDS = "DeltaIndexingDao.findSubCompounds";

  /**
   * Constant for the named query DeltaIndexingDao.deleteBySource.
   */
  public static final String NAMED_QUERY_DELETE_BY_SOURCE = "DeltaIndexingDao.deleteBySource";

  /**
   * Constant for the named query DeltaIndexingDao.deleteAll.
   */
  public static final String NAMED_QUERY_DELETE_ALL = "DeltaIndexingDao.deleteAll";

  /**
   * Constant for the named query DeltaIndexingDao.resetFlags.
   */
  public static final String NAMED_QUERY_RESET_FLAGS = "DeltaIndexingDao.resetFlags";

  /**
   * Constant for the named query DeltaIndexingDao.countEntries.
   */
  public static final String NAMED_QUERY_COUNT_ENTRIES = "DeltaIndexingDao.countEntries";

  /**
   * Constant for the named query DeltaIndexingDao.countEntriesBySource.
   */
  public static final String NAMED_QUERY_COUNT_ENTRIES_BY_SOURCE = "DeltaIndexingDao.countEntriesBySource";

  /**
   * Constant for the entity attribute _source.
   */
  public static final String NAMED_QUERY_PARAM_SOURCE = "source";

  /**
   * Constant for the entity attribute _parentIdHash.
   */
  public static final String NAMED_QUERY_PARAM_PARENT_ID_HASH = "parentIdHash";

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = 6500268394234442139L;

  /**
   * The id hash value.
   */
//  @Id
//  @Column(name = "ID_HASH", length = 1024)
  private String _idHash;

  /**
   * The delta indexing hash value.
   */
//  @Column(name = "HASH", length = 1024)
  private String _hash;

  /**
   * The id of the data source.
   */
//  @Column(name = "SOURCE_ID")
  private String _dataSourceId;

  /**
   * The isCompound flag.
   */
//  @Column(name = "IS_COMPOUND")
  private boolean _isCompound;

  /**
   * The id hash value.
   */
//  @Column(name = "PARENT_ID_HASH", length = 1024)
  private String _parentIdHash;

  /**
   * The visited flag.
   */
//  @Column(name = "VISITED")
  private boolean _visited;

  /**
   * The modified flag.
   */
//  @Column(name = "MODIFIED")
  private boolean _modified;

  /**
   * The serialized components of a record.
   */
//  @Column(name = "ID", columnDefinition = "BLOB")
  private byte[] _serializedId;

  /**
   * Default Constructor, used by JPA.
   */
  protected DeltaIndexingDao() {
  }

  /**
   * Conversion Constructor. Converts a id, hash and visited flag into a DeltaIndexingDao.
   * 
   * @param id
   *          the record id
   * @param hash
   *          the delta indexing hash
   * @param isCompound
   *          boolean flag if the record is a compound or not
   * @param visited
   *          a boolean flag whether this id was already visited or not
   * @throws IOException
   *           if any error occurs
   */
  public DeltaIndexingDao(final org.eclipse.smila.datamodel.id.Id id, final String hash, final boolean isCompound,
    final boolean visited) throws IOException {
    if (id == null) {
      throw new IllegalArgumentException("parameter id is null");
    }
    if (hash == null) {
      throw new IllegalArgumentException("parameter hash is null");
    }
    _idHash = id.getIdHash();
    _hash = hash;
    _dataSourceId = id.getSource();
    _isCompound = isCompound;
    _visited = visited;

    try {
      final org.eclipse.smila.datamodel.id.Id parentId = id.createCompoundId();
      if (parentId != null) {
        _parentIdHash = parentId.getIdHash();
      }
    } catch (IdHandlingException e) {
      ; // ignore IdHandlingException, it is thrown if the id does not have a parent
    }

    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    final ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
    objectStream.writeObject(id);
    objectStream.close();

    _serializedId = byteStream.toByteArray();
  }

  /**
   * Converts this DeltaIndexingDao into a Id object.
   * 
   * @return a Id object.
   * @throws IOException
   *           if any exception occurs
   * @throws ClassNotFoundException
   *           if any exception occurs
   */
  public org.eclipse.smila.datamodel.id.Id toId() throws IOException, ClassNotFoundException {
    final ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(_serializedId));
    return (org.eclipse.smila.datamodel.id.Id) objectStream.readObject();
  }

  /**
   * Returns the id hash value.
   * 
   * @return the id hash value
   */
  public String getIdHash() {
    return _idHash;
  }

  /**
   * Returns the delta indexing hash.
   * 
   * @return the delta indexing hash
   */
  public String getHash() {
    return _hash;
  }

  /**
   * Returns the data source id.
   * 
   * @return the data source id
   */
  public String getDataSourceId() {
    return _dataSourceId;
  }

  /**
   * Returns if this delta indexing entry is a compound or not.
   * 
   * @return true if the this delta indexing is a compound, false otherwise
   */
  public boolean isCompound() {
    return _isCompound;
  }

  /**
   * Returns the parent id hash value.
   * 
   * @return the parent id hash value
   */
  public String getParentIdHash() {
    return _parentIdHash;
  }

  /**
   * Returns if this delta indexing entry was visited or not.
   * 
   * @return true if this delta indexing entry was visited, false otherwise
   */
  public boolean isVisited() {
    return _visited;
  }

  /**
   * Returns if this delta indexing entry was modified or not.
   * 
   * @return true if this delta indexing entry was modified, false otherwise
   */
  public boolean isModified() {
    return _modified;
  }

  /**
   * Sets the visited flag to true.
   */
  public void visit() {
    _visited = true;
  }

  /**
   * Sets both the modified and visited flag.
   */
  public void modifyAndVisit() {
    _visited = true;
    _modified = true;
  }
}
