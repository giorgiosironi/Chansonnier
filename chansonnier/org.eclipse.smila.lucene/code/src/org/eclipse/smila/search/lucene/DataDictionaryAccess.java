/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene;

import org.eclipse.smila.search.datadictionary.messages.datadictionary.DConnection;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DDException;
import org.eclipse.smila.search.lucene.messages.datadictionaryconnection.DConnectionCodec;
import org.w3c.dom.Element;


/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DataDictionaryAccess extends org.eclipse.smila.search.datadictionary.DataDictionaryAccess {

  /**
   * 
   */
  public DataDictionaryAccess() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IDataDictionaryAccess#decodeConnection(org.w3c.dom.Element)
   */
  @Override
  public DConnection decodeConnection(Element eConnection) throws DDException {
    return DConnectionCodec.decode(eConnection);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.smila.search.plugin.IDataDictionaryAccess#encodeConnection(org.eclipse.smila.search.datadictionary.messages.datadictionary.DConnection,
   *      org.w3c.dom.Element)
   */
  @Override
  public Element encodeConnection(DConnection dConnection, Element element) throws DDException {
    if (!(dConnection instanceof org.eclipse.smila.search.lucene.messages.datadictionaryconnection.DConnection)) {
      throw new DDException("invalid class type for encoding [" + dConnection.getClass().getName() + "]");
    }
    return DConnectionCodec.encode(
      (org.eclipse.smila.search.lucene.messages.datadictionaryconnection.DConnection) dConnection, element);
  }
  
}
