/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class DeleteDocumentsOperation.
 */
public class DeleteDocumentsOperation implements ISynchronizedOperation<IndexWriter, Void> {

  /**
   * The _term.
   */
  private final Term _term;

  /**
   * Instantiates a new exists operation.
   * 
   * @param term
   *          the term
   */
  public DeleteDocumentsOperation(final Term term) {
    _term = term;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation#process(java.lang.Object)
   */
  public Void process(final IndexWriter object) throws IndexException {
    try {
      object.deleteDocuments(_term);
    } catch (final Exception e) {
      throw new IndexException("Unable to delete documents by term [" + _term.text() + "]", e);
    }
    return null;
  }

}
