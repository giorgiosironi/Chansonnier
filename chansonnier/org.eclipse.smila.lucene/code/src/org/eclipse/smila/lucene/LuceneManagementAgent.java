/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.lucene;

import org.eclipse.smila.management.DeclarativeServiceManagementAgent;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class LuceneManagementAgent.
 */
public class LuceneManagementAgent extends DeclarativeServiceManagementAgent<LuceneIndexService> {

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.LocatedManagementAgentBase#getCategory()
   */
  @Override
  protected String getCategory() {
    // return "Index";
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.ManagementAgent#getName()
   */
  @Override
  public String getName() {
    return "LuceneService";
  }

  /**
   * Checks if is index exists.
   * 
   * @param indexName
   *          the index name
   * 
   * @return true, if is index exists
   */
  public boolean isIndexExists(final String indexName) {
    try {
      return _service.isIndexExists(indexName);
    } catch (final Throwable e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Reorganize index.
   * 
   * @param indexName
   *          the index name
   */
  public void reorganizeIndex(final String indexName) {
    try {
      _service.reorganizeIndex(indexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Delete index.
   * 
   * @param indexName
   *          the index name
   */
  public void deleteIndex(final String indexName) {
    try {
      _service.deleteIndex(indexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Rename index.
   * 
   * @param oldIndexName
   *          the old index name
   * @param newIndexName
   *          the new index name
   */
  public void renameIndex(final String oldIndexName, final String newIndexName) {
    try {
      _service.renameIndex(oldIndexName, newIndexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Creates the index.
   * 
   * @param indexName
   *          the index name
   */
  public void createIndex(final String indexName) {
    // final DIndex dIndex;
    // try {
    // // TODO: encoding - "utf-16"?
    // dIndex = DataDictionaryController.decodeDIndex(dIndexString.getBytes());
    // } catch (final Throwable e) {
    // _log.error(e);
    // throw new RuntimeException(String.format(
    // "Error while deserializing DIndex from input string with exception:\n%s", e.getMessage()));
    // }
    try {
      _service.createIndex(indexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(String.format("Error while creating index [%s] with exception:\n%s", indexName, e
        .getMessage()));
    }
  }

  /**
   * Flushes the index.
   * 
   * @param indexName
   *          name of the index to flush
   */
  public void flushIndex(final String indexName) {
    try {
      _service.flushIndex(indexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(String.format("Error while flushing index [%s] with exception:\n%s", indexName, e
        .getMessage()));
    }
  }

  /**
   * Removes the write lock on an index if any exists.
   * 
   * @param indexName
   *          name of the index
   */
  public void removeWriteLock(final String indexName) {
    try {
      _service.removeWriteLock(indexName);
    } catch (final IndexException e) {
      _log.error(e);
      throw new RuntimeException(String.format("Error while remowing write.lock on index [%s] with exception:\n%s",
        indexName, e.getMessage()));
    }
  }
}
