/***********************************************************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.ontology.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.ManagementRegistration;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.eclipse.smila.ontology.config.HttpStore;
import org.eclipse.smila.ontology.config.MemoryStore;
import org.eclipse.smila.ontology.config.NativeStore;
import org.eclipse.smila.ontology.config.RdbmsStore;
import org.eclipse.smila.ontology.config.RepositoryConfig;
import org.eclipse.smila.ontology.config.SesameConfiguration;
import org.eclipse.smila.ontology.config.Stackable;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.StackableSail;
import org.osgi.service.component.ComponentContext;

/**
 * Implementation of Sesame based ontology manager.
 *
 * @author jschumacher
 *
 */
public class SesameOntologyManagerImpl implements SesameOntologyManager {
  /**
   * name of service config file.
   */
  public static final String CONFIG_NAME = "sesameConfig.xml";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * parsed configuration.
   */
  private SesameConfiguration _configuration;

  /**
   * Management agent for this service.
   */
  private SesameOntologyAgent _agent;

  /**
   * The _agent location.
   */
  private ManagementAgentLocation _agentLocation;

  /**
   * current open repositories.
   */
  private final Map<String, Repository> _repositories = new HashMap<String, Repository>();

  /**
   * default constructor with tracing.
   */
  public SesameOntologyManagerImpl() {
    _log.trace("instance created");
  }

  /**
   * {@inheritDoc}
   *
   * @throws RepositoryException
   *
   * @see org.eclipse.smila.ontology.SesameOntologyManager#getConnection(java.lang.String)
   */
  public synchronized RepositoryConnection getConnection(final String name) throws RepositoryException {
    Repository repository = _repositories.get(name);
    if (repository == null) {
      final RepositoryConfig config = findRepositoryConfig(name);
      repository = openRepository(config);
      _repositories.put(name, repository);
    }
    return repository.getConnection();
  }

  /**
   * {@inheritDoc}
   *
   * @throws RepositoryException
   *
   * @see org.eclipse.smila.ontology.SesameOntologyManager#getDefaultConnection()
   */
  public RepositoryConnection getDefaultConnection() throws RepositoryException {
    if (_configuration == null) {
      throw new RepositoryException(
        "SesameOntologyManager does not have a configuration, restart service to initialize.");
    }
    return getConnection(_configuration.getDefault());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.ontology.SesameOntologyManager#getRepositoryNames()
   */
  public List<String> getRepositoryNames() {
    final List<String> result = new ArrayList<String>();
    if (_configuration != null) {
      final List<RepositoryConfig> configs = _configuration.getRepositoryConfig();
      if (configs != null) {
        for (final RepositoryConfig config : configs) {
          result.add(config.getName());
        }
      }
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  protected void activate(final ComponentContext context) {
    _log.trace("activating");
    try {
      final InputStream configStream = ConfigUtils.getConfigStream(BUNDLE_ID, CONFIG_NAME);
      if (configStream != null) {
        try {
          final Unmarshaller parser = SesameConfigurationHandler.createConfigurationUnmarshaller(true);
          _configuration = (SesameConfiguration) parser.unmarshal(configStream);
          _agent = new SesameOntologyAgent(this);
          _agentLocation = ManagementRegistration.INSTANCE.getCategory("Ontology").getLocation("Sesame");
          ManagementRegistration.INSTANCE.registerAgent(_agentLocation, _agent);
        } catch (final JAXBException ex) {
          _log.error("error reading " + CONFIG_NAME, ex);
        } finally {
          IOUtils.closeQuietly(configStream);
        }
      } else {
        _log.error("could not find configuration file " + CONFIG_NAME);
      }
    } catch (final Exception ex) {
      _log.error("could not find configuration file " + CONFIG_NAME);
    }
    if (_configuration == null) {
      _log.info("no configuration read, service is inactive. restart bundle to retry.");
    } else {
      _log.info("active!");
    }
  }

  /**
   * OSGi Declarative Services service deactivation method.
   *
   * @param context
   *          OSGi service component context.
   */
  protected void deactivate(final ComponentContext context) {
    _log.trace("deactivating");
    ManagementRegistration.INSTANCE.unregisterAgent(_agentLocation);
    _agent = null;
    for (final String repoName : _repositories.keySet()) {
      try {
        _log.debug("shutting down repository " + repoName);
        _repositories.get(repoName).shutDown();
      } catch (final Exception ex) {
        _log.error("error during shutdown of repository " + repoName, ex);
      }
    }
    _repositories.clear();
    _log.info("deactivated!");
  }



  /**
   * find named repository configuration.
   *
   * @param name
   *          repository name
   * @return repository configuration.
   * @throws RepositoryException
   *           no configuration exists (initialization failed) or invalid name.
   */
  private RepositoryConfig findRepositoryConfig(final String name) throws RepositoryException {
    if (_configuration == null) {
      throw new RepositoryException(
        "SesameOntologyManager does not have a configuration, restart service to initialize.");
    }
    for (final RepositoryConfig config : _configuration.getRepositoryConfig()) {
      if (name.equals(config.getName())) {
        return config;
      }
    }
    throw new RepositoryException("Unknown repository configuration '" + name + "'.");
  }

  /**
   * create repository from configuration.
   *
   * @param config
   *          repository configuration
   * @return repository.
   * @throws RepositoryException
   *           error creating the repository.
   */
  private Repository openRepository(final RepositoryConfig config) throws RepositoryException {
    try {
      final File dataDir = WorkspaceHelper.createWorkingDir(BUNDLE_ID, config.getName());
      Repository repository = null;
      if (config.getHttpStore() != null) {
        repository = openHttpRepository(config.getHttpStore(), dataDir);
      } else {
        Sail sail = null;
        if (config.getMemoryStore() != null) {
          sail = openMemoryStore(config.getMemoryStore(), dataDir);
        } else if (config.getNativeStore() != null) {
          sail = openNativeStore(config.getNativeStore(), dataDir);
        } else if (config.getRdbmsStore() != null) {
          sail = openRdbmsStore(config.getRdbmsStore(), dataDir);
        }
        if (config.getStackable() != null) {
          for (final Stackable stackable : config.getStackable()) {
            try {
              final StackableSail stackSail = (StackableSail) Class.forName(stackable.getClassname()).newInstance();
              stackSail.setBaseSail(sail);
              sail = stackSail;
            } catch (final Exception ex) {
              throw new RepositoryException("Stackable of class " + stackable.getClassname()
                + " could not be created.", ex);
            }
          }
        }
        if (sail != null) {
          repository = new SailRepository(sail);
          repository.initialize();
        }
      }
      return repository;
    } catch (final IOException ex) {
      throw new RepositoryException("Could not create repository workspace directory " + config.getName(), ex);
    }
  }

  /**
   * create a native store instance. It is not yet initialized, only configured.
   *
   * @param config
   *          config of native store
   * @param dataDir
   *          data directory for this store
   * @return configured, but uninitialized native store
   */
  private Sail openNativeStore(final NativeStore config, final File dataDir) {
    final org.openrdf.sail.nativerdf.NativeStore natStore = new org.openrdf.sail.nativerdf.NativeStore(dataDir);
    natStore.setTripleIndexes(config.getIndexes());
    natStore.setForceSync(config.isForceSync());
    return natStore;
  }

  /**
   * create a memory store instance. It is not yet initialized, only configured.
   *
   * @param config
   *          config of memory store
   * @param dataDir
   *          data directory for this store
   * @return configured, but uninitialized memory store
   */
  private Sail openMemoryStore(final MemoryStore config, final File dataDir) {
    final org.openrdf.sail.memory.MemoryStore memStore = new org.openrdf.sail.memory.MemoryStore(dataDir);
    memStore.setPersist(config.isPersist());
    memStore.setSyncDelay(config.getSyncDelay());
    return memStore;
  }

  /**
   * create a database store instance. It is not yet initialized, only configured.
   *
   * @param config
   *          config of database store
   * @param dataDir
   *          data directory for this store
   * @return configured, but uninitialized database store
   * @throws RepositoryException
   *           error connecting to the database
   */
  private Sail openRdbmsStore(final RdbmsStore config, final File dataDir) throws RepositoryException {
    try {
      org.openrdf.sail.rdbms.RdbmsStore dbStore;
      if (config.getUser() != null && config.getPassword() != null) {
        dbStore =
          new org.openrdf.sail.rdbms.RdbmsStore(config.getDriver(), config.getUrl(), config.getUser(), config
            .getPassword());
      } else {
        dbStore = new org.openrdf.sail.rdbms.RdbmsStore(config.getDriver(), config.getUrl());
      }
      dbStore.setDataDir(dataDir);
      dbStore.setMaxNumberOfTripleTables(config.getMaxTripleTables());
      dbStore.setIndexed(config.isIndexed());
      dbStore.setSequenced(config.isSequenced());
      return dbStore;
    } catch (final SailException ex) {
      throw new RepositoryException(ex.getMessage(), ex);
    }
  }

  /**
   * create a remote HTTP repository instance. It is not yet initialized, only configured.
   *
   * @param config
   *          config of memory store
   * @param dataDir
   *          data directory for this store
   * @return configured, but uninitialized memory store
   */
  private Repository openHttpRepository(final HttpStore config, final File dataDir) {
    Repository repository;
    final HTTPRepository httpRepository = new HTTPRepository(config.getUrl(), config.getRepositoryId());
    httpRepository.setDataDir(dataDir);
    if (config.getUser() != null && config.getPassword() != null) {
      httpRepository.setUsernameAndPassword(config.getUser(), config.getPassword());
    }
    repository = httpRepository;
    return repository;
  }

}
