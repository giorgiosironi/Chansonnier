/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.framework.compound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.framework.Crawler;
import org.eclipse.smila.connectivity.framework.CrawlerCriticalException;
import org.eclipse.smila.connectivity.framework.CrawlerException;
import org.eclipse.smila.connectivity.framework.schema.config.DataSourceConnectionConfig;
import org.eclipse.smila.datamodel.record.Record;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

/**
 * Abstract base class for CompoundHandlers.
 * Provides implementations for CompoundCrawler instance creation.
 */
public abstract class AbstractCompoundHandler implements CompoundHandler {

  /**
   * The LOG.
   */
  private final Log _log = LogFactory.getLog(this.getClass());

  /**
   * A List of references to CompoundCrawler ComponentFactories.
   */
  private ComponentFactory _compoundCrawlerFactory;

  /**
   * Sets the CompoundCrawler ComponentFactory. Used by OSGi Declarative Services.
   * 
   * @param factory
   *          the CompoundCrawler ComponentFactory to add
   */
  public final void setCompoundCrawlerFactory(final ComponentFactory factory) {
    _compoundCrawlerFactory = factory;
    _log.debug("REGISTERED FACTORY: " + factory.toString() + " - " + this.toString());
  }

  /**
   * Un-sets the CompoundCrawler ComponentFactory. Used by OSGi Declarative Services.
   * 
   * @param factory
   *          the CompoundCrawler ComponentFactory to remove
   */
  public final void unsetCompoundCrawlerFactory(final ComponentFactory factory) {
    if (_compoundCrawlerFactory == factory) {
      _compoundCrawlerFactory = null;
    }
    _log.debug("UNREGISTERED FACTORY: " + factory.toString() + " - " + this.toString());
  }

  /**
   * {@inheritDoc}
   * 
   * @see CompoundHandler#extract(Record, DataSourceConnectionConfig)
   */
  public final Crawler extract(final Record record, final DataSourceConnectionConfig config) throws CompoundException {
    try {
      final CompoundCrawler compoundCrawler = createInstance();
      compoundCrawler.setCompoundRecord(record);
      compoundCrawler.initialize(config);
      return compoundCrawler;
    } catch (CrawlerException e) {
      final String msg = "error during initialization of " + getFactoryComponentName(_compoundCrawlerFactory);
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CompoundException(msg, e);
    } catch (CrawlerCriticalException e) {
      final String msg = "error during initialization of " + getFactoryComponentName(_compoundCrawlerFactory);
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CompoundException(msg, e);

    }
  }

  /**
   * Returns a new CompoundCrawler instance for the given componentId. Uses a ComponentFactory to create new instances.
   * 
   * @return a CompoundCrawler instance
   * @throws CrawlerException
   *           if no CompoundCrawler instance could be created
   */
  private CompoundCrawler createInstance() throws CrawlerException {
    try {
      final ComponentInstance instance = _compoundCrawlerFactory.newInstance(null);
      return (CompoundCrawler) instance.getInstance();
    } catch (final Exception e) {
      final String msg =
        "Could not create instance of CompoundCrawler " + getFactoryComponentName(_compoundCrawlerFactory);
      if (_log.isErrorEnabled()) {
        _log.error(msg, e);
      }
      throw new CrawlerException(msg);
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
