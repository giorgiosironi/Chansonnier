/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.index.access;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.eclipse.smila.search.index.IndexException;

/**
 * The Class AddDocumentOperation.
 */
public class AddDocumentOperation implements ISynchronizedOperation<IndexWriter, Void> {

  /**
   * The _document.
   */
  private final Document _document;

  /**
   * Instantiates a new adds the document operation.
   * 
   * @param document
   *          the document
   */
  public AddDocumentOperation(final Document document) {
    _document = document;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.index.access.ISynchronizedOperation#process(java.lang.Object)
   */
  public Void process(final IndexWriter object) throws IndexException {
    try {
      object.addDocument(_document);
    } catch (final Exception e) {
      throw new IndexException("Unable to add document to index", e);
    }
    return null;
  }
}
