/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.service;

import org.eclipse.smila.utils.UtilsActivator;
import org.eclipse.smila.utils.log.BundleLogHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The Class ServiceHelper.
 * 
 * @param <T>
 *          the type
 */
public final class ServiceUtils<T> {

  /**
   * The Constant DEFAULT_SERVICE_MAX_WAITING.
   */
  private static final int DEFAULT_SERVICE_MAX_WAITING = 10000;

  /**
   * prevents instantiating of a new service utils class.
   */
  private ServiceUtils() {
  }

  /**
   * Uses a ServiceTracker to get the given DeclarativeService.
   * 
   * @param clazz
   *          the class name of the DeclarativeService interface
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * @param context
   *          the context
   * 
   * @return the DeclarativeService as Object. You have to do a type cast.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  private static Object getService(final BundleContext context, final String clazz, final long timeout)
    throws InterruptedException {
    final ServiceReference reference = context.getServiceReference(clazz);
    if (reference != null) {
      final Object service = context.getService(reference);
      if (service != null) {
        return service;
      }
    }
    ServiceTracker st = null;
    try {
      st = new ServiceTracker(context, clazz, null);
      st.open();
      st.waitForService(timeout);
      if (st.getService() == null) {
        BundleLogHelper.logBundlesState();
        throw new RuntimeException("Unable to find service " + clazz);
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
   * @param context
   *          the context
   * @param <T>
   *          expected service class
   * 
   * @return the DeclarativeService.
   * 
   * @throws InterruptedException
   *           a InterruptedException
   */
  @SuppressWarnings("unchecked")
  public static <T> T getService(final BundleContext context, final Class<T> clazz, final long timeout)
    throws InterruptedException {
    return (T) getService(context, clazz.getName(), timeout);
  }

  /**
   * Gets the service with default wait delay.
   * 
   * @param clazz
   *          the clazz
   * @param context
   *          the context
   * 
   * @param <T>
   *          expected service class
   * @return the service
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @SuppressWarnings("unchecked")
  public static <T> T getService(final BundleContext context, final Class<T> clazz) throws InterruptedException {
    return (T) getService(context, clazz.getName(), DEFAULT_SERVICE_MAX_WAITING);
  }

  /**
   * Gets the service with the given wait delay using the BundleContext of UtilsActivator.
   * 
   * @param clazz
   *          the clazz
   * @param timeout
   *          the number of milliseconds to wait for the ServiceTracker to find the service
   * @param <T>
   *          expected service class
   * 
   * @return the service
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public static <T> T getService(final Class<T> clazz, final long timeout) throws InterruptedException {
    return getService(UtilsActivator.getBundleContext(), clazz, timeout);
  }

  /**
   * Gets the service with default wait delay using the BundleContext of UtilsActivator.
   * 
   * @param clazz
   *          the clazz
   * @param <T>
   *          expected service class
   * 
   * @return the service
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public static <T> T getService(final Class<T> clazz) throws InterruptedException {
    return getService(clazz, DEFAULT_SERVICE_MAX_WAITING);
  }
}
