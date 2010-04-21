/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author August Georg Schmidt (BROX)
 * 
 * The <code>QueryScope</code> class is a data structure for defining which part of data must be resolved from a
 * index.
 */
public class QueryScope {
  /**
   * Name of index to collect the data from.
   */
  private String _indexName;

  /**
   * Start in result list (0-based).
   */
  private int _start;

  /**
   * Hits to return.
   */
  private int _hits;

  /**
   * Start selection of record (1-based).
   */
  private int _startSelection;

  /**
   * Records to select (1-based).
   */
  private int _recordsToSelect;

  /**
   * @param indexName
   *          Index name.
   * @param start
   *          Start in result list.
   * @param hits
   *          Hits to return.
   */
  public QueryScope(String indexName, int start, int hits) {

    if (indexName == null) {
      throw new NullPointerException("parameter must not be null [indexName]");
    }

    _indexName = indexName;
    _start = start;
    _hits = hits;
  }

  /**
   * @param indexName
   *          Index name.
   * @param start
   *          Start in result list.
   * @param hits
   *          Hits to return.
   * @param recordsToSelect
   *          Records to select.
   * @param startSelection
   *          Start selection.
   */
  public QueryScope(String indexName, int start, int hits, int recordsToSelect, int startSelection) {

    if (indexName == null) {
      throw new NullPointerException("parameter must not be null [indexName]");
    }

    _indexName = indexName;
    _start = start;
    _hits = hits;
    _recordsToSelect = recordsToSelect;
    _startSelection = startSelection;
  }

  /**
   * @return Returns the number of hits.
   */
  public int getHits() {
    return _hits;
  }

  /**
   * @return Returns the indexName.
   */
  public String getIndexName() {
    return _indexName;
  }

  /**
   * @return Returns the start.
   */
  public int getStart() {
    return _start;
  }

  /**
   * @param hitsToAdd
   *          Hits to add.
   */
  public void addHits(int hitsToAdd) {
    _hits = _hits + hitsToAdd;
  }

  /**
   * @return Returns the recordsToSelect.
   */
  public int getRecordsToSelect() {
    return _recordsToSelect;
  }

  /**
   * @param toSelect
   *          The recordsToSelect to set.
   */
  public void setRecordsToSelect(int toSelect) {
    _recordsToSelect = toSelect;
  }

  /**
   * @return Returns the startSelection.
   */
  public int getStartSelection() {
    return _startSelection;
  }

  /**
   * @param selection
   *          The startSelection to set.
   */
  public void setStartSelection(int selection) {
    _startSelection = selection;
  }

  /**
   * @see java.lang.Object#toString()
   * @return Object as String.
   */
  @Override
  public String toString() {
    final String objectAsString = new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).toString();
    return objectAsString;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   * @param object
   *          Object to compare.
   * @return Whether object is equals.
   */
  @Override
  public boolean equals(Object object) {
    boolean equals = false;
    if ((object != null) && (QueryScope.class.isAssignableFrom(object.getClass()))) {
      final QueryScope queryScope = (QueryScope) object;

      equals =
        new EqualsBuilder().append(_hits, queryScope.getHits()).append(_indexName, queryScope.getHits()).append(
          _recordsToSelect, queryScope.getRecordsToSelect()).append(_start, queryScope.getStart()).append(
          _startSelection, queryScope.getStartSelection()).isEquals();
    }
    return equals;
  }

  /**
   * @see java.lang.Object#hashCode()
   * @return Hash code.
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(_hits).append(_indexName).append(_recordsToSelect).append(_start).append(
      _startSelection).toHashCode();
  }

}
