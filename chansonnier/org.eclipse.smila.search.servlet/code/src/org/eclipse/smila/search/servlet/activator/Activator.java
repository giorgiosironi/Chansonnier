/*******************************************************************************
 * Copyright (c) 2009 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.search.servlet.activator;

import org.eclipse.smila.lucene.LuceneIndexService;
import org.eclipse.smila.search.api.SearchService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Activator for search servlet bundle. Purpose is to create a ServiceTracker for {@link SearchService} services.
 * 
 * @author jschumacher
 * 
 */
public class Activator implements BundleActivator {
  /**
   * bundle name.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.search.servlet";

  /**
   * Bundle context.
   */
  private static BundleContext s_bundleContext;

  /**
   * service tracker for SMILA search service.
   */
  private static ServiceTracker s_searchServiceTracker;

  
  /**
   * service tracker for SMILA LuceneIndexService.
   */
  private static ServiceTracker s_luceneIndexServiceTracker;
  
  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    s_bundleContext = context;
    s_searchServiceTracker = new ServiceTracker(context, SearchService.class.getName(), null);
    s_searchServiceTracker.open();
    s_luceneIndexServiceTracker = new ServiceTracker(context, LuceneIndexService.class.getName(), null);
    s_luceneIndexServiceTracker.open();    
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    ; // do nothing
  }

  /**
   * @return a currently available search service, or null if none is registered.
   */
  public static SearchService getSearchService() {
    return (SearchService) s_searchServiceTracker.getService();
  }

  /**
   * @return a currently available LuceneIndexService, or null if none is registered.
   */
  public static LuceneIndexService getLuceneIndexService() {
    return (LuceneIndexService) s_luceneIndexServiceTracker.getService();
  }
  
  /**
   * @return OSGI bundle context
   */
  public static BundleContext getBundleContxt() {
    return s_bundleContext;
  }

}
