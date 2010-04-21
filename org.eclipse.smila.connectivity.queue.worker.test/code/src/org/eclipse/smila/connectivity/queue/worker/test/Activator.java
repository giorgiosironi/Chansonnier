/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.connectivity.queue.worker.Listener;
import org.eclipse.smila.connectivity.queue.worker.Operation;
import org.eclipse.smila.connectivity.queue.worker.Router;
import org.eclipse.smila.connectivity.queue.worker.RouterException;
import org.eclipse.smila.datamodel.id.IdFactory;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.test.RecordCreator;
import org.eclipse.smila.utils.service.ServiceUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class Activator - its not used now but it helps to make stress testing.
 */
public class Activator implements BundleActivator {

  /**
   * The Constant PAUSE.
   */
  private static final int PAUSE = 10;

  /**
   * The log.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * The _stop.
   */
  private boolean _stop;

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(final BundleContext context) throws Exception {
    try {
      final Router router = ServiceUtils.getService(context, Router.class);
      // final BrokerConnectionService bks = ServiceUtils.getService(context, BrokerConnectionService.class);
      _log.info("Router found!");
      ServiceUtils.getService(context, Listener.class);
      _log.info("Listener found!");
      _stop = false;
      int i = 0;
      final int size = 500;
      while (!_stop && i++ < size) {
        route(router, i);
        Thread.sleep(PAUSE);
      }
    } catch (final Throwable e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }

  /**
   * Route.
   * 
   * @param router
   *          the router
   * @param index
   *          the index
   * 
   * @throws RouterException
   *           the router exception
   */
  private void route(final Router router, final int index)
    throws RouterException {
    _log.info("Routing record " + index + "...");
    final Record record = RecordCreator.createTestRecord1();
    record.setId(IdFactory.DEFAULT_INSTANCE.createId("source", "key" + index));
    // do not need it because router do it byself
    // blackboard.create(record.getId());
    // blackboard.setRecord(record);
    // blackboard.commit(record.getId());
    router.route(new Record[]{record}, Operation.ADD);
    _log.info("Record " + index + " routed.");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(final BundleContext context) throws Exception {
    _stop = true;
  }

}
