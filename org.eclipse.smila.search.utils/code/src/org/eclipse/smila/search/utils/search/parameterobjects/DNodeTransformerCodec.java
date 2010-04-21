/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.utils.search.parameterobjects;

import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.set.DParameterSetCodec;
import org.eclipse.smila.search.utils.search.DSearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DNodeTransformerCodec {

  public static DNodeTransformer decode(Element element) throws DSearchException {
    final DNodeTransformer dNodeTransformer = new DNodeTransformer();

    dNodeTransformer.setName(element.getAttribute("Name"));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("ParameterSet".equals(nl.item(i).getLocalName())) {
        try {
          dNodeTransformer.setParameterSet(DParameterSetCodec.decode((Element) nl.item(i)));
        } catch (final ParameterException e) {
          throw new DSearchException("Unable to decode parameters for NodeTransformer: " + e.getMessage(), e);
        }
      }
    }

    return dNodeTransformer;
  } // End Method def.

  public static Element encode(DNodeTransformer dNodeTransformer, Element element) throws DSearchException {
    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS("http://www.anyfinder.de/Search/ParameterObjects", "NodeTransformer");
    el.setAttribute("Name", dNodeTransformer.getName());

    try {
      DParameterSetCodec.encode(dNodeTransformer.getParameterSet(), el);
    } catch (final ParameterException e) {
      throw new DSearchException("Unable to encode parameters for NodeTransformer: " + e.getMessage(), e);
    }

    element.appendChild(el);
    return el;
  }

}
