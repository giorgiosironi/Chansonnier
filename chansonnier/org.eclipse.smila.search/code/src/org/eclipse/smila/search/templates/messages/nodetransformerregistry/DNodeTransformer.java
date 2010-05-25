/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.nodetransformerregistry;

import org.eclipse.smila.search.utils.param.def.DParameterDefinition;

public class DNodeTransformer {
  private String _name;

  private String _className;

  private String _description;

  private DParameterDefinition _parameterDefinition;

  public DNodeTransformer() {
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public String getClassName() {
    return _className;
  }

  public void setClassName(String className) {
    this._className = className;
  }

  public String getDescription() {
    return _description;
  }

  public void setDescription(String description) {
    this._description = description;
  }

  /**
   * @return DParameterDefinition
   */
  public DParameterDefinition getParameterDefinition() {
    return _parameterDefinition;
  }

  /**
   * Sets the parameter definition.
   * 
   * @param parameterDefinition
   *          The parameterDefinition to set
   */
  public void setParameterDefinition(DParameterDefinition parameterDefinition) {
    this._parameterDefinition = parameterDefinition;
  }

}
