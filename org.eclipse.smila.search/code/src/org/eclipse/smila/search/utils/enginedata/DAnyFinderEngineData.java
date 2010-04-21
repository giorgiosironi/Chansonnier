/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.enginedata;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public class DAnyFinderEngineData {

  /**
   * Version of engine.
   */
  private String _version;

  /**
   * Rapid Deployer.
   */
  private DRapidDeployer _rapidDeployer;

  /**
   * SDK.
   */
  private DSDK _sdk;

  /**
   * Name of engine.
   */
  private String _name;

  /**
   * 
   */
  public DAnyFinderEngineData() {
  }

  /**
   * @return Returns the version.
   */
  public String getVersion() {
    return _version;
  }

  /**
   * @param version
   *          The version to set.
   */
  public void setVersion(String version) {
    this._version = version;
  }

  /**
   * @return Returns the rapidDeployer.
   */
  public DRapidDeployer getRapidDeployer() {
    return _rapidDeployer;
  }

  /**
   * @param rapidDeployer
   *          The rapidDeployer to set.
   */
  public void setRapidDeployer(DRapidDeployer rapidDeployer) {
    this._rapidDeployer = rapidDeployer;
  }

  /**
   * @return Returns the sdk.
   */
  public DSDK getSDK() {
    return _sdk;
  }

  /**
   * @param sdk
   *          The sdk to set.
   */
  public void setSDK(DSDK sdk) {
    this._sdk = sdk;
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
