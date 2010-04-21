/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.ode;

import org.apache.ode.axis2.EndpointReferenceContextImpl;
import org.apache.ode.axis2.MessageExchangeContextImpl;
import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.EndpointReferenceContext;
import org.apache.ode.bpel.iapi.MessageExchangeContext;

/**
 * server context factory to create context object for invoking web services.
 * 
 * @author jschumacher
 * 
 */
public class WebServiceContextFactory implements ODEServerContextFactory {

  /**
   * local binding context.
   */
  protected WebServiceBindingContext _bindingContext;

  /**
   * local epr context.
   */
  protected EndpointReferenceContextImpl _eprContext;

  /**
   * local mex context.
   */
  protected MessageExchangeContextImpl _mexContext;

  /**
   * {@inheritDoc}
   * 
   * @return {@link WebServiceBindingContext}
   */
  public BindingContext createBindingContext(ODEServer server) {
    if (_bindingContext == null) {
      _bindingContext = new WebServiceBindingContext(server);
    }
    return _bindingContext;
  }

  /**
   * {@inheritDoc}
   * 
   * @return {@link EndpointReferenceContextImpl}
   */
  public EndpointReferenceContext createEPRContext() {
    if (_eprContext == null) {
      _eprContext = new EndpointReferenceContextImpl(null);
    }
    return _eprContext;
  }

  /**
   * {@inheritDoc}
   * 
   * @return {@link MessageExchangeContextImpl}
   */
  public MessageExchangeContext createMessageExchangeContext() {
    if (_mexContext == null) {
      _mexContext = new MessageExchangeContextImpl(null);
    }
    return _mexContext;
  }

}
