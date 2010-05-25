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
public class DNumberField extends DField implements Cloneable {

  private Long _min;

  private Long _max;

  private INFParameter _parameter = null;

  /**
   * Constructor
   */
  public DNumberField() {
  }

  @Override
  public Object clone() {
    final DNumberField nf = (DNumberField) super.clone();

    if (_min != null) {
      nf.setMin(new Long(_min.longValue()));
    }

    if (_max != null) {
      nf.setMax(new Long(_max.longValue()));
    }

    if (_parameter != null) {
      nf.setParameter((INFParameter) _parameter.clone());
    }

    return nf;
  }

  public Long getMax() {
    return _max;
  }

  public Long getMin() {
    return _min;
  }

  public INFParameter getParameter() {
    return _parameter;
  }

  public void setMax(Long value) {
    _max = value;
  }

  public void setMin(Long value) {
    _min = value;
  }

  public void setParameter(INFParameter parameter) {
    this._parameter = parameter;
  }
}
