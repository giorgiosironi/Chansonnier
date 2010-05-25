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

package org.eclipse.smila.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;

/**
 * Implementation of SimplePipeletTracker service. Registered as a OSGi services by DS. It works as a
 * SynchronousBundleListener to get notified about starting and stopping bundles.
 *
 * @author jschumacher
 *
 */
public class PipeletTrackerImpl implements SynchronousBundleListener, PipeletTracker {

  /**
   * Manifest header name for pipelet class names: "SMILA-Pipelets".
   */
  private static final String MANIFEST_SMILA_PIPELETS = "SMILA-Pipelets";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(PipeletTrackerImpl.class);

  /**
   * currently known pipelet classes: map of bundle name to map of class name to pipelet class.
   */
  private final Map<String, Map<String, Class<? extends IPipelet>>> _knownPipelets =
    new HashMap<String, Map<String, Class<? extends IPipelet>>>();

  /**
   * registered listener for tracker events.
   */
  private final Collection<PipeletTrackerListener> _listeners = new ArrayList<PipeletTrackerListener>();

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.PipeletTracker#getRegisteredPipelets()
   */
  public Map<String, Class<? extends IPipelet>> getRegisteredPipelets() {
    final Map<String, Class<? extends IPipelet>> registeredPipelets =
      new HashMap<String, Class<? extends IPipelet>>();
    for (final Map<String, Class<? extends IPipelet>> bundlePipelets : _knownPipelets.values()) {
      registeredPipelets.putAll(bundlePipelets);
    }
    return registeredPipelets;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.PipeletTracker #addListener(org.eclipse.smila.processing.PipeletTrackerListener)
   */
  public void addListener(final PipeletTrackerListener listener) {
    _listeners.add(listener);
    for (final Map<String, Class<? extends IPipelet>> pipeletClassNames : _knownPipelets.values()) {
      listener.pipeletsAdded(pipeletClassNames);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.smila.processing.PipeletTracker
   *      #removeListener(org.eclipse.smila.processing.PipeletTrackerListener)
   */
  public void removeListener(final PipeletTrackerListener listener) {
    _listeners.remove(listener);
  }

  /**
   * activate declarative service. It registers the services a bundle listener and searches all currently active bundles
   * for pipelets and notifies all currently known listeners.
   *
   * @param componentContext
   *          service component context.
   */
  protected void activate(final ComponentContext componentContext) {
    final BundleContext bundleContext = componentContext.getBundleContext();
    bundleContext.addBundleListener(this);
    final Bundle[] bundles = bundleContext.getBundles();
    for (final Bundle bundle : bundles) {
      if ((bundle.getState() & (Bundle.ACTIVE | Bundle.RESOLVED | Bundle.STARTING)) > 0) {
        bundleAdded(bundle);
      }
    }
  }

  /**
   * deactivate declarative service. Removes this as a bundle listener.
   *
   * @param componentContext
   *          service component context.
   */
  protected void deactivate(final ComponentContext componentContext) {
    final BundleContext bundleContext = componentContext.getBundleContext();
    bundleContext.removeBundleListener(this);
  }

  /**
   * Check newly resolved or stopping bundles for contained SimplePipelets.
   *
   * {@inheritDoc}
   *
   * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
   */
  public void bundleChanged(final BundleEvent event) {
    switch (event.getType()) {
      case BundleEvent.RESOLVED:
        bundleAdded(event.getBundle());
        break;
      case BundleEvent.STARTING:
        bundleAdded(event.getBundle());
        break;
      case BundleEvent.STOPPING:
        bundleRemoved(event.getBundle());
        break;
      default:
        // ignore
    }
  }

  /**
   * Check bundle for contained SimplePipelets. It looks for a Manifest entry named "SMILA-Pipelets", that contains a
   * comma-separated list of class names in this bundle, that implement SimplePipelet. Finally listeners are notified
   * about the new pipelet classes.
   *
   * @param bundle
   *          bundle to examine for pipelets.
   */
  private void bundleAdded(final Bundle bundle) {
    final String bundleName = bundle.getSymbolicName();
    if (_knownPipelets.containsKey(bundleName)) {
      if (_log.isDebugEnabled()) {
        _log.debug("Pipelets from bundle " + bundleName + " have been loaded already, skipping.");
      }
    } else {
      final String pipeletManifest = (String) bundle.getHeaders().get(MANIFEST_SMILA_PIPELETS);
      if (pipeletManifest != null) {
        if (_log.isDebugEnabled()) {
          _log.debug("Found manifest header " + MANIFEST_SMILA_PIPELETS + " = " + pipeletManifest + " in bundle "
            + bundleName);
        }
        final Map<String, Class<? extends IPipelet>> pipeletClasses =
          new HashMap<String, Class<? extends IPipelet>>();
        final String[] pipeletManifestEntries = pipeletManifest.split(",");
        for (final String pipeletClassName : pipeletManifestEntries) {
          loadPipeletClass(pipeletClasses, pipeletClassName, bundle);
        }
        if (!pipeletClasses.isEmpty()) {
          _knownPipelets.put(bundleName, pipeletClasses);
          for (final PipeletTrackerListener listener : _listeners) {
            listener.pipeletsAdded(pipeletClasses);
          }
        }
      }
      // else if (LOG.isDebugEnabled()) {
      //   LOG.debug("No manifest header " + MANIFEST_SMILA_PIPELETS + " in bundle " + bundleName);
      // }
    }
  }

  /**
   * load pipelet class from bundle and add it to the map.
   *
   * @param pipeletClasses
   *          pipelet registry.
   * @param pipeletClassName
   *          name of pipelet.
   * @param bundle
   *          bundle containing pipelet
   */
  @SuppressWarnings("unchecked")
  private void loadPipeletClass(final Map<String, Class<? extends IPipelet>> pipeletClasses,
    final String pipeletClassName, final Bundle bundle) {
    final String trimmedClassName = pipeletClassName.trim();
    if (trimmedClassName.length() > 0) {
      if (_log.isDebugEnabled()) {
        _log.debug("Found pipelet class name = " + trimmedClassName);
      }
      try {
        final Class<? extends IPipelet> pipeletClass = bundle.loadClass(trimmedClassName);
        pipeletClasses.put(trimmedClassName, pipeletClass);
        if (_log.isDebugEnabled()) {
          _log.debug("Pipelet class " + trimmedClassName + " loaded.");
        }
      } catch (final ClassNotFoundException ex) {
        _log.error("Pipelet class " + trimmedClassName + " could not be loaded from bundle "
          + bundle.getSymbolicName(), ex);
      }
    }
  }

  /**
   * remove pipelets found in this bundle from list of known pipelets. Notify listeners about lost pipelets.
   *
   * @param bundle
   *          bundle being stopped.
   */
  private void bundleRemoved(final Bundle bundle) {
    final String bundleName = bundle.getSymbolicName();
    if (_knownPipelets.containsKey(bundleName)) {
      final Map<String, Class<? extends IPipelet>> pipeletClassNames = _knownPipelets.remove(bundleName);
      for (final PipeletTrackerListener listener : _listeners) {
        listener.pipeletsRemoved(pipeletClassNames);
      }
    }
  }

}
