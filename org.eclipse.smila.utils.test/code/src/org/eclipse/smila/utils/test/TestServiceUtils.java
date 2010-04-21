/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 **********************************************************************************************************************/
package org.eclipse.smila.utils.test;

import junit.framework.TestCase;

import org.eclipse.smila.utils.UtilsActivator;
import org.eclipse.smila.utils.service.ServiceUtils;
import org.osgi.framework.BundleContext;

/**
 * The Class TestServiceUtils.
 */
public class TestServiceUtils extends TestCase {

  /**
   * The Constant ALT_TIMEOUT.
   */
  private static final int ALT_TIMEOUT = 20000;

  /**
   * The Constant ALT_SMALL_TIMEOUT.
   */
  private static final int ALT_SMALL_TIMEOUT = 200;

  /**
   * The _utils context.
   */
  private BundleContext _utilsContext;

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    _utilsContext = UtilsActivator.getBundleContext();
    assertNotNull(_utilsContext);
    _utilsContext.registerService(TestService.class.getName(), new TestService(), null);
  }

  /**
   * {@inheritDoc}
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    _utilsContext = null;
    super.tearDown();
  }

  /**
   * Test get service.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetService() throws InterruptedException {
    final TestService service = ServiceUtils.getService(TestService.class);
    assertNotNull(service);
  }

  /**
   * Test get service alt timeout.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetServiceAltTimeout() throws InterruptedException {
    final TestService service = ServiceUtils.getService(TestService.class, ALT_TIMEOUT);
    assertNotNull(service);
  }

  /**
   * Test get service from spec context.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetServiceFromSpecContext() throws InterruptedException {
    final TestService service = ServiceUtils.getService(_utilsContext, TestService.class);
    assertNotNull(service);
  }

  /**
   * Test get service from spec context alt timeout.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetServiceFromSpecContextAltTimeout() throws InterruptedException {
    final TestService service = ServiceUtils.getService(_utilsContext, TestService.class, ALT_TIMEOUT);
    assertNotNull(service);
  }

  /**
   * Test get service delayed registration.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetServiceDelayedRegistration() throws InterruptedException {
    final Thread thread = new Thread(new RegisterServiceTask());
    thread.start();
    final TestService service = ServiceUtils.getService(_utilsContext, TestService.class, ALT_TIMEOUT);
    assertNotNull(service);
  }

  /**
   * Test get no service ex.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testGetNoServiceEx() throws InterruptedException {
    // smaller timeout used because there is no service 3
    TestService3 service = null;
    try {
      service = ServiceUtils.getService(TestService3.class, ALT_SMALL_TIMEOUT);
    } catch (final RuntimeException e) {
      ;// ok
    }
    assertNull(service);
  }

  /**
   * The Class RegisterServiceTask.
   */
  private class RegisterServiceTask implements Runnable {

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        ;// nothing
      }
      _utilsContext.registerService(TestService2.class.getName(), new TestService2(), null);
    }

  }

  /**
   * The Class TestService.
   */
  private class TestService {

  }

  /**
   * The Class TestService2.
   */
  private class TestService2 {

  }

  /**
   * The Class TestService3.
   */
  private class TestService3 {

  }
}
