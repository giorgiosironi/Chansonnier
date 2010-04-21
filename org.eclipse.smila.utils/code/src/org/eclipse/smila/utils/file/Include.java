/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.utils.file;

import java.util.Date;

/**
 * Interface for element Include.
 * 
 * @author brox IT-Solutions GmbH
 * 
 */
public interface Include {

  /**
   * Getter method for attribute DateFrom.
   * 
   * @return DateFrom
   */
  Date getDateFrom();

  /**
   * Getter method for attribute DateTo.
   * 
   * @return DateTo
   */
  Date getDateTo();

  /**
   * Getter method for attribute Name.
   * 
   * @return Name
   */
  String getName();

  /**
   * Getter method for attribute Period.
   * 
   * @return Period
   */
  String getPeriod();

}
