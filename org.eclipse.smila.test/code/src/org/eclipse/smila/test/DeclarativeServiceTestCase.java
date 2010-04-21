/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.test;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.smila.utils.log.BundleLogHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Abstract base class for JUnit tests dealing with DeclarativeServices.
 */
public abstract class DeclarativeServiceTestCase extends TestCase {

  /**
   * The Constant DEFAULT_SERVICE_MAX_WAITING.
   */
  protected static final int DEFAULT_SERVICE_MAX_WAITING = 60000;

  /**
   * static reference to a BundleContext. It is set in the BundleActivator.
   */
  private static BundleContext s_bundleContext;

  /**
   * The log.
   */
  protected final Log _log = LogFactory.getLog(getClass());

  /**
   * Sets the BundleContext.
   * 
   * @param bundleContext
   *          the BundleContext
   */
  public static void setBundleContext(final BundleContext bundleContext) {
    s_bundleContext = bundleContext;
  }

  /**
   * Uses a ServiceTracker to get the given DeclarativeService.
   * 
   * @param clazz
   *          the class name of the DeclarativeService interface
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * 
   * @return the DeclarativeService as Object. You have to do a type cast.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  @Deprecated
  protected Object getService(final String clazz, final long timeout) throws InterruptedException {    
    final ServiceReference reference = s_bundleContext.getServiceReference(clazz);
    if (reference != null) {
      final Object service = s_bundleContext.getService(reference);
      if (service != null) {
        return service;
      }
    }
    ServiceTracker st = null;
    try {
      st = new ServiceTracker(s_bundleContext, clazz, null);
      st.open();
      st.waitForService(timeout);
      if (st.getService() == null) {
        final String msg = "Unable to find service " + clazz;
        _log.error(msg);
        BundleLogHelper.logBundlesState();
        throw new RuntimeException(msg);
      }
      return st.getService();
    } finally {
      if (st != null) {
        st.close();
      }
    }
  }

  /**
   * Uses a ServiceTracker to get the given DeclarativeService.
   * 
   * @param clazz
   *          the service class of the DeclarativeService interface
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * 
   * @return the DeclarativeService.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  @SuppressWarnings("unchecked")
  @Deprecated
  protected <T> T getService(final Class<T> clazz, final long timeout) throws InterruptedException {
    return (T) getService(clazz.getName(), timeout);
  }

  /**
   * Gets the service with default wait delay.
   * 
   * @param clazz
   *          the clazz
   * @return the service
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @SuppressWarnings("unchecked")
  protected <T> T getService(final Class<T> clazz) throws InterruptedException {
    return (T) getService(clazz.getName(), DEFAULT_SERVICE_MAX_WAITING);
  }

  /**
   * Uses a ServiceTracker to get the given DeclarativeService.
   * 
   * @param clazz
   *          the service class of the DeclarativeService interface
   * @param filter
   *          the filter
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * 
   * @return the DeclarativeService.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  protected <T> T getService(final Class<T> clazz, final String filter, final long timeout)
    throws InterruptedException {
    return (T) getService(clazz.getName(), filter, timeout);
  }

  /**
   * Gets the service with default wait delay.
   * 
   * @param clazz
   *          the clazz
   * @param filter
   *          the filter
   * 
   * @return the service
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @SuppressWarnings("unchecked")
  protected <T> T getService(final Class<T> clazz, final String filter) throws InterruptedException {
    return (T) getService(clazz.getName(), filter, DEFAULT_SERVICE_MAX_WAITING);
  }

  /**
   * Uses a ServiceTracker to get the given DeclarativeService.
   * 
   * @param clazz
   *          the class name of the DeclarativeService interface
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * 
   * @return the DeclarativeService as Object. You have to do a type cast.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  @Deprecated
  protected Object getService(final String clazz, final String filter, final long timeout)
    throws InterruptedException {
    try {
      final ServiceReference[] references = s_bundleContext.getServiceReferences(clazz, filter);
      if (references != null) {
        final Object service = s_bundleContext.getService(references[0]);
        if (service != null) {
          return service;
        }
      }
      ServiceTracker st = null;
      try {
        final Filter serviceFilter = s_bundleContext.createFilter(filter);
        st = new ServiceTracker(s_bundleContext, serviceFilter, null);
        st.open();
        st.waitForService(timeout);
        if (st.getService() == null) {
          final String msg = "Unable to find service " + clazz;
          _log.error(msg);
          BundleLogHelper.logBundlesState();
          throw new RuntimeException(msg);
        }
        return st.getService();
      } finally {
        if (st != null) {
          st.close();
        }
      }
    } catch (final InvalidSyntaxException e) {
      final String msg = "Unable to find service " + clazz;
      _log.error(msg);
      BundleLogHelper.logBundlesState();
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Registers the given service and returns it.
   * 
   * @param service
   *          the service to register
   * @param properties
   *          properties
   * @param clazz
   *          the service class of the DeclarativeService interface
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * 
   * @return the DeclarativeService
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  protected <T> T registerService(final Object service, final Dictionary<Object, Object> properties,
    final Class<T> clazz, final long timeout) throws InterruptedException {
    s_bundleContext.registerService(clazz.getName(), service, properties);
    return getService(clazz, timeout);
  }

  /**
   * Force start bundle.
   * 
   * @param bundleId
   *          the bundle id
   */
  protected void forceStartBundle(final String bundleId) {
    try {
      final Bundle bundle = Platform.getBundle(bundleId);
      if (bundle == null) {
        throw new RuntimeException("Unable to find bundle with symbolic name " + bundleId);
      }
      if (bundle.getState() != Bundle.ACTIVE) {
        bundle.start();
      }
    } catch (final Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * Resolve resource URL.
   * 
   * @param relativePath
   *          the relative path
   * 
   * @return the uRL
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected URL resolveResourceURL(final String relativePath) throws IOException {
    final URL nativeUrl = getClass().getClassLoader().getResource(relativePath);
    final URL url = FileLocator.resolve(nativeUrl);
    return url;
  }

}
