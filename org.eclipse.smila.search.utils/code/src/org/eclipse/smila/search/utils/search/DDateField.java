/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search;

import java.util.Calendar;

/**
 * A Class class.
 * <P>
 * 
 * @author BROX IT-Solutions GmbH
 */
public class DDateField extends DField implements Cloneable {

  private Calendar _min;

  private Calendar _max;

  private IDFParameter _parameter = null;

  /**
   * Constructor
   */
  public DDateField() {
  }

  @Override
  public Object clone() {
    final DDateField df = (DDateField) super.clone();

    if (_min != null) {
      df.setDateMin((Calendar) _min.clone());
    }

    if (_max != null) {
      df.setDateMax((Calendar) _max.clone());
    }

    if (_parameter != null) {
      df.setParameter((IDFParameter) _parameter.clone());
    }

    return df;
  }

  public Calendar getDateMax() {
    return _max;
  }

  public Calendar getDateMin() {
    return _min;
  }

  public IDFParameter getParameter() {
    return _parameter;
  }

  public void setDateMax(Calendar value) {
    _max = value;
  }

  public void setDateMin(Calendar value) {
    _min = value;
  }

  public void setParameter(IDFParameter parameter) {
    this._parameter = parameter;
  }
}
