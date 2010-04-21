/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author August Georg Schmidt (BROX) HitsPerIndex is a support data structure.
 */
public class HitsPerIndex {

  /**
   * Score for index hits.
   */
  private int _score;

  /**
   * Hits at score level.
   */
  private int _hits;

  /**
   * Name of index.
   */
  private String _indexName;

  /**
   * @param indexName
   *          Name of index.
   * @param score
   *          Score.
   * @param hits
   *          Hits at score level.
   */
  public HitsPerIndex(String indexName, int score, int hits) {

    if (indexName == null) {
      throw new NullPointerException("parameter must not be null [indexName]");
    }

    _indexName = indexName;
    _score = score;
    _hits = hits;
  }

  /**
   * @return Returns the hits.
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
   * @return Returns the score.
   */
  public int getScore() {
    return _score;
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

}
