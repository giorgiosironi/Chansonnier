/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.def;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DParameter {

  protected boolean _defaultExists;

  private String _name;

  private String _constraint;

  private String _description;

  private String _type;

  /**
   * @return String
   */
  public String getConstraint() {
    return _constraint;
  }

  /**
   * @return String
   */
  public String getDescription() {
    return _description;
  }

  /**
   * @return String
   */
  public String getName() {
    return _name;
  }

  /**
   * @return String
   */
  public String getType() {
    return _type;
  }

  public boolean hasDefault() {
    return _defaultExists;
  }

  /**
   * Sets the constraint.
   * 
   * @param constraint
   *          The constraint to set
   */
  public void setConstraint(String constraint) {
    this._constraint = constraint;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          The description to set
   */
  public void setDescription(String description) {
    this._description = description;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          The name to set
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          The type to set
   */
  public void setType(String type) {
    this._type = type;
  }

}
