/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import java.util.Hashtable;
import java.util.Iterator;

public class DConfiguration {

  private DDefaultConfig _defaultConfig;

  private final Hashtable<String, DNamedConfig> _namedConfig = new Hashtable<String, DNamedConfig>();

  private DQueryConstraints _queryConstraints;

  public DConfiguration() {
  }

  public void addNamedConfig(DNamedConfig namedConfig) {
    this._namedConfig.put(namedConfig.getName(), namedConfig);
  }

  public DDefaultConfig getDefaultConfig() {
    return _defaultConfig;
  }

  public DNamedConfig getNamedConfig(String name) {
    return _namedConfig.get(name);
  }

  public int getNamedConfigCount() {
    return _namedConfig.size();
  }

  public Iterator getNamedConfigs() {
    return _namedConfig.values().iterator();
  }

  /**
   * @return DQueryConstraints
   */
  public DQueryConstraints getQueryConstraints() {
    return _queryConstraints;
  }

  public boolean hasNamedConfig(String name) {
    return _namedConfig.containsKey(name);
  }

  public void removeNamedConfig(String name) {
    this._namedConfig.remove(name);
  }

  public void setDefaultConfig(DDefaultConfig defaultConfig) {
    this._defaultConfig = defaultConfig;
  }

  public void setNamedConfig(DNamedConfig[] namedConfig) {
    this._namedConfig.clear();
    for (int i = 0; i < namedConfig.length; i++) {
      this._namedConfig.put(namedConfig[i].getName(), namedConfig[i]);
    }
  }

  /**
   * Sets the queryConstraints.
   * 
   * @param queryConstraints
   *          The queryConstraints to set
   */
  public void setQueryConstraints(DQueryConstraints queryConstraints) {
    this._queryConstraints = queryConstraints;
  }

}
