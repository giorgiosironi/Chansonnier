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

import java.util.HashMap;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.deployment.FileSystemConfigurator;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.axis2.ExternalService;
import org.apache.ode.axis2.httpbinding.HttpExternalService;
import org.apache.ode.axis2.soapbinding.SoapExternalService;
import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.utils.wsdl.WsdlUtils;

/**
 * SMILA implementation of the {@link org.apache.ode.bpel.iapi.BindingContext} interface. Deals with the activation of
 * endpoints for external webservices.
 * 
 * Some of the code here is copied and adapted from class {@link org.apache.ode.axis2.BindingContextImpl} in the AXIS2
 * integration layer of ODE by Maciej Szefler - m s z e f l e r @ g m a i l . c o m
 * 
 */
public class WebServiceBindingContext implements BindingContext {
  /**
   * associated server.
   */
  private final ODEServer _server;

  /**
   * Service EPRs of active BPEL processes.
   */
  private final HashMap<String, EndpointReferenceImpl> _activated = new HashMap<String, EndpointReferenceImpl>();

  /**
   * EPRs of all endpoints of all BPEL processes.
   */
  private final HashMap<String, EndpointReferenceImpl> _endpoints = new HashMap<String, EndpointReferenceImpl>();

  /**
   * map of external web services.
   */
  private final MultiKeyMap _externalServices = new MultiKeyMap();

  /**
   * default axis configuration for SOAP invocations.
   */
  private AxisConfiguration _axisConfig;

  /**
   * logger for this class.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * @param server
   *          associated server.
   */
  public WebServiceBindingContext(ODEServer server) {
    _server = server;
    try {
      // StAXUtils.setFactoryPerClassLoader(false);
      final FileSystemConfigurator configurator = new FileSystemConfigurator(null, null);
      _axisConfig = configurator.getAxisConfiguration();
    } catch (Throwable ex) {
      _log.warn("error loading Axis configuration, will not be able to invoke external web services", ex);
      _axisConfig = new AxisConfiguration(); // just a dummy.
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.BindingContext#activateMyRoleEndpoint(javax.xml.namespace.QName,
   *      org.apache.ode.bpel.iapi.Endpoint)
   */
  public EndpointReference activateMyRoleEndpoint(QName processId, Endpoint myRoleEndpoint) {
    final EndpointReferenceImpl epr = new EndpointReferenceImpl(processId, myRoleEndpoint);
    _activated.put(myRoleEndpoint.toString(), epr);
    return epr;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.BindingContext#deactivateMyRoleEndpoint(org.apache.ode.bpel.iapi.Endpoint)
   */
  public void deactivateMyRoleEndpoint(Endpoint myRoleEndpoint) {
    _activated.remove(myRoleEndpoint.toString());
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.iapi.BindingContext#createPartnerRoleChannel(javax.xml.namespace.QName,
   *      javax.wsdl.PortType, org.apache.ode.bpel.iapi.Endpoint)
   */
  public PartnerRoleChannel createPartnerRoleChannel(QName processId, PortType portType,
    Endpoint initialPartnerEndpoint) {
    final ProcessConf pconf = _server.getProcessConfiguration(processId);
    final Definition wsdl = pconf.getDefinitionForService(initialPartnerEndpoint.serviceName);
    if (wsdl == null) { // assume that this is an invocation of a sub pipeline.
      final EndpointReferenceImpl epr = new EndpointReferenceImpl(processId, initialPartnerEndpoint);
      _endpoints.put(initialPartnerEndpoint.serviceName.toString(), epr);
      return new PartnerRoleChannelImpl(epr);
    } else {
      return createExternalService(pconf, initialPartnerEndpoint.serviceName, initialPartnerEndpoint.portName);
    }
  }

  /**
   * create external service object for partner web service.
   * 
   * @param pconf
   *          configuraton of BPEL process
   * @param serviceName
   *          name of external service.
   * @param portName
   *          name of port of external service.
   * @return external service invoker
   */
  private ExternalService createExternalService(ProcessConf pconf, QName serviceName, String portName) {
    ExternalService extService = (ExternalService) _externalServices.get(serviceName);
    if (extService != null) {
      return extService;
    }

    final Definition def = pconf.getDefinitionForService(serviceName);
    try {
      if (WsdlUtils.useHTTPBinding(def, serviceName, portName)) {
        if (_log.isDebugEnabled()) {
          _log.debug("Creating HTTP-bound external service " + serviceName);
        }
        extService = new HttpExternalService(pconf, serviceName, portName, _server.getBpelServer());
      } else if (WsdlUtils.useSOAPBinding(def, serviceName, portName)) {
        if (_log.isDebugEnabled()) {
          _log.debug("Creating SOAP-bound external service " + serviceName);
        }
        extService = new SoapExternalService(def, serviceName, portName, _axisConfig, pconf);
      }
    } catch (Exception ex) {
      _log.error("Could not create external service.", ex);
      throw new ContextException("Error creating external service! name:" + serviceName + ", port:" + portName, ex);
    }

    // if not SOAP nor HTTP binding
    if (extService == null) {
      throw new ContextException("Only SOAP and HTTP binding supported!");
    }

    _externalServices.put(serviceName, portName, extService);
    _log.debug("Created external service " + serviceName);
    return extService;
  }
}
