/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.templates.messages.nodetransformerregistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.search.utils.param.ParameterException;
import org.eclipse.smila.search.utils.param.def.DParameterDefinitionCodec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class DNodeTransformerCodec {

  /**
   * Constructor.
   */
  private DNodeTransformerCodec() {

  }

  /**
   * @param dNodeTransformer -
   * @param element -
   * @return Element
   * @throws DNodeTransformerRegistryException -
   */
  protected static Element encode(DNodeTransformer dNodeTransformer, Element element)
    throws DNodeTransformerRegistryException {
    final Log log = LogFactory.getLog(DNodeTransformerCodec.class);

    final Document doc = element.getOwnerDocument();
    final Element el =
      (Element) element.appendChild(doc.createElementNS(DNodeTransformerRegistryCodec.NS, "NodeTransformer"));

    el.setAttribute("Name", dNodeTransformer.getName());

    Element elTemp = null;
    elTemp = doc.createElementNS(DNodeTransformerRegistryCodec.NS, "ClassName");
    elTemp.appendChild(doc.createTextNode(dNodeTransformer.getClassName()));
    el.appendChild(elTemp);

    elTemp = doc.createElementNS(DNodeTransformerRegistryCodec.NS, "Description");
    elTemp.appendChild(doc.createTextNode(dNodeTransformer.getDescription()));
    el.appendChild(elTemp);

    try {
      DParameterDefinitionCodec.encode(dNodeTransformer.getParameterDefinition(), el);
    } catch (ParameterException e) {
      log.error("Unable to encode parameters for NodeTransformer [" + dNodeTransformer.getName() + "]: "
        + e.getMessage(), e);
      throw new DNodeTransformerRegistryException("Unable to encode parameters for NodeTransformer ["
        + dNodeTransformer.getName() + "]: " + e.getMessage());
    }

    return el;
  }

  protected static DNodeTransformer decode(Element element) throws DNodeTransformerRegistryException {
    final Log log = LogFactory.getLog(DNodeTransformerCodec.class);

    final DNodeTransformer dNodeTransformer = new DNodeTransformer();

    dNodeTransformer.setName(element.getAttribute("Name"));

    final NodeList nl = element.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if ("ClassName".equals(nl.item(i).getLocalName())) {
        final Element elTemp = (Element) nl.item(i);
        dNodeTransformer.setClassName(((Text) elTemp.getFirstChild()).getNodeValue());
      } else if ("Description".equals(nl.item(i).getLocalName())) {
        final Element elTemp = (Element) nl.item(i);
        dNodeTransformer.setDescription(((Text) elTemp.getFirstChild()).getNodeValue());
      } else if ("ParameterDefinition".equals(nl.item(i).getLocalName())) {
        try {
          dNodeTransformer.setParameterDefinition(DParameterDefinitionCodec.decode((Element) nl.item(i)));
        } catch (ParameterException e) {
          log.error("Unable to decode parameters for NodeTransformer [" + dNodeTransformer.getName() + "]: "
            + e.getMessage(), e);
          throw new DNodeTransformerRegistryException("Unable to decode parameters for NodeTransformer ["
            + dNodeTransformer.getName() + "]: " + e.getMessage());
        }
      }

    }
    return dNodeTransformer;
  }
}
