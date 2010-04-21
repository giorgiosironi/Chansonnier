/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

public class DNumField extends DTermContent implements Cloneable {

  /**
   * fieldNo.
   */
  private int _fieldNo;

  /**
   * min.
   */
  private long _min;

  /**
   * max.
   */
  private long _max;

  /**
   * Constructor.
   */
  public DNumField() {
  }

  /**
   * Constructor.
   * 
   * @param fieldNo -
   * @param min -
   * @param max -
   */
  public DNumField(int fieldNo, long min, long max) {
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
    return super.clone();
  }

  /**
   * @return long
   */
  public long getMin() {
    return _min;
  }

  /**
   * @param min -
   */
  public void setMin(long min) {
    this._min = min;
  }

  /**
   * @return long
   */
  public long getMax() {
    return _max;
  }

  /**
   * @param max -
   */
  public void setMax(long max) {
    this._max = max;
  }

  /**
   * @return int
   */
  public int getFieldNo() {
    return _fieldNo;
  }

  /**
   * @param fieldNo -
   */
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.lucene.messages.advsearch.DTermContent#getType()
   * @return String
   */
  @Override
  public String getType() {
    return TC_NUMFIELD;
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
