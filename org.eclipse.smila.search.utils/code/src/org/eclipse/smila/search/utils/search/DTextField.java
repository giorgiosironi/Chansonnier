/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public class DTextField extends DField implements Cloneable {

  private String _text;

  private ITFParameter _parameter = null;

  /**
   * Constructor
   */
  public DTextField() {
  }

  @Override
  public Object clone() {

    final DTextField tf = (DTextField) super.clone();

    if (_parameter != null) {
      tf.setParameter((ITFParameter) _parameter.clone());
    }

    return tf;
  }

  public ITFParameter getParameter() {
    return _parameter;
  }

  public String getText() {
    return _text;
  }

  public void setParameter(ITFParameter parameter) {
    this._parameter = parameter;
  }

  public void setText(String value) {
    _text = value;
  }
}
