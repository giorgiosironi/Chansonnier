/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: brox IT-Solutions GmbH - initial creator
 **********************************************************************************************************************/

package org.eclipse.smila.search.datadictionary.messages.ddconfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.set.DParameterSetCodec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author brox IT-Solutions GmbH
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code
 * and Comments
 */
public abstract class DHighlightingTransformerCodec {

  public static DHighlightingTransformer decode(Element element) throws ConfigurationException {
    final Log log = LogFactory.getLog(DHighlightingTransformerCodec.class);

    final DHighlightingTransformer dHighlightingTransformer = new DHighlightingTransformer();

    dHighlightingTransformer.setName(element.getAttribute("Name"));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("ParameterSet".equals(nl.item(i).getLocalName())) {
        try {
          dHighlightingTransformer.setParameterSet(DParameterSetCodec.decode((Element) nl.item(i)));
        } catch (final ParameterException e) {
          log.error("Unable to decode parameters for HighlightingTransformer: " + e.getMessage(), e);
          throw new ConfigurationException("Unable to decode parameters for HighlightingTransformer: "
            + e.getMessage());
        }
      }
    }

    return dHighlightingTransformer;
  } // End Method def.

  public static Element encode(DHighlightingTransformer dHighlightingTransformer, Element element)
    throws ConfigurationException {
    final Log log = LogFactory.getLog(DHighlightingTransformerCodec.class);

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DConfigurationCodec.NS, "HighlightingTransformer");

    el.setAttribute("Name", dHighlightingTransformer.getName());

    try {
      DParameterSetCodec.encode(dHighlightingTransformer.getParameterSet(), el);
    } catch (final ParameterException e) {
      log.error("Unable to encode parameters for HighlightingTransformer: " + e.getMessage(), e);
      throw new ConfigurationException("Unable to encode parameters for HighlightingTransformer: " + e.getMessage());
    }

    element.appendChild(el);
    return el;
  }

}
