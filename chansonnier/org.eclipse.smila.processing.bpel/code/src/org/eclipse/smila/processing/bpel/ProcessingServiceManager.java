/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.rtrep.v2.OExtensionActivity;
import org.apache.ode.bpel.rtrep.v2.OProcess;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.ProcessorMessage;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchProcessingService;
import org.w3c.dom.Element;

/**
 * SimplePipelet Manager and Invoker.
 * 
 * @author jschumacher
 * 
 */
public final class ProcessingServiceManager extends ExtensionManager {

  /**
   * singleton instance.
   */
  private static final ProcessingServiceManager INSTANCE = new ProcessingServiceManager();

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * singleton constructor.
   */
  private ProcessingServiceManager() {
    super();
  }

  /**
   * singleton instance access method.
   * 
   * @return singleton instance of ProcessingServiceManager
   */
  public static ProcessingServiceManager getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager#getExtensionName()
   */
  @Override
  public String getExtensionName() {
    return SMILAExtensionBundle.TAG_INVOKE_SERVICE;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager#registerActivity(org.apache.ode.bpel.o.OProcess,
   *      org.apache.ode.bpel.o.OExtensionActivity, org.w3c.dom.Element, java.lang.String)
   */
  @Override
  public ProcessingServiceInvoker doRegisterActivity(final OProcess pipelineProcess,
    final OExtensionActivity activity, final Element content, final String key) {
    final ProcessingServiceInvoker invoker = new ProcessingServiceInvoker();
    invoker.setServiceName(getAttributeOfElement(content, "service", "name"));
    if (invoker.getServiceName() == null) {
      throw new CompilationException(createErrorCompilationMessage(key, "Missing definition of service name"));
    }
    final String location = activity.name.substring(activity.name.lastIndexOf("line-"));
    invoker
      .setCounter(new ServicePerformanceCounter(pipelineProcess.getName(), invoker.getServiceName(), location));
    return invoker;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager
   *      #checkAvailability(org.eclipse.smila.processing.bpel.ExtensionAdapter,
   *      org.eclipse.smila.processing.bpel.ODEWorkflowProcessor)
   */
  @Override
  public void checkAvailability(final ExtensionAdapter adapter, final ODEWorkflowProcessor processor)
    throws ProcessingException {
    final ProcessingService service = getProcessingService(adapter, processor);
    if (service == null) {
      final SearchProcessingService searchService = getSearchProcessingService(adapter, processor);
      if (searchService == null) {
        throw new ProcessingException(adapter.getPrintName() + " for activity " + adapter.getKey()
          + " is not yet registered.");
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager
   *      #doInvoke(org.eclipse.smila.processing.bpel.ExtensionAdapter,
   *      org.eclipse.smila.processing.bpel.ODEWorkflowProcessor, org.eclipse.smila.blackboard.Blackboard,
   *      org.eclipse.smila.processing.ProcessorMessage)
   */
  @Override
  public ProcessorMessage doInvoke(final ExtensionAdapter adapter, final ODEWorkflowProcessor processor,
    final Blackboard blackboard, final ProcessorMessage request) throws ProcessingException {
    int incomingIds = 0;
    int outgoingIds = 0;
    try {
      if (request instanceof SearchMessage) {
        final SearchProcessingService searchService = getSearchProcessingService(adapter, processor);
        if (searchService != null) {
          incomingIds = getRecordCount(((SearchMessage) request).getRecords()) + 1;
          final SearchMessage result = searchService.process(blackboard, (SearchMessage) request);
          outgoingIds = getRecordCount(result.getRecords()) + 1;
          return result;
        }
      }
      final ProcessingService procService = getProcessingService(adapter, processor);
      if (procService != null) {
        if (request instanceof SearchMessage && !((SearchMessage) request).hasRecords()) {
          final SearchMessage searchRequest = (SearchMessage) request;
          if (searchRequest.hasQuery()) {
            final Id[] query = new Id[] { searchRequest.getQuery() };
            incomingIds = 1;
            final Id[] result = procService.process(blackboard, query);
            outgoingIds = getRecordCount(result);
            if (result.length > 0) {
              searchRequest.setQuery(query[0]);
              if (result.length > 1) {
                _log.warn(adapter.getPrintName() + " has splitted a query object. Extra objects are ignored.");
              }
            }
          }
        } else {
          incomingIds = getRecordCount(request.getRecords());
          final Id[] result = procService.process(blackboard, request.getRecords());
          outgoingIds = getRecordCount(result);
          request.setRecords(result);
        }
        return request;
      }
      // we should not end up here.
      throw new ProcessingException("Could not invoke " + adapter.getPrintName()
        + "Either the service does not exist or you tried to invoke a search service with a simple variable.");
    } finally {
      if (adapter.getCounter() != null) {
        adapter.getCounter().countIds(incomingIds, outgoingIds);
      }
    }
  }

  /**
   * get processing service for activity.
   * 
   * @param adapter
   *          processing service invoker
   * @param processor
   *          associated processor
   * @return service instance.
   */
  private ProcessingService getProcessingService(final ExtensionAdapter adapter,
    final ODEWorkflowProcessor processor) {
    final String serviceName = ((ProcessingServiceInvoker) adapter).getServiceName();
    return processor.getProcessingService(serviceName);
  }

  /**
   * get search service for activity.
   * 
   * @param adapter
   *          processing service invoker
   * @param processor
   *          associated processor
   * @return service instance.
   */
  private SearchProcessingService getSearchProcessingService(final ExtensionAdapter adapter,
    final ODEWorkflowProcessor processor) {
    final String serviceName = ((ProcessingServiceInvoker) adapter).getServiceName();
    return processor.getSearchProcessingService(serviceName);
  }

}
