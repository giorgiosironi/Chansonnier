/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.tools.search.lucene;

import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.IDFParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DDateFieldParameterCodec {

  /**
   * PARAMETER_NS.
   */
  private static final String PARAMETER_NS = "http://www.anyfinder.de/Search/DateField";

  /**
   * Constructor.
   */
  private DDateFieldParameterCodec() {

  }

  public static Element encode(DDateFieldParameter dDateFieldParameter, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(PARAMETER_NS, "Parameter");
    // el.setAttribute("xmlns", PARAMETER_NS);
    element.appendChild(el);

    return el;
  }

  public static Element encode(IDFParameter dDFP, Element element) throws DSearchException {
    if (dDFP instanceof DDateFieldParameter) {
      return encode((DDateFieldParameter) dDFP, element);
    } else {
      throw new DSearchException("DDateFieldParameter type is invalid [" + dDFP.getClass().getName() + "]");
    }
  }

  public static IDFParameter decode(Element element) throws DSearchException {

    return new DDateFieldParameter();
  } // End Method def.

}
