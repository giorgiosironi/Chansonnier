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

package org.eclipse.smila.webservice.test;

import junit.framework.TestCase;

import org.eclipse.smila.webservice.helloworld.client.HelloWorld;
import org.eclipse.smila.webservice.helloworld.client.HelloWorldService;

/**
 * test a simple webservice published by SMILA.
 * 
 * @author jschumacher
 * 
 */
public class TestHelloWorldService extends TestCase {

  /**
   * try to call the published webservice.
   * 
   * @throws Exception
   *           test fails.
   */
  public void testService() throws Exception {
    final HelloWorldService service = new HelloWorldService();
    assertNotNull(service);
    final HelloWorld port = service.getHelloWorldPort();
    assertNotNull(port);
    final String response = port.sayHi("SMILA");
    assertNotNull(response);
    assertEquals("Hello SMILA", response);
  }
}
