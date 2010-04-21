/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates;

import java.io.InputStream;

import org.eclipse.smila.search.index.IndexConnection;
import org.eclipse.smila.search.utils.advsearch.ITermContent;
import org.eclipse.smila.search.utils.param.ParameterSet;
import org.eclipse.smila.search.utils.search.DField;

public abstract class NodeTransformer {

  private ParameterSet _parameterSet;

  private IndexConnection _indexConnection;

  public NodeTransformer() throws NodeTransformerException {
  }

  public abstract ITermContent transformNode(DField dField) throws NodeTransformerException;

  /**
   * @return DParameterSet
   */
  public ParameterSet getParameterSet() {
    return _parameterSet;
  }

  /**
   * Sets the parameterSet.
   * 
   * @param parameterSet
   *          The parameterSet to set
   */
  public void setParameterSet(ParameterSet parameterSet) {
    this._parameterSet = parameterSet;
  }

  /**
   * @return IndexConnection
   */
  public IndexConnection getIndexConnection() {
    return _indexConnection;
  }

  /**
   * Sets the IndexConnection.
   * 
   * @param indexConnection
   *          The IndexConnection to set
   */
  public void setIndexConnection(IndexConnection indexConnection) {
    this._indexConnection = indexConnection;
  }

  /**
   * Get parameter definition as InputStream.
   * 
   * @return Parameter definition as InputStream.
   */
  protected java.io.InputStream getParameterDefinition() {

    final String className = getClass().getSimpleName();

    final InputStream inputStream = getClass().getResourceAsStream(className + ".xml");
    return inputStream;
  }

}
