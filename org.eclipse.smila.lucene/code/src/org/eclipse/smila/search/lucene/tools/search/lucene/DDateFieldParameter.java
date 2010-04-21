/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.tools.search.lucene;

import org.eclipse.smila.search.utils.search.IDFParameter;

/**
 * @author gschmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDateFieldParameter implements IDFParameter {

  /**
   * 
   */
  public DDateFieldParameter() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.search.IParameter#getCodecClass()
   */
  public Class getCodecClass() {
    return DDateFieldParameterCodec.class;
  }

  @Override
  public Object clone() {
    return new DDateFieldParameter();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.utils.search.IParameter#isComplete()
   */
  public boolean isComplete() {
    return true;
  }

}
