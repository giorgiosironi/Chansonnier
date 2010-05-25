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

import org.eclipse.smila.datamodel.record.impl.AnnotatableImpl;

/**
 * base class of items adapting extension activities to their execution objects.
 * 
 * @author jschumacher
 * 
 */
public abstract class ExtensionAdapter extends AnnotatableImpl {

  /**
   * serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * key of activity.
   */
  private String _key;

  /**
   * BPEL input variable name.
   */
  private String _inputVariable;

  /**
   * BPEL output variable name.
   */
  private String _outputVariable;

  /**
   * performance counter for this adapter.
   */
  private ProcessingPerformanceCounter _counter;

  /**
   * @return key of activity
   */
  public String getKey() {
    return _key;
  }

  /**
   * @param key
   *          new key
   */
  public void setKey(String key) {
    _key = key;
  }

  /**
   * 
   * @return BPEL input variable name.
   */
  public String getInputVariable() {
    return _inputVariable;
  }

  /**
   * 
   * @param inputVariable
   *          set BPEL input variable name.
   */
  public void setInputVariable(String inputVariable) {
    this._inputVariable = inputVariable;
  }

  /**
   * 
   * @return BPEL output variable name.
   */
  public String getOutputVariable() {
    return _outputVariable;
  }

  /**
   * 
   * @param outputVariable
   *          set BPEL output variable name.
   */
  public void setOutputVariable(String outputVariable) {
    this._outputVariable = outputVariable;
  }

  /**
   * @return counter of this adapter.
   */
  public ProcessingPerformanceCounter getCounter() {
    return _counter;
  }

  /**
   * @param counter
   *          a new performance counter
   */
  public void setCounter(ProcessingPerformanceCounter counter) {
    _counter = counter;
  }

  /**
   * @return a description of this adapter, e.g. for logging.
   */
  public abstract String getPrintName();

}
