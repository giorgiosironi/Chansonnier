/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class DSelector implements Cloneable {

  /**
   * Field no.
   */
  private int _fieldNo;

  /**
   * Name.
   */
  private String _name;

  /**
   * Filter expression (optional).
   */
  private String _filterExpression;

  /**
   * 
   */
  public DSelector() {
    super();
  }

  /**
   * @return Returns the fieldNo.
   */
  public int getFieldNo() {
    return _fieldNo;
  }

  /**
   * @param fieldNo
   *          The fieldNo to set.
   */
  public void setFieldNo(int fieldNo) {
    this._fieldNo = fieldNo;
  }

  /**
   * @return Returns the filterExpression.
   */
  public String getFilterExpression() {
    return _filterExpression;
  }

  /**
   * @param filterExpression
   *          The filterExpression to set.
   */
  public void setFilterExpression(String filterExpression) {
    this._filterExpression = filterExpression;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return _name;
  }

  /**
   * @param name
   *          The name to set.
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    final DSelector newSelector = (DSelector) super.clone();

    return newSelector;
  }

}
