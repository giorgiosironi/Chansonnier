/***********************************************************************************************************************
 * Copyright (c) 2008, 2009 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Juergen Schumacher (empolis GmbH) - initial API and implementation
 **********************************************************************************************************************/

package org.eclipse.smila.processing.bpel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.compiler.api.CompilationMessage;
import org.apache.ode.bpel.evar.ExternalVariableModuleException;
import org.apache.ode.bpel.extension.ExtensibleElement;
import org.apache.ode.bpel.rtrep.common.extension.ExtensionContext;
import org.apache.ode.bpel.rtrep.v2.OActivity;
import org.apache.ode.bpel.rtrep.v2.OExtensionActivity;
import org.apache.ode.bpel.rtrep.v2.OMessageVarType;
import org.apache.ode.bpel.rtrep.v2.OProcess;
import org.apache.ode.bpel.rtrep.v2.OScope.Variable;
import org.apache.ode.utils.DOMUtils;
import org.eclipse.smila.blackboard.BlackboardAccessException;
import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotatable;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.dom.RecordParser;
import org.eclipse.smila.datamodel.record.impl.AnnotatableImpl;
import org.eclipse.smila.ode.ODEServer;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.ProcessorMessage;
import org.eclipse.smila.processing.SearchMessage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class with methods needed in implementating ODE extension actitivities for SMILA.
 * 
 * @author jschumacher
 * 
 */
public abstract class ExtensionManager {

  /**
   * local name of setAnnotations element.
   */
  private static final String TAG_SETANNOTATIONS = "setAnnotations";

  /**
   * parser to use for parsing annotations.
   */
  private static final RecordParser RECORD_PARSER = new RecordParser();

  /**
   * mapping pipeline names to owning workflow processors for access to resources during invocation.
   */
  private final Map<QName, ODEWorkflowProcessor> _ownerMap = new HashMap<QName, ODEWorkflowProcessor>();

  /**
   * mapping extension activity keys to extension adapters for lookup at invocation.
   */
  private final Map<String, ExtensionAdapter> _adapterMap = new HashMap<String, ExtensionAdapter>();

  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * create adapter for detected extension actitity. Throws a
   * {@link org.apache.ode.bpel.compiler.api.CompilationException} if an error occurs.
   * 
   * @param activity
   *          the activity to register
   * @param element
   *          DOM element from BPEL describing the actvity
   */
  public void registerActivity(final OExtensionActivity activity, final ExtensibleElement element) {
    final OProcess process = activity.getOwner();
    final String key = getActivityKey(activity);
    final Element content = element.getNestedElement();
    final ExtensionAdapter adapter = doRegisterActivity(process, activity, content, key);
    if (adapter != null) {
      adapter.setKey(key);
      adapter.setInputVariable(getAttributeOfElement(content, "variables", "input"));
      adapter.setOutputVariable(getAttributeOfElement(content, "variables", "output"));
      parseAnnotations(content, adapter);
      if (_log.isInfoEnabled()) {
        _log.info(key + ": found " + adapter.getPrintName() + ", processing " + adapter.getInputVariable() + " -> "
          + adapter.getOutputVariable());
      }
      _adapterMap.put(key, adapter);
    }
  }

  /**
   * register extension actvity. Should throw a {@link org.apache.ode.bpel.compiler.api.CompilationException} if an
   * error occurs.
   * 
   * @param pipelineProcess
   *          process that contains the activity.
   * @param activity
   *          the activity to register
   * @param content
   *          XML content of actvity
   * @param key
   *          key of actvity
   * @return adapter for activity.
   */
  public abstract ExtensionAdapter doRegisterActivity(final OProcess pipelineProcess,
    final OExtensionActivity activity, final Element content, final String key);

  /**
   * register owner of pipeline.
   * 
   * @param processor
   *          ODE processor that owns this pipeline.
   * @param processName
   *          pipeline to register
   */
  public void registerPipeline(final ODEWorkflowProcessor processor, final QName processName) {
    _ownerMap.put(processName, processor);
  }

  /**
   * @return local name of tag of watched extension activities
   */
  public abstract String getExtensionName();

  /**
   * invoke extension activity from BPEL process.
   * 
   * @param context
   *          BPEL extension context.
   * @param element
   *          DOM representation of extension element.
   */
  public void invokeActivity(final ExtensionContext context, final Element element) {
    final long startTime = System.nanoTime();
    ProcessingPerformanceCounter counter = null;
    boolean success = false;
    try {
      if (_log.isDebugEnabled()) {
        _log.debug("activity name = " + context.getActivityName());
        _log.debug("process id = " + context.getProcessId());
      }
      final OActivity activity = context.getOActivity();
      final String key = getActivityKey(activity);
      final ExtensionAdapter adapter = getExtensionAdapter(key);
      final ODEWorkflowProcessor processor = getProcessor(activity);
      counter = adapter.getCounter();
      invokeAdapter(context, key, adapter, processor);
      context.complete();
      success = true;
    } catch (final Exception ex) {
      if (counter != null) {
        counter.addError(ex, false);
      }
      context.completeWithFault(ex);
    } finally {
      if (counter != null) {
        counter.countInvocationNanos(System.nanoTime() - startTime, success, 0, 0);
      }
    }
  }

  /**
   * invoke adapter to execute an extension activity.
   * 
   * @param context
   *          extension context
   * @param key
   *          activity key
   * @param adapter
   *          registered adapter
   * @param processor
   *          processor owning the process which contains the activity
   * @throws ProcessingException
   *           invocation failed.
   */
  private void invokeAdapter(final ExtensionContext context, final String key, final ExtensionAdapter adapter,
    final ODEWorkflowProcessor processor) throws ProcessingException {

    String requestId = null;
    final String inputVariableName = adapter.getInputVariable();
    String outputVariableName = adapter.getOutputVariable();
    if (outputVariableName == null) {
      outputVariableName = inputVariableName;
    }
    if (_log.isDebugEnabled()) {
      _log.debug(key + ": invoking " + adapter.getPrintName() + ", processing " + inputVariableName + " -> "
        + outputVariableName);
    }

    // added by jschumacher, 2009-03-23
    // this allows pipelets/services to call external webservices using the included org.apache.axis2 bundle.
    final ClassLoader tcclBackup = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ODEServer.class.getClassLoader());

    try {
      final Element inputVariable = (Element) context.readVariable(inputVariableName);
      final QName varType = getVariableType(context, inputVariableName);
      if (_log.isDebugEnabled()) {
        final String inputVariableString = DOMUtils.domToString(inputVariable);
        _log.debug(key + ": input = " + inputVariableString);
      }
      final MessageHelper messageHelper = processor.getMessageHelper();
      requestId = messageHelper.parseRequestId(inputVariable);

      checkAvailability(adapter, processor);

      final Blackboard blackboard = processor.getBlackboard(requestId);
      final ProcessorMessage request = parseMessage(blackboard, inputVariable, varType, messageHelper);
      copyAnnotations(blackboard, request, adapter);

      final ProcessorMessage result = doInvoke(adapter, processor, blackboard, request);
      Element outputVariable = inputVariable;
      outputVariable = createMessage(blackboard, result, messageHelper);

      messageHelper.addRequestId(outputVariable, requestId, varType);
      if (_log.isDebugEnabled()) {
        final String outputVariableString = DOMUtils.domToString(outputVariable);
        _log.debug(key + ": output = " + outputVariableString);
      }
      context.writeVariable(outputVariableName, outputVariable);
    } catch (final ProcessingException ex) {
      throw newProcessingException("processing", ex, key, requestId, processor);
    } catch (final BlackboardAccessException ex) {
      throw newProcessingException("blackboard access", ex, key, requestId, processor);
    } catch (final FaultException ex) {
      throw newProcessingException("BPEL variable access", ex, key, requestId, processor);
    } catch (final ExternalVariableModuleException ex) {
      throw newProcessingException("BPEL variable access", ex, key, requestId, processor);
    } catch (final RuntimeException ex) {
      throw newProcessingException("runtime", ex, key, requestId, processor);
    } finally {
      Thread.currentThread().setContextClassLoader(tcclBackup);
    }
  }

  /**
   * create ProcessingException from an exception thrown in pipeline element invocation. The exception is also stored in
   * the associated processor for later returning to the client, if possible.
   * 
   * @param description
   *          readable error name
   * @param cause
   *          exception thrown in the invocation
   * @param key
   *          pipeline element key.
   * @param requestId
   *          request id (may be null, if error occurs before it could be determined).
   * @param processor
   *          associated processor
   * @return processing exception to throw
   */
  private ProcessingException newProcessingException(final String description, final Exception cause,
    final String key, final String requestId, final ODEWorkflowProcessor processor) {
    String message = null;
    ProcessingException procEx = null;
    if (cause instanceof ProcessingException) {
      message = "Invocation of pipeline element " + key + " failed: " + cause.getMessage();
      procEx = new ProcessingException(message, cause.getCause());
    } else {
      message = "Invocation of pipeline element " + key + " failed due to " + description + " error.";
      procEx = new ProcessingException(message, cause);
    }
    // _log.error(message, cause);
    if (requestId != null) {
      processor.setPipeletException(requestId, procEx);
    }
    return procEx;
  }

  /**
   * check if element invoked by the adapter is already available.
   * 
   * @param adapter
   *          adapter to check
   * @param processor
   *          associated processor
   * @throws ProcessingException
   *           if elements represented by adapter cannot be invoked.
   */
  public abstract void checkAvailability(ExtensionAdapter adapter, ODEWorkflowProcessor processor)
    throws ProcessingException;

  /**
   * actually invoke the adapter.
   * 
   * @param adapter
   *          adapter to invoke
   * @param processor
   *          associated processor
   * @param blackboard
   *          blackboard instance to work on.
   * @param request
   *          record Ids of request.
   * @return Ids of result records
   * @throws ProcessingException
   *           error while processing.
   */
  public abstract ProcessorMessage doInvoke(final ExtensionAdapter adapter, final ODEWorkflowProcessor processor,
    final Blackboard blackboard, final ProcessorMessage request) throws ProcessingException;

  /**
   * create unique name for given key.
   * 
   * @param activity
   *          a ODE activity
   * @return unique name.
   */
  public String getActivityKey(final OActivity activity) {
    return activity.getOwner().getName() + "/" + activity.name;
  }

  /**
   * @return an unmodifiable collection of all currently known adapters.
   */
  public Collection<ExtensionAdapter> getAdapters() {
    return Collections.unmodifiableCollection(_adapterMap.values());
  }

  /**
   * get extension adapter for key.
   * 
   * @param key
   *          activity key
   * @return associated adapter, if any.
   * @throws ProcessingException
   *           no adapter found.
   */
  public ExtensionAdapter getExtensionAdapter(final String key) throws ProcessingException {
    final ExtensionAdapter adapter = _adapterMap.get(key);
    if (adapter == null) {
      throw new ProcessingException("no registration found for activity " + key);
    }
    return adapter;
  }

  /**
   * @param activity
   *          an extension activity
   * @return processor owning the process containing this activity.
   * @throws ProcessingException
   *           no processor found.
   */
  public ODEWorkflowProcessor getProcessor(final OActivity activity) throws ProcessingException {
    final QName processName = activity.getOwner().getQName();
    final ODEWorkflowProcessor processor = _ownerMap.get(processName);
    if (processor == null) {
      throw new ProcessingException("no owning processor found for " + getActivityKey(activity));
    }
    return processor;
  }

  /**
   * find element with given local name (proc: namespace) and return value of given attribute. return null, if not
   * found.
   * 
   * @param content
   *          an element to search in.
   * @param localName
   *          local name of element to look for.
   * @param attribute
   *          attribute name.
   * @return attribute value if found, else null.
   */
  public String getAttributeOfElement(final Element content, final String localName, final String attribute) {
    String attributeValue = null;
    final NodeList nodes = content.getElementsByTagNameNS(ODEWorkflowProcessor.NAMESPACE_PROCESSOR, localName);
    if (nodes.getLength() > 0) {
      attributeValue = ((Element) nodes.item(0)).getAttribute(attribute);
      if (StringUtils.isBlank(attributeValue)) {
        attributeValue = null;
      }
    }
    return attributeValue;
  }

  /**
   * parse annotations from given element and attach them to annotatble object.
   * 
   * @param content
   *          DOM objects to search for annotations
   * @param annotatable
   *          object to attach annotations to.
   */
  public void parseAnnotations(final Element content, final AnnotatableImpl annotatable) {
    final NodeList annotations =
      content.getElementsByTagNameNS(ODEWorkflowProcessor.NAMESPACE_PROCESSOR, TAG_SETANNOTATIONS);
    if (annotations != null && annotations.getLength() > 0) {
      for (int j = 0; j < annotations.getLength(); j++) {
        final Element setAnnotations = (Element) annotations.item(j);
        final NodeList childs = setAnnotations.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
          final Node child = childs.item(i);
          if (child instanceof Element) {
            final Element element = (Element) child;
            if (RecordParser.TAG_ANNOTATION.equals(element.getLocalName())) {
              RECORD_PARSER.parseAnnotation(annotatable, element);
            }
          }
        }
      }
    }
  }

  /**
   * copy annotations from annotable object to each record on blackboard identified by the given Ids.
   * 
   * @param blackboard
   *          blackboard
   * @param message
   *          target record Ids.
   * @param annotatable
   *          annotation source.
   * @throws BlackboardAccessException
   *           error copying annotations
   */
  public void copyAnnotations(final Blackboard blackboard, final ProcessorMessage message,
    final Annotatable annotatable) throws BlackboardAccessException {
    if (annotatable.hasAnnotations()) {
      if (message instanceof SearchMessage) {
        final SearchMessage searchMessage = (SearchMessage) message;
        if (searchMessage.hasQuery()) {
          copyAnnotations(blackboard, annotatable, searchMessage.getQuery());
        }
      }
      if (message.hasRecords()) {
        for (final Id id : message.getRecords()) {
          copyAnnotations(blackboard, annotatable, id);
        }
      }
    }
  }

  /**
   * utility method for subclasses to use for creating error messages during activity registration. They must throw a
   * {@link org.apache.ode.bpel.compiler.api.CompilationException} which needs to be created with a
   * {@link CompilationMessage}. The causing exception can be added to the
   * {@link org.apache.ode.bpel.compiler.api.CompilationException} itself.
   * 
   * @param key
   *          activity key
   * @param message
   *          message describing the error.
   * @return CompilationMessage describing an error in phase=0.
   */
  protected CompilationMessage createErrorCompilationMessage(final String key, final String message) {
    final CompilationMessage msg = new CompilationMessage();
    msg.severity = CompilationMessage.ERROR;
    msg.phase = 0;
    msg.code = key;
    msg.messageText = message;
    return msg;
  }

  /**
   * Counts the number of recordIds in a given Id[].
   * 
   * @param recordIds
   *          the Id[]
   * @return the count
   */
  protected int getRecordCount(final Id[] recordIds) {
    if (recordIds != null) {
      return recordIds.length;
    }
    return 0;
  }

  /**
   * get the declared type of the variable from the process definition. If no declaration is found,
   * {@value MessageHelper#TYPE_PROCESSORMESSAGE} is returned.
   * 
   * @param context
   *          ODE extension context
   * @param variableName
   *          name of variable
   * @return type of variable.
   */
  private QName getVariableType(final ExtensionContext context, final String variableName) {
    QName varType = MessageHelper.TYPE_PROCESSORMESSAGE;
    try {
      final Variable variable = context.getVisibleVariables().get(variableName);
      if (variable.type instanceof OMessageVarType) {
        final QName type = ((OMessageVarType) variable.type).messageType;
        if (type != null) {
          varType = type;
        }
      }
    } catch (Exception ex) {
      ex = null;
    }
    return varType;
  }

  /**
   * parse the variable value according to its type and sync the parsed workflow objects with the blackboard.
   * 
   * @param blackboard
   *          blackboard to sync workflow objects to
   * @param variable
   *          DOM variable value.
   * @param varType
   *          type of variable ({@link MessageHelper#TYPE_PROCESSORMESSAGE} or {@link MessageHelper#TYPE_SEARCHMESSAGE})
   * @param helper
   *          helper for parsing and syncing
   * @return the parsed message, instance of {@link ProcessorMessage} or {@link SearchMessage}
   */
  private ProcessorMessage parseMessage(final Blackboard blackboard, final Element variable, final QName varType,
    final MessageHelper helper) {
    if (MessageHelper.TYPE_SEARCHMESSAGE.equals(varType)) {
      return helper.parseSearchMessage(blackboard, variable);
    } else {
      return helper.parseMessage(blackboard, variable);
    }
  }

  /**
   * copy annotations from annotable object to a record on blackboard identified by the given Id.
   * 
   * @param blackboard
   *          blackboard
   * @param annotatable
   *          annotation source.
   * @param id
   *          target record Id.
   * @throws BlackboardAccessException
   *           error copying annotations
   */
  private void copyAnnotations(final Blackboard blackboard, final Annotatable annotatable, final Id id)
    throws BlackboardAccessException {
    final Iterator<String> names = annotatable.getAnnotationNames();
    while (names.hasNext()) {
      final String name = names.next();
      blackboard.removeAnnotation(id, null, name);
      final Collection<Annotation> annotations = annotatable.getAnnotations(name);
      for (final Annotation annotation : annotations) {
        final Annotation target = copyAnnotation(blackboard, id, annotation);
        blackboard.addAnnotation(id, null, name, target);
      }
    }
  }

  /**
   * create a deep copy of the given annotation.
   * 
   * @param blackboard
   *          blackboard
   * @param id
   *          ID of record to annotate
   * @param annotation
   *          annotation to copy
   * @return deep copy of annotation
   * @throws BlackboardAccessException
   *           error creating annotation
   */
  private Annotation copyAnnotation(final Blackboard blackboard, final Id id, final Annotation annotation)
    throws BlackboardAccessException {
    final Annotation target = blackboard.createAnnotation(id);
    if (annotation.hasAnonValues()) {
      target.setAnonValues(annotation.getAnonValues());
    }
    if (annotation.hasNamedValues()) {
      final Iterator<String> valueNames = annotation.getValueNames();
      while (valueNames.hasNext()) {
        final String valueName = valueNames.next();
        target.setNamedValue(valueName, annotation.getNamedValue(valueName));
      }
    }
    if (annotation.hasAnnotations()) {
      final Iterator<String> names = annotation.getAnnotationNames();
      while (names.hasNext()) {
        final String name = names.next();
        final Collection<Annotation> annotations = annotation.getAnnotations(name);
        for (final Annotation subAnnotation : annotations) {
          final Annotation subTarget = copyAnnotation(blackboard, id, subAnnotation);
          target.addAnnotation(name, subTarget);
        }
      }
    }
    return target;
  }

  /**
   * create DOM representation of {@link ProcessorMessage} or {@link SearchMessage}.
   * 
   * @param blackboard
   *          blackboard to read record data from
   * @param message
   *          the message to transform
   * @param helper
   *          helper for XML building
   * @return DOM message
   * @throws ProcessingException
   *           probably an error accessing the record on the blackboard.
   */
  private Element createMessage(final Blackboard blackboard, final ProcessorMessage message,
    final MessageHelper helper) throws ProcessingException {
    if (message instanceof SearchMessage) {
      return helper.createSearchMessage(blackboard, (SearchMessage) message);
    } else {
      return helper.createMessage(blackboard, message);
    }
  }

}
