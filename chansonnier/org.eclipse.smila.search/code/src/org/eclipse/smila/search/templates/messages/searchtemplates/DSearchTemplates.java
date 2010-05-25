/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.searchtemplates;

import java.util.ArrayList;
import java.util.Iterator;

public class DSearchTemplates {
  private String _indexName;

  private ArrayList<DTemplate> _templates = new ArrayList<DTemplate>();

  public DSearchTemplates() {
  }

  public DSearchTemplates(String indexName) {
    this._indexName = indexName;
  }

  public void setIndexName(String indexName) {
    this._indexName = indexName;
  }

  public String getIndexName() {
    return this._indexName;
  }

  public void addTemplate(DTemplate dTemplate) {
    _templates.add(dTemplate);
  }

  public void removeTemplate(int index) {
    _templates.remove(index);
  }

  public void setTemplate(int index, DTemplate dTemplate) {
    _templates.set(index, dTemplate);
  }

  public DTemplate getTemplate(int index) {
    return _templates.get(index);
  }

  public Iterator<DTemplate> getTemplates() {
    return _templates.iterator();
  }

  public int getTemplateCount() {
    return _templates.size();
  }

}
