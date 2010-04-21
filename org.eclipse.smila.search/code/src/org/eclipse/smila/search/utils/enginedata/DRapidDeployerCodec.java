/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.enginedata;

import org.apache.xerces.impl.dv.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author gschmidt
 * 
 */
abstract class DRapidDeployerCodec {

  /**
   * Constructor.
   */
  private DRapidDeployerCodec() {

  }

  /**
   * @param dRapidDeployer -
   * @param element -
   * @return Element
   * @throws DEngineDataException -
   */
  protected static Element encode(DRapidDeployer dRapidDeployer, Element element) throws DEngineDataException {

    final Document doc = element.getOwnerDocument();
    final Element el = doc.createElementNS(DAnyFinderEngineDataCodec.NS, "RapidDeployer");

    if (dRapidDeployer != null) {
      final Element plugin = doc.createElementNS(DAnyFinderEngineDataCodec.NS, "Plugin");
      plugin.appendChild(doc.createTextNode(Base64.encode(dRapidDeployer.getPlugin())));
      el.appendChild(plugin);
    } else {
      throw new DEngineDataException("plugin not specified");
    }

    element.appendChild(el);
    return el;
  } // End Method encode

  protected static DSDK decode(Element element) throws DEngineDataException {
    throw new DEngineDataException("not supported");
  }
}
