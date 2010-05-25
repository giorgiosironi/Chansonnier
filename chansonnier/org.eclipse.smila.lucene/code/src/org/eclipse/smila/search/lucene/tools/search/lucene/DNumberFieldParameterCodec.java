/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.tools.search.lucene;

import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.INFParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author GSchmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DNumberFieldParameterCodec {

  /**
   * PARAMETER_NS.
   */
  private static final String PARAMETER_NS = "http://www.anyfinder.de/Search/NumberField";

  /**
   * Constructor.
   */
  private DNumberFieldParameterCodec() {

  }

  /**
   * @param dNumberFieldParameter -
   * @param element -
   * @return ELEMENT
   * @throws DSearchException -
   */
  public static Element encode(DNumberFieldParameter dNumberFieldParameter, Element element)
    throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(PARAMETER_NS, "Parameter");
    // el.setAttribute("xmlns", PARAMETER_NS);
    element.appendChild(el);

    return el;
  }

  public static Element encode(INFParameter dNFP, Element element) throws DSearchException {
    if (dNFP instanceof DNumberFieldParameter) {
      return encode((DNumberFieldParameter) dNFP, element);
    } else {
      throw new DSearchException("DNumberFieldParameter type is invalid [" + dNFP.getClass().getName() + "]");
    }
  }

  public static INFParameter decode(Element element) throws DSearchException {

    return new DNumberFieldParameter();
  } // End Method def.

}
