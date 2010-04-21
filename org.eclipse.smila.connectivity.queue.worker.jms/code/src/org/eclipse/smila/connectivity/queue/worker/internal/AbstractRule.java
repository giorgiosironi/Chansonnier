/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Ivan Churkin (brox IT Solutions GmbH) - initial creator
 * Sebastian Voigt (brox IT Solutions GmbH)
 **********************************************************************************************************************/
package org.eclipse.smila.connectivity.queue.worker.internal;

import org.eclipse.smila.connectivity.queue.worker.config.BaseRuleType;


/**
 * The Class AbstractRule.
 * 
 * @param <ConfigType>
 *          configuration type
 */
public abstract class AbstractRule<ConfigType extends BaseRuleType> extends AbstractLoggedComponent {

  /**
   * The _access point.
   */
  protected final ServicesAccessPoint _accessPoint;

  /**
   * The _rule config.
   */
  protected final ConfigType _ruleConfig;

  /**
   * Instantiates a new abstract rule.
   * 
   * @param accessPoint
   *          the access point
   * @param ruleConfig
   *          the rule config
   */
  public AbstractRule(final ServicesAccessPoint accessPoint, final ConfigType ruleConfig) {
    super(ruleConfig.getName());
    _accessPoint = accessPoint;
    _ruleConfig = ruleConfig;
  }

}
