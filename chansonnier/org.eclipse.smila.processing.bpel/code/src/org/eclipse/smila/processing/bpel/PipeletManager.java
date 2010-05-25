/*******************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 *******************************************************************************/

package org.eclipse.smila.processing.bpel;

import java.io.StringReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.rtrep.v2.OExtensionActivity;
import org.apache.ode.bpel.rtrep.v2.OProcess;
import org.apache.ode.utils.DOMUtils;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.ode.ODEServer;
import org.eclipse.smila.processing.IPipelet;
import org.eclipse.smila.processing.PipeletTrackerListener;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessorMessage;
import org.eclipse.smila.processing.SearchMessage;
import org.eclipse.smila.processing.SearchPipelet;
import org.eclipse.smila.processing.SimplePipelet;
import org.eclipse.smila.processing.configuration.PipeletConfiguration;
import org.eclipse.smila.processing.configuration.PipeletConfigurationLoader;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Pipelet Manager and Invoker.
 * 
 * @author jschumacher
 * 
 */
public final class PipeletManager extends ExtensionManager implements PipeletTrackerListener {
  /**
   * singleton instance.
   */
  private static final PipeletManager INSTANCE = new PipeletManager();

  /**
   * Map of class names to currently active simple pipelet classes.
   */
  private final Map<String, Class<? extends IPipelet>> _activePipeletClasses =
    new HashMap<String, Class<? extends IPipelet>>();

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * init SimplePipelet manager for given ODE processor.
   */
  private PipeletManager() {
    super();
  }

  /**
   * singleton instance access method.
   * 
   * @return singleton instance of SimpletonPipeletManager
   */
  public static PipeletManager getInstance() {
    return INSTANCE;
  }

  /**
   * register this object as a listener to pipelet change events by SimplePipeletTracker.
   * 
   * @param context
   *          bundle context.
   */
  public void registerAsListener(final BundleContext context) {
    final Dictionary<String, String> properties = new Hashtable<String, String>();
    context.registerService(PipeletTrackerListener.class.getName(), this, properties);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager#getExtensionName()
   */
  @Override
  public String getExtensionName() {
    return SMILAExtensionBundle.TAG_INVOKE_PIPELET;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager#registerActivity(org.apache.ode.bpel.o.OProcess,
   *      org.apache.ode.bpel.o.OExtensionActivity, org.w3c.dom.Element, java.lang.String)
   */
  @Override
  public PipeletInstance doRegisterActivity(final OProcess pipelineProcess, final OExtensionActivity activity,
    final Element content, final String key) {
    final PipeletInstance instance = new PipeletInstance();
    instance.setClassName(getAttributeOfElement(content, "pipelet", "class"));
    if (instance.getClassName() == null) {
      throw new CompilationException(createErrorCompilationMessage(key, "Missing definition of pipelet class"));
    }
    try {
      instance.setConfiguration(parseConfiguration(content));
      if (instance.getConfiguration() == null) {
        _log.info(key + ": no pipelet configuration found.");
      } else {
        _log.info(key + ": pipelet configuration parsed.");
      }
    } catch (final JAXBException ex) {
      throw new CompilationException(createErrorCompilationMessage(key, "Error parsing configuration"), ex);
    }
    final String location = activity.name.substring(activity.name.lastIndexOf("line-"));
    instance
      .setCounter(new PipeletPerformanceCounter(pipelineProcess.getName(), instance.getClassName(), location));
    try {
      initPipeletInstance(instance);
    } catch (final ProcessingException ex) {
      throw new CompilationException(createErrorCompilationMessage(key, "error initialising pipelet"), ex);
    }
    return instance;
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
    final IPipelet pipelet = getPipelet(adapter);
    if (pipelet == null) {
      throw new ProcessingException("Pipelet of class " + ((PipeletInstance) adapter).getClassName()
        + " for activity " + adapter.getKey() + " is not yet instantiated.");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.bpel.ExtensionManager
   *      #doInvoke(org.eclipse.smila.processing.bpel.ExtensionAdapter,
   *      org.eclipse.smila.processing.bpel.ODEWorkflowProcessor, org.eclipse.smila.datamodel.id.Id[])
   */
  @Override
  public ProcessorMessage doInvoke(final ExtensionAdapter adapter, final ODEWorkflowProcessor processor,
    final Blackboard blackboard, final ProcessorMessage request) throws ProcessingException {
    int incomingIds = 0;
    int outgoingIds = 0;
    try {
      final IPipelet pipelet = getPipelet(adapter);
      if (pipelet instanceof SearchPipelet && request instanceof SearchMessage) {
        incomingIds = getRecordCount(((SearchMessage) request).getRecords()) + 1;
        final SearchMessage result = ((SearchPipelet) pipelet).process(blackboard, (SearchMessage) request);
        outgoingIds = getRecordCount(result.getRecords()) + 1;
        return result;
      }
      if (pipelet instanceof SimplePipelet) {
        final SimplePipelet simplePipelet = (SimplePipelet) pipelet;
        if (request instanceof SearchMessage && !((SearchMessage) request).hasRecords()) {
          final SearchMessage searchRequest = (SearchMessage) request;
          if (searchRequest.hasQuery()) {
            final Id[] query = new Id[] { searchRequest.getQuery() };
            incomingIds = 1;
            final Id[] result = simplePipelet.process(blackboard, query);
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
          final Id[] result = simplePipelet.process(blackboard, request.getRecords());
          outgoingIds = getRecordCount(result);
          request.setRecords(result);
        }
        return request;
      }
      // we should not end up here.
      throw new ProcessingException("Could not invoke " + adapter.getPrintName()
        + "Either the pipelet class does not exist or you tried to invoke a search pipelet with a simple variable.");
    } finally {
      if (adapter.getCounter() != null) {
        adapter.getCounter().countIds(incomingIds, outgoingIds);
      }
    }
  }

  /**
   * return pipelet.
   * 
   * @param adapter
   *          simple pipelet instance
   * @return pipelet.
   */
  private IPipelet getPipelet(final ExtensionAdapter adapter) {
    final PipeletInstance instance = (PipeletInstance) adapter;
    return instance.getPipelet();
  }

  /**
   * learn about new pipelet classes and instantiate pipelets waiting for their classes.
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.PipeletTrackerListener#pipeletsAdded(java.util.Map)
   */
  public void pipeletsAdded(final Map<String, Class<? extends IPipelet>> pipeletClasses) {
    _log.info("Pipelets have been added: " + pipeletClasses.keySet());
    _activePipeletClasses.putAll(pipeletClasses);
    for (final ExtensionAdapter adapter : getAdapters()) {
      final PipeletInstance instance = (PipeletInstance) adapter;
      try {
        initPipeletInstance(instance);
      } catch (final ProcessingException ex) {
        _log.error("error when initializing pending pipelet", ex);
      }
    }
  }

  /**
   * forget pipelet classes and remove instances.
   * 
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.processing.PipeletTrackerListener#pipeletsRemoved(java.util.Map)
   */
  public void pipeletsRemoved(final Map<String, Class<? extends IPipelet>> pipeletClasses) {
    _log.info("Pipelets have been removed: " + pipeletClasses.keySet());
    for (final ExtensionAdapter adapter : getAdapters()) {
      final PipeletInstance instance = (PipeletInstance) adapter;
      if (pipeletClasses.containsKey(instance.getClassName())) {
        instance.setPipelet(null);
      }
    }
    for (final String className : pipeletClasses.keySet()) {
      _activePipeletClasses.remove(className);
    }
  }

  /**
   * initialize a pipelet instance.
   * 
   * @param instance
   *          instance.
   * @throws ProcessingException
   *           error during initialization.
   */
  private void initPipeletInstance(final PipeletInstance instance) throws ProcessingException {
    if (instance.getPipelet() == null) {
      final String className = instance.getClassName();
      if (_activePipeletClasses.containsKey(className)) {
        final Class<? extends IPipelet> pipeletClass = _activePipeletClasses.get(className);
        IPipelet pipelet;
        try {
          final Object obj = pipeletClass.newInstance();

          if (obj instanceof SimplePipelet) {
            pipelet = (SimplePipelet) obj;
          } else if (obj instanceof SearchPipelet) {
            pipelet = (SearchPipelet) obj;
          } else {
            throw new ProcessingException(
              "error instantiating pipelet class. pipelet is neither simple nor search pipelet [" + className + "]");
          }
          if (instance.getConfiguration() != null) {
            // added by jschumacher, 2009-03-23
            // this allows pipelets/services to init clients to external webservices using the
            // included org.apache.axis2 bundle.
            final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ODEServer.class.getClassLoader());
            try {
              pipelet.configure(instance.getConfiguration());
            } finally {
              Thread.currentThread().setContextClassLoader(tcclBackup);
            }
          }
          instance.setPipelet(pipelet);
        } catch (final InstantiationException e) {
          throw new ProcessingException("error instantiating pipelet class " + className, e);
        } catch (final IllegalAccessException e) {
          throw new ProcessingException("error instantiating pipelet class " + className, e);
        }
      }
    }
  }

  /**
   * parse a PipeletConfiguration from the content of extension activity.
   * 
   * @param content
   *          extension activity content.
   * @return the parsed PipeletConfiguration if one was contained, else null.
   * @throws JAXBException
   *           parse error.
   */
  private PipeletConfiguration parseConfiguration(final Element content) throws JAXBException {
    PipeletConfiguration configuration = null;
    final NodeList config =
      content.getElementsByTagNameNS(ODEWorkflowProcessor.NAMESPACE_PROCESSOR, "PipeletConfiguration");
    if (config.getLength() > 0) {
      final String configString = DOMUtils.domToString(config.item(0));
      // if (LOG.isDebugEnabled()) {
      // LOG.debug("Configuration to parse: " + configString);
      // }
      final Unmarshaller unmarshaller = PipeletConfigurationLoader.createPipeletConfigurationUnmarshaller(false);
      configuration = (PipeletConfiguration) unmarshaller.unmarshal(new StringReader(configString));
    }
    return configuration;
  }

}
