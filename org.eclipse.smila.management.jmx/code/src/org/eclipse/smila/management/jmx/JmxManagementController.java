/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.management.ManagementAgentLocation;
import org.eclipse.smila.management.RegistrationException;
import org.eclipse.smila.management.controller.ManagementController;

/**
 * The Class JmxManagementController.
 */
public class JmxManagementController implements ManagementController {

  /**
   * The Constant DOMAIN.
   */
  private static final String DOMAIN = "SMILA";

  /**
   * Log for this class.
   */
  private final Log _log = LogFactory.getLog(JmxManagementController.class);

  /**
   * The _monitor.
   */
  private final Object _monitor = new Object();

  /**
   * The _server.
   */
  private final MBeanServer _server = ManagementFactory.getPlatformMBeanServer();

  /**
   * The _agents.
   */
  private final Set<ManagementAgentLocation> _agents = new HashSet<ManagementAgentLocation>();

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.controller.ManagementController#registerAgent(org.eclipse.smila.management.ManagementAgentLocation,
   *      org.eclipse.smila.management.ManagementAgent)
   */
  public void registerAgent(final ManagementAgentLocation location, final ManagementAgent agent)
    throws RegistrationException {
    final ObjectName objectName = buildObjectName(location);
    final DynamicMBeanBuilder dynamicMBeanBuilder = new DynamicMBeanBuilder(objectName, location, agent);
    final Object dmBean = dynamicMBeanBuilder.build();
    synchronized (_monitor) {
      register(objectName, dmBean);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.management.controller.ManagementController#unregisterAgent(java.lang.String)
   */
  public void unregisterAgent(final ManagementAgentLocation location) throws RegistrationException {
    final ObjectName objectName = buildObjectName(location);
    unregister(objectName);
  }

  /**
   * Unregister all.
   */
  public void unregisterAll() {
    synchronized (_monitor) {
      for (final ManagementAgentLocation location : _agents) {
        try {
          unregisterAgent(location);
        } catch (final Throwable exception) {
          _log.error("Error unregistering agent = " + location.getPath(), exception);
        }
      }
    }
  }

  /**
   * Register.
   * 
   * @param objectName
   *          the object name
   * @param bean
   *          the bean
   * 
   * @throws RegistrationException
   *           the jmx exception
   */
  private void register(final ObjectName objectName, final Object bean) throws RegistrationException {
    unregister(objectName);
    try {
      _server.registerMBean(bean, objectName);
    } catch (final Throwable e) {
      throw new RegistrationException(e);
    }
  }

  /**
   * Unregister.
   * 
   * @param objectName
   *          the object name
   * 
   * @throws RegistrationException
   *           the jmx exception
   */
  private void unregister(final ObjectName objectName) throws RegistrationException {
    try {
      final MBeanInfo beanInfo = _server.getMBeanInfo(objectName);
      if (beanInfo != null) {
        _server.unregisterMBean(objectName);
      }
    } catch (final InstanceNotFoundException e1) {
      ; // ok;
    } catch (final Throwable e) {
      throw new RegistrationException(e);
    }

  }

  /**
   * Builds the object name.
   * 
   * @param location
   *          the location
   * 
   * @return the object name
   * 
   * @throws RegistrationException
   *           the registration exception
   */
  private ObjectName buildObjectName(final ManagementAgentLocation location) throws RegistrationException {
    ObjectName objectName = null;
    String objectNameString = DOMAIN + ":";
    if (location.getCategory() != null && location.getCategory().getPath() != null
      && !"".equals(location.getCategory().getPath())) {
      final String[] categories = location.getCategory().getPath().split("/");
      for (int i = 0; i < categories.length; i++) {
        objectNameString += String.format("C%d=%s,", i, categories[i]);
      }
    }
    objectNameString += "Agent=" + location.getName();
    _log.info(objectNameString);
    try {
      objectName = new ObjectName(objectNameString);
    } catch (final Throwable e) {
      throw new RegistrationException(e);
    }
    return objectName;
  }
}
