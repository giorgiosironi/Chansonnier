/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import org.eclipse.smila.search.index.IndexAdmin;
import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.index.IndexException;
import org.eclipse.smila.search.plugin.IIndexAccess;

/**
 * Class to get access to index administration.
 * 
 * @author August Georg Schmidt (BROX)
 */
public class IndexAccess implements IIndexAccess {

  /**
   * Index admin.
   */
  private IndexAdmin _indexAdmin;

  /**
   * 
   */
  public IndexAccess() {
    super();

    _indexAdmin = new org.eclipse.smila.search.lucene.index.IndexAdmin();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.IIndexAccess#getIndexAdmin()
   */
  public IndexAdmin getIndexAdmin() {
    return _indexAdmin;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.plugin.IIndexAccess#getIndexConnection(java.lang.String)
   */
  public IndexConnection getIndexConnection(String indexName) throws IndexException {
    return new org.eclipse.smila.search.lucene.index.IndexConnection(indexName);
  }

}
