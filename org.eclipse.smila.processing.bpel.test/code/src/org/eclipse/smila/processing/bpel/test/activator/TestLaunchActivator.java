/*******************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel.test.activator;

import java.util.List;

import org.eclipse.smila.blackboard.BlackboardFactory;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Activator for error tracking launches. Not used by JUnit tests.
 * 
 * @author jschumacher
 * 
 */
public class TestLaunchActivator implements BundleActivator {

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    final ServiceReference bbref = context.getServiceReference(BlackboardFactory.class.getName());
    final BlackboardFactory factory = (BlackboardFactory) context.getService(bbref);
    if (factory == null) {
      System.out.println("BLACKBOARD FACTORY is NULL");
    } else {
      System.out.println("have BLACKBOARD FACTORY");
    }
    final ServiceReference ref = context.getServiceReference(WorkflowProcessor.class.getName());
    final WorkflowProcessor proc = (WorkflowProcessor) context.getService(ref);
    if (proc == null) {
      System.out.println("WORKFLOWPROCESSOR is NULL");
      // final ODEServerException ex = new ODEServerException("test");
    } else {
      System.out.println("have WORKFLOWPROCESSOR");
      final List<String> pipelines = proc.getWorkflowNames();
      System.out.println("available pipelines: " + pipelines);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    // nothing to do

  }

}
