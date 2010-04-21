/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import java.util.Date;

/**
 * @author geschmidt
 * 
 */
public class DDateField extends DTermContent implements Cloneable {

  /**
   * fieldNo.
   */
  private int _fieldNo;

  /**
   * min.
   */
  private Date _min;

  /**
   * max.
   */
  private Date _max;

  /**
   * Constructor.
   */
  public DDateField() {
  }

  /**
   * Constructor.
   * 
   * @param fieldNo -
   * @param min -
   * @param max -
   */
  public DDateField(int fieldNo, Date min, Date max) {
    this._fieldNo = fieldNo;
    this._min = min;
    this._max = max;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.messages.advsearch.DTermContent#clone()
   */
  @Override
  public Object clone() {

    final DDateField obj = (DDateField) super.clone();

    if (_min != null) {
      obj.setMin((Date) _min.clone());
    }

    if (_max != null) {
      obj.setMax((Date) _max.clone());
    }

    return obj;
  }

  public Date getMin() {
    return _min;
  }

  public void setMin(Date min) {
    this._min = min;
  }

  public Date getMax() {
    return _max;
  }

  public void setMax(Date max) {
    this._max = max;
  }

  public int getFieldNo() {
    return _fieldNo;
  }

  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  @Override
  public String getType() {
    return TC_DATEFIELD;
  }

  /**
   * ************************************************************************ Calls the toString() method on the given
   * Object and THIS instance and then compares the resultant Strings with the equals() method.
   * 
   * @param obj -
   * @return boolean
   */
  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

}
