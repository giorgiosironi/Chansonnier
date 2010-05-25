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

package org.eclipse.smila.processing.bpel;

import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.compiler.v2.BpelCompiler20;
import org.apache.ode.bpel.extension.ExtensibleElement;
import org.apache.ode.bpel.extension.ExtensionOperation;
import org.apache.ode.bpel.extension.ExtensionValidator;
import org.apache.ode.bpel.rtrep.common.extension.AbstractExtensionBundle;
import org.apache.ode.bpel.rtrep.common.extension.ExtensionContext;
import org.apache.ode.bpel.rtrep.v2.OExtensionActivity;
import org.w3c.dom.Element;

/**
 * ODE extension bundle for compiling and invoking invokePipelet and invokeService extension activities.
 * 
 * @author jschumacher
 * 
 */
public class SMILAExtensionBundle extends AbstractExtensionBundle {
  /**
   * local name of InvokePipelet extension activity: "invokePipelet".
   */
  public static final String TAG_INVOKE_PIPELET = "invokePipelet";

  /**
   * local name of InvokeService extension activity: "invokeService".
   */
  public static final String TAG_INVOKE_SERVICE = "invokeService";

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.runtime.extension.AbstractExtensionBundle#getNamespaceURI()
   */
  @Override
  public String getNamespaceURI() {
    return ODEWorkflowProcessor.NAMESPACE_PROCESSOR;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.ode.bpel.runtime.extension.AbstractExtensionBundle#registerExtensionActivities()
   */
  @Override
  public void registerExtensionActivities() {
    registerExtensionOperation(TAG_INVOKE_PIPELET, InvokePipeletActivity.class);
    registerExtensionOperation(TAG_INVOKE_SERVICE, InvokeServiceActivity.class);
  }

  /**
   * Extension activity to invoke simple pipelets.
   * 
   * @author jschumacher
   * 
   */
  public static class InvokePipeletActivity implements ExtensionOperation, ExtensionValidator {

    /**
     * promote invocation to simple pipelet manager.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.runtime.extension.ExtensionOperation
     *      #run(org.apache.ode.bpel.runtime.extension.ExtensionContext, org.w3c.dom.Element)
     */
    public void run(ExtensionContext context, Element element) throws FaultException {
      PipeletManager.getInstance().invokeActivity(context, element);
    }

    /**
     * promote invocation to simple pipelet manager.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.extension.ExtensionOperation#run(java.lang.Object, org.w3c.dom.Element)
     */
    public void run(Object contexto, Element element) throws FaultException {
      final ExtensionContext context = (ExtensionContext) contexto;
      run(context, element);
    }

    /**
     * to be called during compilation of invokePipelet activities.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.extension.ExtensionValidator#validate(java.lang.Object,
     *      org.apache.ode.bpel.extension.ExtensibleElement)
     */
    public void validate(Object compilerContext, ExtensibleElement element) {
      final BpelCompiler20 compiler = (BpelCompiler20) compilerContext;
      final OExtensionActivity activity = (OExtensionActivity) compiler.getCurrent();
      PipeletManager.getInstance().registerActivity(activity, element);
    }
  }

  /**
   * Extension activity to invoke processing services.
   * 
   * @author jschumacher
   * 
   */
  public static class InvokeServiceActivity implements ExtensionOperation, ExtensionValidator {

    /**
     * promote invocation to processing service manager.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.runtime.extension.ExtensionOperation
     *      #run(org.apache.ode.bpel.runtime.extension.ExtensionContext, org.w3c.dom.Element)
     */
    public void run(ExtensionContext context, Element element) throws FaultException {
      ProcessingServiceManager.getInstance().invokeActivity(context, element);
    }

    /**
     * promote invocation to processing service manager.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.extension.ExtensionOperation#run(java.lang.Object, org.w3c.dom.Element)
     */
    public void run(Object contexto, Element element) throws FaultException {
      final ExtensionContext context = (ExtensionContext) contexto;
      run(context, element);
    }

    /**
     * to be called during compilation of invokeService activities.
     * 
     * {@inheritDoc}
     * 
     * @see org.apache.ode.bpel.extension.ExtensionValidator#validate(java.lang.Object,
     *      org.apache.ode.bpel.extension.ExtensibleElement)
     */
    public void validate(Object compilerContext, ExtensibleElement element) {
      final BpelCompiler20 compiler = (BpelCompiler20) compilerContext;
      final OExtensionActivity activity = (OExtensionActivity) compiler.getCurrent();
      ProcessingServiceManager.getInstance().registerActivity(activity, element);
    }

  }

}
