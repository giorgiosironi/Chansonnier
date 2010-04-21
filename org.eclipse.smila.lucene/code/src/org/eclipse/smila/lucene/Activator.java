/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Daniel Stucky (empolis GmbH) - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.lucene;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.lucene.index.access.IndexWriterPool;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Bundle Activator for bundle org.eclipse.smila.lucene.
 */
public class Activator implements BundleActivator {

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(Activator.class);

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
  }

  /**
   * {@inheritDoc} Closes all opened IndexWriters.
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    try {
      IndexWriterPool.closeAll();
    } catch (Exception e) {
      if (_log.isErrorEnabled()) {
        _log.error("Error closing Lucene IndexWriters", e);
      }
    }
  }

}
