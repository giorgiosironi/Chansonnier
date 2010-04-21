/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.ontology;

import java.util.List;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Interface of Sesame based ontology manager.
 * 
 * @author jschumacher
 * 
 */
public interface SesameOntologyManager {
  /**
   * name of containing bundle. used for accessing configs and workspace.
   */
  String BUNDLE_ID = "org.eclipse.smila.ontology";

  /**
   * create a new connection to the default repository.
   * 
   * @return connection to default repository.
   * @throws RepositoryException
   *           error creating connection
   */
  RepositoryConnection getDefaultConnection() throws RepositoryException;

  /**
   * create a new connection to the named repository.
   * 
   * @param name
   *          repository name.
   * @return connection to named repository.
   * @throws RepositoryException
   *           invalid name or error creating connection
   */
  RepositoryConnection getConnection(String name) throws RepositoryException;

  /**
   * 
   * @return names of configured repositories
   */
  List<String> getRepositoryNames();

}
