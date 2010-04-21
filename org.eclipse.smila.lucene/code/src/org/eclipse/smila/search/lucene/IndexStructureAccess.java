/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import org.eclipse.smila.search.lucene.messages.indexstructure.DIndexStructureCodec;
import org.eclipse.smila.search.utils.indexstructure.DIndexStructure;
import org.eclipse.smila.search.utils.indexstructure.ISException;
import org.w3c.dom.Element;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IndexStructureAccess extends org.eclipse.smila.search.utils.indexstructure.IndexStructureAccess {

  /**
   * 
   */
  public IndexStructureAccess() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IIndexStructureAccess#decode(org.w3c.dom.Element)
   */
  @Override
  public DIndexStructure decode(Element eIndexStructure) throws ISException {
    return DIndexStructureCodec.decode(eIndexStructure);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IIndexStructureAccess#encode(org.eclipse.smila.search.utils.indexstructure.DIndexStructure,
   *      org.w3c.dom.Element)
   */
  @Override
  public Element encode(DIndexStructure dIndexStructure, Element element) throws ISException {
    return DIndexStructureCodec.encode(
      (org.eclipse.smila.search.lucene.messages.indexstructure.DIndexStructure) dIndexStructure, element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.Plugin#dataTypeMatches(java.lang.String, java.lang.String)
   */
  @Override
  public boolean dataTypeMatches(String indexDataType, String searchDataType) {
    if ("FTText".equals(searchDataType) && "Text".equals(indexDataType)) {
      return true;
    }
    if ("FTNumber".equals(searchDataType) && "Number".equals(indexDataType)) {
      return true;
    }
    if ("FTDate".equals(searchDataType) && "Date".equals(indexDataType)) {
      return true;
    }
    return false;
  }
}
