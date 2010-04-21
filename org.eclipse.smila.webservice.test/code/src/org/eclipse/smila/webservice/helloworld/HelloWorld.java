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

package org.eclipse.smila.webservice.helloworld;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * a simple webservice.
 * 
 * @author jschumacher
 * 
 */
@WebService()
public class HelloWorld {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * default constructor.
   */
  public HelloWorld() {
  }

  /**
   * the webservice method: prepend "Hello " to the argument and return the result.
   * 
   * @param text
   *          some text
   * @return "Hello " + <code>text</code>
   */
  @WebMethod()
  public String sayHi(final String text) {
    _log.info("Received a Hello from " + text);
    return "Hello " + text;
  }
}
