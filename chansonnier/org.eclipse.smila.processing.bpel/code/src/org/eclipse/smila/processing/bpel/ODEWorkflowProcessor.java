/***********************************************************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.bpel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.utils.DOMUtils;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.ode.ODEConfigProperties;
import org.eclipse.smila.ode.ODEServer;
import org.eclipse.smila.ode.ODEServerException;
import org.eclipse.smila.ode.WebServiceContextFactory;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessingService;
import org.eclipse.smila.processing.ProcessorMessage;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchProcessingService;
import org.eclipse.smila.processing.WorkflowProcessor;
import org.eclipse.smila.utils.config.ConfigUtils;
import org.eclipse.smila.utils.workspace.WorkspaceHelper;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.w3c.dom.Element;

/**
 * SMILA Workflow Processor that uses the Apache ODE BPEL engine to orchestrate SMILA pipelets in BPEL processes.
 * 
 * @author jschumacher
 * 
 */
public class ODEWorkflowProcessor implements WorkflowProcessor {

  /**
   * name of OSGi service reference to pielets.
   */
  public static final String REFERENCE_PROCSERCVICE = "processingService";

  /**
   * name of OSGi service reference to search processing services.
   */
  public static final String REFERENCE_SEARCHPROCSERCVICE = "searchProcessingService";

  /**
   * name of WSDL operation of SMILA BPEL processes.
   */
  public static final String OPERATION_NAME = "process";

  /**
   * name of bundle. Used in configuration reading.
   */
  public static final String BUNDLE_NAME = "org.eclipse.smila.processing.bpel";

  /**
   * name of configuration directory. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_DIR = "configuration/" + BUNDLE_NAME;

  /**
   * name of configuration file. Hardcoded for now (or fallback), configuration properties should be received from
   * configuration service later.
   */
  public static final String CONFIGURATION_FILE = "processor.properties";

  /**
   * configuration property name: directory of BPEL pipeline files in configuration directory = "pipeline.dir".
   */
  public static final String PROP_PIPELINE_DIR = "pipeline.dir";

  /**
   * default value for PROP_PIPELINE_DIR = "piplines".
   */
  public static final String DEFAULT_PIPELINE_DIR = "pipelines";

  /**
   * prefix of ODE configuration properties in property file.
   */
  public static final String PROP_PREFIX_ODE = "ode.";

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(ODEWorkflowProcessor.class);

  /**
   * BPEL server.
   */
  private ODEServer _bpelServer;

  /**
   * Context factory for ODE server.
   */
  private WebServiceContextFactory _processingContext;

  /**
   * Blackboard services.
   */
  private final Map<String, Blackboard> _blackboards = new HashMap<String, Blackboard>();

  /**
   * Request Id sequence.
   */
  private final AtomicLong _requestIdSequence = new AtomicLong(0);

  /**
   * last exception thrown in pipelet/service execution. To be reused in case the pipelet fails.
   */
  private final Map<String, Exception> _pipeletExceptions = new HashMap<String, Exception>();

  /**
   * configuraton properties.
   */
  private Properties _properties;

  /**
   * helper to create and parse XML DOM messages coming in and out of ODE engine.
   */
  private MessageHelper _messageHelper;

  /**
   * known SMILA processing services that can be invoked by BPEL processes.
   */
  private final Map<String, ProcessingService> _processingServices = new HashMap<String, ProcessingService>();

  /**
   * service references to SMILA processing services.
   */
  private final Map<String, ServiceReference> _processingServiceRefs = new HashMap<String, ServiceReference>();

  /**
   * known SMILA search processing services that can be invoked by BPEL processes.
   */
  private final Map<String, SearchProcessingService> _searchProcessingServices =
    new HashMap<String, SearchProcessingService>();

  /**
   * service references to SMILA search processing services.
   */
  private final Map<String, ServiceReference> _searchProcessingServiceRefs =
    new HashMap<String, ServiceReference>();

  /**
   * performance counters for measurement of pipeline performance.
   */
  private final Map<String, PipelinePerformanceCounter> _pipelinePerformanceCounter =
    new HashMap<String, PipelinePerformanceCounter>();

  /**
   * OSGi service context used to lookup pipelets after bind events.
   */
  private ComponentContext _osgiContext;

  /**
   * process methods use read lock, deactivate needs write lock.
   */
  private ReadWriteLock _lock = new ReentrantReadWriteLock(true);

  /**
   * create processor. BPEL server is initialized in activate method.
   */
  public ODEWorkflowProcessor() {
    _log.debug(getClass().getName() + " instance created.");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.WorkflowProcessor#process(java.lang.String, org.eclipse.smila.datamodel.id.Id[])
   */
  public Id[] process(final String workflowName, final Blackboard blackboard, final Id[] recordIds)
    throws ProcessingException {
    final long startTime = System.nanoTime();
    int outgoingIds = 0;
    int incomingIds = 0;
    if (recordIds != null) {
      incomingIds = recordIds.length;
    }
    final String requestId = initRequest(blackboard);
    boolean success = false;
    final PipelinePerformanceCounter counter = _pipelinePerformanceCounter.get(workflowName);
    try {
      if (_bpelServer == null) {
        throw new ProcessingException("Cannot process request, because BPEL engine is not yet initialised");
      }
      final ProcessorMessage input = new ProcessorMessage(recordIds);
      final Element message = _messageHelper.createMessage(blackboard, input);
      _messageHelper.addRequestId(message, requestId, MessageHelper.TYPE_PROCESSORMESSAGE);
      if (_log.isTraceEnabled()) {
        _log.trace("Request: " + DOMUtils.domToString(message));
      }
      final QName processQName = new QName(NAMESPACE_PROCESSOR, workflowName);

      try {
        final Element result = _bpelServer.invoke(processQName, OPERATION_NAME, message);
        if (result != null) {
          if (_log.isTraceEnabled() || true) {
            _log.trace("Final Result: " + DOMUtils.domToString(result));
          }
          final ProcessorMessage resultIds = _messageHelper.parseMessage(blackboard, result);
          success = true;
          if (resultIds != null && resultIds.getRecords() != null) {
            outgoingIds = resultIds.getRecords().length;
          }
          return resultIds.getRecords();
        }
        return null;
      } catch (final ODEServerException ex) {
        final Exception pipeletEx = _pipeletExceptions.get(requestId);
        if (pipeletEx == null) {
          throw new ProcessingException("Error processing BPEL workflow " + workflowName + ": " + ex.getMessage(),
            ex);
        }
        throw new ProcessingException("Error processing BPEL workflow " + workflowName + ": "
          + pipeletEx.getMessage(), pipeletEx);
      }
    } catch (final ProcessingException ex) {
      if (counter != null) {
        counter.addError(ex, false);
      }
      throw ex;
    } catch (final Throwable ex) {
      if (counter != null) {
        counter.addError(ex, true);
      }
      throw new ProcessingException(ex);
    } finally {
      cleanupRequest(requestId);
      if (counter != null) {
        counter.countInvocationNanos(System.nanoTime() - startTime, success, incomingIds, outgoingIds);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.WorkflowProcessor#process(java.lang.String,
   *      org.eclipse.smila.blackboard.Blackboard, org.eclipse.smila.processing.SearchMessage)
   */
  public SearchMessage process(final String workflowName, final Blackboard blackboard, final SearchMessage query)
    throws ProcessingException {
    final long startTime = System.nanoTime();
    int outgoingIds = 0;
    int incomingIds = 0;
    if (query != null) {
      incomingIds = 1;
    }
    final String requestId = initRequest(blackboard);
    boolean success = false;
    final PipelinePerformanceCounter counter = _pipelinePerformanceCounter.get(workflowName);
    try {
      if (_bpelServer == null) {
        throw new ProcessingException("Cannot process request, because BPEL engine is not yet initialised");
      }
      final Element message = _messageHelper.createSearchMessage(blackboard, query);
      _messageHelper.addRequestId(message, requestId, MessageHelper.TYPE_SEARCHMESSAGE);
      if (_log.isDebugEnabled()) {
        _log.debug("Request: " + DOMUtils.domToString(message));
      }
      final QName processQName = new QName(NAMESPACE_PROCESSOR, workflowName);

      try {
        final Element result = _bpelServer.invoke(processQName, OPERATION_NAME, message);
        if (result != null) {
          if (_log.isDebugEnabled()) {
            _log.debug("Final Result: " + DOMUtils.domToString(result));
          }
          final SearchMessage searchResult = _messageHelper.parseSearchMessage(blackboard, result);
          success = true;
          if (searchResult != null && searchResult.getRecords() != null) {
            outgoingIds = searchResult.getRecords().length;
          }
          return searchResult;
        }
        return null;
      } catch (final ODEServerException ex) {
        final Exception pipeletEx = _pipeletExceptions.get(requestId);
        if (pipeletEx == null) {
          throw new ProcessingException("Error processing BPEL workflow " + workflowName + ": " + ex.getMessage(),
            ex);
        }
        throw new ProcessingException("Error processing BPEL workflow " + workflowName + ": "
          + pipeletEx.getMessage(), pipeletEx);
      }
    } catch (final ProcessingException ex) {
      if (counter != null) {
        counter.addError(ex, false);
      }
      throw ex;
    } catch (final Throwable ex) {
      if (counter != null) {
        counter.addError(ex, true);
      }
      throw new ProcessingException(ex);
    } finally {
      cleanupRequest(requestId);
      if (counter != null) {
        counter.countInvocationNanos(System.nanoTime() - startTime, success, incomingIds, outgoingIds);
      }
    }
  }

  /**
   * get the pipeline names of the active BPEL processes. The pipeline name is the local part of the EPR service name.
   * 
   * @return pipeline names of the active BPEL processes, or null, if engine is not active yet.
   */
  public List<String> getWorkflowNames() {
    if (_pipelinePerformanceCounter == null) {
      return null;
    }
    return new ArrayList<String>(_pipelinePerformanceCounter.keySet());

  }

  /**
   * get the processing service registered with the specified name.
   * 
   * @param name
   *          name of processing service
   * @return processing service registered for this name.
   */
  protected ProcessingService getProcessingService(final String name) {
    ProcessingService service = _processingServices.get(name);
    if (service == null) {
      if (_processingServiceRefs.containsKey(name)) {
        final ServiceReference reference = _processingServiceRefs.get(name);
        service = (ProcessingService) _osgiContext.locateService(REFERENCE_PROCSERCVICE, reference);
        if (service == null) {
          _log.error("Failed to locate service " + name);
        } else {
          _processingServices.put(name, service);
          if (_log.isInfoEnabled()) {
            _log.info("Resolved service reference " + name);
          }
        }
      } else {
        if (_log.isTraceEnabled()) {
          _log.trace("Do not have a reference to service " + name);
        }
      }
    }
    return service;
  }

  /**
   * get the search processing service registered with the specified name.
   * 
   * @param name
   *          name of searchprocessing service
   * @return search processing service registered for this name.
   */
  protected SearchProcessingService getSearchProcessingService(final String name) {
    SearchProcessingService service = _searchProcessingServices.get(name);
    if (service == null) {
      if (_searchProcessingServiceRefs.containsKey(name)) {
        final ServiceReference reference = _searchProcessingServiceRefs.get(name);
        service = (SearchProcessingService) _osgiContext.locateService(REFERENCE_SEARCHPROCSERCVICE, reference);
        if (service == null) {
          _log.error("Failed to locate service " + name);
        } else {
          _searchProcessingServices.put(name, service);
          if (_log.isInfoEnabled()) {
            _log.info("Resolved service reference " + name);
          }
        }
      } else {
        if (_log.isTraceEnabled()) {
          _log.trace("Do not have a reference to search service " + name);
        }
      }
    }
    return service;
  }

  /**
   * get blackboard service for request.
   * 
   * @param id
   *          request ID
   * @return blackboard service.
   * @throws ProcessingException
   *           no blackboard associated with id
   */
  public Blackboard getBlackboard(final String id) throws ProcessingException {
    final Blackboard blackboard = _blackboards.get(id);
    if (blackboard == null) {
      throw new ProcessingException("Blackboard for request " + id + " is not registered anymore.");
    }
    return blackboard;
  }

  /**
   * store a pipelet exception for better error reporting if the engine finally fails.
   * 
   * @param requestId
   *          id of request
   * @param ex
   *          exception thrown during pipelet execution.
   */
  public void setPipeletException(final String requestId, final Exception ex) {
    _pipeletExceptions.put(requestId, ex);
  }

  /**
   * @return message helper that converts Processor/SearchMessages to XML and vice versa
   */
  public MessageHelper getMessageHelper() {
    return _messageHelper;
  }

  /**
   * add a processing service as an OSGi service reference. To be used by Declarative Services as the bind method.
   * 
   * @param serviceReference
   *          service reference to add.
   */
  protected void setProcessingService(final ServiceReference serviceReference) {
    final String serviceName = serviceReference.getProperty(ProcessingService.PROPERTY_NAME).toString();
    if (serviceName == null) {
      _log.error("Cannot use processing service without property " + ProcessingService.PROPERTY_NAME);
    } else {
      _processingServiceRefs.put(serviceName, serviceReference);
      if (_log.isInfoEnabled()) {
        _log.info("Have reference to processing service " + serviceName);
      }
    }
  }

  /**
   * remove a processing service. To be used by Declarative Services as the unbind method.
   * 
   * @param serviceReference
   *          service reference to remove.
   */
  protected void unsetProcessingService(final ServiceReference serviceReference) {
    final String serviceName = serviceReference.getProperty(ProcessingService.PROPERTY_NAME).toString();
    if (serviceName != null) {
      _processingServices.remove(serviceName);
      _processingServiceRefs.remove(serviceName);
      if (_log.isInfoEnabled()) {
        _log.info("Have lost processing service " + serviceName);
      }
    }
  }

  /**
   * add a search processing service as an OSGi service reference. To be used by Declarative Services as the bind
   * method.
   * 
   * @param serviceReference
   *          service reference to add.
   */
  protected void setSearchProcessingService(final ServiceReference serviceReference) {
    final String serviceName = serviceReference.getProperty(ProcessingService.PROPERTY_NAME).toString();
    if (serviceName == null) {
      _log.error("Cannot use search processing service without property " + ProcessingService.PROPERTY_NAME);
    } else {
      _searchProcessingServiceRefs.put(serviceName, serviceReference);
      if (_log.isInfoEnabled()) {
        _log.info("Have reference to search processing service " + serviceName);
      }
    }
  }

  /**
   * remove a search processing service. To be used by Declarative Services as the unbind method.
   * 
   * @param serviceReference
   *          service reference to remove.
   */
  protected void unsetSearchProcessingService(final ServiceReference serviceReference) {
    final String serviceName = serviceReference.getProperty(ProcessingService.PROPERTY_NAME).toString();
    if (serviceName != null) {
      _searchProcessingServices.remove(serviceName);
      _searchProcessingServiceRefs.remove(serviceName);
      if (_log.isInfoEnabled()) {
        _log.info("Have lost search processing service " + serviceName);
      }
    }
  }

  /**
   * OSGi Declarative Services service activation method. Initializes BPEL engine.
   * 
   * @param context
   *          OSGi service component context.
   */
  protected void activate(final ComponentContext context) {
    _lock.writeLock().lock();
    try {
      _osgiContext = context;
      if (_bpelServer == null) {
        try {
          readConfiguration();
          _messageHelper = new MessageHelper(_properties);
          initializeBPEL();
        } catch (final IOException ex) {
          _log.error("Start of BPEL workflow service aborted: Could not read configuration."
            + "Service is non-functional, please fix problem and restart bundle", ex);
        } catch (final ODEServerException ex) {
          _log.error("Start of BPEL workflow service aborted: Initialization of ODE failed."
            + "Service is non-functional, please fix problem and restart bundle", ex);
        } catch (final ProcessingException ex) {
          _log.error("Start of BPEL workflow service aborted: Deployment of pipelines failed."
            + "Service is non-functional, please fix problem and restart bundle", ex);
        } catch (final RuntimeException ex) {
          // necessary to prevent automatic restarts of service before problem can be fixed.
          _log.error("Start of BPEL workflow service aborted: Unknown error."
            + "Service is non-functional, please fix problem and restart bundle", ex);
        } catch (final Throwable ex) {
          // necessary to prevent automatic restarts of service before problem can be fixed.
          _log.error("Start of BPEL workflow service aborted: Unknown fatal error."
            + "Service is non-functional, please fix problem and restart bundle", ex);
        }
      }
    } finally {
      _lock.writeLock().unlock();
    }
  }

  /**
   * OSGi Declarative Services service deactivation method. Shuts down BPEL engine.
   * 
   * @param context
   *          OSGi service component context.
   */
  protected void deactivate(final ComponentContext context) {
    _lock.writeLock().lock();
    try {
      if (_bpelServer != null) {
        _bpelServer.shutdown();
        _bpelServer = null;
        _processingContext = null;
        _osgiContext = null;
      }
    } finally {
      _lock.writeLock().unlock();
    }
  }

  /**
   * initialize BPEL engine. *
   * 
   * @throws IOException
   *           error reading the configuration
   * @throws ODEServerException
   *           error initializing the ODE server
   * @throws ProcessingException
   *           error deploying the pipelines
   */
  private void initializeBPEL() throws IOException, ODEServerException, ProcessingException {
    _log.debug("Initialize BPEL engine");
    final ODEConfigProperties odeConfig = new ODEConfigProperties(_properties, PROP_PREFIX_ODE);
    _processingContext = new WebServiceContextFactory();
    _bpelServer = new ODEServer(odeConfig, _processingContext);
    _bpelServer.registerExtensionBundle(new SMILAExtensionBundle());
    deployPipelines();
    _log.debug("Initialization of BPEL engine successful");
  }

  /**
   * copy files in pipeline directory to temporary directory and use this as deployment directory for ODEServer.
   * 
   * @throws ProcessingException
   *           error initializing pipelines.
   * @throws IOException
   *           error creating deployment directory.
   */
  private void deployPipelines() throws ProcessingException, IOException {
    final String pipelineDirName = _properties.getProperty(PROP_PIPELINE_DIR, DEFAULT_PIPELINE_DIR);
    final File pipelineDeployDir = WorkspaceHelper.createWorkingDir(BUNDLE_NAME, pipelineDirName);

    copyPipelineDirectory(pipelineDirName, pipelineDeployDir);

    final Collection<QName> processes = _bpelServer.deploy(pipelineDeployDir);
    // JAXB used for parsing pipelet configs from BPEL needs this to be set.
    final ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    for (final QName processName : processes) {
      _log.info("Registering pipeline " + processName);
      ProcessingServiceManager.getInstance().registerPipeline(this, processName);
      PipeletManager.getInstance().registerPipeline(this, processName);
      final String pipelineName = processName.getLocalPart();
      _pipelinePerformanceCounter.put(pipelineName, new PipelinePerformanceCounter(pipelineName));
    }
    Thread.currentThread().setContextClassLoader(oldCL);
  }

  /**
   * copy pipelines files from configuration directory to workspace directory for deployment.
   * 
   * @param pipelineConfigDirName
   *          name of configuration directory containing BPEL and associated files.
   * @param pipelineDeployDir
   *          target workspace directory to deploy from
   * @throws IOException
   *           error during copying
   */
  private void copyPipelineDirectory(final String pipelineConfigDirName, final File pipelineDeployDir)
    throws IOException {
    _log.info("Pipeline deploy directory is " + pipelineDeployDir.getAbsolutePath());
    FileUtils.cleanDirectory(pipelineDeployDir);
    final File configDir = ConfigUtils.getConfigFolder(BUNDLE_NAME, pipelineConfigDirName);
    FileUtils.copyDirectory(configDir, pipelineDeployDir, new NotFileFilter(new WildcardFileFilter(".*")));
    _log.info("Pipeline configuration directory has been copied to workspace successfully.");
  }

  /**
   * read configuration property file.
   * 
   * @throws IOException
   *           error reading configuration file
   */
  private void readConfiguration() throws IOException {
    _properties = new Properties();
    InputStream configurationFileStream = null;
    try {
      configurationFileStream = ConfigUtils.getConfigStream(BUNDLE_NAME, CONFIGURATION_FILE);
      _properties.load(configurationFileStream);
    } catch (final IOException ex) {
      throw new IOException("Could not read configuration property file " + CONFIGURATION_FILE + ": "
        + ex.toString());
    } finally {
      IOUtils.closeQuietly(configurationFileStream);
    }
  }

  /**
   * generate new request id and store blackboard.
   * 
   *@param blackboard
   *          request blackboard.
   * @return new request Id
   */
  private String initRequest(final Blackboard blackboard) {
    _lock.readLock().lock();
    final String requestId = Long.toString(_requestIdSequence.getAndIncrement());
    if (_log.isDebugEnabled()) {
      _log.debug("Starting to process request ID = " + requestId);
    }
    _blackboards.put(requestId, blackboard);
    return requestId;
  }

  /**
   * release blackboard and caught exception.
   * 
   * @param requestId
   *          request Id
   */
  private void cleanupRequest(final String requestId) {
    if (_log.isDebugEnabled()) {
      _log.debug("Cleaning up request ID = " + requestId);
    }
    _blackboards.remove(requestId);
    _pipeletExceptions.remove(requestId);
    _lock.readLock().unlock();

  }

}
