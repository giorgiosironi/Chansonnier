/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class GetDocumentOperation.
 */
public class GetDocumentOperation implements ISynchronizedOperation<IndexSearcher, Document> {

  /**
   * The _key.
   */
  private final String _key;

  /**
   * Instantiates a new gets the document operation.
   * 
   * @param key
   *          the key
   */
  public GetDocumentOperation(final String key) {
    _key = key;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation#process(java.lang.Object)
   */
  public Document process(final IndexSearcher object) throws IndexException {
    Hits hits;
    try {
      hits = object.search(new TermQuery(new Term("##key", _key)));
    } catch (final IOException e) {
      throw new IndexException("Unable to search document [" + _key + "]", e);
    }
    if (hits.length() == 0) {
      throw new IndexException("can not locate document [" + _key + "]");
    } else if (hits.length() > 1) {
      throw new IndexException("duplicate key [" + _key + "] found in index (" + hits.length() + " occurrences)");
    }
    try {
      return hits.doc(0);
    } catch (final Throwable e) {
      throw new IndexException("Unable to fetch first document [" + _key + "]", e);
    }
  }
}
