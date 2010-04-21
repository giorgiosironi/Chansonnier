/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.fieldtemplates;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class DFieldTemplates implements Cloneable {
  private String _indexName;

  private ArrayList<DFieldTemplate> _templates = new ArrayList<DFieldTemplate>();

  /**
   * 
   */
  public DFieldTemplates() {
  }

  public DFieldTemplates(String indexName) {
    _indexName = indexName;
  }

  public Object clone() {
    DFieldTemplates obj = null;

    try {
      obj = (DFieldTemplates) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("unable to clone field templates");
    }

    obj._templates = new ArrayList<DFieldTemplate>();

    for (int i = 0; i < _templates.size(); i++) {
      try {
        obj.addTemplate((DFieldTemplate) _templates.get(i).clone());
      } catch (CloneNotSupportedException ex) {
        throw new RuntimeException("unable to clone field templates");
      }
    }

    return obj;

  }

  public void setIndexName(String indexName) {
    this._indexName = indexName;
  }

  public String getIndexName() {
    return this._indexName;
  }

  public void addTemplate(DFieldTemplate dTemplate) {
    _templates.add(dTemplate);
  }

  public void removeTemplate(int index) {
    _templates.remove(index);
  }

  public void setTemplate(int index, DFieldTemplate dTemplate) {
    _templates.set(index, dTemplate);
  }

  public DFieldTemplate getTemplate(int index) {
    return (DFieldTemplate) _templates.get(index);
  }

  public Iterator<DFieldTemplate> getTemplates() {
    return _templates.iterator();
  }

  public int getTemplateCount() {
    return _templates.size();
  }

}
