/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Dmitry Hazin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (Brox IT Solutions GmbH) -
 * initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.management;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.activator.Activator;
import org.eclipse.smila.management.controller.ManagementController;
import org.eclipse.smila.management.internal.ManagementCategoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The Class ManagementRegistration.
 */
public final class ManagementRegistration {

  /**
   * The INSTANCE.
   */
  public static final ManagementRegistration INSTANCE = new ManagementRegistration();

  /**
   * Log for this class.
   */
  private final Log _log = LogFactory.getLog(ManagementRegistration.class);

  /**
   * The _context.
   */
  private final BundleContext _context;

  /**
   * The _management service tracker.
   */
  private final ManagementServiceTracker _managementServiceTracker;

  /**
   * ManagementAgent service tracjer.
   */
  // private final AgentServiceTracker _agentServiceTracker;
  /**
   * The _monitor.
   */
  private final Object _monitor = new Object();

  /**
   * The _agents.
   */
  private final Map<ManagementAgentLocation, ManagementAgent> _agents =
    new HashMap<ManagementAgentLocation, ManagementAgent>();

  /**
   * The _controllers.
   */
  private final Set<ServiceReference> _controllers = new HashSet<ServiceReference>();

  /**
   * Instantiates a new management registration.
   */
  private ManagementRegistration() {
    _context = Activator.getBundleContext();
    _managementServiceTracker = new ManagementServiceTracker(_context);
    _managementServiceTracker.open();
    // _agentServiceTracker = new AgentServiceTracker(_context);
    // _agentServiceTracker.open();
  }

  /**
   * Log.
   * 
   * @param text
   *          the text
   */
  private void log(final String text) {
    if (_log.isInfoEnabled()) {
      _log.info(String.format("[Management Registration] %s", text));
    }
  }

  /**
   * Register agent.
   * 
   * @param location
   *          the location
   * @param agent
   *          the agent
   */
  public void registerAgent(final ManagementAgentLocation location, final ManagementAgent agent) {
    log(String.format("Registering new agent [%s]", location.getPath()));
    synchronized (_monitor) {
      final Iterator<ServiceReference> iterator = _controllers.iterator();
      while (iterator.hasNext()) {
        final ServiceReference reference = iterator.next();
        final ManagementController controller = (ManagementController) _context.getService(reference);
        if (controller != null) {
          try {
            log(String.format("Registering new agent [%s] in old controller [%s]", location.getPath(), controller
              .getClass().getName()));
            controller.registerAgent(location, agent);
          } catch (final RegistrationException exception) {
            _log.error(String.format("Error registering agent [%s]", location.getPath()), exception);
          }
        } else {
          iterator.remove();
        }
      }
      _agents.put(location, agent);
    }
  }

  /**
   * Unregister agent.
   * 
   * @param location
   *          the location
   */
  public void unregisterAgent(final ManagementAgentLocation location) {
    log(String.format("Unregistering agent [%s]", location));
    synchronized (_monitor) {
      final Iterator<ServiceReference> iterator = _controllers.iterator();
      while (iterator.hasNext()) {
        final ServiceReference reference = iterator.next();
        final ManagementController controller = (ManagementController) _context.getService(reference);
        if (controller != null) {
          try {
            log(String.format("Unregistering agent [%s] from controller [%s]", location, controller.getClass()
              .getName()));
            controller.unregisterAgent(location);
          } catch (final RegistrationException exception) {
            _log.error(String.format("Error unregistering agent [%s]", location), exception);
          }
        } else {
          iterator.remove();
        }
      }
      _agents.remove(location);
    }
  }

  /**
   * Register agent.
   * 
   * @param agent
   *          the agent
   */
  public void registerAgent(final LocatedManagementAgent agent) {
    registerAgent(agent.getLocation(), agent);
  }

  /**
   * Unregister agent.
   * 
   * @param agent
   *          the agent
   */
  public void unregisterAgent(final LocatedManagementAgent agent) {
    unregisterAgent(agent.getLocation());
  }

  /**
   * Gets the agent.
   * 
   * @param location
   *          the location
   * 
   * @return the agent
   */
  public ManagementAgent getAgent(final ManagementAgentLocation location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null!");
    }
    final ManagementAgent agent;
    synchronized (_monitor) {
      agent = _agents.get(location);
    }
    if (agent == null) {
      throw new ManagementAgentNotFoundException(location);
    }
    return agent;
  }

  /**
   * Gets the agent.
   * 
   * @param path
   *          the path
   * 
   * @return the agent
   */
  public ManagementAgent getAgent(final String path) {
    return getAgent(getLocation(path));
  }

  /**
   * The Class ManagementServiceTracker.
   */
  private class ManagementServiceTracker extends ServiceTracker {

    /**
     * Instantiates a new management service tracker.
     * 
     * @param context
     *          the context
     */
    public ManagementServiceTracker(final BundleContext context) {
      super(context, ManagementController.class.getName(), null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
     */
    @Override
    public Object addingService(final ServiceReference reference) {
      final ManagementController controller = (ManagementController) context.getService(reference);
      // register all collected here agents
      synchronized (_monitor) {
        log(String.format("Found new controller [%s]", controller.getClass().getName()));
        _controllers.add(reference);
        for (final ManagementAgentLocation location : _agents.keySet()) {
          try {
            log(String.format("Registering old agent [%s] in new controller [%s]", location, controller.getClass()
              .getName()));
            controller.registerAgent(location, _agents.get(location));
          } catch (final RegistrationException exception) {
            _log.error("Error registering ManagementController service", exception);
          }
        }
      }
      return controller;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void removedService(final ServiceReference reference, final Object service) {
      final ManagementController controller = (ManagementController) context.getService(reference);
      synchronized (_monitor) {
        for (final ManagementAgentLocation location : _agents.keySet()) {
          try {
            log(String.format("Unregistering old agent [%s] from removed controller [%s]", location, controller
              .getClass().getName()));
            controller.unregisterAgent(location);
          } catch (final RegistrationException exception) {
            _log.error("Error unregistering ManagementController service", exception);
          }
        }
        _controllers.remove(reference);
      }
    }
  }

  // /**
  // * The Class AgentServiceTracker.
  // */
  // private class AgentServiceTracker extends ServiceTracker {
  //
  // /**
  // * Instantiates a new management service tracker.
  // *
  // * @param context
  // * the context
  // */
  // public AgentServiceTracker(final BundleContext context) {
  // super(context, ManagementAgent.class.getName(), null);
  // }
  //
  // /**
  // * {@inheritDoc}
  // *
  // * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
  // */
  // @Override
  // public Object addingService(final ServiceReference reference) {
  // final ManagementAgent agent = (ManagementAgent) context.getService(reference);
  // registerAgent(agent);
  // return agent;
  // }
  //
  // /**
  // * {@inheritDoc}
  // *
  // * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
  // */
  // @Override
  // public void removedService(final ServiceReference reference, final Object service) {
  // final ManagementAgent agent = (ManagementAgent) context.getService(reference);
  // unregisterAgent(agent.getName());
  // }
  // }

  /**
   * Gets the category.
   * 
   * @param name
   *          the name
   * 
   * @return the category
   */
  public ManagementCategory getCategory(final String name) {
    return new ManagementCategoryImpl(name);
  }

  /**
   * Gets the location.
   * 
   * @param path
   *          the path
   * 
   * @return the location
   */
  public ManagementAgentLocation getLocation(final String path) {
    if (path == null) {
      throw new IllegalArgumentException("Agent name cannot be null!");
    }
    String category;
    String name;
    final int index = path.lastIndexOf('/');
    if (index > 0) {
      name = path.substring(index + 1);
      category = path.substring(0, index);
    } else {
      name = path;
      category = null;
    }
    return getCategory(category).getLocation(name);
  }

  /**
   * Gets the monitor.
   * 
   * @return the monitor
   */
  public Object getMonitor() {
    return _monitor;
  }

}
