/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.param.set;

import org.eclipse.smila.search.utils.param.ParameterException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DParameterSetCodec {

  /**
   * NS.
   */
  public static final String NS = "http://www.brox.de/ParameterSet";

  /**
   * Constructor.
   */
  private DParameterSetCodec() {

  }

  /**
   * @param element -
   * @return DParameterSet
   * @throws ParameterException -
   */
  public static DParameterSet decode(Element element) throws ParameterException {

    final DParameterSet dParameterSet = new DParameterSet();

    // decode query information
    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("Parameter".equals(nl.item(i).getLocalName())) {
        dParameterSet.addParameter(DParameterCodec.decode((Element) nl.item(i)));
      }
    }

    return dParameterSet;
  } // End Method decode

  public static Element encode(DParameterSet dParameterSet, Element element) throws ParameterException {

    final Document doc = element.getOwnerDocument();
    final Element rootElement = doc.createElementNS(DParameterSetCodec.NS, "ParameterSet");

    final DParameter[] params = dParameterSet.getParameters();
    for (int i = 0; i < params.length; i++) {
      DParameterCodec.encode(params[i], rootElement);
    }

    element.appendChild(rootElement);
    return element;
  }
}
