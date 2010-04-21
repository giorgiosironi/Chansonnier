/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.searchresult;

import java.io.Serializable;
import java.util.List;

import org.eclipse.smila.datamodel.record.Record;

/**
 * Result container for Lucene results.
 */
public class LuceneSearchResult implements Serializable {

  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = -5964551886053841182L;

  /**
   * The name of the index that produced this result.
   */
  private String _indexName;

  /**
   * The DHitDistribution.
   */
  private DHitDistribution _hitDistribution;

  /**
   * The record result list.
   */
  private List<Record> _resultList;

  /**
   * Returns the name of the index that produced this result.
   * 
   * @return the name of the index that produced this result.
   */
  public String getIndexName() {
    return _indexName;
  }

  /**
   * Set the name of the index that produced this result.
   * 
   * @param name
   *          the index name
   */
  public void setIndexName(String name) {
    _indexName = name;
  }

  /**
   * Returns the hit distribution.
   * 
   * @return the DHitDistribution
   */
  public DHitDistribution getHitDistribution() {
    return _hitDistribution;
  }

  /**
   * Set the hit distribution.
   * 
   * @param distribution
   *          the hit distribution
   */
  public void setHitDistribution(DHitDistribution distribution) {
    _hitDistribution = distribution;
  }

  /**
   * Get the result list.
   * 
   * @return the result list
   */
  public List<Record> getResultList() {
    return _resultList;
  }

  /**
   * Set the result list.
   * 
   * @param list
   *          the result list
   */
  public void setResultList(List<Record> list) {
    _resultList = list;
  }
}
