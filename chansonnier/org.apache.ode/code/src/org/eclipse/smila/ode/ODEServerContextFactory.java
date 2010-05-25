/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.ode;

import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.EndpointReferenceContext;
import org.apache.ode.bpel.iapi.MessageExchangeContext;

/**
 * Interface for factories providing the ODEServer with the necessary context objects for integration into runtime
 * process.
 * 
 * @author jschumacher
 * 
 */
public interface ODEServerContextFactory {

  /**
   * @param server
   *          the server to integrate
   * @return BindingContext for integration
   */
  BindingContext createBindingContext(ODEServer server);

  /**
   * @return EndpointReferenceContext
   */
  EndpointReferenceContext createEPRContext();

  /**
   * @return MessageExchangeContext for integration.
   */
  MessageExchangeContext createMessageExchangeContext();

}
