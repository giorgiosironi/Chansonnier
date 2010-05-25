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

import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Simple implementation of {@link EndpointReference}.
 * 
 * XML representation is
 * 
 * <pre>
 * &lt;service-ref xmlns=&quot;http://docs.oasis-open.org/wsbpel/2.0/serviceref&quot;&gt;serviceName&lt;/service-ref&gt;
 * </pre>
 * 
 * @author jschumacher
 * 
 */
public class EndpointReferenceImpl implements EndpointReference {
  /**
   * ID of BPEL process.
   */
  private final QName _processId;

  /**
   * endpoint of BPEL process: name and port of provided or invoked service.
   */
  private final Endpoint _endpoint;

  /**
   * XML representation of EPR.
   */
  private Document _doc;

  /**
   * create new EPR.
   * 
   * @param processId
   *          process id
   * @param endpoint
   *          endpoint
   */
  public EndpointReferenceImpl(QName processId, Endpoint endpoint) {
    _processId = processId;
    _endpoint = endpoint;
  }

  /**
   * @return process Id.
   */
  public QName getProcessId() {
    return _processId;
  }

  /**
   * @return endpoint.
   */
  public Endpoint getEndpoint() {
    return _endpoint;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.EndpointReference#toXML()
   */
  public Document toXML() {
    if (_doc == null) {
      _doc = DOMUtils.newDocument();
      final Element serviceRef =
        _doc.createElementNS(SERVICE_REF_QNAME.getNamespaceURI(), SERVICE_REF_QNAME.getLocalPart());
      serviceRef.appendChild(_doc.createTextNode(_endpoint.serviceName.toString()));
      _doc.appendChild(serviceRef);
    }
    return _doc;
  }
}
