/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

import org.eclipse.smila.search.utils.advsearch.ITerm;

/**
 * @author August Georg Schmidt (BROX)
 */
public class DFieldTemplate implements Cloneable {
  /**
   * Name.
   */
  private String _name = "";

  /**
   * Description.
   */
  private String _description = "";

  /**
   * Term.
   */
  private ITerm _term;

  /**
   * Selector expression.
   */
  private DSelector _selector;

  /**
   * 
   */
  public DFieldTemplate() {
  }

  /**
   * @return ITerm
   */
  public ITerm getTerm() {
    return _term;
  }

  /**
   * Sets the term.
   * 
   * @param term
   *          The term to set
   */
  public void setTerm(ITerm term) {
    this._term = term;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    final DFieldTemplate newTemplate = (DFieldTemplate) super.clone();

    if (_term != null) {
      newTemplate.setTerm((ITerm) _term.clone());
    }

    if (_selector != null) {
      newTemplate.setSelector((DSelector) _selector.clone());
    }

    return newTemplate;
  }

  /**
   * @return Returns the selector.
   */
  public DSelector getSelector() {
    return _selector;
  }

  /**
   * @param selector
   *          The selector to set.
   */
  public void setSelector(DSelector selector) {
    this._selector = selector;
  }

  /**
   * @return Returns the description.
   */
  public String getDescription() {
    return _description;
  }

  /**
   * @param description
   *          The description to set.
   */
  public void setDescription(String description) {
    this._description = description;
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

}
