/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import org.eclipse.smila.search.utils.advsearch.IAdvSearch;

public class DTemplate {
  private String _name;

  private String _description;

  private IAdvSearch _advSearch;

  private DSelector _selector;

  public DTemplate() {
  }

  public void setName(String name) {
    this._name = name;
  }

  public String getName() {
    return this._name;
  }

  public void setDescription(String description) {
    this._description = description;
  }

  public String getDescription() {
    return this._description;
  }

  public void setAdvSearch(IAdvSearch advSearch) {
    this._advSearch = advSearch;
  }

  public IAdvSearch getAdvSearch() {
    return this._advSearch;
  }

  public void setSelector(DSelector dSelector) {
    this._selector = dSelector;
  }

  public DSelector getSelector() {
    return this._selector;
  }

}
