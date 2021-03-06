/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.advsearch;

/**
 * @author brox IT-Solutions GmbH
 * 
 */
public interface ITerm extends Cloneable {
  /**
   * @return Cloned object.
   * @throws CloneNotSupportedException
   *           Unable to clone object.
   */
  Object clone() throws CloneNotSupportedException;

  /**
   * @return Term content.
   */
  ITermContent getTerm();

  /**
   * @param term
   *          Term content.
   * @return Term content.
   */
  ITermContent setTerm(ITermContent term);
}
