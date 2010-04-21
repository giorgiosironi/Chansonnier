/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.utils.extensions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;

/**
 * The Class AbstractPluginRegistryBase.
 * 
 * @param <T>
 *          plug-in interface
 */
public abstract class AbstractPluginRegistryBase<T> {

  /** The _listener. */
  protected IRegistryChangeListener _listener = new RegistryListener();

  /** The _mutex. */
  protected Object _mutex = new Object();

  /** The Constant LOG. */
  private final Log _log = LogFactory.getLog(AbstractPluginRegistryBase.class);

  /**
   * Instantiates a new abstract plug-in registry base.
   */
  public AbstractPluginRegistryBase() {
    initialize();
    Platform.getExtensionRegistry().addRegistryChangeListener(_listener);
  }

  /**
   * Gets the extention point name space.
   * 
   * @return the extention point name space
   */
  protected abstract String getExtensionPointNameSpace();

  /**
   * Gets the extension point local name.
   * 
   * @return the extension point local name
   */
  protected abstract String getExtensionPointLocalName();

  /**
   * Initialize.
   */
  protected void initialize() {
    final IExtensionPoint extensionPoint =
      Platform.getExtensionRegistry().getExtensionPoint(getExtensionPointFullName());
    final IExtension[] extensions = extensionPoint.getExtensions();
    for (final IExtension extension : extensions) {
      try {
        extensionAdded(extension.getUniqueIdentifier(), extension);
      } catch (final Throwable e) {
        _log.error("Error initializing AbstractPluginRegistryBase", e);
      }
    }
    finalizeExtensionChanges();
  }

  /**
   * Extension added.
   * 
   * @param id
   *          the id
   * @param extension
   *          the extension
   * 
   * @throws CoreException
   *           the core exception
   */
  protected abstract void extensionAdded(String id, IExtension extension) throws CoreException;

  /**
   * Extension removed.
   * 
   * @param id
   *          the id
   * 
   * @throws CoreException
   *           the core exception
   */
  protected abstract void extensionRemoved(final String id) throws CoreException;

  /**
   * Finalize extension changes.
   */
  protected abstract void finalizeExtensionChanges();

  /**
   * Gets the extension point full name.
   * 
   * @return the extension point full name
   */
  protected String getExtensionPointFullName() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getExtensionPointNameSpace());
    sb.append('.');
    sb.append(getExtensionPointLocalName());
    return sb.toString();
  }

  /**
   * Creates the instance.
   * 
   * @param extension
   *          the extension
   * 
   * @return the t
   * 
   * @throws CoreException
   *           the core exception
   */
  @SuppressWarnings("unchecked")
  protected T createInstance(final IExtension extension) throws CoreException {
    return (T) extension.getConfigurationElements()[0].createExecutableExtension("class");
  }

  /**
   * The listener interface for receiving registry events. The class that is interested in processing a registry event
   * implements this interface, and the object created with that class is registered with a component using the
   * component's <code>addRegistryListener</code> method. When the registry event occurs, that object's appropriate
   * method is invoked.
   * 
   * @see RegistryEvent
   */
  protected class RegistryListener implements IRegistryChangeListener {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IRegistryChangeListener#
     *      registryChanged(org.eclipse.core.runtime.IRegistryChangeEvent)
     */
    public void registryChanged(final IRegistryChangeEvent event) {
      final IExtensionDelta[] deltas =
        event.getExtensionDeltas(getExtensionPointNameSpace(), getExtensionPointLocalName());
      if (_log.isTraceEnabled()) {
        _log.trace("RegistryListener registryChanged: DELTAS length=" + deltas.length);
      }
      if (deltas.length > 0) {
        synchronized (_mutex) {
          for (final IExtensionDelta delta : deltas) {
            final String id = delta.getExtension().getUniqueIdentifier();
            if (delta.getKind() == IExtensionDelta.ADDED) {
              if (_log.isTraceEnabled()) {
                _log.trace("Extension " + id + " state changed (ADDED)");
              }
              try {
                extensionAdded(id, delta.getExtension());
              } catch (final Throwable e) {
                _log.error(e);
              }
            } else if (delta.getKind() == IExtensionDelta.REMOVED) {
              if (_log.isTraceEnabled()) {
                _log.trace("Extension " + id + " state changed (REMOVED)");
              }
              try {
                extensionRemoved(id);
              } catch (final Throwable e) {
                _log.error(e);
              }
            } else {
              throw new RuntimeException("Unknown Delta Kind=" + delta.getKind());
            }
            finalizeExtensionChanges();
          }
        }
      }
    }

  }

}
