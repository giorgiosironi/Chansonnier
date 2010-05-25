/***********************************************************************************************************************
 * Copyright (c) 2008 empolis GmbH and brox IT Solutions GmbH. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: August Georg Schmidt (brox IT Solutions GmbH) - initial API and implementation
 **********************************************************************************************************************/
package org.eclipse.smila.search.utils.enginedata;

import org.eclipse.smila.utils.xml.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author August Georg Schmidt (BROX)
 * 
 */
public abstract class DAnyFinderEngineDataCodec {

  /**
   * Namespace.
   */
  public static final String NS = "http://www.anyfinder.de/EngineData";

  /**
   * Constructor.
   */
  private DAnyFinderEngineDataCodec() {

  }

  public static Document encode(DAnyFinderEngineData dAnyFinderEngineData) throws DEngineDataException {

    final Document doc = XMLUtils.getDocument();
    final Element rootElement = doc.createElementNS(DAnyFinderEngineDataCodec.NS, "AnyFinderEngineData");

    Attr attr = null;
    attr = doc.createAttribute("xmlns:xsi");
    attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
    rootElement.setAttributeNode(attr);

    attr = doc.createAttribute("xsi:schemaLocation");
    attr.setValue(NS + " ../xml/AnyFinderEngineData.xsd");
    rootElement.setAttributeNode(attr);

    final Element elVersion = doc.createElementNS(DAnyFinderEngineDataCodec.NS, "Version");
    elVersion.appendChild(doc.createTextNode(dAnyFinderEngineData.getVersion()));
    rootElement.appendChild(elVersion);

    if (dAnyFinderEngineData.getName() != null) {
      final Element elName = doc.createElementNS(DAnyFinderEngineDataCodec.NS, "Name");
      elName.appendChild(doc.createTextNode(dAnyFinderEngineData.getName()));
      rootElement.appendChild(elName);
    }

    DRapidDeployerCodec.encode(dAnyFinderEngineData.getRapidDeployer(), rootElement);
    DSDKCodec.encode(dAnyFinderEngineData.getSDK(), rootElement);

    doc.appendChild(rootElement);
    return doc;
  }

  public static DAnyFinderEngineData decode(Element element) throws DEngineDataException {
    throw new DEngineDataException("not supported");
  } // End Method decode
}
