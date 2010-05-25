/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.messages.datadictionaryconnection;

/**
 * Title: Any Finder Description: Copyright: Copyright (c) 2000 Company: BROX IT-Solutions GmbH
 * 
 * @author Georg Schmidt
 * @version 1.0
 */

import org.eclipse.smila.search.datadictionary.messages.datadictionary.DAnyFinderDataDictionaryCodec;
import org.eclipse.smila.search.datadictionary.messages.datadictionary.DDException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class DConnectionCodec {

  /**
   * Constructor.
   */
  private DConnectionCodec() {

  }

  /**
   * @param dConnection -
   * @param element -
   * @return Element
   * @throws DDException -
   */
  public static Element encode(DConnection dConnection, Element element) throws DDException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DAnyFinderDataDictionaryCodec.NS_CONNECTION, "Connection");

    el.setAttribute("MaxConnections", dConnection.getMaxConnections() + "");

    element.appendChild(el);

    return el;
  } // End Method encode

  public static DConnection decode(Element element) throws DDException {

    final DConnection dConnection = new DConnection();

    // get max connection information
    if (!element.getAttribute("MaxConnections").toString().equals("")) {
      dConnection.setMaxConnections(Integer.parseInt(element.getAttribute("MaxConnections")));
    } else {
      dConnection.setMaxConnections(1);
    }
    return dConnection;
  } // End Method decodeXML
} // End class Def.
