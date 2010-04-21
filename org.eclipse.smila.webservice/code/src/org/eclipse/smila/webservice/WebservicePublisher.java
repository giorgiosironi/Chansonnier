/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.webservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tracks services with property <code>org.eclipse.smila.ws</code> and tries to publish them as a JAX-WS webservice. The
 * value of the property gives the webservice name. The publisher does not check if the service implementor is really a
 * JAX-WS service implementor (i.e. has the correct annotations and such), but leaves this to JAX-WS, so you will get
 * JAX-WS exceptions when trying to publish services that are not suitable as a webservice.
 * 
 * Some properties are configured in the configuration file <tt>webservice.properties</tt>. See the bundle for a example
 * file.
 * 
 * @author jschumacher
 * 
 */
public class WebservicePublisher extends ServiceTracker {
  /**
   * name of property of tracked services.
   */
  public static final String PROP_WEBSERVICENAME = "org.eclipse.smila.ws";

  /**
   * filename of my config file.
   */
  public static final String PROPERTY_FILENAME = "webservice.properties";

  /**
   * map of webservice names to the service references.
   */
  private final Map<String, ServiceReference> _references = new HashMap<String, ServiceReference>();

  /**
   * map of webservice names to published endpoints.
   */
  private final Map<String, Endpoint> _endpoints = new HashMap<String, Endpoint>();

  /**
   * configuration properties read from config file <code>webservice.properties</code>, or defaults.
   */
  private WebserviceProperties _properties;

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * create instance.
   * 
   * @param bundleContext
   *          bundle context.
   */
  public WebservicePublisher(final BundleContext bundleContext) {
    super(bundleContext, createFilter(bundleContext), null);
  }

  /**
   * create the filter for a service tracker that tracks all services with the property
   * <code>org.eclipse.smila.ws</code> set.
   * 
   * @param bundleContext
   *          bundle context.
   * @return a filter for <code>org.eclipse.smila.ws=*</code>
   */
  private static Filter createFilter(final BundleContext bundleContext) {
    try {
      return bundleContext.createFilter("(" + PROP_WEBSERVICENAME + "=*)");
    } catch (final InvalidSyntaxException e) {
      throw new RuntimeException("should not happen");
    }
  }

  /**
   * start the service tracker, read the configuration.
   */
  @Override
  public void open() {
    try {
      final InputStream stream = ConfigUtils.getConfigStream(Activator.BUNDLE_ID, PROPERTY_FILENAME);
      _properties = new WebserviceProperties(stream);
      stream.close();
    } catch (final Exception ex) {
      _log.error("Error reading webservice.properties, using defaults.");
      _properties = new WebserviceProperties();
    }
    super.open();
  }

  /**
   * stop the service tracker, stop all published webservices, clean up.
   */
  @Override
  public void close() {
    super.close();
    for (final Endpoint endpoint : _endpoints.values()) {
      try {
        endpoint.stop();
      } catch (final RuntimeException ex) {
        _log.warn("error while shutting down", ex);
      }
    }
    _endpoints.clear();
    _references.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object addingService(final ServiceReference reference) {
    final Object implementor = super.addingService(reference);
    publishWebservice(reference, implementor);
    return implementor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removedService(final ServiceReference reference, final Object service) {
    super.removedService(reference, service);
    stopWebservice(reference);
  }

  /**
   * publish a webservice for a tracked service.
   * 
   * @param reference
   *          service reference for added service.
   * @param implementor
   *          service implementor.
   */
  public void publishWebservice(final ServiceReference reference, final Object implementor) {
    final String name = reference.getProperty(PROP_WEBSERVICENAME).toString();
    if (_endpoints.containsKey(name)) {
      _log.warn("Already have registered a Webservice endpoint with name " + name + ", ignoring new service.");
    } else {
      final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(implementor.getClass().getClassLoader());
      try {
        _log.debug("Creating endpoint for webservice " + name);
        final Endpoint ep = Endpoint.create(implementor);
        if (ep != null) {
          final String url = _properties.getBaseURL() + name;
          _log.debug("Publishing webservice " + name + " at " + url);
          ep.publish(url);
          _endpoints.put(name, ep);
          _references.put(name, reference);
          _log.info("Webservice successfully published: " + url);
        }
      } catch (final Throwable ex) {
        _log.error("Error publishing webservice " + name, ex);
      } finally {
        Thread.currentThread().setContextClassLoader(tccl);
      }
    }
  }

  /**
   * stop a published webservice.
   * 
   * @param reference
   *          service reference for deactivated service.
   */
  public void stopWebservice(final ServiceReference reference) {
    final String name = reference.getProperty(PROP_WEBSERVICENAME).toString();
    final ServiceReference publishedRef = _references.get(name);
    if (reference.equals(publishedRef)) {
      try {
        final Endpoint ep = _endpoints.get(name);
        if (ep != null) {
          _log.debug("Stopping webservice " + name);
          ep.stop();
          _endpoints.remove(name);
          _references.remove(name);
          _log.info("Webservice " + name + " successfully stopped.");
        }
      } catch (final Throwable ex) {
        _log.error("Error stopping webservice " + name, ex);
      }
    } else {
      _log.warn("Service reference for webservice " + name + " differs from removed reference, ignoring event.");
    }
  }
}
