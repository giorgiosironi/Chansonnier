/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.tools.search.lucene;

import org.eclipse.smila.search.utils.search.ITFParameter;

/**
 * @author gschmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class DTextFieldParameter implements ITFParameter {

  /**
   * _tolerance.
   */
  private DTolerance _tolerance;

  /**
   * _operator.
   */
  private DOperator _operator;

  /**
   * @author gschmidt
   * 
   */
  public static final class DOperator {
    /**
     * AND.
     */
    public static final DOperator AND = new DOperator("AND");

    /**
     * OR.
     */
    public static final DOperator OR = new DOperator("OR");

    /**
     * PHRASE.
     */
    public static final DOperator PHRASE = new DOperator("PHRASE");

    /**
     * _mStrValue.
     */
    public String _mStrValue;

    /**
     * Constructor.
     * 
     * @param value -
     */
    private DOperator(String value) {
      _mStrValue = value;
    }

    /**
     * @param operator -
     * @return DOperator
     */
    public static DOperator getInstance(String operator) {

      if ("AND".equals(operator)) {
        return DOperator.AND;
      }

      if ("OR".equals(operator)) {
        return DOperator.OR;
      }

      if ("PHRASE".equals(operator)) {
        return DOperator.PHRASE;
      }

      return null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return _mStrValue;

    }
  }

  /**
   * @author gschmidt.
   * 
   */
  public static class DTolerance {
    /**
     * TOLERANT.
     */
    public static final DTolerance TOLERANT = new DTolerance("tolerant");

    /**
     * EXACT.
     */
    public static final DTolerance EXACT = new DTolerance("exact");

    /**
     * mStrValue.
     */
    public String _mStrValue;

    /**
     * DTolerance.
     * 
     * @param value -
     */
    private DTolerance(String value) {
      _mStrValue = value;
    }

    /**
     * @param tolerance -
     * @return DTolerance
     */
    public static DTolerance getInstance(String tolerance) {

      if ("tolerant".equals(tolerance)) {
        return DTolerance.TOLERANT;
      }

      if ("exact".equals(tolerance)) {
        return DTolerance.EXACT;
      }

      return null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return _mStrValue;

    }
  }

  /**
   * DTextFieldParameter.
   */
  public DTextFieldParameter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   * @return Object
   */
  @Override
  public Object clone() {
    final DTextFieldParameter tfp = new DTextFieldParameter();
    tfp.setOperator(_operator);
    tfp.setTolerance(_tolerance);
    return tfp;
  }

  /**
   * @return DOperator
   */
  public DOperator getOperator() {
    return _operator;
  }

  /**
   * @param value -
   */
  public void setOperator(DOperator value) {
    _operator = value;
  }

  /**
   * @return DTolerance
   */
  public DTolerance getTolerance() {
    return _tolerance;
  }

  /**
   * @param value -
   */
  public void setTolerance(DTolerance value) {
    _tolerance = value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.search.IParameter#getCodecClass()
   */
  public Class getCodecClass() {
    return DTextFieldParameterCodec.class;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.eclipse.smila.search.utils.search.IParameter#isComplete()
   */
  public boolean isComplete() {

    if (_tolerance == null) {
      return false;
    }

    if (_operator == null) {
      return false;
    }

    return true;
  }
}
