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

package org.eclipse.smila.ode;

import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;

/**
 * Simple implementation of ODE {@link PartnerRoleChannel}.
 * 
 * @author jschumacher
 * 
 */
public class PartnerRoleChannelImpl implements PartnerRoleChannel {

  /**
   * initial EPR of this PartnerRoleChannel.
   */
  private final EndpointReference _initialEPR;

  /**
   * create new PRC for specified EPR.
   * 
   * @param epr
   *          the EPR
   */
  public PartnerRoleChannelImpl(EndpointReference epr) {
    _initialEPR = epr;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.PartnerRoleChannel#getInitialEndpointReference()
   */
  public EndpointReference getInitialEndpointReference() {
    return _initialEPR;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.PartnerRoleChannel#close()
   */
  public void close() {
    // nothing to do.
  }

}
