/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.advsearch;

import org.eclipse.smila.search.utils.advsearch.ITermContent;

/**
 * This class can contain exactly one of the possible terms at a time.
 * 
 * 
 */
public abstract class DTermContent implements ITermContent {

  /**
   * TC_NUMFIELD.
   */
  public static final String TC_NUMFIELD = "NUMFIELD";

  /**
   * TC_DATEFIELD.
   */
  public static final String TC_DATEFIELD = "NUMFIELD";

  /**
   * TC_TEXTFIELD.
   */
  public static final String TC_TEXTFIELD = "TEXTFIELD";

  /**
   * TC_TEXTFIELD.
   */
  public static final String TC_TEMPLATEFIELD = "TEMPLATEFIELD";

  /**
   * TC_OP_1.
   */
  public static final String TC_OP_1 = "OP_1";

  /**
   * TC_OP_N.
   */
  public static final String TC_OP_N = "OP_N";

  /**
   * TC_WMEAN.
   */
  public static final String TC_WMEAN = "WMEAN";

  /**
   * @return String.
   */
  public abstract String getType();

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    DTermContent obj = null;
    try {
      obj = (DTermContent) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("Cannot clone DTermContent", e);
    }
    return obj;
  }

}
