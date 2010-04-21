/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.ConnectivityException;
import org.eclipse.smila.connectivity.ConnectivityManager;
import org.eclipse.smila.connectivity.deltaindexing.DeltaIndexingManager;
import org.eclipse.smila.connectivity.framework.compound.CompoundManager;
import org.eclipse.smila.connectivity.framework.schema.ConfigurationLoader;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.connectivity.framework.schema.config.DeltaIndexingType;
import org.eclipse.smila.connectivity.framework.schema.config.DataConnectionID.DataConnectionType;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * Base class for Crawler- and AgentController. Contains shared functionality.
 */
public abstract class AbstractController {

  /**
   * service methods use read lock, deactivate needs write lock.
   */
  protected final ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(this.getClass());

  /**
   * A reference to a ConnectivityManager.
   */
  private ConnectivityManager _connectivityManager;

  /**
   * A reference to a DeltaIndexingManager.
   */
  private DeltaIndexingManager _diManager;

  /**
   * A reference to a CompoundManager.
   */
  private CompoundManager _compoundManager;

  /**
   * A List of references to ComponentFactory.
   */
  private java.util.List<ComponentFactory> _componentFactories;

  /**
   * @return the connectivityManager
   */
  public ConnectivityManager getConnectivityManager() {
    return _connectivityManager;
  }

  /**
   * Sets the ConnectivityManager. Used by OSGi Declarative Services.
   * 
   * @param connectivityManager
   *          the connectivityManager to set
   */
  public void setConnectivityManager(final ConnectivityManager connectivityManager) {
    _connectivityManager = connectivityManager;
  }

  /**
   * Set the _connectivityManager to null. Used by OSGi Declarative Services.
   * 
   * @param connectivityManager
   *          the connectivityManager to unset
   */
  public void unsetConnectivityManager(final ConnectivityManager connectivityManager) {
    if (_connectivityManager == connectivityManager) {
      _connectivityManager = null;
    }
  }

  /**
   * @return the DeltaIndexingManager
   */
  public DeltaIndexingManager getDeltaIndexingManager() {
    return _diManager;
  }

  /**
   * Sets the DeltaIndexingManager. Used by OSGi.
   * 
   * @param diManager
   *          the DeltaIndexingManager to set
   */
  public void setDeltaIndexingManager(final DeltaIndexingManager diManager) {
    if (_log.isTraceEnabled()) {
      _log.trace("Binding DeltaIndexingManager");
    }
    _diManager = diManager;
  }

  /**
   * Unsets the DeltaIndexingManager. Used by OSGi.
   * 
   * @param diManager
   *          the DeltaIndexingManager to unset
   */
  public void unsetDeltaIndexingManager(final DeltaIndexingManager diManager) {
    if (_log.isTraceEnabled()) {
      _log.trace("Unbinding DeltaIndexingManager");
    }
    if (_diManager == diManager) {
      _diManager = null;
    }
  }

  /**
   * @return the compoundManager
   */
  public CompoundManager getCompoundManager() {
    return _compoundManager;
  }

  /**
   * Sets the compoundManager. Used by OSGi Declarative Services.
   * 
   * @param compoundManager
   *          the compoundManager to set
   */
  public void setCompoundManager(final CompoundManager compoundManager) {
    _compoundManager = compoundManager;
  }

  /**
   * Set the compoundManager to null. Used by OSGi Declarative Services.
   * 
   * @param compoundManager
   *          the compoundManager to unset
   */
  public void unsetCompoundManager(final CompoundManager compoundManager) {
    if (_compoundManager == compoundManager) {
      _compoundManager = null;
    }
  }

  /**
   * Adds a ComponentFactory to the internal List of ComponentFactory. Used by OSGi Declarative Services.
   * 
   * @param factory
   *          the ComponentFactory to add
   */
  public void addComponentFactory(final ComponentFactory factory) {
    if (_componentFactories == null) {
      _componentFactories = new ArrayList<ComponentFactory>();
    }
    _componentFactories.add(factory);
    _log.debug("REGISTERED FACTORY: " + factory.toString() + " - " + this.toString());
  }

  /**
   * Removes a ComponentFactory from the internal List of ComponentFactory. Used by OSGi Declarative Services.
   * 
   * @param factory
   *          the ComponentFactory to remove
   */
  public void removeComponentFactory(final ComponentFactory factory) {
    if (_componentFactories != null) {
      _componentFactories.remove(factory);
    }
    _log.debug("UNREGISTERED FACTORY: " + factory.toString() + " - " + this.toString());
  }

  /**
   * Returns the component names of all available ComponentFactories.
   * 
   * @return a Collection with the component names of all available ComponentFactories.
   */
  protected Collection<String> getAvailableFactories() {
    final Collection<String> availFactories = new ArrayList<String>();
    if (_componentFactories != null) {
      final Iterator<ComponentFactory> it = _componentFactories.iterator();
      while (it.hasNext()) {
        final ComponentFactory cf = it.next();
        availFactories.add(getFactoryComponentName(cf));
      }
    }
    return availFactories;
  }

  /**
   * Returns a new Crawler or Agent instance for the given componentId. Uses a ComponentFactory to create new instances.
   * 
   * @param <T>
   *          the return type
   * @param clazz
   *          the class of the created instance (Agent or Crawler)
   * @param componentId
   *          the Id of the Crawler or Agent
   * @return the an Instance of type T
   * @throws ConnectivityException
   *           if no ComponentFactory instance with the given Id could be created
   */
  protected <T> T createInstance(final Class<T> clazz, final String componentId) throws ConnectivityException {
    final String msg =
      "Could not create instance of " + clazz.getName() + " with Id " + componentId
        + " Perhaps the bundle or the OSGi service is not started.";
    for (int i = 0; i < _componentFactories.size(); i++) {
      final ComponentFactory factory = _componentFactories.get(i);
      try {
        // factoryId and componentId are both equal to the OSGi
        // component name
        final String factoryId = getFactoryComponentName(factory);
        if (componentId.equals(factoryId)) {
          final ComponentInstance instance = factory.newInstance(null);
          return (T) instance.getInstance();
        }
      } catch (final Exception e) {
        if (_log.isErrorEnabled()) {
          _log.error(msg, e);
        }
      }
    }
    throw new ConnectivityException(msg);
  }

  /**
   * Returns the DataSourceConnectionConfig for the given dataSourceId.
   * 
   * @param bundleId
   *          the id of the bundle
   * @param dataSourceId
   *          the Id of the data source
   * @return the DataSourceConnectionConfig
   * @throws ConnectivityException
   *           if no DataSource with the given Id exists
   * 
   */
  protected DataSourceConnectionConfig getConfiguration(final String bundleId, final String dataSourceId)
    throws ConnectivityException {
    // TODO implement correctly
    // for now assume that dataSourceId contains a filename (mainly file
    // extension, by default ".xml" added)
    if (dataSourceId == null || "".equals(dataSourceId)) {
      throw new IllegalArgumentException("DataSourceId cannot be null!");
    }
    final String fileName;
    if (dataSourceId.contains(".")) {
      fileName = dataSourceId;
    } else {
      fileName = dataSourceId + ".xml";
    }
    try {
      final DataSourceConnectionConfig configuration =
        ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream(bundleId, fileName));
      if (!configuration.getDataSourceID().equals(dataSourceId)) {
        throw new RuntimeException(String.format("DataSourceId is case sensetive, %s != %s ! ", dataSourceId,
          configuration.getDataSourceID()));
      }
      return configuration;
    } catch (final Exception e) {
      final String msg = "Error loading DataSource with DataSourceId '" + dataSourceId + "'";
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new ConnectivityException(msg, e);
    }
  }

  /**
   * Returns the data source ids of all available configurations of a given DataConnectionType.
   * 
   * @param bundleId
   *          the id of the bundle
   * @param type
   *          the DataConnectionType
   * @return a Collection of Strings containing the data source ids
   */
  protected Collection<String> getConfigurations(final String bundleId, final DataConnectionType type) {
    final ArrayList<String> configFiles = new ArrayList<String>();
    final List<String> files = ConfigUtils.getConfigEntries(bundleId, "");
    for (String file : files) {
      try {
        final DataSourceConnectionConfig configuration =
          ConfigurationLoader.unmarshall(ConfigUtils.getConfigStream(bundleId, file));

        if (type.equals(configuration.getDataConnectionID().getType())) {
          configFiles.add(configuration.getDataSourceID());
        }
      } catch (Exception e) {
        if (_log.isWarnEnabled()) {
          _log.warn("Error while checking for available '" + type + "' configurations in file " + file + ": "
            + e.getMessage());
        }
      }
    }
    return configFiles;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.ControllerCallback#doCheckForUpdate(DeltaIndexingType)
   */
  public boolean doCheckForUpdate(final DeltaIndexingType deltaIndexingType) {
    assertDeltaIndexingManager(deltaIndexingType);
    switch (deltaIndexingType) {
      case FULL:
        return true;
      case ADDITIVE:
        return true;
      case INITIAL:
        return false;
      case DISABLED:
        return false;
      default:
        throw new RuntimeException("Unknwon DeltaIndexingType " + deltaIndexingType.toString());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.ControllerCallback#doDeltaIndexing(DeltaIndexingType)
   */
  public boolean doDeltaIndexing(final DeltaIndexingType deltaIndexingType) {
    assertDeltaIndexingManager(deltaIndexingType);
    switch (deltaIndexingType) {
      case FULL:
        return true;
      case ADDITIVE:
        return true;
      case INITIAL:
        return true;
      case DISABLED:
        return false;
      default:
        throw new RuntimeException("Unknwon DeltaIndexingType " + deltaIndexingType.toString());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.connectivity.framework.util.ControllerCallback#doDeltaDelete(DeltaIndexingType)
   */
  public boolean doDeltaDelete(final DeltaIndexingType deltaIndexingType) {
    assertDeltaIndexingManager(deltaIndexingType);
    switch (deltaIndexingType) {
      case FULL:
        return true;
      case ADDITIVE:
        return false;
      case INITIAL:
        return false;
      case DISABLED:
        return false;
      default:
        throw new RuntimeException("Unknwon DeltaIndexingType " + deltaIndexingType.toString());
    }
  }

  /**
   * Utility method that checks if a DeltaIndexingManager is bound. If none is bound and the DeltaIndexingType is not
   * DISABLED throw a RuntimeException.
   * 
   * @param deltaIndexingType
   *          the delta indexing type
   */
  private void assertDeltaIndexingManager(final DeltaIndexingType deltaIndexingType) {
    if (_diManager == null && !DeltaIndexingType.DISABLED.equals(deltaIndexingType)) {
      throw new RuntimeException("No DeltaIndexingManager is bound. Only valid DeltaIndexingType is "
        + DeltaIndexingType.DISABLED);
    }
  }

  /**
   * Gets the component name of a ComponentFactory.
   * 
   * @param factory
   *          the ComponentFactory
   * @return a String containing the component name or null if none was found
   */
  private String getFactoryComponentName(final ComponentFactory factory) {
    final String factoryName = factory.toString();
    if (factoryName != null) {
      return factoryName.substring(factoryName.lastIndexOf(" ") + 1, factoryName.length());
    }
    return null;
  }
}
