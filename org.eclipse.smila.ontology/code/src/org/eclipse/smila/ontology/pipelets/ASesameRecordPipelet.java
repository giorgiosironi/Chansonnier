/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.smila.ontology.pipelets;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.activator.Activator;
import org.eclipse.smila.ontology.records.SesameValueHelper;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.parameters.ParameterAccessor;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * abstract base class of Sesame-Record-IO-pipelets.
 *
 * @author jschumacher
 *
 */
public abstract class ASesameRecordPipelet implements SimplePipelet {

  /**
   * parameter/config property to specify a record filter. It is used to:
   * <ul>
   * <li>write only attributes in filter to Sesame
   * <li>read only attributes in filter from Sesame
   * </ul>
   * If no filter is specified, all attributes are written to the ontology, and all statements are used for attribute
   * values. The referenced filters must be configured in the blackboard configuration package.
   */
  public static final String PARAM_RECORDFILTER = "recordFilter";

  /**
   * parameter/config property to specify a non-default repository to use for IO.
   */
  public static final String PARAM_REPOSITORY = "sesameRepository";

  /**
   * my config.
   */
  protected PipeletConfiguration _configuration;

  /**
   * value creation helper.
   */
  protected SesameValueHelper _valueHelper = SesameValueHelper.INSTANCE;

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.IPipelet
   *      #configure(org.eclipse.smila.processing.configuration.PipeletConfiguration)
   */
  public void configure(final PipeletConfiguration configuration) throws ProcessingException {
    _configuration = configuration;
  }

  /**
   * @return active instance of {@link SesameOntologyManager} service.
   * @throws ProcessingException
   *           no instance could be found.
   */
  protected SesameOntologyManager getSesameOntologyManager() throws ProcessingException {
    final SesameOntologyManager manager = Activator.getService();
    if (manager == null) {
      throw new ProcessingException("no SesameOntologyManager service available.");
    }
    return manager;
  }

  /**
   * create parameter accessor to use.
   *
   * @param blackboard
   *          request blackboard.
   * @return parameter accessor.
   */
  protected ParameterAccessor getParameters(final Blackboard blackboard) {
    return new ParameterAccessor(blackboard).setPipeletConfiguration(_configuration);
  }

  /**
   * get repository connection as described by parameters, pipelet config or default.
   *
   * @param parameters
   *          parameter accessor
   * @return repository connection
   * @throws ProcessingException
   *           error accessing {@link SesameOntologyManager} service or the named repository.
   */
  protected RepositoryConnection getRepositoryConnection(final ParameterAccessor parameters)
    throws ProcessingException {
    final String repoName = parameters.getParameter(ASesameRecordPipelet.PARAM_REPOSITORY, null);
    final SesameOntologyManager manager = getSesameOntologyManager();
    if (repoName == null) {
      try {
        return manager.getDefaultConnection();
      } catch (final RepositoryException ex) {
        throw new ProcessingException("Could not get connection to default repository", ex);
      }
    }
    try {
      return manager.getConnection(repoName);
    } catch (final RepositoryException ex) {
      throw new ProcessingException("Could not get connection to repository '" + repoName + "'", ex);
    }
  }

  /**
   * resolve namespace prefixes in URI string.
   *
   * @param connection
   *          connection to ask for namespace resolving and use as value factory.
   * @param uriString
   *          an uri string that possibly contains a namespace prefix.
   * @return Sesame URI
   */
  protected URI createUri(final RepositoryConnection connection, final String uriString) {
    return _valueHelper.createUri(connection, uriString);
  }
}
