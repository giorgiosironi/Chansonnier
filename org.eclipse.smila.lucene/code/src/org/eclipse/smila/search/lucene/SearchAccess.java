/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import org.eclipse.smila.search.lucene.tools.search.lucene.DDateFieldParameterCodec;
import org.eclipse.smila.search.lucene.tools.search.lucene.DNumberFieldParameterCodec;
import org.eclipse.smila.search.lucene.tools.search.lucene.DTextFieldParameterCodec;
import org.eclipse.smila.search.utils.search.IDFParameter;
import org.eclipse.smila.search.utils.search.INFParameter;
import org.eclipse.smila.search.utils.search.ITFParameter;


/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SearchAccess extends org.eclipse.smila.search.utils.search.SearchAccess {

  /**
   * 
   */
  public SearchAccess() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.ISearchAccess#getCodecClass(org.eclipse.smila.search.utils.search.ITFParameter)
   */
  @Override
  public Class getCodecClass(ITFParameter parameter) {
    return DTextFieldParameterCodec.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.ISearchAccess#getCodecClass(org.eclipse.smila.search.utils.search.IDFParameter)
   */
  @Override
  public Class getCodecClass(IDFParameter parameter) {
    return DDateFieldParameterCodec.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.ISearchAccess#getCodecClass(org.eclipse.smila.search.utils.search.INFParameter)
   */
  @Override
  public Class getCodecClass(INFParameter parameter) {
    return DNumberFieldParameterCodec.class;
  }

  @Override
  public Class getCodecClass(String className) throws ClassNotFoundException {
    return Class.forName(className);
  }

}
