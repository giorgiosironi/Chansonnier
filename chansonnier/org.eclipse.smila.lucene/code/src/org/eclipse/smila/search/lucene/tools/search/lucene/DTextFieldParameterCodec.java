/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.lucene.tools.search.lucene;

import org.eclipse.smila.search.utils.search.DSearchException;
import org.eclipse.smila.search.utils.search.ITFParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author gschmidt
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DTextFieldParameterCodec {

  /**
   * PARAMETER_NS.
   */
  private static final String PARAMETER_NS = "http://www.anyfinder.de/Search/TextField";

  /**
   * Constructor.
   */
  private DTextFieldParameterCodec() {

  }

  /**
   * @param dTextFieldParameter -
   * @param element -
   * @return Element
   * @throws DSearchException -
   */
  public static Element encode(DTextFieldParameter dTextFieldParameter, Element element) throws DSearchException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(PARAMETER_NS, "Parameter");
    // el.setAttribute("xmlns", PARAMETER_NS);
    element.appendChild(el);

    // create parameter element

    if (dTextFieldParameter.getOperator() != null) {
      el.setAttribute("Operator", dTextFieldParameter.getOperator().toString());
    }

    if (dTextFieldParameter.getTolerance() != null) {
      el.setAttribute("Tolerance", dTextFieldParameter.getTolerance().toString());
    }

    return el;
  }

  public static Element encode(ITFParameter dTFP, Element element) throws DSearchException {
    if (dTFP instanceof DTextFieldParameter) {
      return encode((DTextFieldParameter) dTFP, element);
    } else {
      throw new DSearchException("DTextFieldParameter type is invalid [" + dTFP.getClass().getName() + "]");
    }
  }

  public static ITFParameter decode(Element element) throws DSearchException {

    final DTextFieldParameter dTFP = new DTextFieldParameter();

    if (element.hasAttribute("Operator")) {
      dTFP.setOperator(DTextFieldParameter.DOperator.getInstance(element.getAttribute("Operator")));
    }

    if (element.hasAttribute("Tolerance")) {
      dTFP.setTolerance(DTextFieldParameter.DTolerance.getInstance(element.getAttribute("Tolerance")));
    }

    return dTFP;
  } // End Method def.

}
